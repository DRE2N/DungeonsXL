package io.github.dre2n.dungeonsxl.dungeon;

import io.github.dre2n.dungeonsxl.DungeonsXL;

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
	
	public DLootInventory(Player player, ItemStack[] istacks) {
		plugin.getDLootInventories().add(this);
		
		inventory = Bukkit.createInventory(player, 54, ChatColor.translateAlternateColorCodes('&', DungeonsXL.getPlugin().getDMessages().get("Player_Treasures")));
		for (ItemStack istack : istacks) {
			if (istack != null) {
				inventory.addItem(istack);
			}
		}
		this.player = player;
	}
	
	public static DLootInventory get(Player player) {
		for (DLootInventory inventory : plugin.getDLootInventories()) {
			if (inventory.player == player) {
				return inventory;
			}
		}
		
		return null;
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
	
}
