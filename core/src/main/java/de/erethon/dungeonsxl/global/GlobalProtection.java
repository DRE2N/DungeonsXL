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
package de.erethon.dungeonsxl.global;

import de.erethon.commons.chat.MessageUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.player.DGlobalPlayer;
import java.io.File;
import java.util.Collection;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * @author Daniel Saukel
 */
public abstract class GlobalProtection {

    protected DungeonsXL plugin;
    protected GlobalProtectionCache protections;
    private FileConfiguration config;

    public static final String SIGN_TAG = "[DXL]";

    private String world;
    private int id;

    protected GlobalProtection(DungeonsXL plugin, World world, int id) {
        this.plugin = plugin;
        protections = plugin.getGlobalProtectionCache();
        config = plugin.getGlobalData().getConfig();

        this.world = world.getName();
        this.id = id;

        protections.addProtection(this);
    }

    /**
     * @return the world
     */
    public World getWorld() {
        return Bukkit.getWorld(world);
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /* Actions */
    /**
     * Delete this protection.
     */
    public void delete() {
        protections.removeProtection(this);
    }

    /**
     * Save the data to the default file
     */
    public void save() {
        save(config);
    }

    /**
     * @param file the file to save the protection to
     */
    public void save(File file) {
        save(YamlConfiguration.loadConfiguration(file));
    }

    public boolean onBreak(DGlobalPlayer dPlayer) {
        if (dPlayer.isInBreakMode()) {
            delete();
            MessageUtil.sendMessage(dPlayer.getPlayer(), DMessage.PLAYER_PROTECTED_BLOCK_DELETED.getMessage());
            MessageUtil.sendMessage(dPlayer.getPlayer(), DMessage.CMD_BREAK_PROTECTED_MODE.getMessage());
            dPlayer.setInBreakMode(false);
            return false;

        } else {
            return true;
        }
    }

    /* Abstracts */
    /**
     * @param config the config to save the protection to
     */
    public abstract void save(FileConfiguration config);

    /**
     * @return a collection of all blocks covered by this protection
     */
    public abstract Collection<Block> getBlocks();

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{ID=" + id + "}";
    }

}
