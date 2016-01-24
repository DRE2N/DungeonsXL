package io.github.dre2n.dungeonsxl.dungeon;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.MessageConfig.Messages;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class DLootInventory {
	
	static DungeonsXL plugin = DungeonsXL.getPlugin();
	
	private Inventory inventory;
	private InventoryView inventoryView;
	private Player player;
	
	private long time;
	
	public DLootInventory(Player player, ItemStack[] itemStacks) {
		plugin.getDLootInventories().add(this);
		
		inventory = Bukkit.createInventory(player, 54, ChatColor.translateAlternateColorCodes('&', plugin.getMessageConfig().getMessage(Messages.PLAYER_TREASURES)));
		for (ItemStack itemStack : itemStacks) {
			if (itemStack != null) {
				inventory.addItem(itemStack);
			}
		}
		this.player = player;
	}
	
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
	
	// Static
	
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
