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
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.SignData;
import io.github.dre2n.dungeonsxl.config.WorldConfig;
import java.io.File;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

/**
 * This class represents unloaded worlds.
 *
 * @author Daniel Saukel
 */
public class ResourceWorld {

    DungeonsXL plugin = DungeonsXL.getInstance();

    private File folder;
    private WorldConfig config;
    private SignData signData;

    public ResourceWorld(File folder) {
        this.folder = folder;

        File configFile = new File(folder, "config.yml");
        if (configFile.exists()) {
            config = new WorldConfig(configFile);
        }

        File signData = new File(folder, "DXLData.data");
        if (signData.exists()) {
            this.signData = new SignData(signData);
        }
    }

    /* Getters and setters */
    /**
     * @return the folder that stores the world
     */
    public File getFolder() {
        return folder;
    }

    /**
     * @return the name of the world
     */
    public String getName() {
        return folder.getName();
    }

    /**
     * @param name
     * the name to set
     */
    public void setName(String name) {
        folder.renameTo(new File(folder.getParentFile(), name));
    }

    /**
     * @return the WorldConfig
     */
    public WorldConfig getConfig() {
        return config;
    }

    /**
     * @return the DXLData.data file
     */
    public SignData getSignData() {
        return signData;
    }

    /* Actions */
    /**
     * @param game
     * whether the instance is a GameWorld
     * @return an instance of this world
     */
    public InstanceWorld instantiate(boolean game) {
        int id = plugin.getWorlds().getInstances().size();
        String name = "DXL_" + (game ? "Game" : "Edit") + "_" + id;
        File instanceFolder = new File(Bukkit.getWorldContainer(), name);
        FileUtil.copyDirectory(folder, instanceFolder, DungeonsXL.EXCLUDED_FILES);

        if (Bukkit.getWorld(name) != null) {
            return null;
        }

        World world = plugin.getServer().createWorld(WorldCreator.name(name));

        File idFile = new File(name, InstanceWorld.ID_FILE_PREFIX + getName());
        try {
            idFile.createNewFile();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        InstanceWorld instance = new InstanceWorld(this, world, id);

        try {
            if (game) {
                signData.deserializeSigns((GameWorld) instance);
            } else {
                signData.deserializeSigns((EditWorld) instance);
            }

        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return instance;
    }

    /**
     * @return an instance of this world
     */
    public EditWorld instantiateAsEditWorld() {
        return (EditWorld) instantiate(false);
    }

    /**
     * @return an instance of this world
     */
    public GameWorld instantiateAsGameWorld() {
        return (GameWorld) instantiate(true);
    }

}
