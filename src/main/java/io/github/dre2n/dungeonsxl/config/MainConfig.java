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
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class MainConfig extends BRConfig {

    public static final int CONFIG_VERSION = 1;

    private String language = "en";
    private boolean enableEconomy = false;

    /* Tutorial */
    private boolean tutorialActivated = false;
    private String tutorialDungeon = "tutorial";
    private String tutorialStartGroup = "default";
    private String tutorialEndGroup = "player";

    /* Default Dungeon Settings */
    private WorldConfig defaultWorldConfig;

    private List<String> editCommandWhitelist = new ArrayList<>();

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
     * @return the tutorialEndGroup
     */
    public String getTutorialEndGroup() {
        return tutorialEndGroup;
    }

    /**
     * @return the editCommandWhitelist
     */
    public List<String> getEditCommandWhitelist() {
        return editCommandWhitelist;
    }

    /**
     * @return the defaultWorldConfig
     */
    public WorldConfig getDefaultWorldConfig() {
        return defaultWorldConfig;
    }

    /**
     * @param defaultWorldConfig
     * the defaultWorldConfig to set
     */
    public void setDefaultWorldConfig(WorldConfig defaultWorldConfig) {
        this.defaultWorldConfig = defaultWorldConfig;
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

        if (!config.contains("editCommandWhitelist")) {
            config.set("editCommandWhitelist", editCommandWhitelist);
        }

        /* Default Dungeon Config */
        if (!config.contains("default")) {
            config.createSection("default");
        }
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

        if (config.contains("editCommandWhitelist")) {
            editCommandWhitelist = config.getStringList("editCommandWhitelist");
        }

        /* Default Dungeon Config */
        ConfigurationSection configSection = config.getConfigurationSection("default");
        if (configSection != null) {
            setDefaultWorldConfig(new WorldConfig(configSection));
            WorldConfig.defaultConfig = defaultWorldConfig;// TODO
        }
    }

}
