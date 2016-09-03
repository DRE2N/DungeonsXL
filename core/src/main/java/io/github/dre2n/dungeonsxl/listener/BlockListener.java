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
import io.github.dre2n.dungeonsxl.world.DEditWorld;
import io.github.dre2n.dungeonsxl.world.DGameWorld;
import io.github.dre2n.dungeonsxl.world.DInstanceWorld;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.SignChangeEvent;

/**
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel, Wooyoung Son
 */
public class BlockListener implements Listener {

    DungeonsXL plugin = DungeonsXL.getInstance();
    DPlayers dPlayers = plugin.getDPlayers();

    @EventHandler(priority = EventPriority.HIGH)
    public void onPhysics(BlockPhysicsEvent event) {
        if (DPortal.getByBlock(event.getBlock()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBreakWithSignOnIt(BlockBreakEvent event){
        Block block = event.getBlock();
        Player player = event.getPlayer();
        
        Block blockAbove = block.getRelative(BlockFace.UP);
        //get the above block and return if there is nothing
        if(blockAbove == null)
        	return;
        
        //return if above block is not a sign
        if(blockAbove.getType() != Material.SIGN_POST && blockAbove.getType() != Material.WALL_SIGN)
        	return;
        
        //let onBreak() method to handle the sign
        BlockBreakEvent bbe = new BlockBreakEvent(blockAbove, player);
        onBreak(bbe);
        
        //follow the onBreak()
        event.setCancelled(bbe.isCancelled());
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        DGlobalPlayer dGlobalPlayer = dPlayers.getByPlayer(player);

        GlobalProtection protection = plugin.getGlobalProtections().getByBlock(event.getBlock());
        if (protection != null) {
            if (protection.onBreak(dGlobalPlayer)) {
                event.setCancelled(true);
            }
            return;
        }

        // DEditWorld Signs
        DEditWorld editWorld = DEditWorld.getByWorld(block.getWorld());
        if (editWorld != null) {
            editWorld.getSigns().remove(event.getBlock());
            return;
        }

        // Deny DGameWorld block breaking
        DGameWorld gameWorld = DGameWorld.getByWorld(block.getWorld());
        if (gameWorld != null) {
            if (gameWorld.onBreak(event)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();

        // Deny DGameWorld Blocks
        DGameWorld gameWorld = DGameWorld.getByWorld(block.getWorld());
        if (gameWorld == null) {
            return;
        }

        if (gameWorld.onPlace(event.getPlayer(), block, event.getBlockAgainst(), event.getItemInHand())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        String[] lines = event.getLines();
        DEditWorld editWorld = DEditWorld.getByWorld(player.getWorld());

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
                    editWorld.registerSign(block);
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
        Block block = event.getSource();

        DInstanceWorld instance = plugin.getDWorlds().getInstanceByName(block.getWorld().getName());
        if (instance != null && block.getType() == Material.VINE) {
            event.setCancelled(true);

        } else if (DPortal.getByBlock(block) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onRedstoneEvent(BlockRedstoneEvent event) {
        new RedstoneEventTask(event.getBlock()).runTaskLater(plugin, 1);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onIgnite(BlockIgniteEvent event) {
        if (plugin.getDWorlds().getInstanceByName(event.getBlock().getWorld().getName()) == null) {
            return;
        }

        if (event.getCause() != IgniteCause.FLINT_AND_STEEL) {
            event.setCancelled(true);
        }
    }

}
