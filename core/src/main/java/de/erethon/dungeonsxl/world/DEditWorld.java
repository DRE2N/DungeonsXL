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

import de.erethon.commons.misc.FileUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.api.world.EditWorld;
import de.erethon.dungeonsxl.event.editworld.EditWorldSaveEvent;
import de.erethon.dungeonsxl.event.editworld.EditWorldUnloadEvent;
import java.io.File;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class DEditWorld extends DInstanceWorld implements EditWorld {

    public static String ID_FILE_PREFIX = ".id_";

    private File idFile;

    DEditWorld(DungeonsXL plugin, DResourceWorld resourceWorld, File folder, World world, int id) {
        super(plugin, resourceWorld, folder, world, id);
    }

    DEditWorld(DungeonsXL plugin, DResourceWorld resourceWorld, File folder, int id) {
        this(plugin, resourceWorld, folder, null, id);
    }

    /* Getters and setters */
    /**
     * Returns the file that stores the ID
     *
     * @return the file that stores the ID
     */
    public File getIdFile() {
        return idFile;
    }

    /**
     * Generates an ID file for identification upon server restarts
     */
    public void generateIdFile() {
        try {
            idFile = new File(getFolder(), ID_FILE_PREFIX + getName());
            idFile.createNewFile();

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /* Actions */
    @Override
    public void registerSign(Block block) {
        if (block.getState() instanceof Sign) {
            Sign sign = (Sign) block.getState();
            String[] lines = sign.getLines();

            if (lines[0].equalsIgnoreCase("[lobby]")) {
                setLobbyLocation(block.getLocation());
            }
        }
    }

    @Override
    public void save() {
        EditWorldSaveEvent event = new EditWorldSaveEvent(this);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        getWorld().save();

        FileUtil.copyDir(getFolder(), getResource().getFolder(), DungeonsXL.EXCLUDED_FILES);
        DResourceWorld.deleteUnusedFiles(getResource().getFolder());

        getResource().getSignData().serializeSigns(signs.values());
    }

    @Override
    public void delete() {
        delete(true);
    }

    @Override
    public void delete(boolean save) {
        EditWorldUnloadEvent event = new EditWorldUnloadEvent(this, true);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        kickAllPlayers();

        if (save) {
            Bukkit.unloadWorld(getWorld(), true);
        }
        FileUtil.copyDir(getFolder(), getResource().getFolder(), DungeonsXL.EXCLUDED_FILES);
        DResourceWorld.deleteUnusedFiles(getResource().getFolder());
        if (!save) {
            Bukkit.unloadWorld(getWorld(), true);
        }

        FileUtil.removeDir(getFolder());
        plugin.getInstanceCache().remove(this);
    }

}
