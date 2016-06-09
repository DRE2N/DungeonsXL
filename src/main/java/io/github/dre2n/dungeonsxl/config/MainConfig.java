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

import io.github.dre2n.commons.config.BRConfig;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class MainConfig extends BRConfig {

    public static final int CONFIG_VERSION = 9;

    private String language = "english";
    private boolean enableEconomy = false;

    /* Tutorial */
    private boolean tutorialActivated = false;
    private String tutorialDungeon = "tutorial";
    private String tutorialStartGroup = "default";
    private String tutorialEndGroup = "player";

    /* Announcers */
    private List<Short> groupColorPriority = new ArrayList<>(Arrays.asList(
            (short) 11,
            (short) 14,
            (short) 4,
            (short) 5,
            (short) 10,
            (short) 1,
            (short) 0,
            (short) 15
    ));
    private double announcementInterval = 30;

    /* Misc */
    private boolean sendFloorTitle = true;
    private Map<String, Object> externalMobProviders = new HashMap<>();
    private int maxInstances = 10;

    /* Secure Mode */
    private boolean secureModeEnabled = false;
    private double secureModeCheckInterval = 5;
    private boolean openInventories = false;
    private boolean dropItems = false;
    private List<String> editCommandWhitelist = new ArrayList<>();

    /* Permissions bridge */
    private List<String> editPermissions = new ArrayList<>();

    /* Default Dungeon Settings */
    private WorldConfig defaultWorldConfig;

    public MainConfig(File file) {
        super(file, CONFIG_VERSION);

        if (initialize) {
            initialize();
        }
        load();
    }

    /**
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param language
     * the language to set
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * @return if DungeonsXL should use economy features provided by Vault
     */
    public boolean isEconomyEnabled() {
        return enableEconomy;
    }

    /**
     * @param enabled
     * if DungeonsXL should use economy features provided by Vault
     */
    public void setEconomyEnabled(boolean enabled) {
        enableEconomy = enabled;
    }

    /**
     * @return if the tutorial is activated
     */
    public boolean isTutorialActivated() {
        return tutorialActivated;
    }

    /**
     * @param activated
     * if new players start in a tutorial
     */
    public void setTutorialActivated(boolean activated) {
        tutorialActivated = activated;
    }

    /**
     * @return the tutorial dungeon
     */
    public String getTutorialDungeon() {
        return tutorialDungeon;
    }

    /**
     * @param dungeon
     * the tutorial dungeon to set
     */
    public void setTutorialDungeon(String dungeon) {
        tutorialDungeon = dungeon;
    }

    /**
     * @return the tutorialStartGroup
     */
    public String getTutorialStartGroup() {
        return tutorialStartGroup;
    }

    /**
     * @param group
     * the group the player gets when he plays the tutorial
     */
    public void setTutorialStartGroup(String group) {
        tutorialStartGroup = group;
    }

    /**
     * @return the tutorialEndGroup
     */
    public String getTutorialEndGroup() {
        return tutorialEndGroup;
    }

    /**
     * @param group
     * the group the player gets when he finshs the tutorial
     */
    public void setTutorialEndGroup(String group) {
        tutorialEndGroup = group;
    }

    /**
     * @return the group colors
     */
    public List<Short> getGroupColorPriority() {
        return groupColorPriority;
    }

    /**
     * @param dataValues
     * wool data values
     */
    public void setGroupColorPriority(List<Short> dataValues) {
        groupColorPriority = dataValues;
    }

    /**
     * @return the announcement interval
     */
    public long getAnnouncmentInterval() {
        return (long) (announcementInterval * 20);
    }

    /**
     * @param interval
     * the interval to set
     */
    public void setAnnouncementInterval(double interval) {
        announcementInterval = interval;
    }

    /**
     * @return if the floor title shall be sent
     */
    public boolean isSendFloorTitleEnabled() {
        return sendFloorTitle;
    }

    /**
     * @param enabled
     * if the floor title shall be sent
     */
    public void setSendFloorTitleEnabled(boolean enabled) {
        sendFloorTitle = enabled;
    }

    /**
     * @return the custom external mob providers
     */
    public Map<String, Object> getExternalMobProviders() {
        return externalMobProviders;
    }

    /**
     * @return the maximum amount of worlds to instantiate at once
     */
    public int getMaxInstances() {
        return maxInstances;
    }

    /**
     * @param maxInstances
     * the maximum amount of worlds to instantiate at once
     */
    public void setMaxInstances(int maxInstances) {
        this.maxInstances = maxInstances;
    }

    /**
     * @return if the secure mode is enabled
     */
    public boolean isSecureModeEnabled() {
        return secureModeEnabled;
    }

    /**
     * @param enabled
     * if the secure mode is enabled
     */
    public void setSecureModeEnabled(boolean enabled) {
        secureModeEnabled = enabled;
    }

    /**
     * @return if players may open inventories while editing; false if secure mode disabled
     */
    public boolean getOpenInventories() {
        return openInventories && secureModeEnabled;
    }

    /**
     * @param openInventories
     * if inventories can be opened in edit mode
     */
    public void setOpenInventories(boolean openInventories) {
        this.openInventories = openInventories;
    }

    /**
     * @return if players may drop items while editing; false if secure mode disabled
     */
    public boolean getDropItems() {
        return dropItems && secureModeEnabled;
    }

    /**
     * @param dropItems
     * if items may be dropped in edit mode
     */
    public void setDropItems(boolean dropItems) {
        this.dropItems = dropItems;
    }

    /**
     * @return the interval for the check task
     */
    public long getSecureModeCheckInterval() {
        return (long) (secureModeCheckInterval * 20);
    }

    /**
     * @param interval
     * the interval for the check task
     */
    public void setSecureModeCheckInterval(double interval) {
        secureModeCheckInterval = interval;
    }

    /**
     * @return the editCommandWhitelist
     */
    public List<String> getEditCommandWhitelist() {
        return editCommandWhitelist;
    }

    /**
     * @return the edit mode permissions
     */
    public List<String> getEditPermissions() {
        return editPermissions;
    }

    /**
     * @return the defaultWorldConfig
     */
    public WorldConfig getDefaultWorldConfig() {
        return defaultWorldConfig;
    }

    @Override
    public void initialize() {
        /* Main Config */
        if (!config.contains("language")) {
            config.set("language", language);
        }

        if (!config.contains("enableEconomy")) {
            config.set("enableEconomy", enableEconomy);
        }

        if (!config.contains("tutorial.activated")) {
            config.set("tutorial.activated", tutorialActivated);
        }

        if (!config.contains("tutorial.dungeon")) {
            config.set("tutorial.dungeon", tutorialDungeon);
        }

        if (!config.contains("tutorial.startGroup")) {
            config.set("tutorial.startGroup", tutorialStartGroup);
        }

        if (!config.contains("tutorial.endGroup")) {
            config.set("tutorial.endgroup", tutorialEndGroup);
        }

        if (!config.contains("groupColorPriority")) {
            config.set("groupColorPriority", groupColorPriority);
        }

        if (!config.contains("announcementInterval")) {
            config.set("announcementInterval", announcementInterval);
        }

        if (!config.contains("sendFloorTitle")) {
            config.set("sendFloorTitle", sendFloorTitle);
        }

        if (!config.contains("externalMobProviders")) {
            config.createSection("externalMobProviders");
        }

        if (!config.contains("maxInstances")) {
            config.set("maxInstances", maxInstances);
        }

        if (!config.contains("secureMode.enabled")) {
            config.set("secureMode.enabled", secureModeEnabled);
        }

        if (!config.contains("secureMode.openInventories")) {
            config.set("secureMode.openInventories", openInventories);
        }

        if (!config.contains("secureMode.dropItems")) {
            config.set("secureMode.dropItems", dropItems);
        }

        if (!config.contains("secureMode.checkInterval")) {
            config.set("secureMode.checkInterval", secureModeCheckInterval);
        }

        if (!config.contains("secureMode.editCommandWhitelist")) {
            config.set("secureMode.editCommandWhitelist", editCommandWhitelist);
        }

        if (!config.contains("editPermissions")) {
            config.set("editPermissions", editPermissions);
        }

        /* Default Dungeon Config */
        if (!config.contains("default")) {
            config.createSection("default");
        }

        save();
    }

    @Override
    public void load() {
        /* Main Config */
        if (config.contains("language")) {
            language = config.getString("language");
        }

        if (config.contains("enableEconomy")) {
            enableEconomy = config.getBoolean("enableEconomy");
        }

        if (config.contains("tutorial.activated")) {
            tutorialActivated = config.getBoolean("tutorial.activated");
        }

        if (config.contains("tutorial.dungeon")) {
            tutorialDungeon = config.getString("tutorial.dungeon");
        }

        if (config.contains("tutorial.startgroup")) {
            tutorialStartGroup = config.getString("tutorial.startgroup");
        }

        if (config.contains("tutorial.endgroup")) {
            tutorialEndGroup = config.getString("tutorial.endgroup");
        }

        if (config.contains("groupColorPriority")) {
            groupColorPriority = config.getShortList("groupColorPriority");
        }

        if (config.contains("announcementInterval")) {
            announcementInterval = config.getDouble("announcementInterval");
        }

        if (config.contains("sendFloorTitle")) {
            sendFloorTitle = config.getBoolean("sendFloorTitle");
        }

        if (config.contains("externalMobProviders")) {
            externalMobProviders = config.getConfigurationSection("externalMobProviders").getValues(false);
        }

        if (config.contains("maxInstances")) {
            maxInstances = config.getInt("maxInstances");
        }

        if (config.contains("secureMode.enabled")) {
            secureModeEnabled = config.getBoolean("secureMode.enabled");
        }

        if (config.contains("secureMode.openInventories")) {
            openInventories = config.getBoolean("secureMode.openInventories");
        }

        if (config.contains("secureMode.dropItems")) {
            dropItems = config.getBoolean("secureMode.dropItems");
        }

        if (config.contains("secureMode.checkInterval")) {
            secureModeCheckInterval = config.getDouble("secureMode.checkInterval");
        }

        if (config.contains("secureMode.editCommandWhitelist")) {
            editCommandWhitelist = config.getStringList("secureMode.editCommandWhitelist");
        }

        if (config.contains("editPermissions")) {
            editPermissions = config.getStringList("editPermissions");
        }

        /* Default Dungeon Config */
        ConfigurationSection configSection = config.getConfigurationSection("default");
        if (configSection != null) {
            defaultWorldConfig = new WorldConfig(configSection);
        }
    }

}
