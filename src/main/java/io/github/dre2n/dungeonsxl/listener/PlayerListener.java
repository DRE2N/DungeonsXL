/*
 * Copyright (C) 2012-2016 Frank Baumann
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.dre2n.dungeonsxl.listener;

import io.github.dre2n.commons.util.messageutil.MessageUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.DMessages;
import io.github.dre2n.dungeonsxl.event.dgroup.DGroupCreateEvent;
import io.github.dre2n.dungeonsxl.event.dplayer.DPlayerDeathEvent;
import io.github.dre2n.dungeonsxl.event.dplayer.DPlayerKickEvent;
import io.github.dre2n.dungeonsxl.game.Game;
import io.github.dre2n.dungeonsxl.global.DPortal;
import io.github.dre2n.dungeonsxl.global.GameSign;
import io.github.dre2n.dungeonsxl.global.GlobalProtection;
import io.github.dre2n.dungeonsxl.global.GroupSign;
import io.github.dre2n.dungeonsxl.global.LeaveSign;
import io.github.dre2n.dungeonsxl.player.DEditPlayer;
import io.github.dre2n.dungeonsxl.player.DGamePlayer;
import io.github.dre2n.dungeonsxl.player.DGlobalPlayer;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.player.DInstancePlayer;
import io.github.dre2n.dungeonsxl.player.DPermissions;
import io.github.dre2n.dungeonsxl.player.DPlayers;
import io.github.dre2n.dungeonsxl.player.DSavePlayer;
import io.github.dre2n.dungeonsxl.reward.DLootInventory;
import io.github.dre2n.dungeonsxl.reward.RewardChest;
import io.github.dre2n.dungeonsxl.task.RespawnTask;
import io.github.dre2n.dungeonsxl.trigger.InteractTrigger;
import io.github.dre2n.dungeonsxl.trigger.UseItemTrigger;
import io.github.dre2n.dungeonsxl.world.EditWorld;
import io.github.dre2n.dungeonsxl.world.GameWorld;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

/**
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class PlayerListener implements Listener {

    DungeonsXL plugin = DungeonsXL.getInstance();
    DPlayers dPlayers = plugin.getDPlayers();

    @EventHandler(priority = EventPriority.HIGH)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        GameWorld gameWorld = GameWorld.getByWorld(player.getLocation().getWorld());
        if (gameWorld == null) {
            return;
        }

        Game game = Game.getByGameWorld(gameWorld);
        if (game == null) {
            return;
        }

        DGamePlayer dPlayer = DGamePlayer.getByPlayer(player);
        if (dPlayer == null) {
            return;
        }

        DPlayerDeathEvent dPlayerDeathEvent = new DPlayerDeathEvent(dPlayer, event, 1);
        plugin.getServer().getPluginManager().callEvent(dPlayerDeathEvent);

        if (dPlayerDeathEvent.isCancelled()) {
            return;
        }

        if (gameWorld.getGame() != null) {
            if (!gameWorld.getGame().getType().hasLives()) {
                return;
            }
        } else {
            return;
        }

        dPlayer.setLives(dPlayer.getLives() - dPlayerDeathEvent.getLostLives());

        if (dPlayer.getLives() != -1) {
            MessageUtil.sendMessage(player, DMessages.PLAYER_DEATH.getMessage(String.valueOf(dPlayer.getLives())));

            if (game.getRules().getKeepInventoryOnDeath()) {
                dPlayer.setRespawnInventory(event.getEntity().getInventory().getContents());
                dPlayer.setRespawnArmor(event.getEntity().getInventory().getArmorContents());
                // Delete all drops
                for (ItemStack item : event.getDrops()) {
                    item.setType(Material.AIR);
                }
            }
        }

        if (dPlayer.getLives() == 0 && dPlayer.isReady()) {
            DPlayerKickEvent dPlayerKickEvent = new DPlayerKickEvent(dPlayer, DPlayerKickEvent.Cause.DEATH);
            plugin.getServer().getPluginManager().callEvent(dPlayerKickEvent);

            if (!dPlayerKickEvent.isCancelled()) {
                MessageUtil.broadcastMessage(DMessages.PLAYER_DEATH_KICK.getMessage(player.getName()));
                dPlayer.leave();
                if (game.getRules().getKeepInventoryOnEscape()) {
                    dPlayer.applyRespawnInventory();
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        DGlobalPlayer dGlobalPlayer = dPlayers.getByPlayer(player);
        Block clickedBlock = event.getClickedBlock();

        if (dGlobalPlayer.isInBreakMode()) {
            return;
        }

        if (clickedBlock != null) {
            // Block Enderchests
            if (GameWorld.getByWorld(player.getWorld()) != null || EditWorld.getByWorld(player.getWorld()) != null) {
                if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
                    if (clickedBlock.getType() == Material.ENDER_CHEST) {
                        if (!DPermissions.hasPermission(player, DPermissions.BYPASS)) {
                            MessageUtil.sendMessage(player, DMessages.ERROR_ENDERCHEST.getMessage());
                            event.setCancelled(true);
                        }

                    } else if (clickedBlock.getType() == Material.BED_BLOCK) {
                        if (!DPermissions.hasPermission(player, DPermissions.BYPASS)) {
                            MessageUtil.sendMessage(player, DMessages.ERROR_BED.getMessage());
                            event.setCancelled(true);
                        }
                    }
                }
            }

            // Block Dispensers
            if (GameWorld.getByWorld(player.getWorld()) != null) {
                if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
                    if (clickedBlock.getType() == Material.DISPENSER) {
                        if (!DPermissions.hasPermission(player, DPermissions.BYPASS)) {
                            MessageUtil.sendMessage(player, DMessages.ERROR_DISPENSER.getMessage());
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }

        // Check Portals
        if (event.getItem() != null) {
            ItemStack item = event.getItem();

            if (dGlobalPlayer.isCreatingPortal()) {
                if (item.getType() == Material.WOOD_SWORD) {
                    if (clickedBlock != null) {
                        for (GlobalProtection protection : plugin.getGlobalProtections().getProtections(DPortal.class)) {
                            DPortal dPortal = (DPortal) protection;
                            if (!dPortal.isActive()) {
                                if (dPortal == dGlobalPlayer.getPortal()) {
                                    if (dPortal.getBlock1() == null) {
                                        dPortal.setBlock1(event.getClickedBlock());
                                        MessageUtil.sendMessage(player, DMessages.PLAYER_PORTAL_PROGRESS.getMessage());

                                    } else if (dPortal.getBlock2() == null) {
                                        dPortal.setBlock2(event.getClickedBlock());
                                        dPortal.setActive(true);
                                        dPortal.create();
                                        MessageUtil.sendMessage(player, DMessages.PLAYER_PORTAL_CREATED.getMessage());
                                    }
                                    event.setCancelled(true);
                                }
                            }
                        }
                    }
                }
            }

            // Copy/Paste a Sign and Block-info
            if (EditWorld.getByWorld(player.getWorld()) != null) {
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if (item.getType() == Material.STICK) {
                        DEditPlayer dPlayer = DEditPlayer.getByPlayer(player);
                        if (dPlayer != null) {
                            dPlayer.poke(clickedBlock);
                            event.setCancelled(true);
                        }
                    }
                }
            }

            // Trigger UseItem Signs
            GameWorld gameWorld = GameWorld.getByWorld(player.getWorld());
            if (gameWorld != null) {
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
                    if (UseItemTrigger.hasTriggers(gameWorld)) {
                        String name = null;
                        if (item.hasItemMeta()) {
                            if (item.getItemMeta().hasDisplayName()) {
                                name = item.getItemMeta().getDisplayName();

                            } else if (item.getType() == Material.WRITTEN_BOOK || item.getType() == Material.BOOK_AND_QUILL) {
                                if (item.getItemMeta() instanceof BookMeta) {
                                    BookMeta meta = (BookMeta) item.getItemMeta();
                                    if (meta.hasTitle()) {
                                        name = meta.getTitle();
                                    }
                                }
                            }
                        }
                        if (name == null) {
                            name = item.getType().toString();
                        }
                        UseItemTrigger trigger = UseItemTrigger.get(name, gameWorld);
                        if (trigger != null) {
                            trigger.onTrigger(player);
                        }
                    }
                }
            }
        }

        // Check Signs
        if (clickedBlock != null) {

            if (clickedBlock.getType() == Material.WALL_SIGN || clickedBlock.getType() == Material.SIGN_POST) {
                // Check Group Signs
                if (GroupSign.playerInteract(event.getClickedBlock(), player)) {
                    event.setCancelled(true);
                }

                // Check Game Signs
                if (GameSign.playerInteract(event.getClickedBlock(), player)) {
                    event.setCancelled(true);
                }

                // Leave Sign
                if (LeaveSign.playerInteract(event.getClickedBlock(), player)) {
                    event.setCancelled(true);
                }

                DGamePlayer dPlayer = DGamePlayer.getByPlayer(player);
                if (dPlayer != null) {

                    // Check GameWorld Signs
                    GameWorld gameWorld = GameWorld.getByWorld(player.getWorld());
                    if (gameWorld != null) {

                        // Trigger InteractTrigger
                        InteractTrigger trigger = InteractTrigger.get(clickedBlock, gameWorld);
                        if (trigger != null) {
                            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                                trigger.onTrigger(player);
                            } else {
                                MessageUtil.sendMessage(player, DMessages.ERROR_LEFT_CLICK.getMessage());
                            }
                        }

                        // Class Signs
                        for (Sign classSign : gameWorld.getSignClass()) {
                            if (classSign != null) {
                                if (classSign.getLocation().distance(clickedBlock.getLocation()) < 1) {
                                    if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                                        dPlayer.setDClass(ChatColor.stripColor(classSign.getLine(1)));
                                    } else {
                                        MessageUtil.sendMessage(player, DMessages.ERROR_LEFT_CLICK.getMessage());
                                    }
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        DGlobalPlayer dPlayer = dPlayers.getByPlayer(player);
        if (dPlayer == null) {
            return;
        }

        if (dPlayer instanceof DEditPlayer && !plugin.getMainConfig().getDropItems() && !DPermissions.hasPermission(player, DPermissions.INSECURE)) {
            event.setCancelled(true);
        }

        if (!(dPlayer instanceof DGamePlayer)) {
            return;
        }

        DGamePlayer gamePlayer = (DGamePlayer) dPlayer;

        DGroup dGroup = DGroup.getByPlayer(player);
        if (dGroup == null) {
            return;
        }

        if (!dGroup.isPlaying()) {
            event.setCancelled(true);
            return;
        }

        if (!gamePlayer.isReady()) {
            event.setCancelled(true);
            return;
        }

        Game game = Game.getByWorld(gamePlayer.getWorld());

        for (Material material : game.getRules().getSecureObjects()) {
            if (material == event.getItemDrop().getItemStack().getType()) {
                event.setCancelled(true);
                MessageUtil.sendMessage(player, DMessages.ERROR_DROP.getMessage());
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        plugin.getDPlayers().getByPlayer(player).applyRespawnInventory();

        DGlobalPlayer dPlayer = DGamePlayer.getByPlayer(player);
        if (dPlayer == null) {
            return;
        }

        if (dPlayer instanceof DEditPlayer) {
            EditWorld editWorld = EditWorld.getByWorld(((DEditPlayer) dPlayer).getWorld());
            if (editWorld == null) {
                return;
            }

            if (editWorld.getLobbyLocation() == null) {
                event.setRespawnLocation(editWorld.getWorld().getSpawnLocation());

            } else {
                event.setRespawnLocation(editWorld.getLobbyLocation());
            }

        } else if (dPlayer instanceof DGamePlayer) {
            DGamePlayer gamePlayer = (DGamePlayer) dPlayer;

            GameWorld gameWorld = GameWorld.getByWorld(gamePlayer.getWorld());

            if (gameWorld == null) {
                return;
            }

            DGroup dGroup = DGroup.getByPlayer(dPlayer.getPlayer());

            Location respawn = gamePlayer.getCheckpoint();

            if (respawn == null) {
                respawn = dGroup.getGameWorld().getStartLocation();
            }

            if (respawn == null) {
                respawn = dGroup.getGameWorld().getLobbyLocation();
            }

            if (respawn == null) {
                respawn = dGroup.getGameWorld().getWorld().getSpawnLocation();
            }

            // Because some plugins set another respawn point, DXL teleports a few ticks later.
            new RespawnTask(player, respawn).runTaskLater(plugin, 10);

            // Don't forget Doge!
            if (gamePlayer.getWolf() != null) {
                gamePlayer.getWolf().teleport(respawn);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPortal(PlayerPortalEvent event) {
        if (DPortal.getByLocation(event.getFrom()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        DGamePlayer dPlayer = DGamePlayer.getByPlayer(player);

        if (dPlayer == null) {
            return;
        }

        if (dPlayer.getWorld() == event.getTo().getWorld()) {
            return;
        }

        if (!DPermissions.hasPermission(player, DPermissions.BYPASS)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        DGamePlayer dPlayer = DGamePlayer.getByPlayer(player);
        if (dPlayer == null) {
            return;
        }

        if (dPlayer.isInDungeonChat()) {
            dPlayer.sendMessage(player.getDisplayName() + ": " + event.getMessage());
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (!(dPlayers.getByPlayer(player) instanceof DInstancePlayer)) {
            return;
        }
        DInstancePlayer dPlayer = (DInstancePlayer) dPlayers.getByPlayer(player);

        if (dPlayer == null) {
            dPlayers.removePlayer(dPlayer);
            return;
        }

        DGroup dGroup = DGroup.getByPlayer(player);

        // Check GameWorld
        Game game = Game.getByWorld(player.getWorld());
        if (game != null) {
            int timeUntilKickOfflinePlayer = game.getRules().getTimeUntilKickOfflinePlayer();

            if (timeUntilKickOfflinePlayer == 0) {
                dPlayer.leave();

            } else if (timeUntilKickOfflinePlayer > 0) {
                dGroup.sendMessage(DMessages.PLAYER_OFFLINE.getMessage(dPlayer.getPlayer().getName(), String.valueOf(timeUntilKickOfflinePlayer)), player);
                ((DGamePlayer) dPlayer).setOfflineTime(System.currentTimeMillis() + timeUntilKickOfflinePlayer * 1000);

            } else {
                dGroup.sendMessage(DMessages.PLAYER_OFFLINE_NEVER.getMessage(dPlayer.getPlayer().getName()), player);
            }

        } else if (dPlayer instanceof DEditPlayer) {
            dPlayer.leave();
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        new DGlobalPlayer(player);

        // Check dPlayers
        DGamePlayer dPlayer = DGamePlayer.getByName(player.getName());
        if (dPlayer != null) {
            DGroup dGroup = DGroup.getByPlayer(dPlayer.getPlayer());
            if (dGroup != null) {
                dGroup.getPlayers().remove(dPlayer.getPlayer());
                dGroup.getPlayers().add(player);
            }
            dPlayer.setPlayer(player);

            // Check offlineTime
            dPlayer.setOfflineTime(0);

        } else {
            DSavePlayer dSavePlayer = dPlayers.getDSavePlayerByPlayer(player);

            Location target = Bukkit.getServer().getWorlds().get(0).getSpawnLocation();
            if (dSavePlayer != null) {
                target = dSavePlayer.getOldLocation();
            }

            if (EditWorld.getByWorld(player.getWorld()) != null || GameWorld.getByWorld(player.getWorld()) != null) {
                player.teleport(target);
            }
        }

        // Tutorial Mode
        if (!plugin.getMainConfig().isTutorialActivated()) {
            return;
        }

        if (DGamePlayer.getByPlayer(player) != null) {
            return;
        }

        if (plugin.getPermissionProvider() == null) {
            return;
        }

        if ((plugin.getMainConfig().getTutorialDungeon() == null || plugin.getMainConfig().getTutorialStartGroup() == null || plugin.getMainConfig().getTutorialEndGroup() == null)) {
            return;
        }

        for (String group : plugin.getPermissionProvider().getPlayerGroups(player)) {
            if (!plugin.getMainConfig().getTutorialStartGroup().equalsIgnoreCase(group)) {
                continue;
            }

            DGroup dGroup = new DGroup(player, plugin.getMainConfig().getTutorialDungeon(), false);

            DGroupCreateEvent createEvent = new DGroupCreateEvent(dGroup, player, DGroupCreateEvent.Cause.GROUP_SIGN);
            plugin.getServer().getPluginManager().callEvent(createEvent);

            if (createEvent.isCancelled()) {
                dGroup = null;
            }

            if (dGroup == null) {
                continue;
            }

            if (dGroup.getGameWorld() == null) {
                dGroup.setGameWorld(GameWorld.load(DGroup.getByPlayer(player).getMapName()));
                dGroup.getGameWorld().setTutorial(true);
            }

            if (dGroup.getGameWorld() == null) {
                MessageUtil.sendMessage(player, DMessages.ERROR_TUTORIAL_NOT_EXIST.getMessage());
                continue;
            }

            new DGamePlayer(player, dGroup.getGameWorld());
        }
    }

    // Deny Player Cmds
    @EventHandler(priority = EventPriority.HIGH)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (DPermissions.hasPermission(event.getPlayer(), DPermissions.BYPASS)) {
            return;
        }

        if (!(dPlayers.getByPlayer(event.getPlayer()) instanceof DInstancePlayer)) {
            return;
        }
        DInstancePlayer dPlayer = (DInstancePlayer) dPlayers.getByPlayer(event.getPlayer());

        String command = event.getMessage().toLowerCase();
        ArrayList<String> commandWhitelist = new ArrayList<>();

        Game game = Game.getByWorld(dPlayer.getWorld());

        if (dPlayer instanceof DEditPlayer) {
            if (DPermissions.hasPermission(event.getPlayer(), DPermissions.CMD_EDIT)) {
                return;

            } else {
                commandWhitelist.addAll(plugin.getMainConfig().getEditCommandWhitelist());
            }

        } else if (game != null) {
            if (game.getRules() != null) {
                commandWhitelist.addAll(game.getRules().getGameCommandWhitelist());
            }
        }

        commandWhitelist.add("dungeonsxl");
        commandWhitelist.add("dungeon");
        commandWhitelist.add("dxl");

        event.setCancelled(true);

        for (String whitelistEntry : commandWhitelist) {
            if (command.equals('/' + whitelistEntry.toLowerCase()) || command.startsWith('/' + whitelistEntry.toLowerCase() + ' ')) {
                event.setCancelled(false);
            }
        }

        if (event.isCancelled()) {
            MessageUtil.sendMessage(event.getPlayer(), DMessages.ERROR_CMD.getMessage());
        }
    }

    // Inventory Events
    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        RewardChest.onOpenInventory(event);

        if (!plugin.getMainConfig().getOpenInventories() && !DPermissions.hasPermission(event.getPlayer(), DPermissions.INSECURE)) {
            World world = event.getPlayer().getWorld();
            if (event.getInventory().getType() != InventoryType.CREATIVE && EditWorld.getByWorld(world) != null) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        for (DLootInventory inventory : plugin.getDLootInventories()) {
            if (event.getView() != inventory.getInventoryView()) {
                continue;
            }

            if (System.currentTimeMillis() - inventory.getTime() <= 500) {
                continue;
            }

            for (ItemStack istack : inventory.getInventory().getContents()) {
                if (istack != null) {
                    player.getWorld().dropItem(player.getLocation(), istack);
                }
            }

            plugin.getDLootInventories().remove(inventory);
        }
    }

    // Player move
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        DLootInventory inventory = DLootInventory.getByPlayer(player);

        DPortal dPortal = DPortal.getByLocation(player.getEyeLocation());
        //TODO: Fix chat spam
        if (dPortal != null) {
            dPortal.teleport(player);
            return;
        }

        if (inventory == null) {
            return;
        }

        if (player.getLocation().getBlock().getRelative(0, 1, 0).getType() != Material.PORTAL && player.getLocation().getBlock().getRelative(0, -1, 0).getType() != Material.PORTAL
                && player.getLocation().getBlock().getRelative(1, 0, 0).getType() != Material.PORTAL && player.getLocation().getBlock().getRelative(-1, 0, 0).getType() != Material.PORTAL
                && player.getLocation().getBlock().getRelative(0, 0, 1).getType() != Material.PORTAL && player.getLocation().getBlock().getRelative(0, 0, -1).getType() != Material.PORTAL) {
            inventory.setInventoryView(player.openInventory(inventory.getInventory()));
            inventory.setTime(System.currentTimeMillis());
        }
    }

}
