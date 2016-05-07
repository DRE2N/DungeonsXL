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

import io.github.dre2n.commons.util.NumberUtil;
import io.github.dre2n.commons.util.messageutil.MessageUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.DMessages;
import io.github.dre2n.dungeonsxl.game.Game;
import io.github.dre2n.dungeonsxl.game.GamePlaceableBlock;
import io.github.dre2n.dungeonsxl.game.GameType;
import io.github.dre2n.dungeonsxl.game.GameTypeDefault;
import io.github.dre2n.dungeonsxl.global.DPortal;
import io.github.dre2n.dungeonsxl.global.GameSign;
import io.github.dre2n.dungeonsxl.global.GlobalProtection;
import io.github.dre2n.dungeonsxl.global.GroupSign;
import io.github.dre2n.dungeonsxl.global.LeaveSign;
import io.github.dre2n.dungeonsxl.player.DGlobalPlayer;
import io.github.dre2n.dungeonsxl.player.DPermissions;
import io.github.dre2n.dungeonsxl.player.DPlayers;
import io.github.dre2n.dungeonsxl.sign.DSign;
import io.github.dre2n.dungeonsxl.task.RedstoneEventTask;
import io.github.dre2n.dungeonsxl.world.EditWorld;
import io.github.dre2n.dungeonsxl.world.GameWorld;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.SignChangeEvent;

/**
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class BlockListener implements Listener {

    DungeonsXL plugin = DungeonsXL.getInstance();
    DPlayers dPlayers = plugin.getDPlayers();

    @EventHandler(priority = EventPriority.HIGH)
    public void onPhysics(BlockPhysicsEvent event) {
        if (event.getBlock().getType() != Material.PORTAL) {
            return;
        }

        if (DPortal.getByBlock(event.getBlock()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        DGlobalPlayer dGlobalPlayer = dPlayers.getByPlayer(player);

        GlobalProtection protection = plugin.getGlobalProtections().getByBlock(event.getBlock());
        if (protection != null) {
            if (dGlobalPlayer.isInBreakMode()) {
                protection.delete();
                MessageUtil.sendMessage(player, plugin.getMessageConfig().getMessage(DMessages.PLAYER_PROTECTED_BLOCK_DELETED));
                MessageUtil.sendMessage(player, plugin.getMessageConfig().getMessage(DMessages.CMD_BREAK_PROTECTED_MODE));
                dGlobalPlayer.setInBreakMode(false);

            } else {
                event.setCancelled(true);
            }

            return;
        }

        // EditWorld Signs
        EditWorld editWorld = EditWorld.getByWorld(block.getWorld());
        if (editWorld != null) {
            editWorld.getSigns().remove(event.getBlock());
            return;
        }

        // Deny GameWorld block breaking
        GameWorld gameWorld = GameWorld.getByWorld(block.getWorld());
        if (gameWorld != null) {
            for (DSign dSign : gameWorld.getDSigns()) {
                if (dSign.getSign().equals(block)) {
                    event.setCancelled(true);
                    return;
                }
            }

            Game game = gameWorld.getGame();
            if (game != null) {
                GameType gameType = game.getType();
                if (gameType == GameTypeDefault.DEFAULT) {
                    event.setCancelled(!game.getRules().canBuild());

                } else if (!gameType.canBuild()) {
                    event.setCancelled(true);
                }

            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();

        // Deny GameWorld Blocks
        GameWorld gameWorld = GameWorld.getByWorld(block.getWorld());
        if (gameWorld == null) {
            return;
        }

        Game game = gameWorld.getGame();
        if (game != null) {
            if (game.getRules().canBuild() || GamePlaceableBlock.canBuildHere(block, block.getFace(event.getBlockAgainst()), event.getItemInHand().getType(), gameWorld)) {
                return;
            }
        }

        // Workaround for a bug that would allow 3-Block-high jumping
        Location loc = event.getPlayer().getLocation();
        if (loc.getY() > block.getY() + 1.0 && loc.getY() <= block.getY() + 1.5) {
            if (loc.getX() >= block.getX() - 0.3 && loc.getX() <= block.getX() + 1.3) {
                if (loc.getZ() >= block.getZ() - 0.3 && loc.getZ() <= block.getZ() + 1.3) {
                    loc.setX(block.getX() + 0.5);
                    loc.setY(block.getY());
                    loc.setZ(block.getZ() + 0.5);
                    event.getPlayer().teleport(loc);
                }
            }
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        String[] lines = event.getLines();
        EditWorld editWorld = EditWorld.getByWorld(player.getWorld());

        // Group Signs
        if (editWorld == null) {
            if (!DPermissions.hasPermission(player, DPermissions.SIGN)) {
                return;
            }

            if (!lines[0].equalsIgnoreCase("[DXL]")) {
                return;
            }

            if (lines[1].equalsIgnoreCase("Game") || lines[1].equalsIgnoreCase("Group")) {
                String dungeonName = lines[2];

                String[] data = lines[3].split("\\,");
                if (data.length >= 2 && data.length <= 3) {
                    int maxObjects = NumberUtil.parseInt(data[0]);
                    int maxMembersPerObject = NumberUtil.parseInt(data[1]);
                    boolean multiFloor = false;
                    if (data.length == 3) {
                        if (data[2].equals("+")) {
                            multiFloor = true;
                        }
                    }

                    if (maxObjects > 0 && maxMembersPerObject > 0) {
                        if (lines[1].equalsIgnoreCase("Game")) {
                            if (GameSign.tryToCreate(event.getBlock(), dungeonName, maxObjects, maxMembersPerObject, multiFloor) != null) {
                                event.setCancelled(true);
                            }

                        } else if (lines[1].equalsIgnoreCase("Group")) {
                            if (GroupSign.tryToCreate(event.getBlock(), dungeonName, maxObjects, maxMembersPerObject, multiFloor) != null) {
                                event.setCancelled(true);
                            }
                        }
                    }
                }

            } else if (lines[1].equalsIgnoreCase("Leave")) {
                if (block.getState() instanceof Sign) {
                    Sign sign = (Sign) block.getState();
                    new LeaveSign(plugin.getGlobalProtections().generateId(LeaveSign.class, sign.getWorld()), sign);
                }

                event.setCancelled(true);
            }

        } else { // Editworld Signs
            Sign sign = (Sign) block.getState();
            if (sign != null) {
                sign.setLine(0, lines[0]);
                sign.setLine(1, lines[1]);
                sign.setLine(2, lines[2]);
                sign.setLine(3, lines[3]);

                DSign dsign = DSign.create(sign, null);

                if (dsign == null) {
                    return;
                }

                if (!DPermissions.hasPermission(player, dsign.getType().getBuildPermission())) {
                    MessageUtil.sendMessage(player, plugin.getMessageConfig().getMessage(DMessages.ERROR_NO_PERMISSIONS));
                    return;
                }

                if (dsign.check()) {
                    editWorld.checkSign(block);
                    editWorld.getSigns().add(block);
                    MessageUtil.sendMessage(player, plugin.getMessageConfig().getMessage(DMessages.PLAYER_SIGN_CREATED));

                } else {
                    MessageUtil.sendMessage(player, plugin.getMessageConfig().getMessage(DMessages.ERROR_SIGN_WRONG_FORMAT));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onSpread(BlockSpreadEvent event) {
        Block block = event.getBlock();
        // Block the Spread off Vines
        if (block.getType() != Material.VINE) {
            return;
        }

        // Check GameWorlds
        GameWorld gameWorld = GameWorld.getByWorld(event.getBlock().getWorld());
        if (gameWorld != null) {
            event.setCancelled(true);
        }

        // Check EditWorlds
        EditWorld editWorld = EditWorld.getByWorld(event.getBlock().getWorld());
        if (editWorld != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onRedstoneEvent(BlockRedstoneEvent event) {
        new RedstoneEventTask(event.getBlock()).runTaskLater(plugin, 1);
    }

}
