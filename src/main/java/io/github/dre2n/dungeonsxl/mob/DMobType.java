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
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.MessageConfig.Messages;
import io.github.dre2n.dungeonsxl.world.GameWorld;
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

/**
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class DMobType {

    protected static DungeonsXL plugin = DungeonsXL.getInstance();

    private String name;
    private EntityType type;

    private int maxHealth;

    private ItemStack ItemHand;
    private ItemStack ItemHelmet;
    private ItemStack ItemChestplate;
    private ItemStack ItemLeggings;
    private ItemStack ItemBoots;

    private Map<ItemStack, Integer> drops = new HashMap<>();

    /* Extra Values for different Mob Types */
    private boolean witherSkeleton = false;
    private String ocelotType = null;

    /* Methods */
    public DMobType(String name, EntityType type) {
        this.name = name;
        this.type = type;
    }

    public void spawn(GameWorld gameWorld, Location loc) {
        if (type == null) {
            return;
        }

        if (!type.isAlive()) {
            return;
        }

        LivingEntity entity = (LivingEntity) gameWorld.getWorld().spawnEntity(loc, type);

        /* Set the Items */
        entity.getEquipment().setItemInHand(ItemHand);
        entity.getEquipment().setHelmet(ItemHelmet);
        entity.getEquipment().setChestplate(ItemChestplate);
        entity.getEquipment().setLeggings(ItemLeggings);
        entity.getEquipment().setBoots(ItemBoots);

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

    // Load Config
    public static Set<DMobType> load(ConfigurationSection configFile) {
        Set<DMobType> set = new HashSet<>();

        if (configFile == null) {
            return set;
        }

        // Read Mobs
        for (String mobName : configFile.getKeys(false)) {
            EntityType type = EntityType.fromName(configFile.getString(mobName + ".Type"));

            if (type == null) {
                plugin.getLogger().info(plugin.getMessageConfig().getMessage(Messages.LOG_ERROR_MOBTYPE, configFile.getString(mobName + ".Type")));
                continue;
            }

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
                mobType.witherSkeleton = configFile.getBoolean(mobName + ".isWitherSkeleton");
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
                                    itemMeta.addEnchant(Enchantment.getByName(splittedEnchantment[0].toUpperCase()), NumberUtil.parseInt(splittedEnchantment[1]), true);
                                } else {
                                    itemMeta.addEnchant(Enchantment.getByName(splittedEnchantment[0].toUpperCase()), 1, true);
                                }
                            } else {
                                plugin.getLogger().info(plugin.getMessageConfig().getMessage(Messages.LOG_ERROR_MOB_ENCHANTMENT, splittedEnchantment[0]));
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
                    mobType.getDrops().put(item, chance);
                }
            }
        }

        return set;
    }

    // Get
    public static DMobType getByName(String name, Set<DMobType> mobTypes) {
        for (DMobType mobType : mobTypes) {
            if (mobType.name.equalsIgnoreCase(name)) {
                return mobType;
            }
        }

        if (plugin.getMainConfig().getDefaultWorldConfig() != null) {
            for (DMobType mobType : plugin.getMainConfig().getDefaultWorldConfig().getMobTypes()) {
                if (mobType.name.equalsIgnoreCase(name)) {
                    return mobType;
                }
            }
        }

        return null;
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
    public ItemStack getItemHand() {
        return ItemHand;
    }

    /**
     * @param itemHand
     * the itemHand to set
     */
    public void setItemHand(ItemStack itemHand) {
        ItemHand = itemHand;
    }

    /**
     * @return the itemHelmet
     */
    public ItemStack getItemHelmet() {
        return ItemHelmet;
    }

    /**
     * @param itemHelmet
     * the itemHelmet to set
     */
    public void setItemHelmet(ItemStack itemHelmet) {
        ItemHelmet = itemHelmet;
    }

    /**
     * @return the itemChestplate
     */
    public ItemStack getItemChestplate() {
        return ItemChestplate;
    }

    /**
     * @param itemChestplate
     * the itemChestplate to set
     */
    public void setItemChestplate(ItemStack itemChestplate) {
        ItemChestplate = itemChestplate;
    }

    /**
     * @return the itemLeggings
     */
    public ItemStack getItemLeggings() {
        return ItemLeggings;
    }

    /**
     * @param itemLeggings
     * the itemLeggings to set
     */
    public void setItemLeggings(ItemStack itemLeggings) {
        ItemLeggings = itemLeggings;
    }

    /**
     * @return the itemBoots
     */
    public ItemStack getItemBoots() {
        return ItemBoots;
    }

    /**
     * @param itemBoots
     * the itemBoots to set
     */
    public void setItemBoots(ItemStack itemBoots) {
        ItemBoots = itemBoots;
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

}
