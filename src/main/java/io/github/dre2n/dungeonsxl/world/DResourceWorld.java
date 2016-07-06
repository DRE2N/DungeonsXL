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
import io.github.dre2n.dungeonsxl.player.DEditPlayer;
import io.github.dre2n.dungeonsxl.task.BackupResourceTask;
import java.io.File;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * This class represents unloaded worlds.
 *
 * @author Daniel Saukel
 */
public class DResourceWorld {

    DungeonsXL plugin = DungeonsXL.getInstance();
    DWorlds worlds;

    private File folder;
    private WorldConfig config;
    private SignData signData;

    public DResourceWorld(DWorlds worlds, String name) {
        this.worlds = worlds;

        folder = new File(DungeonsXL.MAPS, name);
        if (!folder.exists()) {
            folder.mkdir();
        }

        File signDataFile = new File(folder, "DXLData.data");
        if (!signDataFile.exists()) {
            try {
                signDataFile.createNewFile();

            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        signData = new SignData(signDataFile);
    }

    public DResourceWorld(DWorlds worlds, File folder) {
        this.worlds = worlds;

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

    /**
     * @param player
     * the player to invite
     */
    public void addInvitedPlayer(OfflinePlayer player) {
        if (config == null) {
            config = new WorldConfig();
        }

        config.addInvitedPlayer(player.getUniqueId().toString());
        config.save();
    }

    /**
     * @param player
     * the player to uninvite
     */
    public boolean removeInvitedPlayer(OfflinePlayer player) {
        if (config == null) {
            return false;
        }

        config.removeInvitedPlayers(player.getUniqueId().toString(), player.getName().toLowerCase());
        config.save();

        DEditPlayer editPlayer = DEditPlayer.getByName(player.getName());
        if (editPlayer != null) {
            if (DEditWorld.getByWorld(editPlayer.getWorld()).getResource() == this) {
                editPlayer.leave();
            }
        }

        return true;
    }

    /**
     * @param player
     * the player to check
     */
    public boolean isInvitedPlayer(OfflinePlayer player) {
        if (config == null) {
            return false;
        }

        return config.getInvitedPlayers().contains(player.getName().toLowerCase()) || config.getInvitedPlayers().contains(player.getUniqueId().toString());
    }

    /* Actions */
    /**
     * Creates a backup of the resource
     *
     * @param async
     * whether the task shall be performed asyncronously
     */
    public void backup(boolean async) {
        BackupResourceTask task = new BackupResourceTask(this);
        if (async) {
            task.runTaskAsynchronously(plugin);
        } else {
            task.run();
        }
    }

    /**
     * @param game
     * whether the instance is a DGameWorld
     * @return an instance of this world
     */
    public DInstanceWorld instantiate(boolean game) {
        plugin.debug.start("DResourceWorld#instantiate");
        int id = worlds.generateId();
        String name = worlds.generateName(game);
        File instanceFolder = new File(Bukkit.getWorldContainer(), name);
        FileUtil.copyDirectory(folder, instanceFolder, DungeonsXL.EXCLUDED_FILES);

        if (Bukkit.getWorld(name) != null) {
            return null;
        }

        World world = plugin.getServer().createWorld(WorldCreator.name(name));

        DInstanceWorld instance = null;
        try {
            if (game) {
                instance = new DGameWorld(this, instanceFolder, world, id);
                signData.deserializeSigns((DGameWorld) instance);

            } else {
                instance = new DEditWorld(this, instanceFolder, world, id);
                signData.deserializeSigns((DEditWorld) instance);
            }

        } catch (IOException exception) {
            exception.printStackTrace();
        }

        plugin.debug.end("DResourceWorld#instantiate", true);
        return instance;
    }

    /**
     * @return an instance of this world
     */
    public DEditWorld instantiateAsEditWorld() {
        return (DEditWorld) instantiate(false);
    }

    /**
     * @return an instance of this world
     */
    public DGameWorld instantiateAsGameWorld() {
        return (DGameWorld) instantiate(true);
    }

    /**
     * Generate a new DResourceWorld.
     *
     * @return the automatically created DEditWorld instance
     */
    public DEditWorld generate() {
        plugin.debug.start("DResourceWorld#generate");
        String name = worlds.generateName(false);
        WorldCreator creator = WorldCreator.name(name);
        creator.type(WorldType.FLAT);
        creator.generateStructures(false);

        /*EditWorldGenerateEvent event = new EditWorldGenerateEvent(this);
        plugin.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }
         */
        int id = worlds.generateId();
        File folder = new File(Bukkit.getWorldContainer(), name);
        World world = plugin.getServer().createWorld(creator);

        DEditWorld editWorld = new DEditWorld(this, folder, world, id);

        plugin.debug.end("DResourceWorld#generate", true);
        return editWorld;
    }

}
