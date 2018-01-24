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
package io.github.dre2n.dungeonsxl.dungeon;

import io.github.dre2n.commons.config.DREConfig;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.world.DResourceWorld;
import io.github.dre2n.dungeonsxl.world.DWorldCache;
import io.github.dre2n.dungeonsxl.world.WorldConfig;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a dungeon script. See {@link io.github.dre2n.dungeonsxl.dungeon.Dungeon}.
 *
 * @author Daniel Saukel
 */
public class DungeonConfig extends DREConfig {

    DWorldCache worlds = DungeonsXL.getInstance().getDWorlds();

    public static final int CONFIG_VERSION = 1;

    private DResourceWorld startFloor;
    private DResourceWorld endFloor;
    private List<DResourceWorld> floors = new ArrayList<>();
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
    public DResourceWorld getStartFloor() {
        return startFloor;
    }

    /**
     * @param startFloor
     * the startFloor to set
     */
    public void setStartFloor(DResourceWorld startFloor) {
        this.startFloor = startFloor;
    }

    /**
     * @return the endFloor
     */
    public DResourceWorld getEndFloor() {
        return endFloor;
    }

    /**
     * @param endFloor
     * the endFloor to set
     */
    public void setEndFloor(DResourceWorld endFloor) {
        this.endFloor = endFloor;
    }

    /**
     * @return the floors
     */
    public List<DResourceWorld> getFloors() {
        return floors;
    }

    /**
     * @param resource
     * the resource to add
     */
    public void addFloor(DResourceWorld resource) {
        floors.add(resource);
    }

    /**
     * @param resource
     * the resource to remove
     */
    public void removeFloor(DResourceWorld resource) {
        floors.remove(resource);
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

    /**
     * @param resource
     * the DResourceWorld to check
     * @return true if the floor is either in the list or the start / end floor.
     */
    public boolean containsFloor(DResourceWorld resource) {
        return floors.contains(resource) || startFloor.equals(resource) || endFloor.equals(resource);
    }

    /**
     * @param mapName
     * the name of the map to check
     * @return true if the floor is either in the list or the start / end floor.
     */
    public boolean containsFloor(String mapName) {
        return containsFloor(worlds.getResourceByName(mapName));
    }

    @Override
    public void load() {
        if (config.contains("floors")) {
            for (String floor : config.getStringList("floors")) {
                DResourceWorld resource = worlds.getResourceByName(floor);
                if (resource != null) {
                    floors.add(resource);
                }
            }
        }

        if (config.contains("startFloor")) {
            startFloor = worlds.getResourceByName(config.getString("startFloor"));
        }

        if (config.contains("endFloor")) {
            endFloor = worlds.getResourceByName(config.getString("endFloor"));
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
