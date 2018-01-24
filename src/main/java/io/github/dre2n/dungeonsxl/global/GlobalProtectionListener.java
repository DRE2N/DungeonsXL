/*
 * Copyright (C) 2012-2018 Frank Baumann
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
package io.github.dre2n.dungeonsxl.global;

import io.github.dre2n.commons.misc.NumberUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.DMessage;
import io.github.dre2n.dungeonsxl.player.DGlobalPlayer;
import io.github.dre2n.dungeonsxl.player.DPermission;
import io.github.dre2n.dungeonsxl.util.LegacyUtil;
import io.github.dre2n.dungeonsxl.world.DEditWorld;
import java.util.List;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author Daniel Saukel, Wooyoung Son, Frank Baumann, Milan Albrecht
 */
public class GlobalProtectionListener implements Listener {

    DungeonsXL plugin = DungeonsXL.getInstance();

    @EventHandler
    public void onBlockBreakWithSignOnIt(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        Block blockAbove = block.getRelative(BlockFace.UP);
        //get the above block and return if there is nothing
        if (blockAbove == null) {
            return;
        }

        //return if above block is not a sign
        if (!LegacyUtil.isSign(blockAbove)) {
            return;
        }

        //let onBreak() method to handle the sign
        BlockBreakEvent bbe = new BlockBreakEvent(blockAbove, player);
        onBlockBreak(bbe);

        //follow the onBreak()
        event.setCancelled(bbe.isCancelled());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        DGlobalPlayer dGlobalPlayer = plugin.getDPlayers().getByPlayer(player);

        GlobalProtection protection = plugin.getGlobalProtections().getByBlock(block);
        if (protection != null) {
            if (protection.onBreak(dGlobalPlayer)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
        if (DPortal.getByBlock(event.getBlock()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
        if (DPortal.getByBlock(event.getBlock()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        List<Block> blocklist = event.blockList();
        for (Block block : blocklist) {
            if (plugin.getGlobalProtections().isProtectedBlock(block)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        DPortal dPortal = DPortal.getByLocation(player.getEyeLocation());
        if (dPortal == null) {
            return;
        }

        Block blockFrom = event.getFrom().getBlock();
        Block blockTo = event.getTo().getBlock();
        if (blockFrom.equals(blockTo)) {
            return;
        }

        dPortal.teleport(player);
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        Block block1 = event.getFrom().getBlock();
        Block block2 = block1.getRelative(BlockFace.WEST);
        Block block3 = block1.getRelative(BlockFace.NORTH);
        Block block4 = block1.getRelative(BlockFace.EAST);
        Block block5 = block1.getRelative(BlockFace.SOUTH);
        if (DPortal.getByBlock(block1) != null || DPortal.getByBlock(block2) != null || DPortal.getByBlock(block3) != null || DPortal.getByBlock(block4) != null || DPortal.getByBlock(block5) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPortalCreation(PlayerInteractEvent event) {
        DGlobalPlayer dPlayer = plugin.getDPlayers().getByPlayer(event.getPlayer());
        if (!dPlayer.isCreatingPortal()) {
            return;
        }
        ItemStack item = event.getItem();
        Block block = event.getClickedBlock();
        if (item.getType() != LegacyUtil.WOODEN_SWORD || block == null) {
            return;
        }

        for (GlobalProtection protection : plugin.getGlobalProtections().getProtections(DPortal.class)) {
            DPortal dPortal = (DPortal) protection;
            if (dPortal.isActive() || dPortal != dPlayer.getPortal()) {
                continue;
            }

            if (dPortal.getBlock1() == null) {
                dPortal.setBlock1(event.getClickedBlock());
                dPlayer.sendMessage(DMessage.PLAYER_PORTAL_PROGRESS.getMessage());

            } else if (dPortal.getBlock2() == null) {
                dPortal.setBlock2(event.getClickedBlock());
                dPortal.setActive(true);
                dPortal.create(dPlayer);
                dPlayer.sendMessage(DMessage.PLAYER_PORTAL_CREATED.getMessage());
                dPlayer.getPlayer().getInventory().setItemInHand(dPlayer.getCachedItem());
                dPlayer.setCachedItem(null);
            }
            event.setCancelled(true);
        }
    }

    /* SUBJECT TO CHANGE */
    @Deprecated
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (plugin.getDPlayers().getByPlayer(player).isInBreakMode()) {
            return;
        }
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }

        if (LegacyUtil.isSign(clickedBlock)) {
            // Check Group Signs
            if (GroupSign.playerInteract(clickedBlock, player)) {
                event.setCancelled(true);
            }

            // Check Game Signs
            if (GameSign.playerInteract(clickedBlock, player)) {
                event.setCancelled(true);
            }

            LeaveSign leaveSign = LeaveSign.getByBlock(clickedBlock);
            if (leaveSign != null) {
                leaveSign.onPlayerInteract(player);
                event.setCancelled(true);
            }
        }
    }

    @Deprecated
    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        String[] lines = event.getLines();

        // Group Signs
        if (DEditWorld.getByWorld(player.getWorld()) == null) {
            if (!DPermission.hasPermission(player, DPermission.SIGN)) {
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

                    if (maxObjects > 0 && maxMembersPerObject > 0) {
                        if (lines[1].equalsIgnoreCase("Game")) {
                            if (GameSign.tryToCreate(event.getBlock(), dungeonName, maxObjects, maxMembersPerObject) != null) {
                                event.setCancelled(true);
                            }

                        } else if (lines[1].equalsIgnoreCase("Group")) {
                            if (GroupSign.tryToCreate(event.getBlock(), dungeonName, maxObjects, maxMembersPerObject) != null) {
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
        }
    }

}
