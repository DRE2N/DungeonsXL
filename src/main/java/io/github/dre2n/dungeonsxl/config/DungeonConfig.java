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

/**
 * @author Daniel Saukel
 */
public class DungeonConfig extends BRConfig {

    public static final int CONFIG_VERSION = 1;

    private String startFloor;
    private String endFloor;
    private List<String> floors = new ArrayList<>();
    private int floorCount;
    private boolean removeWhenPlayed;
    private WorldConfig overrideValues;
    private WorldConfig defaultValues;

    public DungeonConfig(File file) {
        super(file, CONFIG_VERSION);

        if (initialize) {
            initialize();
        }
        load();
    }

    /**
     * @return the startFloor
     */
    public String getStartFloor() {
        return startFloor;
    }

    /**
     * @param startFloor
     * the startFloor to set
     */
    public void setStartFloor(String startFloor) {
        this.startFloor = startFloor;
    }

    /**
     * @return the endFloor
     */
    public String getEndFloor() {
        return endFloor;
    }

    /**
     * @param endFloor
     * the endFloor to set
     */
    public void setEndFloor(String endFloor) {
        this.endFloor = endFloor;
    }

    /**
     * @return the floors
     */
    public List<String> getFloors() {
        return floors;
    }

    /**
     * @param gameWorld
     * the gameWorld to add
     */
    public void addFloor(String gameWorld) {
        floors.add(gameWorld);
    }

    /**
     * @param gameWorld
     * the gameWorld to remove
     */
    public void removeFloor(String gameWorld) {
        floors.remove(gameWorld);
    }

    /**
     * @return the floorCount
     */
    public int getFloorCount() {
        return floorCount;
    }

    /**
     * @param floorCount
     * the floorCount to set
     */
    public void setFloorCount(int floorCount) {
        this.floorCount = floorCount;
    }

    /**
     * @return the removeWhenPlayed
     */
    public boolean getRemoveWhenPlayed() {
        return removeWhenPlayed;
    }

    /**
     * @param removeWhenPlayed
     * the removeWhenPlayed to set
     */
    public void setRemoveWhenPlayed(boolean removeWhenPlayed) {
        this.removeWhenPlayed = removeWhenPlayed;
    }

    /**
     * The values from this WorldConfig will override all values of the
     * WorldConfigs of inherited maps.
     *
     * @return the override values
     */
    public WorldConfig getOverrideValues() {
        return overrideValues;
    }

    /**
     * @param worldConfig
     * the WorldConfig to set
     */
    public void setOverrideValues(WorldConfig worldConfig) {
        overrideValues = worldConfig;
    }

    /**
     * The values from this WorldConfig will get overriden by values of the
     * WorldConfigs of inherited maps.
     * They will still override the values from the main config, though.
     *
     * @return the default values
     */
    public WorldConfig getDefaultValues() {
        return defaultValues;
    }

    /**
     * @param worldConfig
     * the WorldConfig to set
     */
    public void setDefaultValues(WorldConfig worldConfig) {
        defaultValues = worldConfig;
    }

    @Override
    public void initialize() {
        if (!config.contains("floors")) {
            config.set("floors", floors);
        }

        if (!config.contains("startFloor")) {
            config.set("startFloor", startFloor);
        }

        if (!config.contains("endFloor")) {
            config.set("endFloor", endFloor);
        }

        if (!config.contains("floorCount")) {
            config.set("floorCount", floorCount);
        }

        if (!config.contains("removeWhenPlayed")) {
            config.set("removeWhenPlayed", removeWhenPlayed);
        }

        if (!config.contains("overrideValues")) {
            config.createSection("overrideValues");
        }

        if (!config.contains("defaultValues")) {
            config.createSection("defaultValues");
        }

        save();
    }

    @Override
    public void load() {
        if (config.contains("floors")) {
            floors = config.getStringList("floors");
        }

        if (config.contains("startFloor")) {
            startFloor = config.getString("startFloor");
        }

        if (config.contains("endFloor")) {
            endFloor = config.getString("endFloor");
        }

        if (config.contains("floorCount")) {
            floorCount = config.getInt("floorCount");
        }

        if (config.contains("removeWhenPlayed")) {
            removeWhenPlayed = config.getBoolean("removeWhenPlayed");
        }

        if (config.contains("overrideValues")) {
            overrideValues = new WorldConfig(config.getConfigurationSection("overrideValues"));
        }

        if (config.contains("defaultValues")) {
            defaultValues = new WorldConfig(config.getConfigurationSection("defaultValues"));
        }
    }

}
