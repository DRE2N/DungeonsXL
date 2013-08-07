package com.dre.dungeonsxl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.getspout.spoutapi.Spout;
import org.getspout.spoutapi.player.EntitySkinType;
import com.dre.dungeonsxl.game.DMob;
import com.dre.dungeonsxl.game.GameWorld;

public class DMobType {
	private String name;
	private EntityType type;

	private int maxHealth;

	private ItemStack ItemHand;
	private ItemStack ItemHelmet;
	private ItemStack ItemChestplate;
	private ItemStack ItemLeggings;
	private ItemStack ItemBoots;

	private Map<ItemStack, Integer> drops = new HashMap<ItemStack, Integer>();

	public Map<ItemStack, Integer> getDrops() {
		return this.drops;
	}

	/* Extra Values for different Mob Types */
	private boolean isWitherSkeleton = false;
	private String ocelotType = null;

	/* Spout */
	private String spoutSkinURL;

	/* Methods */
	public DMobType(String name, EntityType type) {
		this.name = name;
		this.type = type;
	}

	public void spawn(GameWorld gWorld, Location loc) {
		if (type != null) {
			if (type.isAlive()) {
				LivingEntity entity = (LivingEntity) gWorld.world.spawnEntity(loc, type);

				/* Set the Items */
				entity.getEquipment().setItemInHand(ItemHand);
				entity.getEquipment().setHelmet(ItemHelmet);
				entity.getEquipment().setChestplate(ItemChestplate);
				entity.getEquipment().setLeggings(ItemLeggings);
				entity.getEquipment().setBoots(ItemBoots);

				/* Check mob specified stuff */
				if (type == EntityType.SKELETON) {
					if (isWitherSkeleton) {
						((Skeleton) entity).setSkeletonType(SkeletonType.WITHER);
					} else {
						((Skeleton) entity).setSkeletonType(SkeletonType.NORMAL);
					}
				}

				if (type == EntityType.OCELOT) {
					Ocelot ocelot = (Ocelot) entity;
					if (ocelotType != null) {
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

				/* Set Spout Skin */
				if (P.p.mainConfig.enableSpout) {
					if (spoutSkinURL != null) {
						Spout.getServer().setEntitySkin(entity, spoutSkinURL, EntitySkinType.DEFAULT);
					}
				}

				/* Set Health */
				if (maxHealth > 0) {
					entity.setMaxHealth(maxHealth);
					entity.setHealth(maxHealth);
				}

				/* Spawn Mob */
				new DMob(entity, gWorld, this);

			}
		}
	}

	// Load Config
	public static Set<DMobType> load(ConfigurationSection configFile) {
		Set<DMobType> set = new HashSet<DMobType>();
		if (configFile != null) {
			// Read Mobs
			for (String mobName : configFile.getKeys(false)) {
				EntityType type = EntityType.fromName(configFile.getString(mobName + ".Type"));

				if (type != null) {
					DMobType mobType = new DMobType(mobName, type);
					set.add(mobType);

					// Load MaxHealth
					if (configFile.contains(mobName + ".MaxHealth")) {
						mobType.maxHealth = configFile.getInt(mobName + ".MaxHealth");
					}

					// Load Items
					if (configFile.contains(mobName + ".ItemHelmet")) {
						mobType.ItemHelmet = new ItemStack(configFile.getInt(mobName + ".ItemHelmet"));// CraftItemStack.asNMSCopy(new
																										// ItemStack(configFile.getInt(mobName+".ItemHelmet"))).getItem();
					}

					if (configFile.contains(mobName + ".ItemChestplate")) {
						mobType.ItemChestplate = new ItemStack(configFile.getInt(mobName + ".ItemChestplate"));// CraftItemStack.asNMSCopy(new
																												// ItemStack(configFile.getInt(mobName+".ItemChestplate"))).getItem();
					}

					if (configFile.contains(mobName + ".ItemBoots")) {
						mobType.ItemBoots = new ItemStack(configFile.getInt(mobName + ".ItemBoots"));// CraftItemStack.asNMSCopy(new
																										// ItemStack(configFile.getInt(mobName+".ItemBoots"))).getItem();
					}

					if (configFile.contains(mobName + ".ItemLeggings")) {
						mobType.ItemLeggings = new ItemStack(configFile.getInt(mobName + ".ItemLeggings"));// CraftItemStack.asNMSCopy(new
																											// ItemStack(configFile.getInt(mobName+".ItemLeggings"))).getItem();
					}

					if (configFile.contains(mobName + ".ItemHand")) {
						mobType.ItemHand = new ItemStack(configFile.getInt(mobName + ".ItemHand"));// CraftItemStack.asNMSCopy(new
																									// ItemStack(configFile.getInt(mobName+".ItemHand"))).getItem();
					}

					// Load different Mob options
					if (configFile.contains(mobName + ".isWitherSkeleton")) {
						mobType.isWitherSkeleton = configFile.getBoolean(mobName + ".isWitherSkeleton");
					}

					if (configFile.contains(mobName + ".ocelotType")) {
						mobType.ocelotType = configFile.getString(mobName + ".ocelotType");
					}

					// Drops
					ConfigurationSection configSetion = configFile.getConfigurationSection(mobName + ".drops");
					if (configSetion != null) {
						Set<String> list = configSetion.getKeys(false);
						for (String dropPath : list) {
							ItemStack item = null;
							ItemMeta itemMeta = null;
							int chance = 100;

							/* Item Stack */
							Material mat = Material.getMaterial(configSetion.getInt(dropPath + ".id"));
							int amount = 1;
							short data = 0;

							if (configSetion.contains(dropPath + ".amount")) {
								amount = configSetion.getInt(dropPath + ".amount");
							}
							if (configSetion.contains(dropPath + ".data")) {
								data = Short.parseShort(configSetion.getString(dropPath + ".data"));
							}

							item = new ItemStack(mat, amount, data);
							itemMeta = item.getItemMeta();

							/* Enchantments */
							if (configSetion.contains(dropPath + ".enchantments")) {
								for (String enchantment : configSetion.getStringList(dropPath + ".enchantments")) {
									String[] splittedEnchantment = enchantment.split(" ");
									if (Enchantment.getByName(splittedEnchantment[0].toUpperCase()) != null) {
										if (splittedEnchantment.length > 1) {
											itemMeta.addEnchant(Enchantment.getByName(splittedEnchantment[0].toUpperCase()), P.p.parseInt(splittedEnchantment[1]), true);
										} else {
											itemMeta.addEnchant(Enchantment.getByName(splittedEnchantment[0].toUpperCase()), 1, true);
										}
									} else {
										P.p.log(P.p.language.get("Log_Error_MobEnchantment", splittedEnchantment[0]));
									}
								}
							}

							/* Item Name */
							if (configSetion.contains(dropPath + ".name")) {
								itemMeta.setDisplayName(configSetion.getString(dropPath + ".name"));
							}

							/* Item Lore */
							if (configSetion.contains(dropPath + ".lore")) {
								String[] lore = configSetion.getString(dropPath + ".lore").split("//");
								itemMeta.setLore(Arrays.asList(lore));
							}

							/* Drop chance */
							if (configSetion.contains(dropPath + ".chance")) {
								chance = configSetion.getInt(dropPath + ".chance");
							}

							/* Add Item to the drops map */
							item.setItemMeta(itemMeta);
							mobType.drops.put(item, chance);
						}
					}

					// Spout Skin
					if (configFile.contains(mobName + ".spoutSkinURL")) {
						mobType.spoutSkinURL = configFile.getString(mobName + ".spoutSkinURL");
					}
				} else {
					P.p.log(P.p.language.get("Log_Error_MobType", configFile.getString(mobName + ".Type")));
				}
			}
		}
		return set;
	}

	// Get
	public static DMobType get(String name, Set<DMobType> mobTypes) {
		for (DMobType mobType : mobTypes) {
			if (mobType.name.equalsIgnoreCase(name)) {
				return mobType;
			}
		}

		if (P.p.mainConfig.defaultDungeon != null) {
			for (DMobType mobType : P.p.mainConfig.defaultDungeon.getMobTypes()) {
				if (mobType.name.equalsIgnoreCase(name)) {
					return mobType;
				}
			}
		}

		return null;
	}

	public String getName() {
		return name;
	}
}
