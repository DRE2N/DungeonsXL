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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class MainConfig extends BRConfig {

    public static final int CONFIG_VERSION = 6;

    private String language = "en";
    private boolean enableEconomy = false;

    /* Tutorial */
    private boolean tutorialActivated = false;
    private String tutorialDungeon = "tutorial";
    private String tutorialStartGroup = "default";
    private String tutorialEndGroup = "player";

    /* Misc */
    private boolean sendFloorTitle = true;
    private Map<String, Object> externalMobProviders = new HashMap<>();

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
     * @return the enableEconomy
     */
    public boolean enableEconomy() {
        return enableEconomy;
    }

    /**
     * @return the tutorialActivated
     */
    public boolean isTutorialActivated() {
        return tutorialActivated;
    }

    /**
     * @return the tutorialDungeon
     */
    public String getTutorialDungeon() {
        return tutorialDungeon;
    }

    /**
     * @return the tutorialStartGroup
     */
    public String getTutorialStartGroup() {
        return tutorialStartGroup;
    }

    /**
     * @return if the floor title shall be sent
     */
    public boolean getSendFloorTitle() {
        return sendFloorTitle;
    }

    /**
     * @return the custom external mob providers
     */
    public Map<String, Object> getExternalMobProviders() {
        return externalMobProviders;
    }

    /**
     * @return the tutorialEndGroup
     */
    public String getTutorialEndGroup() {
        return tutorialEndGroup;
    }

    /**
     * @return if the secure mode is enabled
     */
    public boolean isSecureModeEnabled() {
        return secureModeEnabled;
    }

    /**
     * @return if players may open inventories while editing; false if secure mode disabled
     */
    public boolean getOpenInventories() {
        return openInventories && secureModeEnabled;
    }

    /**
     * @return if players may drop items while editing; false if secure mode disabled
     */
    public boolean getDropItems() {
        return dropItems && secureModeEnabled;
    }

    /**
     * @return the interval for the check task
     */
    public long getSecureModeCheckInterval() {
        return (long) (secureModeCheckInterval * 20);
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

        if (!config.contains("sendFloorTitle")) {
            config.set("sendFloorTitle", sendFloorTitle);
        }

        if (!config.contains("externalMobProviders")) {
            config.createSection("externalMobProviders");
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

        if (config.contains("sendFloorTitle")) {
            sendFloorTitle = config.getBoolean("sendFloorTitle");
        }

        if (config.contains("externalMobProviders")) {
            externalMobProviders = config.getConfigurationSection("externalMobProviders").getValues(false);
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
