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
package de.erethon.dungeonsxl.world;

import de.erethon.commons.chat.MessageUtil;
import de.erethon.commons.misc.FileUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.event.editworld.EditWorldGenerateEvent;
import de.erethon.dungeonsxl.player.DEditPlayer;
import java.io.File;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

/**
 * This class represents unloaded worlds.
 *
 * @author Daniel Saukel
 */
public class DResourceWorld {

    private DungeonsXL plugin;
    private DWorldCache worlds;

    private File folder;
    private WorldConfig config;
    private SignData signData;

    public DResourceWorld(DungeonsXL plugin, String name) {
        this.plugin = plugin;
        worlds = plugin.getDWorldCache();

        folder = new File(DungeonsXL.MAPS, name);
        if (!folder.exists()) {
            folder.mkdir();
        }

        File configFile = new File(folder, "config.yml");
        if (configFile.exists()) {
            config = new WorldConfig(plugin, configFile);
        }

        File signDataFile = new File(folder, "DXLData.data");
        signData = new SignData(plugin, signDataFile);
    }

    public DResourceWorld(DungeonsXL plugin, File folder) {
        this.plugin = plugin;
        worlds = plugin.getDWorldCache();

        this.folder = folder;

        File configFile = new File(folder, "config.yml");
        if (configFile.exists()) {
            config = new WorldConfig(plugin, configFile);
        }

        File signDataFile = new File(folder, "DXLData.data");
        signData = new SignData(plugin, signDataFile);
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
     * @param name the name to set
     */
    public void setName(String name) {
        folder.renameTo(new File(folder.getParentFile(), name));
        folder = new File(folder.getParentFile(), name);
    }

    /**
     * @return the WorldConfig
     */
    public WorldConfig getConfig() {
        return getConfig(false);
    }

    /**
     * @param generate if a config should be generated if none exists
     * @return the WorldConfig
     */
    public WorldConfig getConfig(boolean generate) {
        if (config == null) {
            File file = new File(folder, "config.yml");
            if (file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
            config = new WorldConfig(plugin, file);
        }

        return config;
    }

    public Environment getWorldEnvironment() {
        return (config != null && config.getWorldEnvironment() != null) ? config.getWorldEnvironment() : Environment.NORMAL;
    }

    /**
     * @return the DXLData.data file
     */
    public SignData getSignData() {
        return signData;
    }

    /**
     * @param player the player to invite
     */
    public void addInvitedPlayer(OfflinePlayer player) {
        if (config == null) {
            config = new WorldConfig(plugin);
        }

        config.addInvitedPlayer(player.getUniqueId().toString());
        config.save();
    }

    /**
     * @param player the player to uninvite
     * @return if the action was successful
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
     * @param player the player to check
     * @return if the player is invited
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
     */
    public void backup() {
        File target = new File(DungeonsXL.BACKUPS, getName() + "-" + System.currentTimeMillis());
        FileUtil.copyDir(folder, target);
    }

    /**
     * @param game whether the instance is a DGameWorld
     * @return an instance of this world
     */
    public DInstanceWorld instantiate(boolean game) {
        plugin.setLoadingWorld(true);
        int id = worlds.generateId();
        String name = worlds.generateName(game, id);

        File instanceFolder = new File(Bukkit.getWorldContainer(), name);
        while (instanceFolder.exists()) {
            World world = Bukkit.getWorld(name);
            boolean removed = false;
            if (world != null && world.getPlayers().isEmpty()) {
                Bukkit.unloadWorld(name, false);
            }
            if (world == null || world.getPlayers().isEmpty()) {
                removed = instanceFolder.delete();
            }
            if (!removed) {
                MessageUtil.log(plugin, "&6Warning: An unrecognized junk instance (&4" + name + "&6) has been found, but could not be deleted.");
                id++;
                name = worlds.generateName(game, id);
                instanceFolder = new File(Bukkit.getWorldContainer(), name);
            }
        }

        DInstanceWorld instance = game ? new DGameWorld(plugin, this, instanceFolder, id) : new DEditWorld(plugin, this, instanceFolder, id);

        FileUtil.copyDir(folder, instanceFolder, DungeonsXL.EXCLUDED_FILES);
        instance.world = Bukkit.createWorld(WorldCreator.name(name).environment(getWorldEnvironment()));
        if (Bukkit.getPluginManager().isPluginEnabled("dynmap")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "dynmap pause all");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "dmap worldset " + name + " enabled:false");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "dynmap pause none");
        }

        if (game) {
            signData.deserializeSigns((DGameWorld) instance);
        } else {
            signData.deserializeSigns((DEditWorld) instance);
        }

        plugin.setLoadingWorld(false);
        return instance;
    }

    /**
     * @param ignoreLimit if the global instance limit shall be ignored
     * @return an old or a new instance of this world.
     */
    public DEditWorld instantiateAsEditWorld(boolean ignoreLimit) {
        if (plugin.isLoadingWorld()) {
            return null;
        }
        if (!ignoreLimit && plugin.getMainConfig().getMaxInstances() <= worlds.getInstances().size()) {
            return null;
        }

        for (DEditWorld instance : worlds.getEditWorlds()) {
            if (instance.getName().equals(getName())) {
                return instance;
            }
        }

        DEditWorld editWorld = (DEditWorld) instantiate(false);
        return editWorld;
    }

    /**
     * @param ignoreLimit if the global instance limit shall be ignored
     * @return a new instance of this world
     */
    public DGameWorld instantiateAsGameWorld(boolean ignoreLimit) {
        if (plugin.isLoadingWorld()) {
            return null;
        }
        if (!ignoreLimit && plugin.getMainConfig().getMaxInstances() <= worlds.getInstances().size()) {
            return null;
        }
        return (DGameWorld) instantiate(true);
    }

    /**
     * Generate a new DResourceWorld.
     *
     * @return the automatically created DEditWorld instance
     */
    public DEditWorld generate() {
        String name = worlds.generateName(false);
        int id = worlds.generateId();
        File folder = new File(Bukkit.getWorldContainer(), name);
        WorldCreator creator = new WorldCreator(name);
        creator.type(WorldType.FLAT);
        creator.generateStructures(false);

        DEditWorld editWorld = new DEditWorld(plugin, this, folder, id);

        EditWorldGenerateEvent event = new EditWorldGenerateEvent(editWorld);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return null;
        }

        if (!DWorldCache.RAW.exists()) {
            worlds.createRaw();
        }
        FileUtil.copyDir(DWorldCache.RAW, folder, DungeonsXL.EXCLUDED_FILES);
        editWorld.generateIdFile();
        editWorld.world = creator.createWorld();
        editWorld.generateIdFile();

        return editWorld;
    }

    @Override
    public String toString() {
        return "DResourceWorld{name=" + getName() + "}";
    }

}
