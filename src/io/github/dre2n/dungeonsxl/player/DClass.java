package io.github.dre2n.dungeonsxl.player;

import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.inventory.ItemStack;

public class DClass {
	
	private CopyOnWriteArrayList<ItemStack> items = new CopyOnWriteArrayList<ItemStack>();
	private String name;
	private boolean hasDog;
	
	public DClass(String name, CopyOnWriteArrayList<ItemStack> items, boolean hasDog) {
		this.items = items;
		this.name = name;
		this.hasDog = hasDog;
	}
	
	/**
	 * @return the items
	 */
	public CopyOnWriteArrayList<ItemStack> getItems() {
		return items;
	}
	
	/**
	 * @param itemStack
	 * the ItemStack to add
	 */
	public void setItems(ItemStack itemStack) {
		items.add(itemStack);
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param name
	 * the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return if the class has a dog
	 */
	public boolean hasDog() {
		return hasDog;
	}
	
	/**
	 * @param hasDog
	 * set if the class has a dog
	 */
	public void setDog(boolean hasDog) {
		this.hasDog = hasDog;
	}
	
}
