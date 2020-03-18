/*
 * Copyright (C) 2012-2020 Frank Baumann
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
     * Real dungeon
     *
     * @param plugin the plugin instance
     * @param file   the file to load from
     */
    public DDungeon(DungeonsXL plugin, File file) {
        this.plugin = plugin;

        name = file.getName().replaceAll(".yml", "");
        config = new DungeonConfig(plugin, file);
        map = config.getStartFloor();
        setupRules();
    }

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

    public DungeonConfig getConfig() {
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
        config.setStartFloor(startFloor);
    }

    @Override
    public ResourceWorld getEndFloor() {
        return config.getEndFloor();
    }

    @Override
    public void setEndFloor(ResourceWorld endFloor) {
        config.setEndFloor(endFloor);
    }

    @Override
    public List<ResourceWorld> getFloors() {
        if (isMultiFloor()) {
            return config.getFloors();
        } else {
            return new ArrayList<>(Arrays.asList(map));
        }
    }

    @Override
    public void addFloor(ResourceWorld resource) {
        config.addFloor(resource);
    }

    @Override
    public void removeFloor(ResourceWorld resource) {
        config.removeFloor(resource);
    }

    @Override
    public int getFloorCount() {
        return config.getFloorCount();
    }

    @Override
    public void setFloorCount(int floorCount) {
        config.setFloorCount(floorCount);
    }

    @Override
    public boolean getRemoveWhenPlayed() {
        return config.getRemoveWhenPlayed();
    }

    @Override
    public void setRemoveWhenPlayed(boolean removeWhenPlayed) {
        config.setRemoveWhenPlayed(removeWhenPlayed);
    }

    @Override
    public GameRuleContainer getOverrideValues() {
        return config.getOverrideValues();
    }

    @Override
    public void setOverrideValues(GameRuleContainer rules) {
        config.setOverrideValues(rules);
    }

    @Override
    public GameRuleContainer getDefaultValues() {
        return config.getDefaultValues();
    }

    @Override
    public void setDefaultValues(GameRuleContainer rules) {
        config.setDefaultValues(rules);
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
        rules.merge(GameRuleContainer.DEFAULT_VALUES);
    }

    @Override
    public boolean isSetupCorrect() {
        for (ResourceWorld resource : plugin.getMapRegistry()) {
            if (resource.getName().equals(name)) {
                return false;
            }
        }
        return config.getStartFloor() != null && config.getEndFloor() != null;
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
