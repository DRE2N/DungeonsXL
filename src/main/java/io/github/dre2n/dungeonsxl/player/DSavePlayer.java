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
import java.util.UUID;
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

    protected static DungeonsXL plugin = DungeonsXL.getInstance();
    protected static DPlayers dPlayers = plugin.getDPlayers();

    // Variables
    private String playerName;
    private String uuid;

    private Location oldLocation;
    private ArrayList<ItemStack> oldInventory;
    private ArrayList<ItemStack> oldArmor;
    private ItemStack oldOffHand;
    private int oldLvl;
    private int oldExp;
    private int oldHealth;
    private int oldFoodLevel;
    private int oldFireTicks;
    private GameMode oldGameMode;
    private Collection<PotionEffect> oldPotionEffects;

    public DSavePlayer(String playerName, UUID uuid, Location oldLocation, ArrayList<ItemStack> oldInventory, ArrayList<ItemStack> oldArmor, ItemStack oldOffHand, int oldLvl, int oldExp, int oldHealth, int oldFoodLevel, int oldFireTicks,
            GameMode oldGameMode, Collection<PotionEffect> oldPotionEffects) {
        this.playerName = playerName;
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

    public DSavePlayer(String playerName, UUID uuid, Location oldLocation, ItemStack[] oldInventory, ItemStack[] oldArmor, ItemStack oldOffHand, int oldLvl, int oldExp, int oldHealth, int oldFoodLevel, int oldFireTicks,
            GameMode oldGameMode, Collection<PotionEffect> oldPotionEffects) {
        this(playerName, uuid, oldLocation, new ArrayList<>(Arrays.asList(oldInventory)), new ArrayList<>(Arrays.asList(oldArmor)), oldOffHand, oldLvl, oldExp, oldHealth, oldFoodLevel, oldFireTicks, oldGameMode, oldPotionEffects);
    }

    public void reset(boolean keepInventory) {
        Player player = plugin.getServer().getPlayer(playerName);
        boolean offline = false;
        if (player == null) {
            player = PlayerUtil.getOfflinePlayer(playerName, UUID.fromString(uuid), oldLocation);
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
            configFile.set(savePlayer.playerName + ".uuid", savePlayer.uuid);
            configFile.set(savePlayer.playerName + ".oldGameMode", savePlayer.oldGameMode.toString());
            configFile.set(savePlayer.playerName + ".oldFireTicks", savePlayer.oldFireTicks);
            configFile.set(savePlayer.playerName + ".oldFoodLevel", savePlayer.oldFoodLevel);
            configFile.set(savePlayer.playerName + ".oldHealth", savePlayer.oldHealth);
            configFile.set(savePlayer.playerName + ".oldExp", savePlayer.oldExp);
            configFile.set(savePlayer.playerName + ".oldLvl", savePlayer.oldLvl);
            configFile.set(savePlayer.playerName + ".oldArmor", savePlayer.oldArmor);
            configFile.set(savePlayer.playerName + ".oldInventory", savePlayer.oldInventory);
            configFile.set(savePlayer.playerName + ".oldOffHand", savePlayer.oldOffHand);
            configFile.set(savePlayer.playerName + ".oldLocation.x", savePlayer.oldLocation.getX());
            configFile.set(savePlayer.playerName + ".oldLocation.y", savePlayer.oldLocation.getY());
            configFile.set(savePlayer.playerName + ".oldLocation.z", savePlayer.oldLocation.getZ());
            configFile.set(savePlayer.playerName + ".oldLocation.yaw", savePlayer.oldLocation.getYaw());
            configFile.set(savePlayer.playerName + ".oldLocation.pitch", savePlayer.oldLocation.getPitch());
            configFile.set(savePlayer.playerName + ".oldLocation.world", savePlayer.oldLocation.getWorld().getName());
            configFile.set(savePlayer.playerName + ".oldPotionEffects", savePlayer.oldPotionEffects);
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

        for (String playerName : configFile.getKeys(false)) {
            // Load uuid
            UUID uuid = UUID.fromString(configFile.getString(playerName + ".uuid"));

            // Load inventory data
            ArrayList<ItemStack> oldInventory = (ArrayList<ItemStack>) configFile.get(playerName + ".oldInventory");
            ArrayList<ItemStack> oldArmor = (ArrayList<ItemStack>) configFile.get(playerName + ".oldArmor");
            ItemStack oldOffHand = (ItemStack) configFile.get(playerName + ".oldOffHand");

            // Load other data
            int oldLvl = configFile.getInt(playerName + ".oldLvl");
            int oldExp = configFile.getInt(playerName + ".oldExp");
            int oldHealth = configFile.getInt(playerName + ".oldHealth");
            int oldFoodLevel = configFile.getInt(playerName + ".oldFoodLevel");
            int oldFireTicks = configFile.getInt(playerName + ".oldFireTicks");
            GameMode oldGameMode = GameMode.SURVIVAL;
            if (EnumUtil.isValidEnum(GameMode.class, configFile.getString(playerName + ".oldGameMode"))) {
                oldGameMode = GameMode.valueOf(configFile.getString(playerName + ".oldGameMode"));
            }
            Collection<PotionEffect> oldPotionEffects = (Collection<PotionEffect>) configFile.get(playerName + ".oldPotionEffects");

            // Location
            World world = plugin.getServer().getWorld(configFile.getString(playerName + ".oldLocation.world"));
            if (world == null) {
                world = plugin.getServer().getWorlds().get(0);
            }

            Location oldLocation = new Location(world, configFile.getDouble(playerName + ".oldLocation.x"), configFile.getDouble(playerName + ".oldLocation.y"), configFile.getDouble(playerName
                    + ".oldLocation.z"), configFile.getInt(playerName + ".oldLocation.yaw"), configFile.getInt(playerName + ".oldLocation.pitch"));

            // Create Player
            DSavePlayer savePlayer = new DSavePlayer(playerName, uuid, oldLocation, oldInventory, oldArmor, oldOffHand, oldLvl, oldExp, oldHealth, oldFoodLevel, oldFireTicks, oldGameMode, oldPotionEffects);
            savePlayer.reset(false);
        }
    }

}
