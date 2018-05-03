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

import de.erethon.commons.chat.MessageUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.game.GameRuleProvider;
import de.erethon.dungeonsxl.player.DGamePlayer;
import java.io.File;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * An instance of a resource world.
 *
 * @author Daniel Saukel
 */
public abstract class DInstanceWorld {

    DungeonsXL plugin = DungeonsXL.getInstance();
    DWorldCache worlds = plugin.getDWorlds();

    private DResourceWorld resourceWorld;
    private File folder;
    World world;
    private int id;
    private Location lobby;

    DInstanceWorld(DResourceWorld resourceWorld, File folder, World world, int id) {
        this.resourceWorld = resourceWorld;
        this.folder = folder;
        this.world = world;
        this.id = id;

        worlds.addInstance(this);
    }

    /* Getters and setters */
    /**
     * @return the name of the DResourceWorld
     */
    public String getName() {
        return resourceWorld.getName();
    }

    /**
     * @return the WorldConfig
     */
    public WorldConfig getConfig() {
        return resourceWorld.getConfig();
    }

    /**
     * @return the DResourceWorld of that this world is an instance
     */
    public DResourceWorld getResource() {
        return resourceWorld;
    }

    /**
     * @return the folder of the instance
     */
    public File getFolder() {
        return folder;
    }

    /**
     * @return the instance
     */
    public World getWorld() {
        return world;
    }

    /**
     * @return false if this instance does not have a world, yet
     */
    public boolean exists() {
        return world != null;
    }

    /**
     * @return the unique ID
     */
    public int getId() {
        return id;
    }

    /**
     * @return the location where the player spawns
     */
    public Location getLobbyLocation() {
        return lobby;
    }

    /**
     * @param lobby
     * the spawn location to set
     */
    public void setLobbyLocation(Location lobby) {
        this.lobby = lobby;
    }

    /* Actions */
    /**
     * Sends a message to all players in the instance.
     *
     * @param message
     * the message to send
     */
    public void sendMessage(String message) {
        for (DGamePlayer dPlayer : DGamePlayer.getByWorld(world)) {
            MessageUtil.sendMessage(dPlayer.getPlayer(), message);
        }
    }

    /**
     * @param rules
     * sets up the time and weather to match the rules
     */
    public void setWeather(GameRuleProvider rules) {
        if (world == null) {
            return;
        }

        if (rules.isThundering() != null) {
            if (rules.isThundering()) {
                world.setThundering(true);
                world.setStorm(true);
                world.setThunderDuration(Integer.MAX_VALUE);
            } else {
                world.setThundering(false);
                world.setStorm(false);
            }
        }

        if (rules.getTime() != null) {
            world.setTime(rules.getTime());
        }
    }

    /* Abstracts */
    /**
     * Deletes this instance.
     */
    public abstract void delete();

}
