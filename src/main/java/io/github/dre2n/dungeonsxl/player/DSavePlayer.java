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
package io.github.dre2n.dungeonsxl.player;

import io.github.dre2n.commons.compatibility.CompatibilityHandler;
import io.github.dre2n.commons.compatibility.Version;
import io.github.dre2n.commons.util.EnumUtil;
import io.github.dre2n.commons.util.playerutil.PlayerUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

/**
 * Represents a player in a GameWorld who went offline.
 *
 * @author Frank Baumann, Tobias Schmitz, Milan Albrecht, Daniel Saukel
 */
public class DSavePlayer {

    static DungeonsXL plugin = DungeonsXL.getInstance();
    static DPlayers dPlayers = plugin.getDPlayers();

    // Variables
    private String name;
    private String uuid;

    private Location oldLocation;
    private List<ItemStack> oldInventory;
    private List<ItemStack> oldArmor;
    private ItemStack oldOffHand;
    private int oldLvl;
    private int oldExp;
    private int oldHealth;
    private int oldFoodLevel;
    private int oldFireTicks;
    private GameMode oldGameMode;
    private Collection<PotionEffect> oldPotionEffects;

    public DSavePlayer(String name, UUID uuid, Location oldLocation, ArrayList<ItemStack> oldInventory, ArrayList<ItemStack> oldArmor, ItemStack oldOffHand, int oldLvl, int oldExp, int oldHealth, int oldFoodLevel, int oldFireTicks,
            GameMode oldGameMode, Collection<PotionEffect> oldPotionEffects) {
        this.name = name;
        this.uuid = uuid.toString();

        this.oldLocation = oldLocation;
        this.oldInventory = oldInventory;
        this.oldArmor = oldArmor;
        this.oldOffHand = oldOffHand;
        this.oldExp = oldExp;
        this.oldHealth = oldHealth;
        this.oldFoodLevel = oldFoodLevel;
        this.oldGameMode = oldGameMode;
        this.oldLvl = oldLvl;
        this.oldFireTicks = oldFireTicks;
        this.oldPotionEffects = oldPotionEffects;

        save();
        dPlayers.addDSavePlayer(this);
    }

    public DSavePlayer(String name, UUID uuid, Location oldLocation, ItemStack[] oldInventory, ItemStack[] oldArmor, ItemStack oldOffHand, int oldLvl, int oldExp, int oldHealth, int oldFoodLevel, int oldFireTicks,
            GameMode oldGameMode, Collection<PotionEffect> oldPotionEffects) {
        this(name, uuid, oldLocation, new ArrayList<>(Arrays.asList(oldInventory)), new ArrayList<>(Arrays.asList(oldArmor)), oldOffHand, oldLvl, oldExp, oldHealth, oldFoodLevel, oldFireTicks, oldGameMode, oldPotionEffects);
    }

    /* Getters and setters */
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the uuid
     */
    public UUID getUniqueId() {
        return UUID.fromString(uuid);
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
    public int getOldExp() {
        return oldExp;
    }

    /**
     * @param exp
     * the amount of exp to set
     */
    public void setOldExp(int exp) {
        oldExp = exp;
    }

    /**
     * @return the old health
     */
    public int getOldHealth() {
        return oldHealth;
    }

    /**
     * @param health
     * the health to set
     */
    public void setOldHealth(int health) {
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

    /* Actions */
    public void reset(boolean keepInventory) {
        Player player = plugin.getServer().getPlayer(name);
        boolean offline = false;
        if (player == null) {
            player = PlayerUtil.getOfflinePlayer(name, UUID.fromString(uuid), oldLocation);
            offline = true;
        }
        if (player == null) {
            return;
        }

        try {
            if (!keepInventory) {
                while (oldInventory.size() > 36) {
                    oldInventory.remove(36);
                }
                player.getInventory().setContents(oldInventory.toArray(new ItemStack[36]));
                player.getInventory().setArmorContents(oldArmor.toArray(new ItemStack[4]));
                if (Version.andHigher(Version.MC1_9).contains(CompatibilityHandler.getInstance().getVersion())) {
                    player.getInventory().setItemInOffHand(oldOffHand);
                }
                player.setTotalExperience(oldExp);
                player.setLevel(oldLvl);
                player.setHealth(oldHealth);
                player.setFoodLevel(oldFoodLevel);
                player.setGameMode(oldGameMode);
                player.setFireTicks(oldFireTicks);
                for (PotionEffect effect : player.getActivePotionEffects()) {
                    player.removePotionEffect(effect.getType());
                }
                // Causes NPE if offline
                if (!offline) {
                    player.addPotionEffects(oldPotionEffects);

                } else {
                    player.saveData();
                }
            }

            if (!offline && oldLocation.getWorld() != null) {
                PlayerUtil.secureTeleport(player, oldLocation);
            } else {
                PlayerUtil.secureTeleport(player, Bukkit.getWorlds().get(0).getSpawnLocation());
            }

        } catch (NullPointerException exception) {
            plugin.getLogger().info("Corrupted playerdata detected and removed!");
        }

        save();
        dPlayers.removeDSavePlayer(this);
    }

    /* Statics */
    @Deprecated
    public static void save() {
        FileConfiguration configFile = new YamlConfiguration();

        for (DSavePlayer savePlayer : dPlayers.getDSavePlayers()) {
            configFile.set(savePlayer.name + ".uuid", savePlayer.uuid);
            configFile.set(savePlayer.name + ".oldGameMode", savePlayer.oldGameMode.toString());
            configFile.set(savePlayer.name + ".oldFireTicks", savePlayer.oldFireTicks);
            configFile.set(savePlayer.name + ".oldFoodLevel", savePlayer.oldFoodLevel);
            configFile.set(savePlayer.name + ".oldHealth", savePlayer.oldHealth);
            configFile.set(savePlayer.name + ".oldExp", savePlayer.oldExp);
            configFile.set(savePlayer.name + ".oldLvl", savePlayer.oldLvl);
            configFile.set(savePlayer.name + ".oldArmor", savePlayer.oldArmor);
            configFile.set(savePlayer.name + ".oldInventory", savePlayer.oldInventory);
            configFile.set(savePlayer.name + ".oldOffHand", savePlayer.oldOffHand);
            configFile.set(savePlayer.name + ".oldLocation.x", savePlayer.oldLocation.getX());
            configFile.set(savePlayer.name + ".oldLocation.y", savePlayer.oldLocation.getY());
            configFile.set(savePlayer.name + ".oldLocation.z", savePlayer.oldLocation.getZ());
            configFile.set(savePlayer.name + ".oldLocation.yaw", savePlayer.oldLocation.getYaw());
            configFile.set(savePlayer.name + ".oldLocation.pitch", savePlayer.oldLocation.getPitch());
            configFile.set(savePlayer.name + ".oldLocation.world", savePlayer.oldLocation.getWorld().getName());
            configFile.set(savePlayer.name + ".oldPotionEffects", savePlayer.oldPotionEffects);
        }

        try {
            configFile.save(new File(plugin.getDataFolder(), "savePlayers.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    public static void load() {
        FileConfiguration configFile = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "savePlayers.yml"));

        for (String name : configFile.getKeys(false)) {
            // Load uuid
            UUID uuid = UUID.fromString(configFile.getString(name + ".uuid"));

            // Load inventory data
            ArrayList<ItemStack> oldInventory = (ArrayList<ItemStack>) configFile.get(name + ".oldInventory");
            ArrayList<ItemStack> oldArmor = (ArrayList<ItemStack>) configFile.get(name + ".oldArmor");
            ItemStack oldOffHand = (ItemStack) configFile.get(name + ".oldOffHand");

            // Load other data
            int oldLvl = configFile.getInt(name + ".oldLvl");
            int oldExp = configFile.getInt(name + ".oldExp");
            int oldHealth = configFile.getInt(name + ".oldHealth");
            int oldFoodLevel = configFile.getInt(name + ".oldFoodLevel");
            int oldFireTicks = configFile.getInt(name + ".oldFireTicks");
            GameMode oldGameMode = GameMode.SURVIVAL;
            if (EnumUtil.isValidEnum(GameMode.class, configFile.getString(name + ".oldGameMode"))) {
                oldGameMode = GameMode.valueOf(configFile.getString(name + ".oldGameMode"));
            }
            Collection<PotionEffect> oldPotionEffects = (Collection<PotionEffect>) configFile.get(name + ".oldPotionEffects");

            // Location
            World world = plugin.getServer().getWorld(configFile.getString(name + ".oldLocation.world"));
            if (world == null) {
                world = plugin.getServer().getWorlds().get(0);
            }

            Location oldLocation = new Location(world, configFile.getDouble(name + ".oldLocation.x"), configFile.getDouble(name + ".oldLocation.y"), configFile.getDouble(name
                    + ".oldLocation.z"), configFile.getInt(name + ".oldLocation.yaw"), configFile.getInt(name + ".oldLocation.pitch"));

            // Create Player
            DSavePlayer savePlayer = new DSavePlayer(name, uuid, oldLocation, oldInventory, oldArmor, oldOffHand, oldLvl, oldExp, oldHealth, oldFoodLevel, oldFireTicks, oldGameMode, oldPotionEffects);
            savePlayer.reset(false);
        }
    }

}
