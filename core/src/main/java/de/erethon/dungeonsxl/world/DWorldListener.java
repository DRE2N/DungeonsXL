/*
 * Copyright (C) 2012-2013 Frank Baumann; 2015-2026 Daniel Saukel
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
package de.erethon.dungeonsxl.world;

import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.api.dungeon.BuildMode;
import de.erethon.dungeonsxl.api.dungeon.Game;
import de.erethon.dungeonsxl.api.dungeon.GameRule;
import de.erethon.dungeonsxl.api.dungeon.GameRuleContainer;
import de.erethon.dungeonsxl.api.sign.DungeonSign;
import de.erethon.dungeonsxl.api.world.EditWorld;
import de.erethon.dungeonsxl.api.world.GameWorld;
import de.erethon.dungeonsxl.api.world.InstanceWorld;
import de.erethon.dungeonsxl.player.DPlayerListener;
import de.erethon.dungeonsxl.util.BlockUtilCompat;
import de.erethon.dungeonsxl.util.ContainerAdapter;
import de.erethon.dungeonsxl.world.block.GameBlock;
import de.erethon.dungeonsxl.world.block.MultiBlock;
import de.erethon.dungeonsxl.world.block.PlaceableBlock;
import de.erethon.xlib.XLib;
import de.erethon.xlib.category.Category;
import de.erethon.xlib.compatibility.Version;
import de.erethon.xlib.item.ExItem;
import de.erethon.xlib.item.VanillaItem;
import de.erethon.xlib.mob.ExMob;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * @author Daniel Saukel, Frank Baumann, Milan Albrecht
 */
public class DWorldListener implements Listener {

    private DungeonsXL plugin;
    private XLib xlib;

    public DWorldListener(DungeonsXL plugin) {
        this.plugin = plugin;
        xlib = plugin.getXLib();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInit(WorldInitEvent event) {
        World world = event.getWorld();
        if (plugin.isInstance(world)) {
            world.setKeepSpawnInMemory(false);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        // EditWorld Signs
        EditWorld editWorld = plugin.getEditWorld(block.getWorld());
        if (editWorld != null) {
            editWorld.removeDungeonSign(event.getBlock());
            return;
        }

        // Deny GameWorld block breaking
        DGameWorld gameWorld = (DGameWorld) plugin.getGameWorld(block.getWorld());
        if (gameWorld == null) {
            return;
        }

        Player player = event.getPlayer();
        for (DungeonSign sign : gameWorld.getDungeonSigns()) {
            if (sign == null) {
                continue;
            }
            if ((block.equals(sign.getSign().getBlock()) || block.equals(BlockUtilCompat.getAttachedBlock(sign.getSign().getBlock()))) && sign.isProtected()) {
                event.setCancelled(true);
                return;
            }
        }

        for (GameBlock gameBlock : gameWorld.getGameBlocks()) {
            if (block.equals(gameBlock.getBlock())) {
                if (gameBlock.onBreak(event)) {
                    event.setCancelled(true);
                    return;
                }

            } else if (gameBlock instanceof MultiBlock) {
                if (block.equals(((MultiBlock) gameBlock).getAttachedBlock())) {
                    if (gameBlock.onBreak(event)) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }

        Game game = gameWorld.getGame();
        if (game == null) {
            event.setCancelled(true);
            return;
        }

        GameRuleContainer rules = gameWorld.getDungeon().getRules();
        BuildMode mode = rules.getState(GameRule.BREAK_BLOCKS);

        if (mode == BuildMode.FALSE) {
            event.setCancelled(true);
            return;
        }

        // Cancel if a protected entity is attached
        for (Entity entity : gameWorld.getWorld().getNearbyEntities(block.getLocation(), 2, 2, 2)) {
            if (!(entity instanceof Hanging)) {
                continue;
            }
            if (entity.getLocation().getBlock().getRelative(((Hanging) entity).getAttachedFace()).equals(block)) {
                Hanging hanging = (Hanging) entity;
                if (rules.getState(GameRule.DAMAGE_PROTECTED_ENTITIES).contains(xlib.getExMob(hanging))) {
                    event.setCancelled(true);
                    break;
                }
            }
        }

        boolean breakBlock = !mode.check(player, gameWorld, block);
        if (breakBlock) {
            gameWorld.getPlacedBlocks().remove(block);
        }
        event.setCancelled(breakBlock);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (DPlayerListener.isCitizensNPC(player)) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        GameWorld gameWorld = plugin.getGameWorld(block.getWorld());
        if (gameWorld == null) {
            return;
        }

        if (!gameWorld.isPlaying()) {
            if (!plugin.getMainConfig().areLobbyContainersEnabled() || !ContainerAdapter.isValidContainer(block)) {
                event.setCancelled(true);
            }
            return;
        }

        Map<ExItem, HashSet<ExItem>> blacklist = gameWorld.getDungeon().getRules().getState(GameRule.INTERACTION_BLACKLIST);
        if (blacklist.isEmpty()) {
            return;
        }

        ExItem material = VanillaItem.get(block.getType());
        ExItem tool = xlib.getExItem(getItemInHand(event));
        if (blacklist.containsKey(material)
                && (blacklist.get(material) == null
                || blacklist.get(material).isEmpty()
                || blacklist.get(material).contains(tool))) {
            event.setCancelled(true);
        }
    }

    private ItemStack getItemInHand(PlayerInteractEvent event) {
        PlayerInventory inventory = event.getPlayer().getInventory();
        if (Version.isAtLeast(Version.MC1_9)) {
            return event.getHand() == EquipmentSlot.HAND ? inventory.getItemInMainHand() : inventory.getItemInOffHand();
        } else {
            return inventory.getItemInHand();
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();

        DGameWorld gameWorld = (DGameWorld) plugin.getGameWorld(block.getWorld());
        if (gameWorld == null) {
            return;
        }

        Game game = gameWorld.getGame();
        if (game == null) {
            event.setCancelled(true);
            return;
        }

        GameRuleContainer rules = game.getRules();
        Player player = event.getPlayer();
        if (rules.getState(GameRule.PLACE_BLOCKS).check(player, gameWorld, block)) {
            gameWorld.getPlacedBlocks().add(block);
            event.setCancelled(false);
            return;
        }

        PlaceableBlock placeableBlock = null;
        for (PlaceableBlock gamePlaceableBlock : gameWorld.getPlaceableBlocks()) {
            if (gamePlaceableBlock.canPlace(block, xlib.getExItem(event.getItemInHand()))) {
                placeableBlock = gamePlaceableBlock;
                break;
            }
        }
        if (placeableBlock == null) {
            // Workaround for a bug that would allow 3-Block-high jumping
            Location loc = player.getLocation();
            if (loc.getY() > block.getY() + 1.0 && loc.getY() <= block.getY() + 1.5) {
                if (loc.getX() >= block.getX() - 0.3 && loc.getX() <= block.getX() + 1.3) {
                    if (loc.getZ() >= block.getZ() - 0.3 && loc.getZ() <= block.getZ() + 1.3) {
                        loc.setX(block.getX() + 0.5);
                        loc.setY(block.getY());
                        loc.setZ(block.getZ() + 0.5);
                        player.teleport(loc);
                    }
                }
            }
            event.setCancelled(true);
            return;
        }

        placeableBlock.onPlace(player);
        event.setCancelled(false);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (!plugin.isInstance(event.getBlock().getWorld())) {
            return;
        }

        if (event.getCause() != BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockSpread(BlockSpreadEvent event) {
        Block block = event.getSource();

        if (plugin.isInstance(block.getWorld()) && VanillaItem.VINE.is(block)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityExplode(EntityExplodeEvent event) {
        if (plugin.getGameWorld(event.getEntity().getWorld()) == null) {
            return;
        }
        if (event.getEntity() instanceof LivingEntity) {
            // Disable Creeper explosions in gameWorlds
            event.setCancelled(true);
        } else {// TODO respect block breaking game rules
            // Disable drops from TNT
            event.setYield(0);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {
        onTouch(event, event.getEntity(), false);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onHangingBreak(HangingBreakEvent event) {
        onTouch(event, event.getEntity(), false);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        onTouch(event, event.getRightClicked(), true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        onTouch(event, event.getRightClicked(), true);
    }

    /**
     * @param event    the event
     * @param entity   the entity
     * @param interact true = interact; false = break
     */
    public void onTouch(Cancellable event, Entity entity, boolean interact) {
        GameWorld gameWorld = plugin.getGameWorld(entity.getWorld());
        if (gameWorld == null) {
            return;
        }
        GameRuleContainer rules = gameWorld.getDungeon().getRules();
        Set<ExMob> prot = interact ? rules.getState(GameRule.INTERACTION_PROTECTED_ENTITIES) : rules.getState(GameRule.DAMAGE_PROTECTED_ENTITIES);
        if (prot.contains(xlib.getExMob(entity))) {
            event.setCancelled(true);
        }
    }

    // TODO: Is this necessary?
    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        if (plugin.getGameWorld(event.getLocation().getWorld()) != null) {
            if (Category.SIGNS.containsItem(event.getEntity().getItemStack())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        GameWorld gameWorld = plugin.getGameWorld(event.getBlock().getWorld());
        if (gameWorld == null) {
            return;
        }

        if (!gameWorld.isPlaying()) {
            event.setCancelled(true);
            return;
        }

        Set<ExItem> blockFadeDisabled = gameWorld.getGame().getRules().getState(GameRule.BLOCK_FADE_DISABLED);
        if (blockFadeDisabled.isEmpty()) {
            return;
        }
        if (gameWorld.getGame() != null && blockFadeDisabled.contains(VanillaItem.get(event.getBlock().getType()))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        InstanceWorld instance = plugin.getInstanceWorld(event.getWorld());
        if (instance instanceof EditWorld && event.toWeatherState()) {
            event.setCancelled(true);
        } else if (instance instanceof GameWorld) {
            Boolean raining = ((GameWorld) instance).getDungeon().getRules().getState(GameRule.RAIN);
            if (raining == null) {
                return;
            }
            if ((raining && !event.toWeatherState()) || (!raining && event.toWeatherState())) {
                event.setCancelled(true);
            }
        }
    }

}
