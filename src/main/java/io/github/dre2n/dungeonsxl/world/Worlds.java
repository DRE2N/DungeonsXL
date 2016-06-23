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
package io.github.dre2n.dungeonsxl.world;

import io.github.dre2n.commons.util.FileUtil;
import io.github.dre2n.commons.util.NumberUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;

/**
 * @author Daniel Saukel
 */
public class Worlds {

    private Set<ResourceWorld> resources;
    private Set<InstanceWorld> instances;

    public Worlds(File folder) {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                resources.add(new ResourceWorld(file));
            }
        }
    }

    /* Getters and setters */
    /**
     * @return the ResourceWorld that has this name
     */
    public ResourceWorld getResourceByName(String name) {
        for (ResourceWorld world : resources) {
            if (world.getName().equals(name)) {
                return world;
            }
        }

        return null;
    }

    /**
     * @return the InstanceWorld that has this name
     */
    public InstanceWorld getInstanceByName(String name) {
        String[] splitted = name.split("_");
        if (splitted.length != 3) {
            return null;
        }

        return getInstanceById(NumberUtil.parseInt(splitted[2], -1));
    }

    /**
     * @return the InstanceWorld that has this ID
     */
    public InstanceWorld getInstanceById(int id) {
        for (InstanceWorld world : instances) {
            if (world.getId() == id) {
                return world;
            }
        }

        return null;
    }

    /**
     * @return the ResourceWorlds in the maps folder
     */
    public Set<ResourceWorld> getResources() {
        return resources;
    }

    /**
     * @return the loaded InstanceWorlds in the world container
     */
    public Set<InstanceWorld> getInstances() {
        return instances;
    }

    /**
     * @return the loaded GameWorlds
     */
    public Set<GameWorld> getGameWorlds() {
        Set<GameWorld> gameWorlds = new HashSet<>();
        for (InstanceWorld instance : instances) {
            if (instance instanceof GameWorld) {
                gameWorlds.add((GameWorld) instance);
            }
        }
        return gameWorlds;
    }

    /**
     * @return the loaded EditWorlds
     */
    public Set<EditWorld> getEditWorlds() {
        Set<EditWorld> editWorlds = new HashSet<>();
        for (InstanceWorld instance : instances) {
            if (instance instanceof GameWorld) {
                editWorlds.add((EditWorld) instance);
            }
        }
        return editWorlds;
    }

    /**
     * @param name
     * the name of the map; can either be the resource name or the instance name
     * @return
     * if a map with this name exists
     */
    public boolean exists(String name) {
        for (ResourceWorld resource : resources) {
            if (resource.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }

        for (InstanceWorld instance : instances) {
            if (instance.getFolder().getName().equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check world container for old, remaining instances and delete them.
     */
    public void check() {
        for (File file : Bukkit.getWorldContainer().listFiles()) {
            if (file.getName().startsWith("DXL_Edit_") && file.isDirectory()) {
                for (File mapFile : file.listFiles()) {
                    if (mapFile.getName().startsWith(".id_")) {
                        String name = mapFile.getName().substring(4);

                        FileUtil.copyDirectory(file, new File(DungeonsXL.MAPS, name), DungeonsXL.EXCLUDED_FILES);
                        FileUtil.deleteUnusedFiles(new File(DungeonsXL.MAPS, name));

                        FileUtil.removeDirectory(file);
                    }
                }

            } else if (file.getName().startsWith("DXL_Game_") && file.isDirectory()) {
                FileUtil.removeDirectory(file);
            }
        }
    }

    /**
     * Clean up all instances.
     */
    public void deleteAllInstances() {
        for (InstanceWorld instance : instances) {
            instance.delete();
        }
    }

    /**
     * Saves all EditWorlds.
     */
    public void saveAll() {
        for (EditWorld editWorld : getEditWorlds()) {
            editWorld.save();
        }
    }

    /**
     * @return an ID for the instance
     */
    public int generateId() {
        int id = 0;
        for (InstanceWorld instance : instances) {
            if (instance.getId() >= id) {
                id = instance.getId() + 1;
            }
        }
        return id;
    }

    /**
     * @return a name for the instance
     *
     * @param game
     * whether the instance is a GameWorld
     */
    public String generateName(boolean game) {
        return "DXL_" + (game ? "Game" : "Edit") + "_" + generateId();
    }

}
