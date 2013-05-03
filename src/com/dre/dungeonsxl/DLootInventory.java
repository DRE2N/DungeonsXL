package com.dre.dungeonsxl;

import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class DLootInventory {
	public static CopyOnWriteArrayList<DLootInventory> LootInventorys = new CopyOnWriteArrayList<DLootInventory>();

	public Inventory inventory;
	public InventoryView inventoryView;
	public Player player;

	public long time;

	public DLootInventory(Player player, ItemStack[] istacks) {
		LootInventorys.add(this);

		this.inventory = Bukkit.createInventory(player, 54, "Belohnungen");
		for (ItemStack istack : istacks) {
			if (istack != null) {
				this.inventory.addItem(istack);
			}
		}
		this.player = player;
	}

	public static DLootInventory get(Player player) {
		for (DLootInventory inventory : LootInventorys) {
			if (inventory.player == player) {
				return inventory;
			}
		}

		return null;
	}
}
