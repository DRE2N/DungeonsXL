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
package io.github.dre2n.dungeonsxl.world;

import io.github.dre2n.commons.misc.FileUtil;
import io.github.dre2n.commons.misc.NumberUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.MainConfig;
import io.github.dre2n.dungeonsxl.config.MainConfig.BackupMode;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.scheduler.BukkitTask;

/**
 * World instance manager.
 *
 * @author Daniel Saukel
 */
public class DWorldCache {

    DungeonsXL plugin = DungeonsXL.getInstance();
    MainConfig mainConfig = plugin.getMainConfig();

    public static final File RAW = new File(DungeonsXL.MAPS, ".raw");

    private BukkitTask worldUnloadTask;

    private Set<DResourceWorld> resources = new HashSet<>();
    private Set<DInstanceWorld> instances = new HashSet<>();

    public DWorldCache(File folder) {
        for (File file : folder.listFiles()) {
            if (file.isDirectory() && !file.getName().equals(".raw")) {
                resources.add(new DResourceWorld(this, file));
            }
        }

        if (!RAW.exists()) {
            createRaw();
        }

        startWorldUnloadTask(1200L);
        Bukkit.getPluginManager().registerEvents(new DWorldListener(this), plugin);
    }

    /* Getters and setters */
    /**
     * @return the DResourceWorld that has this name
     */
    public DResourceWorld getResourceByName(String name) {
        for (DResourceWorld world : resources) {
            if (world.getName().equals(name)) {
                return world;
            }
        }

        return null;
    }

    /**
     * @return the DInstanceWorld that represents this world
     */
    public DInstanceWorld getInstanceByWorld(World world) {
        return getInstanceByName(world.getName());
    }

    /**
     * @return the DInstanceWorld that has this name
     */
    public DInstanceWorld getInstanceByName(String name) {
        String[] splitted = name.split("_");
        if (splitted.length != 3) {
            return null;
        }

        return getInstanceById(NumberUtil.parseInt(splitted[2], -1));
    }

    /**
     * @return the DInstanceWorld that has this ID
     */
    public DInstanceWorld getInstanceById(int id) {
        for (DInstanceWorld world : instances) {
            if (world.getId() == id) {
                return world;
            }
        }

        return null;
    }

    /**
     * @return the ResourceWorlds in the maps folder
     */
    public Set<DResourceWorld> getResources() {
        return resources;
    }

    /**
     * @param resource
     * the DResourceWorld to add
     */
    public void addResource(DResourceWorld resource) {
        resources.add(resource);
    }

    /**
     * @param resource
     * the DResourceWorld to remove
     */
    public void removeResource(DResourceWorld resource) {
        resources.remove(resource);
    }

    /**
     * @return the loaded InstanceWorlds in the world container
     */
    public Set<DInstanceWorld> getInstances() {
        return instances;
    }

    /**
     * @param instance
     * the DInstanceWorld to add
     */
    public void addInstance(DInstanceWorld instance) {
        instances.add(instance);
    }

    /**
     * @param instance
     * the DInstanceWorld to remove
     */
    public void removeInstance(DInstanceWorld instance) {
        instances.remove(instance);
    }

    /**
     * @return the loaded GameWorlds
     */
    public Set<DGameWorld> getGameWorlds() {
        Set<DGameWorld> gameWorlds = new HashSet<>();
        for (DInstanceWorld instance : instances) {
            if (instance instanceof DGameWorld) {
                gameWorlds.add((DGameWorld) instance);
            }
        }
        return gameWorlds;
    }

    /**
     * @return the loaded EditWorlds
     */
    public Set<DEditWorld> getEditWorlds() {
        Set<DEditWorld> editWorlds = new HashSet<>();
        for (DInstanceWorld instance : instances) {
            if (instance instanceof DEditWorld) {
                editWorlds.add((DEditWorld) instance);
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
        for (DResourceWorld resource : resources) {
            if (resource.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }

        for (DInstanceWorld instance : instances) {
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
        BackupMode backupMode = mainConfig.getBackupMode();
        HashSet<DInstanceWorld> instances = new HashSet<>(this.instances);
        for (DInstanceWorld instance : instances) {
            if (backupMode == BackupMode.ON_DISABLE | backupMode == BackupMode.ON_DISABLE_AND_SAVE && instance instanceof DEditWorld) {
                instance.getResource().backup(mainConfig.areTweaksEnabled());
            }

            instance.delete();
        }
    }

    /**
     * Saves all EditWorlds.
     */
    public void saveAll() {
        for (DEditWorld editWorld : getEditWorlds()) {
            editWorld.save();
        }
    }

    /**
     * @return an ID for the instance
     */
    public int generateId() {
        int id = 0;
        for (DInstanceWorld instance : instances) {
            if (instance.getId() >= id) {
                id = instance.getId() + 1;
            }
        }
        return id;
    }

    /**
     * @return
     * a name for the instance
     * @param game
     * whether the instance is a DGameWorld
     */
    public String generateName(boolean game) {
        return generateName(game, generateId());
    }

    /**
     * @return
     * a name for the instance
     * @param game
     * whether the instance is a DGameWorld
     * @param id
     * the id to use
     */
    public String generateName(boolean game, int id) {
        return "DXL_" + (game ? "Game" : "Edit") + "_" + id;
    }

    /**
     * Creates a raw, new flat world sothat it can be copied if needed instead of getting generated from scratch.
     */
    public void createRaw() {
        WorldCreator creator = WorldCreator.name(".raw");
        creator.type(WorldType.FLAT);
        creator.generateStructures(false);
        World world = creator.createWorld();
        File worldFolder = new File(Bukkit.getWorldContainer(), ".raw");
        FileUtil.copyDirectory(worldFolder, RAW, DungeonsXL.EXCLUDED_FILES);
        Bukkit.unloadWorld(world, false);
        FileUtil.removeDirectory(worldFolder);
    }

    /* Tasks */
    /**
     * @return the worldUnloadTask
     */
    public BukkitTask getWorldUnloadTask() {
        return worldUnloadTask;
    }

    /**
     * start a new WorldUnloadTask
     */
    public void startWorldUnloadTask(long period) {
        worldUnloadTask = new WorldUnloadTask().runTaskTimer(plugin, period, period);
    }

}
