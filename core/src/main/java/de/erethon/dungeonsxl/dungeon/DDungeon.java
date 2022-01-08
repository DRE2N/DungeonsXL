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
import de.erethon.dungeonsxl.api.dungeon.Dungeon;
import de.erethon.dungeonsxl.api.dungeon.GameRule;
import de.erethon.dungeonsxl.api.dungeon.GameRuleContainer;
import de.erethon.dungeonsxl.api.world.ResourceWorld;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Daniel Saukel
 */
public class DDungeon implements Dungeon {

    private DungeonsXL plugin;

    private String name;
    private DungeonConfig config;
    private ResourceWorld map;
    private GameRuleContainer rules;

    /**
     * Artificial dungeon
     *
     * @param plugin   the plugin instance
     * @param resource the only resource world
     */
    public DDungeon(DungeonsXL plugin, ResourceWorld resource) {
        this.plugin = plugin;

        name = resource.getName();
        map = resource;
        setupRules();
    }

    private DDungeon() {
    }

    /**
     * Real dungeon
     *
     * @param plugin the plugin instance
     * @param file   the file to load from
     * @return the dungeon or null if the config is erroneous
     */
    public static Dungeon create(DungeonsXL plugin, File file) {
        DungeonConfig config = new DungeonConfig(plugin, file);
        if (config.getStartFloor() == null || config.getEndFloor() == null) {
            return null;
        }

        DDungeon dungeon = new DDungeon();
        dungeon.plugin = plugin;
        dungeon.name = file.getName().replaceAll(".yml", "");
        dungeon.config = config;
        dungeon.map = config.getStartFloor();
        if (dungeon.isSetupCorrect()) {
            dungeon.setupRules();
            return dungeon;
        } else {
            return null;
        }
    }

    public DungeonConfig getConfig() {
        if (!isMultiFloor()) {
            throw new IllegalStateException("Tried to access the dungeon config of a single floor dungeon");
        }
        return config;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean isMultiFloor() {
        return config != null;
    }

    @Override
    public ResourceWorld getStartFloor() {
        return map;
    }

    @Override
    public void setStartFloor(ResourceWorld startFloor) {
        getConfig().setStartFloor(startFloor);
    }

    @Override
    public ResourceWorld getEndFloor() {
        return getConfig().getEndFloor();
    }

    @Override
    public void setEndFloor(ResourceWorld endFloor) {
        getConfig().setEndFloor(endFloor);
    }

    @Override
    public List<ResourceWorld> getFloors() {
        if (isMultiFloor()) {
            return new ArrayList<>(getConfig().getFloors());
        } else {
            return new ArrayList<>(Arrays.asList(map));
        }
    }

    @Override
    public void addFloor(ResourceWorld resource) {
        getConfig().addFloor(resource);
    }

    @Override
    public void removeFloor(ResourceWorld resource) {
        getConfig().removeFloor(resource);
    }

    @Override
    public int getFloorCount() {
        return getConfig().getFloorCount();
    }

    @Override
    public void setFloorCount(int floorCount) {
        getConfig().setFloorCount(floorCount);
    }

    @Override
    public boolean getRemoveWhenPlayed() {
        return getConfig().getRemoveWhenPlayed();
    }

    @Override
    public void setRemoveWhenPlayed(boolean removeWhenPlayed) {
        getConfig().setRemoveWhenPlayed(removeWhenPlayed);
    }

    @Override
    public GameRuleContainer getOverrideValues() {
        return getConfig().getOverrideValues();
    }

    @Override
    public GameRuleContainer getDefaultValues() {
        return getConfig().getDefaultValues();
    }

    @Override
    public GameRuleContainer getRules() {
        return rules;
    }

    @Override
    public void setRules(GameRuleContainer rules) {
        this.rules = rules;
    }

    @Override
    public void setupRules() {
        if (rules != null) {
            return;
        }
        if (isMultiFloor()) {
            rules = new GameRuleContainer(getOverrideValues());
            if (map.getRules() != null) {
                rules.merge(map.getRules());
            }
            rules.merge(getDefaultValues());
        } else if (map.getRules() != null) {
            rules = new GameRuleContainer(map.getRules());
        } else {
            rules = new GameRuleContainer();
        }
        rules.merge(plugin.getMainConfig().getDefaultWorldConfig());
        rules.merge(GameRule.DEFAULT_VALUES);
    }

    @Override
    public boolean isSetupCorrect() {
        for (ResourceWorld resource : plugin.getMapRegistry()) {
            if (resource.getName().equals(name)) {
                return false;
            }
        }
        return getConfig() == null || (getConfig().getStartFloor() != null && getConfig().getEndFloor() != null);
    }

    /* Statics */
    public static File getFileFromName(String name) {
        return new File(DungeonsXL.DUNGEONS, name + ".yml");
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{name=" + name + "; multiFloor=" + isMultiFloor() + "}";
    }

}
