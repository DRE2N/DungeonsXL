/*
 * Copyright (C) 2012-2013 Frank Baumann; 2015-2026 Daniel Saukel
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

import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.api.event.world.EditWorldSaveEvent;
import de.erethon.dungeonsxl.api.event.world.EditWorldUnloadEvent;
import de.erethon.dungeonsxl.api.event.world.InstanceWorldPostUnloadEvent;
import de.erethon.dungeonsxl.api.world.EditWorld;
import de.erethon.dungeonsxl.dungeon.DDungeon;
import de.erethon.dungeonsxl.mob.CitizensMobProvider;
import de.erethon.dungeonsxl.player.DEditPlayer;
import de.erethon.xlib.chat.MessageUtil;
import de.erethon.xlib.compatibility.Version;
import de.erethon.xlib.util.FileUtil;
import de.erethon.xlib.util.ProgressBar;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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

    public DEditWorld(DungeonsXL plugin, DDungeon dungeon, File folder) {
        super(plugin, dungeon, folder);
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
        Map<Player, double[]> players = new HashMap<>();
        getWorld().getPlayers().forEach(p -> players.put(p,
                new double[]{
                    p.getLocation().getX(),
                    p.getLocation().getY(),
                    p.getLocation().getZ(),
                    p.getLocation().getYaw(),
                    p.getLocation().getPitch()
                }
        ));
        kickAllPlayers();

        dungeon.setEditWorld(null);
        plugin.getInstanceCache().remove(this);
        dungeon.getSignData().serializeSigns(signs.values());
        Bukkit.unloadWorld(getWorld(), true);
        new ProgressBar(players.keySet(), plugin.getMainConfig().getEditInstanceRemovalDelay()) {
            @Override
            public void onFinish() {
                dungeon.clearFolder();
                FileUtil.copyDir(getFolder(), getDungeon().getFolder(), DungeonsXL.EXCLUDED_FILES);
                DDungeon.deleteUnusedFiles(getDungeon().getFolder());
                FileUtil.removeDir(getFolder());

                plugin.setLoadingWorld(false);
                EditWorld newEditWorld = getDungeon().getOrInstantiateEditWorld(true);
                players.keySet().forEach(p -> {
                    if (p.isOnline()) {
                        new DEditPlayer(plugin, p, newEditWorld);
                        double[] coords = players.get(p);
                        p.teleport(new Location(newEditWorld.getWorld(), coords[0], coords[1], coords[2], (float) coords[3], (float) coords[4]));
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

        FileUtil.copyDir(getFolder(), getDungeon().getFolder(), DungeonsXL.EXCLUDED_FILES);
        DDungeon.deleteUnusedFiles(getDungeon().getFolder());

        dungeon.getSignData().serializeSigns(signs.values());
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

        if (Bukkit.getPluginManager().getPlugin("Citizens") != null) {
            ((CitizensMobProvider) plugin.getExternalMobProviderRegistry().get("CI")).removeSpawnedNPCs(getWorld());
        }

        kickAllPlayers();

        String name = getWorld().getName();
        if (save) {
            dungeon.getSignData().serializeSigns(signs.values());
            boolean unloaded = Bukkit.unloadWorld(getWorld(), true);
            if (!unloaded) {
                MessageUtil.debug(plugin, "Error: Could not unload world " + getWorld());
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    dungeon.clearFolder();
                    FileUtil.copyDir(getFolder(), getDungeon().getFolder(), DungeonsXL.EXCLUDED_FILES);
                    DDungeon.deleteUnusedFiles(getDungeon().getFolder());
                    if (unloaded) {
                        FileUtil.removeDir(getFolder());
                    }
                    Bukkit.getPluginManager().callEvent(new InstanceWorldPostUnloadEvent(getDungeon(), name));
                }
            }.runTaskLater(plugin, plugin.getMainConfig().getEditInstanceRemovalDelay() * 20L);
        }
        if (!save) {
            boolean unloaded = Bukkit.unloadWorld(getWorld(), /* SPIGOT-5225 */ !Version.isAtLeast(Version.MC1_14_4));
            if (!unloaded) {
                MessageUtil.debug(plugin, "Error: Could not unload world " + getWorld());
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (unloaded) {
                        FileUtil.removeDir(getFolder());
                    }
                    Bukkit.getPluginManager().callEvent(new InstanceWorldPostUnloadEvent(getDungeon(), name));
                }
            }.runTaskLater(plugin, plugin.getMainConfig().getEditInstanceRemovalDelay() * 20L);
        }

        dungeon.setEditWorld(null);
        plugin.getInstanceCache().remove(this);
    }

}
