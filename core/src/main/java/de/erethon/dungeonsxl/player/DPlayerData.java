/*
 * Copyright (C) 2012-2022 Frank Baumann
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
package de.erethon.dungeonsxl.player;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import de.erethon.bedrock.chat.MessageUtil;
import de.erethon.bedrock.compatibility.Internals;
import de.erethon.bedrock.compatibility.Version;
import de.erethon.bedrock.config.ConfigUtil;
import de.erethon.bedrock.config.EConfig;
import de.erethon.bedrock.plugin.EPlugin;
import de.erethon.bedrock.misc.EnumUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

/**
 * Represents a player's persistent data.
 *
 * @author Daniel Saukel
 */
public class DPlayerData extends EConfig {

    protected boolean is1_9 = Version.isAtLeast(Version.MC1_9);

    public static final int CONFIG_VERSION = 4;

    public static final String PREFIX_STATE_PERSISTENCE = "savePlayer.";
    public static final String PREFIX_STATS = "stats.";

    // State persistence
    private boolean keepInventoryAfterLogout = true;
    private Location oldLocation;
    private List<ItemStack> oldInventory;
    private List<ItemStack> oldArmor;
    private ItemStack oldOffHand;
    private int oldLvl;
    private float oldExp;
    private double oldHealth;
    private int oldFoodLevel;
    private int oldFireTicks;
    private GameMode oldGameMode;
    private Collection<PotionEffect> oldPotionEffects;
    private Map/*<Attribute, Double>*/ oldAttributeBases;
    private Multimap/*<Attribute, AttributeModifier>*/ oldAttributeMods;
    private boolean oldCollidabilityState;
    private boolean oldFlyingState;
    private boolean oldInvulnerabilityState;

    // Stats
    private Map<String, Long> timeLastStarted = new HashMap<>();
    private Map<String, Long> timeLastFinished = new HashMap<>();
    private Map<String, Long> timeLastLoot = new HashMap<>();
    private boolean finishedTutorial;

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
     * @return if the inventory shall be reset after a logout
     */
    public boolean getKeepInventoryAfterLogout() {
        return keepInventoryAfterLogout;
    }

    /**
     * @param keepInventoryAfterLogout set if the inventory shall be reset after a logout
     */
    public void setKeepInventoryAfterLogout(boolean keepInventoryAfterLogout) {
        this.keepInventoryAfterLogout = keepInventoryAfterLogout;
        config.set(PREFIX_STATE_PERSISTENCE + "keepInventoryAfterLogout", keepInventoryAfterLogout);
        super.save();
    }

    /**
     * @return the old location
     */
    public Location getOldLocation() {
        if (oldLocation.getWorld() == null) {
            return Bukkit.getWorlds().get(0).getSpawnLocation();
        }
        return oldLocation;
    }

    /**
     * @param location the location to set
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
     * @param inventory the inventory to set
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
     * @param inventory the inventory to set
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
     * @param offHand the off hand item to set
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
     * @param level the level to set
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
     * @param exp the amount of exp to set
     */
    public void setOldExp(float exp) {
        oldExp = exp;
    }

    /**
     * @return the old health
     */
    public double getOldHealth() {
        return oldHealth;
    }

    /**
     * @param health the health to set
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
     * @param foodLevel the food level to set
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
     * @param fireTicks the fire ticks to set
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
     * @param gameMode the GameMode to set
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
     * @param potionEffects the potion effects to set
     */
    public void setOldPotionEffects(Collection<PotionEffect> potionEffects) {
        oldPotionEffects = potionEffects;
    }

    /**
     * @return the old attribute bases
     */
    public Map/*<Attribute, Double>*/ getOldAttributeBases() {
        return oldAttributeBases;
    }

    /**
     * @param attributeBases the old attribute bases to set
     */
    public void setOldAttributeBases(Map/*<Attribute, Double>*/ attributeBases) {
        oldAttributeBases = attributeBases;
    }

    /**
     * @return the old attribute modifiers
     */
    public Multimap/*<Attribute, AttributeModifier>*/ getOldAttributeMods() {
        return oldAttributeMods;
    }

    /**
     * @param attributeMods the old attribute modifiers to set
     */
    public void setOldAttributeMods(Multimap/*<Attribute, AttributeModifier>*/ attributeMods) {
        oldAttributeMods = attributeMods;
    }

    /**
     * @return if the player was collidable
     */
    public boolean getOldCollidabilityState() {
        return oldCollidabilityState;
    }

    /**
     * @param collidableState the collidable state to set
     */
    public void setOldCollidabilityState(boolean collidableState) {
        oldCollidabilityState = collidableState;
    }

    /**
     * @return if the player was flying
     */
    public boolean getOldFlyingState() {
        return oldFlyingState;
    }

    /**
     * @param flyingState the flying state to set
     */
    public void setOldFlyingState(boolean flyingState) {
        oldFlyingState = flyingState;
    }

    /**
     * @return if the player was invulnerable
     */
    public boolean getOldInvulnerabilityState() {
        return oldInvulnerabilityState;
    }

    /**
     * @param invulnerabilityState the invulnerability state to set
     */
    public void setOldInvulnerabilityState(boolean invulnerabilityState) {
        oldFlyingState = invulnerabilityState;
    }

    /**
     * @return a map of the player's started dungeons with dates.
     */
    public Map<String, Long> getTimeLastStarted() {
        return timeLastStarted;
    }

    /**
     * @param dungeon the dungeon to check
     * @return the time when the player started the dungeon for the last time
     */
    public long getTimeLastStarted(String dungeon) {
        Long time = timeLastStarted.get(dungeon.toLowerCase());
        if (time == null) {
            return -1;
        } else {
            return time;
        }
    }

    /**
     * @param dungeon the started dungeon
     * @param time    the time when the dungeon was started
     */
    public void setTimeLastStarted(String dungeon, long time) {
        timeLastStarted.put(dungeon.toLowerCase(), time);
        save();
    }

    /**
     * @return a map of the player's finished dungeons with dates.
     */
    public Map<String, Long> getTimeLastFinished() {
        return timeLastFinished;
    }

    /**
     * @param dungeon the dungeon to check
     * @return the time when the player finished the dungeon for the last time
     */
    public long getTimeLastFinished(String dungeon) {
        Long time = timeLastFinished.get(dungeon.toLowerCase());
        if (time == null) {
            return -1;
        } else {
            return time;
        }
    }

    /**
     * @param dungeon the finished dungeon
     * @param time    the time when the dungeon was finished
     */
    public void setTimeLastFinished(String dungeon, long time) {
        timeLastFinished.put(dungeon.toLowerCase(), time);
        save();
    }

    /**
     * @param dungeon the dungeon to check
     * @return the time when the player received loot from the dungeon for the last time
     */
    public long getTimeLastLoot(String dungeon) {
        Long time = timeLastLoot.get(dungeon.toLowerCase());
        if (time == null) {
            return -1;
        } else {
            return time;
        }
    }

    /**
     * @param dungeon the finished dungeon
     * @param time    the time when the dungeon was received
     */
    public void setTimeLastLoot(String dungeon, long time) {
        timeLastLoot.put(dungeon.toLowerCase(), time);
        save();
    }

    /**
     * @return if the player has finished the tutorial
     */
    public boolean hasFinishedTutorial() {
        return finishedTutorial;
    }

    /**
     * @param finishedTutorial if the player has finished the tutorial
     */
    public void setFinishedTutorial(boolean finishedTutorial) {
        this.finishedTutorial = finishedTutorial;
        save();
    }

    /* Actions */
    /**
     * @param dungeon the started dungeon
     */
    public void logTimeLastStarted(String dungeon) {
        timeLastStarted.put(dungeon.toLowerCase(), System.currentTimeMillis());
        save();
    }

    /**
     * @param dungeon the finished dungeon
     */
    public void logTimeLastFinished(String dungeon) {
        timeLastFinished.put(dungeon.toLowerCase(), System.currentTimeMillis());
        save();
    }

    /**
     * @param dungeon the finished dungeon
     */
    public void logTimeLastLoot(String dungeon) {
        timeLastLoot.put(dungeon.toLowerCase(), System.currentTimeMillis());
        save();
    }

    @Override
    public void initialize() {
        if (!config.contains(PREFIX_STATS + "timeLastStarted")) {
            config.createSection(PREFIX_STATS + "timeLastStarted");
        }

        if (!config.contains(PREFIX_STATS + "timeLastFinished")) {
            config.createSection(PREFIX_STATS + "timeLastFinished");
        }

        if (!config.contains(PREFIX_STATS + "timeLastLoot")) {
            config.createSection(PREFIX_STATS + "timeLastLoot");
        }

        if (!config.contains(PREFIX_STATS + "finishedTutorial")) {
            config.set(PREFIX_STATS + "finishedTutorial", finishedTutorial);
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
                MessageUtil.log(EPlugin.getInstance(), "&6A new player data file has been created and saved as " + file.getName());
            } catch (IOException exception) {
            }
        }

        save();
    }

    @Override
    public void load() {
        if (config.isConfigurationSection(PREFIX_STATS + "timeLastStarted")) {
            for (String key : config.getConfigurationSection(PREFIX_STATS + "timeLastStarted").getKeys(false)) {
                timeLastStarted.put(key, config.getLong(PREFIX_STATS + "timeLastStarted." + key));
            }
        }

        if (config.isConfigurationSection(PREFIX_STATS + "timeLastFinished")) {
            for (String key : config.getConfigurationSection(PREFIX_STATS + "timeLastFinished").getKeys(false)) {
                timeLastFinished.put(key, config.getLong(PREFIX_STATS + "timeLastFinished." + key));
            }
        }

        if (config.isConfigurationSection(PREFIX_STATS + "timeLastLoot")) {
            for (String key : config.getConfigurationSection(PREFIX_STATS + "timeLastLoot").getKeys(false)) {
                timeLastLoot.put(key, config.getLong(PREFIX_STATS + "timeLastLoot." + key));
            }
        }

        finishedTutorial = config.getBoolean(PREFIX_STATS + "finishedTutorial", finishedTutorial);

        if (!wasInGame()) {
            return;
        }

        keepInventoryAfterLogout = config.getBoolean(PREFIX_STATE_PERSISTENCE + "keepInventoryAfterLogout");
        oldInventory = (List<ItemStack>) config.get(PREFIX_STATE_PERSISTENCE + "oldInventory");
        oldArmor = (List<ItemStack>) config.get(PREFIX_STATE_PERSISTENCE + "oldArmor");
        oldOffHand = (ItemStack) config.get(PREFIX_STATE_PERSISTENCE + "oldOffHand");

        oldLvl = config.getInt(PREFIX_STATE_PERSISTENCE + "oldLvl");
        oldExp = config.getInt(PREFIX_STATE_PERSISTENCE + "oldExp");
        oldHealth = config.getDouble(PREFIX_STATE_PERSISTENCE + "oldHealth");
        oldFoodLevel = config.getInt(PREFIX_STATE_PERSISTENCE + "oldFoodLevel");
        oldFireTicks = config.getInt(PREFIX_STATE_PERSISTENCE + "oldFireTicks");

        if (EnumUtil.isValidEnum(GameMode.class, config.getString(PREFIX_STATE_PERSISTENCE + "oldGameMode"))) {
            oldGameMode = GameMode.valueOf(config.getString(PREFIX_STATE_PERSISTENCE + "oldGameMode"));
        } else {
            oldGameMode = GameMode.SURVIVAL;
        }
        oldPotionEffects = (Collection<PotionEffect>) config.get(PREFIX_STATE_PERSISTENCE + "oldPotionEffects");

        if (is1_9) {
            Map<String, Object> basesMap = ConfigUtil.getMap(config, PREFIX_STATE_PERSISTENCE + "oldAttributeBases", false);
            if (basesMap != null) {
                oldAttributeBases = new HashMap<>();
                for (Entry<String, Object> entry : basesMap.entrySet()) {
                    if (!(entry.getValue() instanceof Double)) {
                        continue;
                    }
                    Attribute attribute = EnumUtil.getEnum(Attribute.class, entry.getKey());
                    Double base = (Double) entry.getValue();
                    oldAttributeBases.put(attribute, base);
                }
            }

            Map<String, Object> modsMap = ConfigUtil.getMap(config, PREFIX_STATE_PERSISTENCE + "oldAttributeMods", false);
            if (modsMap != null) {
                oldAttributeMods = HashMultimap.create();
                for (Entry<String, Object> entry : modsMap.entrySet()) {
                    if (!(entry.getValue() instanceof Collection)) {
                        continue;
                    }
                    Attribute attribute = EnumUtil.getEnum(Attribute.class, entry.getKey());
                    Collection<AttributeModifier> mods = (Collection<AttributeModifier>) entry.getValue();
                    oldAttributeMods.putAll(attribute, mods);
                }
            }
        }

        try {
            oldLocation = (Location) config.get(PREFIX_STATE_PERSISTENCE + "oldLocation");
        } catch (IllegalArgumentException exception) {
            oldLocation = Bukkit.getWorlds().get(0).getSpawnLocation();
        }

        oldCollidabilityState = config.getBoolean(PREFIX_STATE_PERSISTENCE + "oldCollidabilityState", true);
        oldFlyingState = config.getBoolean(PREFIX_STATE_PERSISTENCE + "oldFlyingState", false);
        oldInvulnerabilityState = config.getBoolean(PREFIX_STATE_PERSISTENCE + "oldInvulnerabilityState", false);
    }

    @Override
    public void save() {
        config.set(PREFIX_STATS + "timeLastStarted", timeLastStarted);
        config.set(PREFIX_STATS + "timeLastFinished", timeLastFinished);
        config.set(PREFIX_STATS + "timeLastLoot", timeLastLoot);
        config.set(PREFIX_STATS + "finishedTutorial", finishedTutorial);
        super.save();
    }

    /**
     * Saves the player's data to the file.
     *
     * @param player the Player to save
     */
    public void savePlayerState(Player player) {
        oldGameMode = player.getGameMode();
        oldFireTicks = player.getFireTicks();
        oldFoodLevel = player.getFoodLevel();
        oldHealth = player.getHealth();
        oldExp = player.getExp();
        oldLvl = player.getLevel();
        PlayerInventory inv = player.getInventory();
        oldArmor = new ArrayList<>(4);
        for (int i = 0; i < 4; i++) {
            ItemStack itemStack = inv.getArmorContents()[i];
            oldArmor.add(itemStack != null ? itemStack.clone() : null);
        }
        oldInventory = new ArrayList<>(36);
        for (int i = 0; i < 36; i++) {
            ItemStack itemStack = inv.getContents()[i];
            oldInventory.add(itemStack != null ? itemStack.clone() : null);
        }
        if (is1_9) {
            oldOffHand = inv.getItemInOffHand().clone();
        }
        oldLocation = player.getLocation();
        oldPotionEffects = player.getActivePotionEffects();
        if (is1_9) {
            oldAttributeBases = new HashMap<>();
            oldAttributeMods = HashMultimap.create();
            for (Attribute attribute : Attribute.values()) {
                AttributeInstance instance = player.getAttribute(attribute);
                if (instance == null) {
                    continue;
                }
                oldAttributeBases.put(attribute, instance.getBaseValue());
                for (AttributeModifier mod : instance.getModifiers()) {
                    oldAttributeMods.put(attribute, mod);
                }
            }
            oldCollidabilityState = player.isCollidable();
            oldInvulnerabilityState = player.isInvulnerable();
        }
        oldFlyingState = player.getAllowFlight();

        config.set(PREFIX_STATE_PERSISTENCE + "oldGameMode", oldGameMode.toString());
        config.set(PREFIX_STATE_PERSISTENCE + "oldFireTicks", oldFireTicks);
        config.set(PREFIX_STATE_PERSISTENCE + "oldFoodLevel", oldFoodLevel);
        config.set(PREFIX_STATE_PERSISTENCE + "oldHealth", oldHealth);
        config.set(PREFIX_STATE_PERSISTENCE + "oldExp", oldExp);
        config.set(PREFIX_STATE_PERSISTENCE + "oldLvl", oldLvl);
        config.set(PREFIX_STATE_PERSISTENCE + "oldArmor", oldArmor);
        config.set(PREFIX_STATE_PERSISTENCE + "oldInventory", oldInventory);
        config.set(PREFIX_STATE_PERSISTENCE + "oldOffHand", oldOffHand);
        config.set(PREFIX_STATE_PERSISTENCE + "oldLocation", oldLocation);
        config.set(PREFIX_STATE_PERSISTENCE + "oldPotionEffects", oldPotionEffects);
        if (is1_9) {
            for (Object object : oldAttributeBases.entrySet()) {
                Entry<Attribute, Double> entry = (Entry<Attribute, Double>) object;
                config.set(PREFIX_STATE_PERSISTENCE + "oldAttributeBases." + entry.getKey().name(), entry.getValue());
            }
            for (Object object : oldAttributeMods.asMap().entrySet()) {
                Entry<Attribute, Collection<AttributeModifier>> entry = (Entry<Attribute, Collection<AttributeModifier>>) object;
                config.set(PREFIX_STATE_PERSISTENCE + "oldAttributeMods." + entry.getKey().name(), entry.getValue());
            }
        }
        config.set(PREFIX_STATE_PERSISTENCE + "oldCollidabilityState", oldCollidabilityState);
        config.set(PREFIX_STATE_PERSISTENCE + "oldFlyingState", oldFlyingState);
        config.set(PREFIX_STATE_PERSISTENCE + "oldInvulnerabilityState", oldInvulnerabilityState);

        save();
    }

    /**
     * Removes the state data from the file
     */
    public void clearPlayerState() {
        oldGameMode = null;
        oldFireTicks = 0;
        oldFoodLevel = 0;
        oldHealth = 0;
        oldExp = 0;
        oldLvl = 0;
        oldArmor = null;
        oldInventory = null;
        oldOffHand = null;
        oldLocation = null;
        oldPotionEffects = null;
        oldAttributeBases = null;
        oldAttributeMods = null;
        oldCollidabilityState = true;
        oldFlyingState = false;
        oldInvulnerabilityState = false;

        if (wasInGame()) {
            config.set("savePlayer", null);
        }
        save();
    }

}
