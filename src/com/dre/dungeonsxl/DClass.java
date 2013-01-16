package com.dre.dungeonsxl;

import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.inventory.ItemStack;

public class DClass {
	public CopyOnWriteArrayList<ItemStack> items=new CopyOnWriteArrayList<ItemStack>();
	public String name;
	public boolean hasDog;
	
	//Spout
	public String spoutSkinURL;
	
	public DClass(String name, CopyOnWriteArrayList<ItemStack> items, boolean hasDog, String spoutSkinURL){
		this.items = items;
		this.name = name;
		this.hasDog = hasDog;
		this.spoutSkinURL = spoutSkinURL;
	}
}
