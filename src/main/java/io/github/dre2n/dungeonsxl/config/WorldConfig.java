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
package io.github.dre2n.dungeonsxl.config;

import io.github.dre2n.commons.util.EnumUtil;
import io.github.dre2n.commons.util.NumberUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.game.GameType;
import io.github.dre2n.dungeonsxl.mob.DMobType;
import io.github.dre2n.dungeonsxl.player.DClass;
import io.github.dre2n.dungeonsxl.requirement.FeeLevelRequirement;
import io.github.dre2n.dungeonsxl.requirement.FeeMoneyRequirement;
import io.github.dre2n.dungeonsxl.requirement.Requirement;
import io.github.dre2n.dungeonsxl.reward.Reward;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

/**
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class WorldConfig {

    DungeonsXL plugin = DungeonsXL.getInstance();

    @Deprecated
    public static WorldConfig defaultConfig = new WorldConfig();

    private File file;

    private List<String> invitedPlayers = new ArrayList<>();

    private boolean keepInventory = false;
    private boolean keepInventoryOnEnter = false;
    private boolean keepInventoryOnEscape = false;
    private boolean keepInventoryOnFinish = false;
    private boolean keepInventoryOnDeath = true;

    private GameMode gameMode = GameMode.SURVIVAL;
    private boolean build = false;

    private boolean playerVersusPlayer = false;
    private boolean friendlyFire = false;

    private List<DClass> dClasses = new ArrayList<>();
    private Map<Integer, String> msgs = new HashMap<>();

    private List<Material> secureObjects = new ArrayList<>();

    private int initialLives = 3;

    private boolean isLobbyDisabled = false;
    private int timeToNextPlay = 0;
    private int timeToNextLoot = 0;
    private int timeToNextWave = 10;

    private int timeUntilKickOfflinePlayer = -1;
    private int timeToFinish = -1;

    private List<Requirement> requirements = new ArrayList<>();
    private List<Reward> rewards = new ArrayList<>();

    private List<String> finishedOne;
    private List<String> finishedAll;
    private int timeLastPlayed = 0;

    private GameType forcedGameType;

    private Set<DMobType> mobTypes = new HashSet<>();

    private List<String> gameCommandWhitelist = new ArrayList<>();
    private List<String> gamePermissions = new ArrayList<>();

    public WorldConfig() {
    }

    public WorldConfig(File file) {
        this.file = file;

        FileConfiguration configFile = YamlConfiguration.loadConfiguration(file);

        load(configFile);
    }

    public WorldConfig(ConfigurationSection configFile) {
        load(configFile);
    }

    // Load & Save
    public void load(ConfigurationSection configFile) {
        /* Classes */
        ConfigurationSection configSectionClasses = configFile.getConfigurationSection("classes");
        if (configSectionClasses != null) {
            Set<String> list = configSectionClasses.getKeys(false);
            for (String className : list) {
                String name = className;
                boolean hasDog = configSectionClasses.getBoolean(className + ".dog");
                /* Items */
                List<String> items = configSectionClasses.getStringList(className + ".items");
                CopyOnWriteArrayList<ItemStack> itemStacks = new CopyOnWriteArrayList<>();

                for (String item : items) {
                    String[] itemSplit = item.split(",");
                    if (itemSplit.length > 0) {
                        int itemId = 0, itemData = 0, itemSize = 1, itemLvlEnchantment = 1;
                        Enchantment itemEnchantment = null;
                        // Check Id & Data
                        String[] idAndData = itemSplit[0].split("/");
                        itemId = NumberUtil.parseInt(idAndData[0]);

                        if (idAndData.length > 1) {
                            itemData = NumberUtil.parseInt(idAndData[1]);
                        }

                        // Size
                        if (itemSplit.length > 1) {
                            itemSize = NumberUtil.parseInt(itemSplit[1]);
                        }
                        // Enchantment
                        if (itemSplit.length > 2) {
                            String[] enchantmentSplit = itemSplit[2].split("/");

                            itemEnchantment = Enchantment.getByName(enchantmentSplit[0]);

                            if (enchantmentSplit.length > 1) {
                                itemLvlEnchantment = NumberUtil.parseInt(enchantmentSplit[1]);
                            }
                        }

                        // Add Item to Stacks
                        ItemStack itemStack = new ItemStack(itemId, itemSize, (short) itemData);
                        if (itemEnchantment != null) {
                            itemStack.addEnchantment(itemEnchantment, itemLvlEnchantment);
                        }
                        itemStacks.add(itemStack);
                    }
                }

                /* Create Class */
                dClasses.add(new DClass(name, itemStacks, hasDog));
            }
        }

        /* Messages */
        ConfigurationSection configSectionMessages = configFile.getConfigurationSection("message");
        if (configSectionMessages != null) {
            Set<String> list = configSectionMessages.getKeys(false);
            for (String messagePath : list) {
                int messageId = NumberUtil.parseInt(messagePath);
                msgs.put(messageId, configSectionMessages.getString(messagePath));
            }
        }

        /* Secure Objects */
        if (configFile.contains("secureObjects")) {
            List<String> secureObjectList = configFile.getStringList("secureObjects");
            for (String id : secureObjectList) {
                if (Material.getMaterial(NumberUtil.parseInt(id)) != null) {
                    secureObjects.add(Material.getMaterial(NumberUtil.parseInt(id)));

                } else if (Material.getMaterial(id) != null) {
                    secureObjects.add(Material.getMaterial(id));
                }
            }
        }

        /* Invited Players */
        if (configFile.contains("invitedPlayers")) {
            invitedPlayers = configFile.getStringList("invitedPlayers");

        }

        /* Keep Inventory */
        if (configFile.contains("keepInventory")) {
            if (!configFile.contains("keepInventoryOnEnter")) {
                keepInventoryOnEnter = configFile.getBoolean("keepInventory");
            }
            if (!configFile.contains("keepInventoryOnEscape")) {
                keepInventoryOnEscape = configFile.getBoolean("keepInventory");
            }
            if (!configFile.contains("keepInventoryOnFinish")) {
                keepInventoryOnFinish = configFile.getBoolean("keepInventory");
            }
        } else if (plugin.getDefaultConfig().keepInventory) {
            keepInventoryOnEnter = plugin.getDefaultConfig().keepInventory;
            keepInventoryOnEscape = plugin.getDefaultConfig().keepInventory;
            keepInventoryOnFinish = plugin.getDefaultConfig().keepInventory;
        }

        if (configFile.contains("keepInventoryOnEnter")) {
            keepInventoryOnEnter = configFile.getBoolean("keepInventoryOnEnter");
        } else {
            keepInventoryOnEnter = plugin.getDefaultConfig().keepInventoryOnEnter;
        }

        if (configFile.contains("keepInventoryOnEscape")) {
            keepInventoryOnEscape = configFile.getBoolean("keepInventoryOnEscape");
        } else {
            keepInventoryOnEscape = plugin.getDefaultConfig().keepInventoryOnEscape;
        }

        if (configFile.contains("keepInventoryOnFinish")) {
            keepInventoryOnFinish = configFile.getBoolean("keepInventoryOnFinish");
        } else {
            keepInventoryOnFinish = plugin.getDefaultConfig().keepInventoryOnFinish;
        }

        if (configFile.contains("keepInventoryOnDeath")) {
            keepInventoryOnDeath = configFile.getBoolean("keepInventoryOnDeath");
        } else {
            keepInventoryOnDeath = plugin.getDefaultConfig().keepInventoryOnDeath;
        }

        /* Build */
        if (configFile.contains("build")) {
            build = configFile.getBoolean("build");
        } else {
            build = plugin.getDefaultConfig().build;
        }

        /* GameMode */
        if (configFile.contains("gameMode")) {
            if (EnumUtil.isValidEnum(GameMode.class, configFile.getString("gameMode").toUpperCase())) {
                gameMode = GameMode.valueOf(configFile.getString("gameMode"));
            } else {
                gameMode = GameMode.getByValue(configFile.getInt("gameMode"));
            }
        } else {
            gameMode = plugin.getDefaultConfig().gameMode;
        }

        /* PvP */
        if (configFile.contains("playerVersusPlayer")) {
            playerVersusPlayer = configFile.getBoolean("playerVersusPlayer");
        } else {
            playerVersusPlayer = plugin.getDefaultConfig().playerVersusPlayer;
        }

        /* Friendly fire */
        if (configFile.contains("friendlyFire")) {
            friendlyFire = configFile.getBoolean("friendlyFire");
        } else {
            friendlyFire = plugin.getDefaultConfig().friendlyFire;
        }

        /* Lives */
        if (configFile.contains("initialLives")) {
            initialLives = configFile.getInt("initialLives");
        } else {
            initialLives = plugin.getDefaultConfig().getInitialLives();
        }

        /* Lobby */
        if (configFile.contains("isLobbyDisabled")) {
            isLobbyDisabled = configFile.getBoolean("isLobbyDisabled");
        } else {
            isLobbyDisabled = plugin.getDefaultConfig().isLobbyDisabled;
        }

        /* Times */
        if (configFile.contains("timeToNextPlay")) {
            timeToNextPlay = configFile.getInt("timeToNextPlay");
        } else {
            timeToNextPlay = plugin.getDefaultConfig().timeToNextPlay;
        }

        if (configFile.contains("timeToNextLoot")) {
            timeToNextLoot = configFile.getInt("timeToNextLoot");
        } else {
            timeToNextLoot = plugin.getDefaultConfig().timeToNextLoot;
        }

        if (configFile.contains("timeToNextWave")) {
            timeToNextWave = configFile.getInt("timeToNextWave");
        } else {
            timeToNextWave = plugin.getDefaultConfig().timeToNextWave;
        }

        if (configFile.contains("timeUntilKickOfflinePlayer")) {
            timeUntilKickOfflinePlayer = configFile.getInt("timeUntilKickOfflinePlayer");
        } else {
            timeUntilKickOfflinePlayer = plugin.getDefaultConfig().timeUntilKickOfflinePlayer;
        }

        if (configFile.contains("timeToFinish")) {
            timeToFinish = configFile.getInt("timeToFinish");
        } else {
            timeToFinish = plugin.getDefaultConfig().timeToFinish;
        }

        /* Dungeon Requirements */
        if (configFile.contains("requirements")) {
            for (String identifier : configFile.getConfigurationSection("requirements").getKeys(false)) {
                Requirement requirement = Requirement.create(plugin.getRequirementTypes().getByIdentifier(identifier));

                // Check for built-in requirements
                if (requirement instanceof FeeMoneyRequirement) {
                    ((FeeMoneyRequirement) requirement).setFee(configFile.getDouble("requirements.feeMoney"));

                } else if (requirement instanceof FeeLevelRequirement) {
                    ((FeeLevelRequirement) requirement).setFee(configFile.getInt("requirements.feeLevel"));
                }

                requirements.add(requirement);
            }
        } else {
            requirements = plugin.getDefaultConfig().requirements;
        }

        if (configFile.contains("mustFinishOne")) {
            finishedOne = configFile.getStringList("mustFinishOne");
        } else {
            finishedOne = new ArrayList<>();
        }

        if (configFile.contains("mustFinishAll")) {
            finishedAll = configFile.getStringList("mustFinishAll");
        } else {
            finishedAll = new ArrayList<>();
        }

        if (configFile.contains("timeLastPlayed")) {
            timeLastPlayed = configFile.getInt("timeLastPlayed");
        }

        /* Mobtypes */
        configSectionMessages = configFile.getConfigurationSection("mobTypes");
        mobTypes = DMobType.load(configSectionMessages);

        if (configFile.contains("gameCommandWhitelist")) {
            gameCommandWhitelist = configFile.getStringList("gameCommandWhitelist");
        } else {
            gameCommandWhitelist = plugin.getDefaultConfig().gameCommandWhitelist;
        }

        if (configFile.contains("gamePermissions")) {
            gamePermissions = configFile.getStringList("gamePermissions");
        } else {
            gamePermissions = plugin.getDefaultConfig().gamePermissions;
        }

        if (configFile.contains("forcedGameType")) {
            GameType gameType = plugin.getGameTypes().getByName(configFile.getString("forcedGameType"));
            if (gameType != null) {
                forcedGameType = gameType;

            } else {
                forcedGameType = null;
            }
        }
    }

    public void save() {
        if (file == null) {
            return;
        }
        FileConfiguration configFile = YamlConfiguration.loadConfiguration(file);

        // Messages
        for (int msgs : this.msgs.keySet()) {
            configFile.set("message." + msgs, this.msgs.get(msgs));
        }

        // Secure Objects
        CopyOnWriteArrayList<Integer> secureObjectsids = new CopyOnWriteArrayList<>();

        for (Material mat : secureObjects) {
            secureObjectsids.add(mat.getId());
        }

        configFile.set("secureObjects", secureObjectsids);

        // Invited Players
        configFile.set("invitedPlayers", invitedPlayers);

        try {
            configFile.save(file);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    // Getters and Setters
    /**
     * @return the UUIDs or names of the players invited to edit the map
     */
    public CopyOnWriteArrayList<String> getInvitedPlayers() {
        CopyOnWriteArrayList<String> tmpInvitedPlayers = new CopyOnWriteArrayList<>();
        tmpInvitedPlayers.addAll(invitedPlayers);
        tmpInvitedPlayers.addAll(plugin.getDefaultConfig().invitedPlayers);
        return tmpInvitedPlayers;
    }

    /**
     * @param uuid
     * the player's unique ID
     */
    public void addInvitedPlayer(String uuid) {
        invitedPlayers.add(uuid);
    }

    /**
     * @param uuid
     * the player's unique ID
     * @param name
     * the player's name
     */
    public void removeInvitedPlayers(String uuid, String name) {
        invitedPlayers.remove(uuid);
        // remove player from a 0.9.1 and lower file
        invitedPlayers.remove(name);
    }

    /**
     * @return the classes
     */
    public List<DClass> getClasses() {
        if (dClasses != null) {
            if (!dClasses.isEmpty()) {
                return dClasses;
            }
        }

        return plugin.getDefaultConfig().dClasses;
    }

    /**
     * @param name
     * the name of the class
     */
    public DClass getClass(String name) {
        for (DClass dClass : dClasses) {
            if (dClass.getName().equals(name)) {
                return dClass;
            }
        }

        for (DClass dClass : plugin.getDefaultConfig().dClasses) {
            if (dClass.getName().equals(name)) {
                return dClass;
            }
        }
        return null;
    }

    /**
     * @param id
     * the id of the message
     * @param returnMainConfig
     * if a default value shall be returned
     */
    public String getMsg(int id, boolean returnMainConfig) {
        String msg = msgs.get(id);
        if (msg != null) {
            return msgs.get(id);
        }
        if (returnMainConfig) {
            return plugin.getDefaultConfig().msgs.get(id);
        }

        return null;
    }

    /**
     * @param msg
     * the message to set
     * @param id
     * the ID of the message
     */
    public void setMsg(String msg, int id) {
        msgs.put(id, msg);
    }

    /**
     * @return the objects to get passed to another player of the group when this player leaves
     */
    public CopyOnWriteArrayList<Material> getSecureObjects() {
        CopyOnWriteArrayList<Material> tmpSecureObjects = new CopyOnWriteArrayList<>();
        tmpSecureObjects.addAll(secureObjects);
        tmpSecureObjects.addAll(plugin.getDefaultConfig().secureObjects);
        return tmpSecureObjects;
    }

    /**
     * @return if the inventory shall be kept when the player enters the dungeon
     */
    public boolean getKeepInventoryOnEnter() {
        return keepInventoryOnEnter;
    }

    /**
     * @return if the inventory shall be kept when the player leaves the dungeon successlessly
     */
    public boolean getKeepInventoryOnEscape() {
        return keepInventoryOnEscape;
    }

    /**
     * @return if the inventory shall be kept when the player finishs the dungeon
     */
    public boolean getKeepInventoryOnFinish() {
        return keepInventoryOnFinish;
    }

    /**
     * @return if the inventory shall be kept on death
     */
    public boolean getKeepInventoryOnDeath() {
        return keepInventoryOnDeath;
    }

    /**
     * @return the gameMode
     */
    public GameMode getGameMode() {
        return gameMode;
    }

    /**
     * @return if players may build
     */
    public boolean canBuild() {
        return build;
    }

    /**
     * @return if players may attack each other
     */
    public boolean isPlayerVersusPlayer() {
        return playerVersusPlayer;
    }

    /**
     * @return if players may attack group members
     */
    public boolean isFriendlyFire() {
        return friendlyFire;
    }

    /**
     * @return the initial amount of lives
     */
    public int getInitialLives() {
        return initialLives;
    }

    /**
     * @return if the lobby is disabled
     */
    public boolean isLobbyDisabled() {
        return isLobbyDisabled;
    }

    /**
     * @return the time until a player can play again
     */
    public int getTimeToNextPlay() {
        return timeToNextPlay;
    }

    /**
     * @return the time until a player can get loot again
     */
    public int getTimeToNextLoot() {
        return timeToNextLoot;
    }

    /**
     * @return the break between two waves
     */
    public int getTimeToNextWave() {
        return timeToNextWave;
    }

    /**
     * @return the time until a player gets kicked from his group if he is offline
     */
    public int getTimeUntilKickOfflinePlayer() {
        return timeUntilKickOfflinePlayer;
    }

    /**
     * @return if the game is "time is running"
     */
    public boolean isTimeIsRunning() {
        return timeToFinish != -1;
    }

    /**
     * @return the time until a player gets kicked from his group
     */
    public int getTimeToFinish() {
        return timeToFinish;
    }

    /**
     * @return the requirements
     */
    public List<Requirement> getRequirements() {
        return requirements;
    }

    /**
     * @return the rewards
     */
    public List<Reward> getRewards() {
        return rewards;
    }

    /**
     * @return the timeLastPlayed
     */
    public int getTimeLastPlayed() {
        return timeLastPlayed;
    }

    /**
     * @return all maps needed to be finished to play this map
     */
    public List<String> getFinishedAll() {
        return finishedAll;
    }

    /**
     * @return all maps needed to be finished to play this map and a collection of maps of which at
     * least one has to be finished
     */
    public List<String> getFinished() {
        List<String> merge = new ArrayList<>();
        merge.addAll(finishedAll);
        merge.addAll(finishedOne);
        return merge;
    }

    /**
     * @return the mobTypes
     */
    public Set<DMobType> getMobTypes() {
        return mobTypes;
    }

    /**
     * @return the gameCommandWhitelist
     */
    public List<String> getGameCommandWhitelist() {
        return gameCommandWhitelist;
    }

    /**
     * @return the gamePermissions
     */
    public List<String> getGamePermissions() {
        return gamePermissions;
    }

    /**
     * @return the forcedGameType
     */
    public GameType getForcedGameType() {
        return forcedGameType;
    }

    /**
     * @param forcedGameType
     * the forcedGameType to set
     */
    public void setForcedGameType(GameType forcedGameType) {
        this.forcedGameType = forcedGameType;
    }

}
