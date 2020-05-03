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
import de.erethon.commons.compatibility.Internals;
import de.erethon.commons.compatibility.Version;
import de.erethon.commons.misc.FileUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.api.dungeon.Dungeon;
import de.erethon.dungeonsxl.api.dungeon.GameRuleContainer;
import de.erethon.dungeonsxl.api.event.world.EditWorldGenerateEvent;
import de.erethon.dungeonsxl.api.player.EditPlayer;
import de.erethon.dungeonsxl.api.world.EditWorld;
import de.erethon.dungeonsxl.api.world.GameWorld;
import de.erethon.dungeonsxl.api.world.ResourceWorld;
import java.io.File;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

/**
 * @author Daniel Saukel
 */
public class DResourceWorld implements ResourceWorld {

    public static final File RAW = new File(DungeonsXL.MAPS, ".raw");

    private DungeonsXL plugin;

    private File folder;
    private WorldConfig config;
    private SignData signData;
    EditWorld editWorld;

    public DResourceWorld(DungeonsXL plugin, String name) {
        this.plugin = plugin;

        folder = new File(DungeonsXL.MAPS, name);
        if (!folder.exists()) {
            folder.mkdir();
        }

        File configFile = new File(folder, WorldConfig.FILE_NAME);
        if (configFile.exists()) {
            config = new WorldConfig(plugin, configFile);
        }

        signData = new SignData(new File(folder, SignData.FILE_NAME));
    }

    public DResourceWorld(DungeonsXL plugin, File folder) {
        this.plugin = plugin;

        this.folder = folder;

        File configFile = new File(folder, WorldConfig.FILE_NAME);
        if (configFile.exists()) {
            config = new WorldConfig(plugin, configFile);
        }

        signData = new SignData(new File(folder, SignData.FILE_NAME));
    }

    /* Getters and setters */
    @Override
    public String getName() {
        return folder.getName();
    }

    @Override
    public void setName(String name) {
        folder.renameTo(new File(folder.getParentFile(), name));
        folder = new File(folder.getParentFile(), name);
    }

    @Override
    public File getFolder() {
        return folder;
    }

    @Override
    public GameRuleContainer getRules() {
        return getConfig(false);
    }

    /**
     * Returns the config of this world.
     *
     * @param generate if a config should be generated if none exists
     * @return the config of this world
     */
    public WorldConfig getConfig(boolean generate) {
        if (config == null) {
            File file = new File(folder, WorldConfig.FILE_NAME);
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

    @Override
    public Environment getWorldEnvironment() {
        return (config != null && config.getWorldEnvironment() != null) ? config.getWorldEnvironment() : Environment.NORMAL;
    }

    @Override
    public void addInvitedPlayer(OfflinePlayer player) {
        if (config == null) {
            config = new WorldConfig(plugin);
        }

        config.addInvitedPlayer(player.getUniqueId().toString());
        config.save();
    }

    @Override
    public boolean removeInvitedPlayer(OfflinePlayer player) {
        if (config == null) {
            return false;
        }

        config.removeInvitedPlayers(player.getUniqueId().toString(), player.getName().toLowerCase());
        config.save();

        EditPlayer editPlayer = plugin.getPlayerCache().getEditPlayer(player.getPlayer());
        if (editPlayer != null) {
            if (plugin.getEditWorld(editPlayer.getWorld()).getResource() == this) {
                editPlayer.leave();
            }
        }

        return true;
    }

    @Override
    public boolean isInvitedPlayer(OfflinePlayer player) {
        if (config == null) {
            return false;
        }

        return config.getInvitedPlayers().contains(player.getName().toLowerCase()) || config.getInvitedPlayers().contains(player.getUniqueId().toString());
    }

    /* Actions */
    @Override
    public void backup() {
        File target = new File(DungeonsXL.BACKUPS, getName() + "-" + System.currentTimeMillis());
        FileUtil.copyDir(folder, target);
    }

    public DInstanceWorld instantiate(boolean game) {
        plugin.setLoadingWorld(true);
        int id = DInstanceWorld.counter;
        String name = DInstanceWorld.generateName(game, id);

        File instanceFolder = new File(Bukkit.getWorldContainer(), name);
        while (instanceFolder.exists()) {
            World world = Bukkit.getWorld(name);
            boolean removed = false;
            if (world != null && world.getPlayers().isEmpty()) {
                Bukkit.unloadWorld(name, /* SPIGOT-5225 */ !Version.isAtLeast(Version.MC1_14_4));
            }
            if (world == null || world.getPlayers().isEmpty()) {
                removed = instanceFolder.delete();
            }
            if (!removed) {
                MessageUtil.log(plugin, "&6Warning: An unrecognized junk instance (&4" + name + "&6) has been found, but could not be deleted.");
                id++;
                name = DInstanceWorld.generateName(game, id);
                instanceFolder = new File(Bukkit.getWorldContainer(), name);
            }
        }

        DInstanceWorld instance = game ? new DGameWorld(plugin, this, instanceFolder) : new DEditWorld(plugin, this, instanceFolder);

        FileUtil.copyDir(folder, instanceFolder, DungeonsXL.EXCLUDED_FILES);
        instance.world = Bukkit.createWorld(WorldCreator.name(name).environment(getWorldEnvironment())).getName();
        if (Internals.isAtLeast(Internals.v1_13_R1)) {
            instance.getWorld().setGameRule(GameRule.DO_FIRE_TICK, false);
        }
        if (Bukkit.getPluginManager().isPluginEnabled("dynmap")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "dynmap pause all");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "dmap worldset " + name + " enabled:false");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "dynmap pause none");
        }

        if (game) {
            signData.deserializeSigns((DGameWorld) instance);
            instance.getWorld().setAutoSave(false);
        } else {
            signData.deserializeSigns((DEditWorld) instance);
        }

        plugin.setLoadingWorld(false);
        return instance;
    }

    @Override
    public EditWorld getEditWorld() {
        return editWorld;
    }

    @Override
    public EditWorld getOrInstantiateEditWorld(boolean ignoreLimit) {
        if (editWorld != null) {
            return editWorld;
        }
        if (plugin.isLoadingWorld()) {
            return null;
        }
        if (!ignoreLimit && plugin.getMainConfig().getMaxInstances() <= plugin.getInstanceCache().size()) {
            return null;
        }

        this.editWorld = (EditWorld) instantiate(false);
        return editWorld;
    }

    @Override
    public GameWorld instantiateGameWorld(boolean ignoreLimit) {
        if (plugin.isLoadingWorld()) {
            return null;
        }
        if (!ignoreLimit && plugin.getMainConfig().getMaxInstances() <= plugin.getInstanceCache().size()) {
            return null;
        }
        return (DGameWorld) instantiate(true);
    }

    @Override
    public Dungeon getSingleFloorDungeon() {
        return plugin.getDungeonRegistry().get(getName());
    }

    /**
     * Returns the DXLData.data file
     *
     * @return the DXLData.data file
     */
    public SignData getSignData() {
        return signData;
    }

    /**
     * Generate a new DResourceWorld.
     *
     * @return the automatically created DEditWorld instance
     */
    public DEditWorld generate() {
        int id = DInstanceWorld.counter;
        String name = DInstanceWorld.generateName(false, id);
        File folder = new File(Bukkit.getWorldContainer(), name);
        WorldCreator creator = new WorldCreator(name);
        creator.type(WorldType.FLAT);
        creator.generateStructures(false);

        DEditWorld editWorld = new DEditWorld(plugin, this, folder);

        EditWorldGenerateEvent event = new EditWorldGenerateEvent(editWorld);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return null;
        }

        if (!RAW.exists()) {
            createRaw();
        }
        FileUtil.copyDir(RAW, folder, DungeonsXL.EXCLUDED_FILES);
        editWorld.generateIdFile();
        editWorld.world = creator.createWorld().getName();
        editWorld.generateIdFile();

        return editWorld;
    }

    void clearFolder() {
        for (File file : FileUtil.getFilesForFolder(getFolder())) {
            if (file.getName().equals(SignData.FILE_NAME) || file.getName().equals(WorldConfig.FILE_NAME)) {
                continue;
            }
            if (file.isDirectory()) {
                FileUtil.removeDir(file);
            } else {
                file.delete();
            }
        }
    }

    /**
     * Removes files that are not needed from a world
     *
     * @param dir the directory to purge
     */
    public static void deleteUnusedFiles(File dir) {
        for (File file : dir.listFiles()) {
            if (file.getName().equalsIgnoreCase("uid.dat") || file.getName().contains(".id_")) {
                file.delete();
            }
        }
    }

    /**
     * Creates the "raw" world that is copied for new instances.
     */
    public static void createRaw() {
        WorldCreator rawCreator = WorldCreator.name(".raw");
        rawCreator.type(WorldType.FLAT);
        rawCreator.generateStructures(false);
        World world = rawCreator.createWorld();
        File worldFolder = new File(Bukkit.getWorldContainer(), ".raw");
        FileUtil.copyDir(worldFolder, RAW, DungeonsXL.EXCLUDED_FILES);
        Bukkit.unloadWorld(world, /* SPIGOT-5225 */ !Version.isAtLeast(Version.MC1_14_4));
        FileUtil.removeDir(worldFolder);
    }

    @Override
    public String toString() {
        return "DResourceWorld{name=" + getName() + "}";
    }

}
