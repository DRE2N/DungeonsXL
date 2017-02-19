/*
 * Copyright (C) 2012-2017 Frank Baumann
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
package io.github.dre2n.dungeonsxl.reward;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.player.DPermissions;
import io.github.dre2n.dungeonsxl.world.DEditWorld;
import io.github.dre2n.dungeonsxl.world.DGameWorld;
import io.github.dre2n.dungeonsxl.world.block.RewardChest;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class RewardListener implements Listener {

    DungeonsXL plugin = DungeonsXL.getInstance();

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
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

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        InventoryView inventory = event.getView();

        DGameWorld gameWorld = DGameWorld.getByWorld(event.getPlayer().getWorld());

        if (gameWorld == null) {
            return;
        }

        if (!(inventory.getTopInventory().getHolder() instanceof Chest)) {
            return;
        }

        Chest chest = (Chest) inventory.getTopInventory().getHolder();

        for (RewardChest rewardChest : gameWorld.getRewardChests()) {
            if (!rewardChest.getChest().equals(chest)) {
                continue;
            }

            rewardChest.onOpen((Player) event.getPlayer());
            event.setCancelled(true);
        }

        if (!plugin.getMainConfig().getOpenInventories() && !DPermissions.hasPermission(event.getPlayer(), DPermissions.INSECURE)) {
            World world = event.getPlayer().getWorld();
            if (event.getInventory().getType() != InventoryType.CREATIVE && DEditWorld.getByWorld(world) != null) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        DLootInventory inventory = DLootInventory.getByPlayer(player);
        if (inventory != null && player.getLocation().getBlock().getRelative(0, 1, 0).getType() != Material.PORTAL && player.getLocation().getBlock().getRelative(0, -1, 0).getType() != Material.PORTAL
                && player.getLocation().getBlock().getRelative(1, 0, 0).getType() != Material.PORTAL && player.getLocation().getBlock().getRelative(-1, 0, 0).getType() != Material.PORTAL
                && player.getLocation().getBlock().getRelative(0, 0, 1).getType() != Material.PORTAL && player.getLocation().getBlock().getRelative(0, 0, -1).getType() != Material.PORTAL) {
            inventory.setInventoryView(player.openInventory(inventory.getInventory()));
            inventory.setTime(System.currentTimeMillis());
        }
    }

}
