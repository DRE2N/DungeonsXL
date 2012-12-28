package com.dre.dungeonsxl;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.server.v1_4_6.Item;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_4_6.entity.CraftSkeleton;
import org.bukkit.craftbukkit.v1_4_6.entity.CraftZombie;
import org.bukkit.craftbukkit.v1_4_6.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import com.dre.dungeonsxl.game.DMob;
import com.dre.dungeonsxl.game.GameWorld;

public class DMobType {
	private static Set<DMobType> mobTypes = new HashSet<DMobType>();

	private String name;
	private EntityType type;

	private int maxHealth;

	private Item ItemHand;
	private Item ItemHelmet;
	private Item ItemChestplate;
	private Item ItemLeggings;
	private Item ItemBoots;

	public DMobType(String name, EntityType type){
		mobTypes.add(this);

		this.name=name;
		this.type=type;

	}

	public void spawn(GameWorld gWorld, Location loc){
		if(type!=null){
			if(type.isAlive()){
				LivingEntity entity=(LivingEntity)gWorld.world.spawnEntity(loc, type);

				/* Set the Items */

				//Check if it's a Zombie
				if(type==EntityType.ZOMBIE){
					CraftZombie entityC = (CraftZombie)entity;
			        net.minecraft.server.v1_4_6.EntityZombie entityMC = entityC.getHandle();

			        if(ItemHand!=null) entityMC.setEquipment(0, new net.minecraft.server.v1_4_6.ItemStack(ItemHand));
			        if(ItemBoots!=null) entityMC.setEquipment(1, new net.minecraft.server.v1_4_6.ItemStack(ItemBoots));
			        if(ItemLeggings!=null) entityMC.setEquipment(2, new net.minecraft.server.v1_4_6.ItemStack(ItemLeggings));
			        if(ItemChestplate!=null) entityMC.setEquipment(3, new net.minecraft.server.v1_4_6.ItemStack(ItemChestplate));
			        if(ItemHelmet!=null) entityMC.setEquipment(4, new net.minecraft.server.v1_4_6.ItemStack(ItemHelmet));
				}

				//Check if it's a Skeleton
				if(type==EntityType.SKELETON){
					CraftSkeleton entityC = (CraftSkeleton)entity;
			        net.minecraft.server.v1_4_6.EntitySkeleton entityMC = entityC.getHandle();

			        if(ItemHand!=null) entityMC.setEquipment(0, new net.minecraft.server.v1_4_6.ItemStack(ItemHand));
			        if(ItemBoots!=null) entityMC.setEquipment(1, new net.minecraft.server.v1_4_6.ItemStack(ItemBoots));
			        if(ItemLeggings!=null) entityMC.setEquipment(2, new net.minecraft.server.v1_4_6.ItemStack(ItemLeggings));
			        if(ItemChestplate!=null) entityMC.setEquipment(3, new net.minecraft.server.v1_4_6.ItemStack(ItemChestplate));
			        if(ItemHelmet!=null) entityMC.setEquipment(4, new net.minecraft.server.v1_4_6.ItemStack(ItemHelmet));
				}

				/* Spawn Mob */
				new DMob(entity, maxHealth, gWorld);

			}
		}
	}

	//Load Config
 	public static void load(File file){
		FileConfiguration configFile = YamlConfiguration.loadConfiguration(file);

		//Read Mobs
		for(String mobName:configFile.getKeys(true)){
			if(!mobName.contains(".")){
				DungeonsXL.p.log("Test"+mobName);

				EntityType type=EntityType.fromName(configFile.getString(mobName+".Type"));

				DMobType mobType=new DMobType(mobName, type);

				//Load MaxHealth
				if(configFile.contains(mobName+".MaxHealth")){
					mobType.maxHealth=configFile.getInt(mobName+".MaxHealth");
				}

				//Load Items
				if(configFile.contains(mobName+".ItemHelmet")){
					mobType.ItemHelmet=CraftItemStack.asNMSCopy(new ItemStack(configFile.getInt(mobName+".ItemHelmet"))).getItem();
				}

				if(configFile.contains(mobName+".ItemChestplate")){
					mobType.ItemChestplate=CraftItemStack.asNMSCopy(new ItemStack(configFile.getInt(mobName+".ItemChestplate"))).getItem();
				}

				if(configFile.contains(mobName+".ItemBoots")){
					mobType.ItemBoots=CraftItemStack.asNMSCopy(new ItemStack(configFile.getInt(mobName+".ItemBoots"))).getItem();
				}

				if(configFile.contains(mobName+".ItemLeggings")){
					mobType.ItemLeggings=CraftItemStack.asNMSCopy(new ItemStack(configFile.getInt(mobName+".ItemLeggings"))).getItem();
				}

				if(configFile.contains(mobName+".ItemHand")){
					mobType.ItemHand=CraftItemStack.asNMSCopy(new ItemStack(configFile.getInt(mobName+".ItemHand"))).getItem();
				}
			}
		}

	}

 	//Get
 	public static DMobType get(String name){
 		for(DMobType mobType:DMobType.mobTypes){
 			if(mobType.name.equalsIgnoreCase(name)){
 				return mobType;
 			}
 		}
 		return null;
 	}
}
