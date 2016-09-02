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
package io.github.dre2n.dungeonsxl.global;

import io.github.dre2n.commons.util.messageutil.MessageUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.DMessages;
import io.github.dre2n.dungeonsxl.player.DGlobalPlayer;
import java.io.File;
import java.util.Collection;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * @author Daniel Saukel
 */
public abstract class GlobalProtection {

    static DungeonsXL plugin = DungeonsXL.getInstance();
    static FileConfiguration config = plugin.getGlobalData().getConfig();
    static GlobalProtections protections = plugin.getGlobalProtections();

    private World world;
    private int id;

    protected GlobalProtection(World world, int id) {
        this.world = world;
        this.id = id;

        protections.addProtection(this);
    }

    /**
     * @return the world
     */
    public World getWorld() {
        return world;
    }

    /**
     * @param world
     * the world to set
     */
    public void setWorld(World world) {
        this.world = world;
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
     * @param file
     * the file to save the protection to
     */
    public void save(File file) {
        save(YamlConfiguration.loadConfiguration(file));
    }

    public boolean onBreak(DGlobalPlayer dPlayer) {
        if (dPlayer.isInBreakMode()) {
            delete();
            MessageUtil.sendMessage(dPlayer.getPlayer(), plugin.getMessageConfig().getMessage(DMessages.PLAYER_PROTECTED_BLOCK_DELETED));
            MessageUtil.sendMessage(dPlayer.getPlayer(), plugin.getMessageConfig().getMessage(DMessages.CMD_BREAK_PROTECTED_MODE));
            dPlayer.setInBreakMode(false);
            return false;

        } else {
            return true;
        }
    }

    /* Abstracts */
    /**
     * @param config
     * the config to save the protection to
     */
    public abstract void save(FileConfiguration config);

    /**
     * @return a collection of all blocks covered by this protection
     */
    public abstract Collection<Block> getBlocks();

}
