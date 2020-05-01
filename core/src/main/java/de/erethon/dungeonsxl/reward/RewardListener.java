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
package de.erethon.dungeonsxl.reward;

import de.erethon.caliburn.item.VanillaItem;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.api.player.GlobalPlayer;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.player.DPermission;
import de.erethon.dungeonsxl.player.DPlayerListener;
import de.erethon.dungeonsxl.util.ContainerAdapter;
import de.erethon.dungeonsxl.world.DGameWorld;
import de.erethon.dungeonsxl.world.block.RewardChest;
import de.erethon.vignette.api.PaginatedInventoryGUI;
import de.erethon.vignette.api.component.InventoryButton;
import de.erethon.vignette.api.layout.PaginatedFlowInventoryLayout;
import de.erethon.vignette.api.layout.PaginatedInventoryLayout.PaginationButtonPosition;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class RewardListener implements Listener {

    private DungeonsXL plugin;

    public RewardListener(DungeonsXL plugin) {
        this.plugin = plugin;
    }

    /*@EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getPlayer();

        for (DLootInventory inventory : plugin.getDLootInventories()) {
            if (PageGUI.getByInventory() != inventory.getInventory()) {
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
    }*/
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        InventoryView inventory = event.getView();

        DGameWorld gameWorld = (DGameWorld) plugin.getGameWorld(event.getPlayer().getWorld());

        if (gameWorld == null) {
            return;
        }

        if (!(ContainerAdapter.isValidContainer(inventory.getTopInventory()))) {
            return;
        }

        for (RewardChest rewardChest : gameWorld.getRewardChests()) {
            if (!rewardChest.getBlock().equals(ContainerAdapter.getHolderBlock(inventory.getTopInventory().getHolder()))) {
                continue;
            }

            rewardChest.onOpen((Player) event.getPlayer());
            event.setCancelled(true);
            break;
        }

        if (!plugin.getMainConfig().getOpenInventories() && !DPermission.hasPermission(event.getPlayer(), DPermission.INSECURE)) {
            World world = event.getPlayer().getWorld();
            if (event.getInventory().getType() != InventoryType.CREATIVE && plugin.getEditWorld(world) != null) {
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
        GlobalPlayer dPlayer = plugin.getPlayerCache().get(player);
        if (plugin.getInstanceWorld(player.getWorld()) != null) {
            return;
        }
        Block block = player.getLocation().getBlock();
        if (dPlayer.hasRewardItemsLeft() && !VanillaItem.NETHER_PORTAL.is(block.getRelative(0, 1, 0)) && !VanillaItem.NETHER_PORTAL.is(block.getRelative(0, -1, 0))
                && !VanillaItem.NETHER_PORTAL.is(block.getRelative(1, 0, 0)) && !VanillaItem.NETHER_PORTAL.is(block.getRelative(-1, 0, 0))
                && !VanillaItem.NETHER_PORTAL.is(block.getRelative(0, 0, 1)) && !VanillaItem.NETHER_PORTAL.is(block.getRelative(0, 0, -1))) {
            PaginatedInventoryGUI lootInventory = new PaginatedInventoryGUI(DMessage.PLAYER_TREASURES.getMessage());
            PaginatedFlowInventoryLayout layout = new PaginatedFlowInventoryLayout(lootInventory, 54, PaginationButtonPosition.BOTTOM);
            layout.setSwitchButtonLinePlaceholdersEnabled(true);
            lootInventory.setLayout(layout);
            lootInventory.register();
            for (ItemStack item : dPlayer.getRewardItems()) {
                if (item != null) {
                    InventoryButton button = new InventoryButton(item);
                    button.setStealable(true);
                    lootInventory.add(button);
                }
            }
            lootInventory.open(player);
            dPlayer.setRewardItems(null);
        }
    }

}
