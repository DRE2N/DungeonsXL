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
package de.erethon.dungeonsxl.dungeon;

import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.api.world.ResourceWorld;
import de.erethon.dungeonsxl.util.commons.config.DREConfig;
import de.erethon.dungeonsxl.world.WorldConfig;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a dungeon script. See {@link de.erethon.dungeonsxl.dungeon.DDungeon}.
 *
 * @author Daniel Saukel
 */
public class DungeonConfig extends DREConfig {

    private DungeonsXL plugin;

    public static final int CONFIG_VERSION = 1;

    private ResourceWorld startFloor;
    private ResourceWorld endFloor;
    private List<ResourceWorld> floors = new ArrayList<>();
    private int floorCount;
    private boolean removeWhenPlayed;
    private WorldConfig overrideValues;
    private WorldConfig defaultValues;

    public DungeonConfig(DungeonsXL plugin, File file) {
        super(file, CONFIG_VERSION);

        this.plugin = plugin;

        if (initialize) {
            initialize();
        }
        load();
    }

    public ResourceWorld getStartFloor() {
        return startFloor;
    }

    public void setStartFloor(ResourceWorld startFloor) {
        this.startFloor = startFloor;
    }

    public ResourceWorld getEndFloor() {
        return endFloor;
    }

    public void setEndFloor(ResourceWorld endFloor) {
        this.endFloor = endFloor;
    }

    public List<ResourceWorld> getFloors() {
        return floors;
    }

    public void addFloor(ResourceWorld resource) {
        floors.add(resource);
    }

    public void removeFloor(ResourceWorld resource) {
        floors.remove(resource);
    }

    public int getFloorCount() {
        return floorCount;
    }

    public void setFloorCount(int floorCount) {
        this.floorCount = floorCount;
    }

    public boolean getRemoveWhenPlayed() {
        return removeWhenPlayed;
    }

    public void setRemoveWhenPlayed(boolean removeWhenPlayed) {
        this.removeWhenPlayed = removeWhenPlayed;
    }

    public WorldConfig getOverrideValues() {
        return overrideValues;
    }

    public WorldConfig getDefaultValues() {
        return defaultValues;
    }

    public boolean containsFloor(ResourceWorld resource) {
        return floors.contains(resource) || startFloor.equals(resource) || endFloor.equals(resource);
    }

    public boolean containsFloor(String mapName) {
        return containsFloor(plugin.getMapRegistry().get(mapName));
    }

    @Override
    public void load() {
        for (String floor : config.getStringList("floors")) {
            ResourceWorld resource = plugin.getMapRegistry().get(floor);
            if (resource != null) {
                floors.add(resource);
            }
        }
        startFloor = plugin.getMapRegistry().get(config.getString("startFloor"));
        endFloor = plugin.getMapRegistry().get(config.getString("endFloor"));
        floorCount = config.getInt("floorCount", floors.size() + 2);
        removeWhenPlayed = config.getBoolean("removeWhenPlayed", removeWhenPlayed);

        overrideValues = new WorldConfig(plugin, config.getConfigurationSection("overrideValues"));
        defaultValues = new WorldConfig(plugin, config.getConfigurationSection("defaultValues"));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{file=" + file.getPath() + "}";
    }

}
