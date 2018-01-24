/*
 * Copyright (C) 2012-2018 Frank Baumann
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
package io.github.dre2n.dungeonsxl.player;

import io.github.dre2n.commons.chat.MessageUtil;
import io.github.dre2n.commons.compatibility.CompatibilityHandler;
import io.github.dre2n.commons.compatibility.Internals;
import io.github.dre2n.commons.config.DREConfig;
import io.github.dre2n.commons.misc.EnumUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.DMessage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

/**
 * Represents a player's persistent data.
 *
 * @author Daniel Saukel
 */
public class DPlayerData extends DREConfig {

    boolean is1_9 = Internals.andHigher(Internals.v1_9_R1).contains(CompatibilityHandler.getInstance().getInternals());

    public static final int CONFIG_VERSION = 2;

    public static final String PREFIX_STATE_PERSISTENCE = "savePlayer.";
    public static final String PREFIX_STATS = "stats.";

    // State persistence
    private Location oldLocation;
    private List<ItemStack> oldInventory;
    private List<ItemStack> oldArmor;
    private ItemStack oldOffHand;
    private int oldLvl;
    private float oldExp;
    private double oldMaxHealth;
    private double oldHealth;
    private int oldFoodLevel;
    private int oldFireTicks;
    private GameMode oldGameMode;
    private Collection<PotionEffect> oldPotionEffects;

    // Stats
    private Map<String, Long> timeLastPlayed = new HashMap<>();

    public DPlayerData(File file) {
        super(file, CONFIG_VERSION);

        if (initialize) {
            initialize();
        }
        load();
    }

    /* Getters and setters */
    /**
     * @return if the player was in a game when he left the game
     */
    public boolean wasInGame() {
        return config.contains(PREFIX_STATE_PERSISTENCE);
    }

    /**
     * @return the old location
     */
    public Location getOldLocation() {
        return oldLocation;
    }

    /**
     * @param location
     * the location to set
     */
    public void setOldLocation(Location location) {
        oldLocation = location;
    }

    /**
     * @return the items in the old inventory
     */
    public List<ItemStack> getOldInventory() {
        return oldInventory;
    }

    /**
     * @param inventory
     * the inventory to set
     */
    public void setOldInventory(List<ItemStack> inventory) {
        oldInventory = inventory;
    }

    /**
     * @return the items in the old armor slots
     */
    public List<ItemStack> getOldArmor() {
        return oldArmor;
    }

    /**
     * @param inventory
     * the inventory to set
     */
    public void setOldArmor(List<ItemStack> inventory) {
        oldArmor = inventory;
    }

    /**
     * @return the items in the old off-hand slot
     */
    public ItemStack getOldOffHand() {
        return oldOffHand;
    }

    /**
     * @param offHand
     * the off hand item to set
     */
    public void setOldOffHand(ItemStack offHand) {
        oldOffHand = offHand;
    }

    /**
     * @return the old level
     */
    public int getOldLevel() {
        return oldLvl;
    }

    /**
     * @param level
     * the level to set
     */
    public void setOldLevel(int level) {
        oldLvl = level;
    }

    /**
     * @return the old exp
     */
    public float getOldExp() {
        return oldExp;
    }

    /**
     * @param exp
     * the amount of exp to set
     */
    public void setOldExp(float exp) {
        oldExp = exp;
    }

    /**
     * @return the old max health
     */
    public double getOldMaxHealth() {
        return oldMaxHealth;
    }

    /**
     * @param maxHealth
     * the maximum health to set
     */
    public void setOldMaxHealth(double maxHealth) {
        oldMaxHealth = maxHealth;
    }

    /**
     * @return the old health
     */
    public double getOldHealth() {
        return oldHealth;
    }

    /**
     * @param health
     * the health to set
     */
    public void setOldHealth(double health) {
        oldHealth = health;
    }

    /**
     * @return the old food level
     */
    public int getOldFoodLevel() {
        return oldFoodLevel;
    }

    /**
     * @param foodLevel
     * the food level to set
     */
    public void setOldFoodLevel(int foodLevel) {
        oldFoodLevel = foodLevel;
    }

    /**
     * @return the old fire ticks
     */
    public int getOldFireTicks() {
        return oldFireTicks;
    }

    /**
     * @param fireTicks
     * the fire ticks to set
     */
    public void setFireTicks(int fireTicks) {
        oldFireTicks = fireTicks;
    }

    /**
     * @return the old GameMode
     */
    public GameMode getOldGameMode() {
        return oldGameMode;
    }

    /**
     * @param gameMode
     * the GameMode to set
     */
    public void setOldGameMode(GameMode gameMode) {
        oldGameMode = gameMode;
    }

    /**
     * @return the old potion effects
     */
    public Collection<PotionEffect> getOldPotionEffects() {
        return oldPotionEffects;
    }

    /**
     * @param potionEffects
     * the potion effects to set
     */
    public void setOldPotionEffects(Collection<PotionEffect> potionEffects) {
        oldPotionEffects = potionEffects;
    }

    /**
     * @return a map of the player's finished dungeons with dates.
     */
    public Map<String, Long> getTimeLastPlayed() {
        return timeLastPlayed;
    }

    /**
     * @param dungeon
     * the dungeon to check
     * @return the time when the player finished the dungeon for the last time
     */
    public long getTimeLastPlayed(String dungeon) {
        Long time = timeLastPlayed.get(dungeon.toLowerCase());
        if (time == null) {
            return -1;
        } else {
            return time;
        }
    }

    /**
     * @param dungeon
     * the finished dungeon
     * @param time
     * the time when the dungeon was finished
     */
    public void setTimeLastPlayed(String dungeon, long time) {
        timeLastPlayed.put(dungeon.toLowerCase(), time);
        save();
    }

    /* Actions */
    /**
     * @param dungeon
     * the finished dungeon
     */
    public void logTimeLastPlayed(String dungeon) {
        timeLastPlayed.put(dungeon.toLowerCase(), System.currentTimeMillis());
        save();
    }

    @Override
    public void initialize() {
        if (!config.contains(PREFIX_STATS + "timeLastPlayed")) {
            config.createSection(PREFIX_STATS + "timeLastPlayed");
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
                MessageUtil.log(DungeonsXL.getInstance(), DMessage.LOG_NEW_PLAYER_DATA.getMessage(file.getName()));
            } catch (IOException exception) {
            }
        }

        save();
    }

    @Override
    public void load() {
        if (config.isConfigurationSection(PREFIX_STATS + "timeLastPlayed")) {
            for (String key : config.getConfigurationSection(PREFIX_STATS + "timeLastPlayed").getKeys(false)) {
                timeLastPlayed.put(key, config.getLong(PREFIX_STATS + "timeLastPlayed." + key));
            }
        }

        if (!wasInGame()) {
            return;
        }

        oldInventory = (List<ItemStack>) config.get(PREFIX_STATE_PERSISTENCE + "oldInventory");
        oldArmor = (List<ItemStack>) config.get(PREFIX_STATE_PERSISTENCE + "oldArmor");
        oldOffHand = (ItemStack) config.get(PREFIX_STATE_PERSISTENCE + "oldOffHand");

        oldLvl = config.getInt(PREFIX_STATE_PERSISTENCE + "oldLvl");
        oldExp = config.getInt(PREFIX_STATE_PERSISTENCE + "oldExp");
        oldMaxHealth = config.getDouble(PREFIX_STATE_PERSISTENCE + "oldMaxHealth");
        oldHealth = config.getDouble(PREFIX_STATE_PERSISTENCE + "oldHealth");
        oldFoodLevel = config.getInt(PREFIX_STATE_PERSISTENCE + "oldFoodLevel");
        oldFireTicks = config.getInt(PREFIX_STATE_PERSISTENCE + "oldFireTicks");

        if (EnumUtil.isValidEnum(GameMode.class, config.getString(PREFIX_STATE_PERSISTENCE + "oldGameMode"))) {
            oldGameMode = GameMode.valueOf(config.getString(PREFIX_STATE_PERSISTENCE + "oldGameMode"));
        } else {
            oldGameMode = GameMode.SURVIVAL;
        }
        oldPotionEffects = (Collection<PotionEffect>) config.get(PREFIX_STATE_PERSISTENCE + "oldPotionEffects");

        oldLocation = (Location) config.get(PREFIX_STATE_PERSISTENCE + "oldLocation");
        if (oldLocation.getWorld() == null) {
            oldLocation = Bukkit.getWorlds().get(0).getSpawnLocation();
        }
    }

    @Override
    public void save() {
        config.set(PREFIX_STATS + "timeLastPlayed", timeLastPlayed);
        super.save();
    }

    /**
     * Saves the player's data to the file.
     *
     * @param player
     * the Player to save
     */
    public void savePlayerState(Player player) {
        oldGameMode = player.getGameMode();
        oldFireTicks = player.getFireTicks();
        oldFoodLevel = player.getFoodLevel();
        oldMaxHealth = player.getMaxHealth();
        oldHealth = player.getHealth();
        oldExp = player.getExp();
        oldLvl = player.getLevel();
        oldArmor = new ArrayList<>(Arrays.asList(player.getInventory().getArmorContents()));
        oldInventory = new ArrayList<>(Arrays.asList(player.getInventory().getContents()));
        if (is1_9) {
            oldOffHand = player.getInventory().getItemInOffHand();
        }
        oldLocation = player.getLocation();
        oldPotionEffects = player.getActivePotionEffects();

        config.set(PREFIX_STATE_PERSISTENCE + "oldGameMode", oldGameMode.toString());
        config.set(PREFIX_STATE_PERSISTENCE + "oldFireTicks", oldFireTicks);
        config.set(PREFIX_STATE_PERSISTENCE + "oldFoodLevel", oldFoodLevel);
        config.set(PREFIX_STATE_PERSISTENCE + "oldMaxHealth", oldMaxHealth);
        config.set(PREFIX_STATE_PERSISTENCE + "oldHealth", oldHealth);
        config.set(PREFIX_STATE_PERSISTENCE + "oldExp", oldExp);
        config.set(PREFIX_STATE_PERSISTENCE + "oldLvl", oldLvl);
        config.set(PREFIX_STATE_PERSISTENCE + "oldArmor", oldArmor);
        config.set(PREFIX_STATE_PERSISTENCE + "oldInventory", oldInventory);
        config.set(PREFIX_STATE_PERSISTENCE + "oldOffHand", oldOffHand);
        config.set(PREFIX_STATE_PERSISTENCE + "oldLocation", oldLocation);
        config.set(PREFIX_STATE_PERSISTENCE + "oldPotionEffects", oldPotionEffects);

        save();
    }

    /**
     * Removes the state data from the file
     */
    public void clearPlayerState() {
        oldGameMode = null;
        oldFireTicks = 0;
        oldFoodLevel = 0;
        oldMaxHealth = 20;
        oldHealth = 0;
        oldExp = 0;
        oldLvl = 0;
        oldArmor = null;
        oldInventory = null;
        oldOffHand = null;
        oldLocation = null;
        oldPotionEffects = null;

        if (wasInGame()) {
            config.set("savePlayer", null);
        }
        save();
    }

}
