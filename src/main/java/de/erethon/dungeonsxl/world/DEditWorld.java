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
package de.erethon.dungeonsxl.world;

import de.erethon.commons.misc.FileUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.event.editworld.EditWorldSaveEvent;
import de.erethon.dungeonsxl.event.editworld.EditWorldUnloadEvent;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * A raw resource world instance to edit the dungeon map.
 * There is never more than one DEditWorld per DResourceWorld.
 *
 * @author Frank Baumann, Daniel Saukel
 */
public class DEditWorld extends DInstanceWorld {

    DWorldCache worlds = plugin.getDWorlds();

    public static String ID_FILE_PREFIX = ".id_";

    private File idFile;
    private CopyOnWriteArrayList<Block> signs = new CopyOnWriteArrayList<>();

    DEditWorld(DResourceWorld resourceWorld, File folder, World world, int id) {
        super(resourceWorld, folder, world, id);
    }

    DEditWorld(DResourceWorld resourceWorld, File folder, int id) {
        this(resourceWorld, folder, null, id);
    }

    /* Getters and setters */
    /**
     * @return the file that stores the ID
     */
    public File getIdFile() {
        return idFile;
    }

    /**
     * @return the ID file
     */
    public void generateIdFile() {
        try {
            idFile = new File(getFolder(), ID_FILE_PREFIX + getName());
            idFile.createNewFile();

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * @return the signs
     */
    public CopyOnWriteArrayList<Block> getSigns() {
        return signs;
    }

    /**
     * @param sign
     * the sign to set
     */
    public void setSigns(CopyOnWriteArrayList<Block> signs) {
        this.signs = signs;
    }

    /* Actions */
    /**
     * Registers the block as a DSign sothat it can later be saved persistently.
     *
     * @param block
     * a DSign block
     */
    public void registerSign(Block block) {
        if (block.getState() instanceof Sign) {
            Sign sign = (Sign) block.getState();
            String[] lines = sign.getLines();

            if (lines[0].equalsIgnoreCase("[lobby]")) {
                setLobbyLocation(block.getLocation());
            }
        }
    }

    /**
     * Saves the sign data and overrides the resource with the changes.
     */
    public void save() {
        EditWorldSaveEvent event = new EditWorldSaveEvent(this);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        getWorld().save();

        if (!plugin.getMainConfig().areTweaksEnabled()) {
            FileUtil.copyDir(getFolder(), getResource().getFolder(), DungeonsXL.EXCLUDED_FILES);
            DWorldCache.deleteUnusedFiles(getResource().getFolder());

        } else {
            new BukkitRunnable() {
                @Override
                public void run() {
                    FileUtil.copyDir(getFolder(), getResource().getFolder(), DungeonsXL.EXCLUDED_FILES);
                    DWorldCache.deleteUnusedFiles(getResource().getFolder());
                }
            }.runTaskAsynchronously(plugin);
        }

        getResource().getSignData().serializeSigns(signs);
    }

    @Override
    public void delete() {
        delete(true);
    }

    /**
     * Deletes this edit instance.
     *
     * @param save
     * whether this world should be saved
     */
    public void delete(final boolean save) {
        EditWorldUnloadEvent event = new EditWorldUnloadEvent(this, true);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        kickAllPlayers();

        if (!plugin.getMainConfig().areTweaksEnabled()) {
            if (save) {
                Bukkit.unloadWorld(getWorld(), true);
            }
            FileUtil.copyDir(getFolder(), getResource().getFolder(), DungeonsXL.EXCLUDED_FILES);
            DWorldCache.deleteUnusedFiles(getResource().getFolder());
            if (!save) {
                Bukkit.unloadWorld(getWorld(), true);
            }
            FileUtil.removeDir(getFolder());
            worlds.removeInstance(this);

        } else {
            final DEditWorld editWorld = this;
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (save) {
                        Bukkit.unloadWorld(getWorld(), true);
                    }
                    FileUtil.copyDir(getFolder(), getResource().getFolder(), DungeonsXL.EXCLUDED_FILES);
                    DWorldCache.deleteUnusedFiles(getResource().getFolder());
                    if (!save) {
                        Bukkit.unloadWorld(getWorld(), true);
                    }
                    FileUtil.removeDir(getFolder());
                    worlds.removeInstance(editWorld);
                }
            }.runTaskAsynchronously(plugin);
        }
    }

    /* Statics */
    /**
     * @param world
     * the instance
     * @return
     * the DEditWorld that represents the world
     */
    public static DEditWorld getByWorld(World world) {
        return getByName(world.getName());
    }

    /**
     * @param world
     * the instance name
     * @return
     * the DEditWorld that represents the world
     */
    public static DEditWorld getByName(String name) {
        DInstanceWorld instance = DungeonsXL.getInstance().getDWorlds().getInstanceByName(name);

        if (instance instanceof DEditWorld) {
            return (DEditWorld) instance;

        } else {
            return null;
        }
    }

}
