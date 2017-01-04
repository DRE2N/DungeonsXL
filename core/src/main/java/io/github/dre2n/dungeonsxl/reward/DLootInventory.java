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
import io.github.dre2n.dungeonsxl.config.DMessages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class DLootInventory {

    static DungeonsXL plugin = DungeonsXL.getInstance();

    private Inventory inventory;
    private InventoryView inventoryView;
    private Player player;

    public DLootInventory(Player player, ItemStack[] itemStacks) {
        plugin.getDLootInventories().add(this);

        inventory = Bukkit.createInventory(player, 54, ChatColor.translateAlternateColorCodes('&', plugin.getMessageConfig().getMessage(DMessages.PLAYER_TREASURES)));
        for (ItemStack itemStack : itemStacks) {
            if (itemStack != null) {
                inventory.addItem(itemStack);
            }
        }
        this.player = player;
    }

    /* Getters and setters */
    /**
     * @return the inventory
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * @param inventory
     * the inventory to set
     */
    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    /**
     * @return the inventoryView
     */
    public InventoryView getInventoryView() {
        return inventoryView;
    }

    /**
     * @param inventoryView
     * the inventoryView to set
     */
    public void setInventoryView(InventoryView inventoryView) {
        this.inventoryView = inventoryView;
    }

    /**
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @param player
     * the player to set
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    private long time;

    /**
     * @return the time
     */
    public long getTime() {
        return time;
    }

    /**
     * @param time
     * the time to set
     */
    public void setTime(long time) {
        this.time = time;
    }

    /* Statics */
    /**
     * @param player
     * the player whose DLootIntentory will be returned
     */
    public static DLootInventory getByPlayer(Player player) {
        for (DLootInventory inventory : plugin.getDLootInventories()) {
            if (inventory.player == player) {
                return inventory;
            }
        }

        return null;
    }

}
