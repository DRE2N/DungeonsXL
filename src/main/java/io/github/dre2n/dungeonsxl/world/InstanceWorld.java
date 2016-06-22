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

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.WorldConfig;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * @author Daniel Saukel
 */
public class InstanceWorld {

    DungeonsXL plugin = DungeonsXL.getInstance();

    public static String ID_FILE_PREFIX = ".id_";

    private ResourceWorld resourceWorld;
    private World world;
    private int id;
    private Location lobby;

    InstanceWorld(ResourceWorld resourceWorld, World world, int id) {
        this.resourceWorld = resourceWorld;
        this.world = world;
        this.id = id;
    }

    /* Getters and setters */
    /**
     * @return the name of the ResourceWorld
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
     * @return the instance
     */
    public World getWorld() {
        return world;
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

    /**
     * @return the ResourceWorld of that this world is an instance
     */
    public ResourceWorld getResource() {
        return resourceWorld;
    }

}
