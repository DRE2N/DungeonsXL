package com.dre.dungeonsxl;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.server.v1_4_6.Item;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_4_6.entity.CraftPigZombie;
import org.bukkit.craftbukkit.v1_4_6.entity.CraftSkeleton;
import org.bukkit.craftbukkit.v1_4_6.entity.CraftZombie;
import org.bukkit.craftbukkit.v1_4_6.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
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
	
	private Map<ItemStack, Integer> drops = new HashMap<ItemStack, Integer>();
	public Map<ItemStack, Integer> getDrops() { return this.drops; }
	
	/* Extra Values for different Mob Types */
	private boolean isWitherSkeleton = false;
	private String ocelotType = null;
	
	/* Methods */
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
				
				//Check if it's a Zombie Pigman
				if(type==EntityType.PIG_ZOMBIE){
					CraftPigZombie entityC = (CraftPigZombie)entity;
			        net.minecraft.server.v1_4_6.EntityPigZombie entityMC = entityC.getHandle();

			        if(ItemHand!=null) entityMC.setEquipment(0, new net.minecraft.server.v1_4_6.ItemStack(ItemHand));
			        if(ItemBoots!=null) entityMC.setEquipment(1, new net.minecraft.server.v1_4_6.ItemStack(ItemBoots));
			        if(ItemLeggings!=null) entityMC.setEquipment(2, new net.minecraft.server.v1_4_6.ItemStack(ItemLeggings));
			        if(ItemChestplate!=null) entityMC.setEquipment(3, new net.minecraft.server.v1_4_6.ItemStack(ItemChestplate));
			        if(ItemHelmet!=null) entityMC.setEquipment(4, new net.minecraft.server.v1_4_6.ItemStack(ItemHelmet));
				}
				
				/* Check mob specified stuff */
				if(type==EntityType.SKELETON){
					if(isWitherSkeleton){
						((Skeleton) entity).setSkeletonType(SkeletonType.WITHER);
					} else {
						((Skeleton) entity).setSkeletonType(SkeletonType.NORMAL);
					}
				}
				
				if(type==EntityType.OCELOT){
					Ocelot ocelot=(Ocelot) entity;
					if(ocelotType!=null){
						if (ocelotType.equalsIgnoreCase("BLACK_CAT")) {
							ocelot.setCatType(Ocelot.Type.BLACK_CAT);
						} else if (ocelotType.equalsIgnoreCase("RED_CAT")) {
							ocelot.setCatType(Ocelot.Type.RED_CAT);
						} else if (ocelotType.equalsIgnoreCase("SIAMESE_CAT")) {
							ocelot.setCatType(Ocelot.Type.SIAMESE_CAT);
						} else if (ocelotType.equalsIgnoreCase("WILD_OCELOT")) {
							ocelot.setCatType(Ocelot.Type.WILD_OCELOT);
						}
					}
				}
				
				/* Spawn Mob */
				new DMob(entity, maxHealth, gWorld, this);

			}
		}
	}
	
	//Load Config
 	public static void load(File file){
		FileConfiguration configFile = YamlConfiguration.loadConfiguration(file);
		
		//Read Mobs
		for(String mobName:configFile.getKeys(false)){
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
			
			//Load different Mob options
			if(configFile.contains(mobName+".isWitherSkeleton")){
				mobType.isWitherSkeleton = configFile.getBoolean(mobName+".isWitherSkeleton");
			}
			
			if(configFile.contains(mobName+".ocelotType")){
				mobType.ocelotType = configFile.getString(mobName+".ocelotType");
			}
			
			//Drops
			ConfigurationSection configSetion = configFile.getConfigurationSection(mobName+".drops");
			if(configSetion!=null){
				Set<String> list=configSetion.getKeys(false);
				for(String dropPath:list){
					ItemStack item = null;
					int chance = 100;
					
					/* Item Stack */
					Material mat = Material.getMaterial(configSetion.getInt(dropPath+".id"));
					int amount = 1;
					short data = 0;
					
					if(configSetion.contains(dropPath+".amount")){
						amount = configSetion.getInt(dropPath+".amount");
					}
					if(configSetion.contains(dropPath+".data")){
						data = Short.parseShort(configSetion.getString(dropPath+".data"));
					}
					
					item = new ItemStack(mat,amount,data);
					
					/* Enchantments */
					if (configSetion.contains(dropPath+".enchantments")) {
						for(String enchantment:configSetion.getStringList(dropPath+".enchantments")){
							String[] splittedEnchantment = enchantment.split(" ");
							if (splittedEnchantment.length>1) {
								item.getItemMeta().addEnchant(Enchantment.getByName(splittedEnchantment[0]), Integer.parseInt(splittedEnchantment[1]), true);
							} else {
								item.getItemMeta().addEnchant(Enchantment.getByName(splittedEnchantment[0]), 1, true);
							}
						}
					}
					
					/* Item Name */
					if (configSetion.contains(dropPath+".name")) {
						item.getItemMeta().setDisplayName(configSetion.getString(dropPath+".name"));
					}
					
					/* Item Lore */
					if (configSetion.contains(dropPath+".lore")) {
						item.getItemMeta().setDisplayName(configSetion.getString(dropPath+".lore"));
					}
					
					/* Drop chance */
					if (configSetion.contains(dropPath+".chance")) {
						chance = configSetion.getInt(dropPath+".chance");
					}
					
					/* Add Item to the drops map */
					mobType.drops.put(item, chance);
				}
			}
		}
	}
 	
 	//Clear
 	public static void clear(){
 		mobTypes.clear();
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
