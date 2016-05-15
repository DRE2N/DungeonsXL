/*
 * Copyright (C) 2012-2016 Frank Baumann
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
package io.github.dre2n.dungeonsxl.mob;

import io.github.dre2n.commons.util.EnumUtil;
import io.github.dre2n.commons.util.NumberUtil;
import io.github.dre2n.commons.util.messageutil.MessageUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.DMessages;
import io.github.dre2n.dungeonsxl.world.GameWorld;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class DMobType {

    static DungeonsXL plugin = DungeonsXL.getInstance();

    private String name;
    private EntityType type;

    private int maxHealth;

    private ItemStack itemHand;
    private ItemStack itemHelmet;
    private ItemStack itemChestplate;
    private ItemStack itemLeggings;
    private ItemStack itemBoots;

    private Map<ItemStack, Integer> drops = new HashMap<>();

    /* Extra Values for different Mob Types */
    private boolean witherSkeleton = false;
    private String ocelotType = null;

    /**
     * @param file
     * the script file
     */
    public DMobType(File file) {
        this(file.getName().substring(0, file.getName().length() - 4), YamlConfiguration.loadConfiguration(file));
    }

    /**
     * @param name
     * the name of the Announcer
     * @param config
     * the config that stores the information
     */
    public DMobType(String name, FileConfiguration config) {
        this.name = name;

        // Read Mobs
        type = EntityType.fromName(config.getString("type"));

        // Load MaxHealth
        if (config.contains("maxHealth")) {
            maxHealth = config.getInt("maxHealth");
        }

        // Load Items
        if (config.contains("itemHelmet")) {
            itemHelmet = new ItemStack(config.getInt("itemHelmet"));
        }

        if (config.contains("itemChestplate")) {
            itemChestplate = new ItemStack(config.getInt("itemChestplate"));
        }

        if (config.contains("itemBoots")) {
            itemBoots = new ItemStack(config.getInt("itemBoots"));
        }

        if (config.contains("itemLeggings")) {
            itemLeggings = new ItemStack(config.getInt("itemLeggings"));
        }

        if (config.contains("itemHand")) {
            itemHand = new ItemStack(config.getInt("itemHand"));
        }

        // Load different Mob options
        if (config.contains("isWitherSkeleton")) {
            witherSkeleton = config.getBoolean("isWitherSkeleton");
        }

        if (config.contains("ocelotType")) {
            ocelotType = config.getString("ocelotType");
        }

        // Drops
        ConfigurationSection configSetion = config.getConfigurationSection("drops");
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
                                itemMeta.addEnchant(Enchantment.getByName(splittedEnchantment[0].toUpperCase()), NumberUtil.parseInt(splittedEnchantment[1]), true);
                            } else {
                                itemMeta.addEnchant(Enchantment.getByName(splittedEnchantment[0].toUpperCase()), 1, true);
                            }
                        } else {
                            MessageUtil.log(DMessages.LOG_ERROR_MOB_ENCHANTMENT.getMessage(splittedEnchantment[0]));
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
                getDrops().put(item, chance);
            }
        }
    }

    /**
     * @param name
     * the name of the DMobType
     * @param type
     * the EntityType of the mob
     */
    public DMobType(String name, EntityType type) {
        this.name = name;
        this.type = type;
    }

    /* Getters and setters */
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
     * @return the type
     */
    public EntityType getType() {
        return type;
    }

    /**
     * @param type
     * the type to set
     */
    public void setType(EntityType type) {
        this.type = type;
    }

    /**
     * @return the maxHealth
     */
    public int getMaxHealth() {
        return maxHealth;
    }

    /**
     * @param maxHealth
     * the maxHealth to set
     */
    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    /**
     * @return the itemHand
     */
    public ItemStack getitemHand() {
        return itemHand;
    }

    /**
     * @param itemHand
     * the itemHand to set
     */
    public void setitemHand(ItemStack itemHand) {
        this.itemHand = itemHand;
    }

    /**
     * @return the itemHelmet
     */
    public ItemStack getitemHelmet() {
        return itemHelmet;
    }

    /**
     * @param itemHelmet
     * the itemHelmet to set
     */
    public void setitemHelmet(ItemStack itemHelmet) {
        this.itemHelmet = itemHelmet;
    }

    /**
     * @return the itemChestplate
     */
    public ItemStack getitemChestplate() {
        return itemChestplate;
    }

    /**
     * @param itemChestplate
     * the itemChestplate to set
     */
    public void setitemChestplate(ItemStack itemChestplate) {
        this.itemChestplate = itemChestplate;
    }

    /**
     * @return the itemLeggings
     */
    public ItemStack getitemLeggings() {
        return itemLeggings;
    }

    /**
     * @param itemLeggings
     * the itemLeggings to set
     */
    public void setitemLeggings(ItemStack itemLeggings) {
        this.itemLeggings = itemLeggings;
    }

    /**
     * @return the itemBoots
     */
    public ItemStack getitemBoots() {
        return itemBoots;
    }

    /**
     * @param itemBoots
     * the itemBoots to set
     */
    public void setitemBoots(ItemStack itemBoots) {
        this.itemBoots = itemBoots;
    }

    /**
     * @return the drops
     */
    public Map<ItemStack, Integer> getDrops() {
        return drops;
    }

    /**
     * @param drops
     * the drops to set
     */
    public void setDrops(Map<ItemStack, Integer> drops) {
        this.drops = drops;
    }

    /**
     * @return if the skeleton is a wither skeleton
     */
    public boolean isWitherSkeleton() {
        return witherSkeleton;
    }

    /**
     * @param witherSkeleton
     * set if the skeleton is a wither skeleton
     */
    public void setWitherSkeleton(boolean witherSkeleton) {
        this.witherSkeleton = witherSkeleton;
    }

    /**
     * @return the ocelotType
     */
    public String getOcelotType() {
        return ocelotType;
    }

    /**
     * @param ocelotType
     * the ocelotType to set
     */
    public void setOcelotType(String ocelotType) {
        this.ocelotType = ocelotType;
    }

    /* Actions */
    public void spawn(GameWorld gameWorld, Location loc) {
        if (type == null) {
            return;
        }

        if (!type.isAlive()) {
            return;
        }

        LivingEntity entity = (LivingEntity) gameWorld.getWorld().spawnEntity(loc, type);

        /* Set the Items */
        entity.getEquipment().setItemInHand(itemHand);
        entity.getEquipment().setHelmet(itemHelmet);
        entity.getEquipment().setChestplate(itemChestplate);
        entity.getEquipment().setLeggings(itemLeggings);
        entity.getEquipment().setBoots(itemBoots);

        /* Check mob specified stuff */
        if (type == EntityType.SKELETON) {
            if (witherSkeleton) {
                ((Skeleton) entity).setSkeletonType(SkeletonType.WITHER);
            } else {
                ((Skeleton) entity).setSkeletonType(SkeletonType.NORMAL);
            }
        }

        if (type == EntityType.OCELOT) {
            Ocelot ocelot = (Ocelot) entity;
            if (EnumUtil.isValidEnum(Ocelot.Type.class, ocelotType.toUpperCase())) {
                ocelot.setCatType(Ocelot.Type.valueOf(ocelotType.toUpperCase()));
            }
        }

        /* Set Health */
        if (maxHealth > 0) {
            entity.setMaxHealth(maxHealth);
            entity.setHealth(maxHealth);
        }

        /* Disable Despawning */
        entity.setRemoveWhenFarAway(false);

        /* Spawn Mob */
        new DMob(entity, gameWorld, this);
    }

}
