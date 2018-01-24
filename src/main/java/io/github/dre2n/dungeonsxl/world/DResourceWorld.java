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
import io.github.dre2n.commons.worldloader.WorldLoader;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.event.editworld.EditWorldGenerateEvent;
import io.github.dre2n.dungeonsxl.player.DEditPlayer;
import java.io.File;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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
    DWorldCache worlds;

    private File folder;
    private WorldConfig config;
    private SignData signData;

    public DResourceWorld(DWorldCache worlds, String name) {
        this.worlds = worlds;

        folder = new File(DungeonsXL.MAPS, name);
        if (!folder.exists()) {
            folder.mkdir();
        }

        File configFile = new File(folder, "config.yml");
        if (configFile.exists()) {
            config = new WorldConfig(configFile);
        }

        File signDataFile = new File(folder, "DXLData.data");
        signData = new SignData(signDataFile);
    }

    public DResourceWorld(DWorldCache worlds, File folder) {
        this.worlds = worlds;

        this.folder = folder;

        File configFile = new File(folder, "config.yml");
        if (configFile.exists()) {
            config = new WorldConfig(configFile);
        }

        File signDataFile = new File(folder, "DXLData.data");
        signData = new SignData(signDataFile);
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
        folder = new File(folder.getParentFile(), name);
    }

    /**
     * @return the WorldConfig
     */
    public WorldConfig getConfig() {
        return getConfig(false);
    }

    /**
     * @param generate
     * if a config should be generated if none exists
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
            config = new WorldConfig(file);
        }

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
    public DInstanceWorld instantiate(final boolean game) {
        int id = worlds.generateId();
        String name = worlds.generateName(game, id);
        final File instanceFolder = new File(Bukkit.getWorldContainer(), name);

        while (Bukkit.getWorld(name) != null) {
            id++;
            name = worlds.generateName(game, id);
        }

        final DInstanceWorld instance = game ? new DGameWorld(this, instanceFolder, id) : new DEditWorld(this, instanceFolder, id);

        if (!plugin.getMainConfig().areTweaksEnabled()) {
            FileUtil.copyDirectory(folder, instanceFolder, DungeonsXL.EXCLUDED_FILES);
            instance.world = Bukkit.createWorld(WorldCreator.name(name));

            if (game) {
                signData.deserializeSigns((DGameWorld) instance);
            } else {
                signData.deserializeSigns((DEditWorld) instance);
            }

        } else {
            new BukkitRunnable() {
                @Override
                public void run() {
                    FileUtil.copyDirectory(folder, instanceFolder, DungeonsXL.EXCLUDED_FILES);
                    instance.world = WorldLoader.createWorld(WorldCreator.name(instanceFolder.getName()));

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (game) {
                                signData.deserializeSigns((DGameWorld) instance);
                            } else {
                                signData.deserializeSigns((DEditWorld) instance);
                            }
                        }
                    }.runTask(plugin);
                }
            }.runTaskAsynchronously(plugin);
        }

        return instance;
    }

    /**
     * @return an old or a new instance of this world.
     */
    public DEditWorld instantiateAsEditWorld() {
        for (DEditWorld instance : worlds.getEditWorlds()) {
            if (instance.getName().equals(getName())) {
                return instance;
            }
        }

        return (DEditWorld) instantiate(false);
    }

    /**
     * @return a new instance of this world
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
        final String name = worlds.generateName(false);
        int id = worlds.generateId();
        final File folder = new File(Bukkit.getWorldContainer(), name);
        final WorldCreator creator = new WorldCreator(name);
        creator.type(WorldType.FLAT);
        creator.generateStructures(false);

        final DEditWorld editWorld = new DEditWorld(this, folder, id);

        EditWorldGenerateEvent event = new EditWorldGenerateEvent(editWorld);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return null;
        }

        if (!plugin.getMainConfig().areTweaksEnabled()) {
            editWorld.world = creator.createWorld();

        } else {
            new BukkitRunnable() {
                @Override
                public void run() {
                    FileUtil.copyDirectory(DWorldCache.RAW, folder, DungeonsXL.EXCLUDED_FILES);
                    editWorld.generateIdFile();
                    editWorld.world = WorldLoader.createWorld(creator);
                }
            }.runTaskAsynchronously(plugin);
        }

        return editWorld;
    }

}
