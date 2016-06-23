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
package io.github.dre2n.dungeonsxl.world;

import io.github.dre2n.commons.util.FileUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.event.editworld.EditWorldSaveEvent;
import io.github.dre2n.dungeonsxl.event.editworld.EditWorldUnloadEvent;
import io.github.dre2n.dungeonsxl.player.DEditPlayer;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class EditWorld extends InstanceWorld {

    static Worlds worlds = plugin.getWorlds();

    private CopyOnWriteArrayList<Block> signs = new CopyOnWriteArrayList<>();

    EditWorld(ResourceWorld resourceWorld, File folder, World world, int id) {
        super(resourceWorld, folder, world, id);
    }

    /* Getters and setters */
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
        plugin.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        getWorld().save();

        FileUtil.copyDirectory(getFolder(), getResource().getFolder(), DungeonsXL.EXCLUDED_FILES);
        FileUtil.deleteUnusedFiles(getResource().getFolder());

        try {
            getResource().getSignData().serializeSigns(signs);
        } catch (IOException exception) {
        }
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
    public void delete(boolean save) {
        EditWorldUnloadEvent event = new EditWorldUnloadEvent(this, true);
        plugin.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        worlds.getInstances().remove(this);
        for (Player player : getWorld().getPlayers()) {
            DEditPlayer dPlayer = DEditPlayer.getByPlayer(player);
            dPlayer.leave();
        }

        if (save) {
            plugin.getServer().unloadWorld(getWorld(), true);
        }

        FileUtil.copyDirectory(getFolder(), getResource().getFolder(), DungeonsXL.EXCLUDED_FILES);
        FileUtil.deleteUnusedFiles(getResource().getFolder());

        if (!save) {
            plugin.getServer().unloadWorld(getWorld(), true);
        }

        FileUtil.removeDirectory(getFolder());
    }

    /* Statics */
    /**
     * @param world
     * the instance
     * @return
     * the EditWorld that represents the world
     */
    public static EditWorld getByWorld(World world) {
        return getByName(world.getName());
    }

    /**
     * @param world
     * the instance name
     * @return
     * the EditWorld that represents the world
     */
    public static EditWorld getByName(String name) {
        InstanceWorld instance = worlds.getInstanceByName(name);

        if (instance instanceof EditWorld) {
            return (EditWorld) instance;

        } else {
            return null;
        }
    }

}
