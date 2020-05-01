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

import de.erethon.commons.compatibility.Version;
import de.erethon.commons.misc.FileUtil;
import de.erethon.commons.misc.ProgressBar;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.api.event.world.EditWorldSaveEvent;
import de.erethon.dungeonsxl.api.event.world.EditWorldUnloadEvent;
import de.erethon.dungeonsxl.api.world.EditWorld;
import de.erethon.dungeonsxl.player.DEditPlayer;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class DEditWorld extends DInstanceWorld implements EditWorld {

    public static String ID_FILE_PREFIX = ".id_";

    private File idFile;

    DEditWorld(DungeonsXL plugin, DResourceWorld resourceWorld, File folder, int id) {
        super(plugin, resourceWorld, folder, id);
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

        plugin.setLoadingWorld(true);
        List<Player> players = getWorld().getPlayers();
        kickAllPlayers();

        getResource().editWorld = null;
        plugin.getInstanceCache().remove(this);
        getResource().getSignData().serializeSigns(signs.values());
        Bukkit.unloadWorld(getWorld(), true);
        new ProgressBar(players, plugin.getMainConfig().getEditInstanceRemovalDelay()) {
            @Override
            public void onFinish() {
                getResource().clearFolder();
                FileUtil.copyDir(getFolder(), getResource().getFolder(), DungeonsXL.EXCLUDED_FILES);
                DResourceWorld.deleteUnusedFiles(getResource().getFolder());
                FileUtil.removeDir(getFolder());

                plugin.setLoadingWorld(false);
                EditWorld newEditWorld = getResource().getOrInstantiateEditWorld(true);
                players.forEach(p -> {
                    if (p.isOnline()) {
                        new DEditPlayer(plugin, p, newEditWorld);
                    }
                });
            }
        }.send(plugin);
    }

    public void forceSave() {
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
            getResource().getSignData().serializeSigns(signs.values());
            Bukkit.unloadWorld(getWorld(), true);
            new BukkitRunnable() {
                @Override
                public void run() {
                    getResource().clearFolder();
                    FileUtil.copyDir(getFolder(), getResource().getFolder(), DungeonsXL.EXCLUDED_FILES);
                    DResourceWorld.deleteUnusedFiles(getResource().getFolder());
                    FileUtil.removeDir(getFolder());
                }
            }.runTaskLaterAsynchronously(plugin, plugin.getMainConfig().getEditInstanceRemovalDelay() * 20L);
        }
        if (!save) {
            Bukkit.unloadWorld(getWorld(), /* SPIGOT-5225 */ !Version.isAtLeast(Version.MC1_14_4));
            new BukkitRunnable() {
                @Override
                public void run() {
                    FileUtil.removeDir(getFolder());
                }
            }.runTaskLaterAsynchronously(plugin, plugin.getMainConfig().getEditInstanceRemovalDelay() * 20L);
        }

        getResource().editWorld = null;
        plugin.getInstanceCache().remove(this);
    }

}
