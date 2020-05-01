/*
 * Copyright (C) 2012-2020 Frank Baumann
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
package de.erethon.dungeonsxl.global;

import de.erethon.caliburn.category.Category;
import de.erethon.caliburn.item.VanillaItem;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.player.DGlobalPlayer;
import de.erethon.dungeonsxl.player.DPermission;
import de.erethon.dungeonsxl.player.DPlayerListener;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author Daniel Saukel, Wooyoung Son, Frank Baumann, Milan Albrecht
 */
public class GlobalProtectionListener implements Listener {

    private DungeonsXL plugin;

    public GlobalProtectionListener(DungeonsXL plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockBreakWithSignOnIt(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        Block blockAbove = block.getRelative(BlockFace.UP);
        //get the above block and return if there is nothing
        if (blockAbove == null) {
            return;
        }

        //return if above block is not a sign
        if (!Category.SIGNS.containsBlock(blockAbove)) {
            return;
        }

        //let onBreak() method to handle the sign
        BlockBreakEvent bbe = new BlockBreakEvent(blockAbove, player);
        onBlockBreak(bbe);

        //follow the onBreak()
        event.setCancelled(bbe.isCancelled());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        DGlobalPlayer dGlobalPlayer = (DGlobalPlayer) plugin.getPlayerCache().get(player);

        GlobalProtection protection = plugin.getGlobalProtectionCache().getByBlock(block);
        if (protection != null) {
            if (protection.onBreak(dGlobalPlayer)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (DPortal.getByBlock(plugin, event.getBlock()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        Player player = event.getPlayer();
        if (DPlayerListener.isCitizensNPC(player)) {
            return;
        }
        Block block = event.getBlockClicked();
        if (DPortal.getByBlock(plugin, block) != null) {
            event.setCancelled(true);
            // Workaround for a bug of Bukkit
            event.getPlayer().sendBlockChange(block.getLocation(), block.getType(), (byte) 0);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockSpread(BlockSpreadEvent event) {
        if (DPortal.getByBlock(plugin, event.getBlock()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        if (DPortal.getByBlock(plugin, event.getBlock()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityExplode(EntityExplodeEvent event) {
        List<Block> blocklist = event.blockList();
        for (Block block : blocklist) {
            if (plugin.getGlobalProtectionCache().isProtectedBlock(block)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (DPlayerListener.isCitizensNPC(player)) {
            return;
        }
        DPortal dPortal = DPortal.getByLocation(plugin, player.getEyeLocation());
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
        Block block2 = block1.getRelative(BlockFace.UP);
        Block block3 = block2.getRelative(BlockFace.UP);
        Block block4 = block1.getRelative(BlockFace.DOWN);
        if (isPortalInNearBy(block1) || isPortalInNearBy(block2) || isPortalInNearBy(block3) || isPortalInNearBy(block4)) {
            event.setCancelled(true);
        }
    }

    private boolean isPortalInNearBy(Block block1) {
        Block block2 = block1.getRelative(BlockFace.WEST);
        Block block3 = block1.getRelative(BlockFace.NORTH);
        Block block4 = block1.getRelative(BlockFace.EAST);
        Block block5 = block1.getRelative(BlockFace.SOUTH);
        Block block6 = block2.getRelative(BlockFace.NORTH);
        Block block7 = block2.getRelative(BlockFace.SOUTH);
        Block block8 = block4.getRelative(BlockFace.NORTH);
        Block block9 = block4.getRelative(BlockFace.SOUTH);
        return (DPortal.getByBlock(plugin, block1) != null || DPortal.getByBlock(plugin, block2) != null || DPortal.getByBlock(plugin, block3) != null
                || DPortal.getByBlock(plugin, block4) != null || DPortal.getByBlock(plugin, block5) != null || DPortal.getByBlock(plugin, block6) != null
                || DPortal.getByBlock(plugin, block7) != null || DPortal.getByBlock(plugin, block8) != null || DPortal.getByBlock(plugin, block9) != null);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPortalCreation(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (DPlayerListener.isCitizensNPC(player)) {
            return;
        }
        DGlobalPlayer dPlayer = (DGlobalPlayer) plugin.getPlayerCache().get(player);
        if (!dPlayer.isCreatingPortal()) {
            return;
        }
        ItemStack item = event.getItem();
        Block block = event.getClickedBlock();
        if (item == null || !VanillaItem.WOODEN_SWORD.is(item) || block == null) {
            return;
        }

        for (GlobalProtection protection : plugin.getGlobalProtectionCache().getProtections(DPortal.class)) {
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
            }
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (DPlayerListener.isCitizensNPC(player) || plugin.getPlayerCache().get(player).isInBreakMode()) {
            return;
        }
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }

        if (Category.SIGNS.containsBlock(clickedBlock)) {
            GroupSign groupSign = GroupSign.getByBlock(plugin, clickedBlock);
            if (groupSign != null) {
                groupSign.onPlayerInteract(clickedBlock, player);
                event.setCancelled(true);
            }

            GameSign gameSign = GameSign.getByBlock(plugin, clickedBlock);
            if (gameSign != null) {
                gameSign.onPlayerInteract(clickedBlock, player);
                event.setCancelled(true);
            }

            LeaveSign leaveSign = LeaveSign.getByBlock(plugin, clickedBlock);
            if (leaveSign != null) {
                leaveSign.onPlayerInteract(player);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        BlockState state = block.getState();
        if (!(state instanceof Sign)) {
            return;
        }

        String[] lines = event.getLines();

        // Group Signs
        if (plugin.getEditWorld(player.getWorld()) == null) {
            if (!DPermission.hasPermission(player, DPermission.SIGN)) {
                return;
            }

            if (!lines[0].equalsIgnoreCase(GlobalProtection.SIGN_TAG)) {
                return;
            }

            if (lines[1].equalsIgnoreCase(GroupSign.GROUP_SIGN_TAG)) {
                if (GroupSign.tryToCreate(plugin, event) != null) {
                    event.setCancelled(true);
                }

            } else if (lines[1].equalsIgnoreCase(GameSign.GAME_SIGN_TAG)) {
                if (GameSign.tryToCreate(plugin, event) != null) {
                    event.setCancelled(true);
                }

            } else if (lines[1].equalsIgnoreCase(LeaveSign.LEAVE_SIGN_TAG)) {
                Sign sign = (Sign) state;
                new LeaveSign(plugin, plugin.getGlobalProtectionCache().generateId(LeaveSign.class, sign.getWorld()), sign);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        World world = event.getWorld();
        for (Entry<UnloadedProtection, String> entry : new HashSet<>(plugin.getGlobalProtectionCache().getUnloadedProtections().entrySet())) {
            if (world.getName().equals(entry.getValue())) {
                entry.getKey().load(world);
            }
        }
    }

}
