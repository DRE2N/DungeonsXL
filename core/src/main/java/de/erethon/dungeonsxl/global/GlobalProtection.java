/*
 * Copyright (C) 2012-2022 Frank Baumann
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

import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.player.DGlobalPlayer;
import de.erethon.dungeonsxl.util.commons.chat.MessageUtil;
import java.util.Collection;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Daniel Saukel
 */
public abstract class GlobalProtection {

    protected DungeonsXL plugin;

    public static final String SIGN_TAG = "[DXL]";

    private String world;
    private int id;

    protected GlobalProtection(DungeonsXL plugin, World world, int id) {
        this.plugin = plugin;

        this.world = world.getName();
        this.id = id;
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
        plugin.getGlobalProtectionCache().removeProtection(this);
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
     * @return the path in the global data file
     */
    public abstract String getDataPath();

    public abstract void save(ConfigurationSection config);

    /**
     * @return a collection of all blocks covered by this protection
     */
    public abstract Collection<Block> getBlocks();

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{ID=" + id + "}";
    }

}
