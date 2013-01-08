package com.dre.dungeonsxl;

import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.inventory.ItemStack;

public class DClass {
	
	
	//Variables
	public CopyOnWriteArrayList<ItemStack> items=new CopyOnWriteArrayList<ItemStack>();
	public String name;
	public boolean hasDog;
	
	public DClass(String name,CopyOnWriteArrayList<ItemStack> items,boolean hasDog){
		this.items=items;
		this.name=name;
		this.hasDog=hasDog;
	}
	
	
	
	
	
}
