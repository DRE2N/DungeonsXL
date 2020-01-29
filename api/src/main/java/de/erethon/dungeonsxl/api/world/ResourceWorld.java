/*
 * Copyright (C) 2014-2020 Daniel Saukel
 *
 * This library is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNULesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.erethon.dungeonsxl.api.world;

import de.erethon.dungeonsxl.api.dungeon.Dungeon;
import java.io.File;
import org.bukkit.OfflinePlayer;
import org.bukkit.World.Environment;

/**
 * A stored world that can be instantiated as an {@link EditWorld} or as a {@link GameWorld}.
 * <p>
 * In the default implementation, these are saved under "plugins/DungeonsXL/maps/".
 * <p>
 * A resource world is not equal to a {@link de.erethon.dungeonsxl.api.dungeon.Dungeon}.
 *
 * @author Daniel Saukel
 */
// Implementation-specific methods: getConfig, getSignData, generate
public interface ResourceWorld {

    /**
     * Returns the name of this resource world.
     * <p>
     * Equals {@link #getFolder()}{@link File#getName() #getName()}.
     *
     * @return name of this resource world
     */
    String getName();

    /**
     * Renames the resource world and its folder.
     *
     * @param name the new name
     */
    void setName(String name);

    /**
     * Returns the folder where this resource is stored.
     *
     * @return the folder where this resource is stored
     */
    File getFolder();

    /**
     * Returns the environment of the world as defined in the config or {@link org.bukkit.World.Environment#NORMAL} if nothing is set.
     *
     * @return the environment of the world as defined in the config or {@link org.bukkit.World.Environment#NORMAL} if nothing is set
     */
    Environment getWorldEnvironment();

    /**
     * Adds the player to the list of players that are invited to edit the resource.
     *
     * @param player the player
     */
    void addInvitedPlayer(OfflinePlayer player);

    /**
     * Returns if the player is invited to edit the resource.
     *
     * @param player the player
     * @return if the player is invited to edit the resource
     */
    boolean isInvitedPlayer(OfflinePlayer player);

    /**
     * Creates a backup of the resource.
     */
    void backup();

    /**
     * Returns the loaded edit instance of this world or generates a new one if none exists.
     *
     * @return the loaded edit instance of this world or generates a new one if none exists
     */
    EditWorld getOrInstantiateEditWorld();

    /**
     * Returns a new game instance of this resource.
     *
     * @return a new game instance of this resource
     */
    GameWorld instantiateGameWorld();

    /**
     * Returns the single floor dungeon of this resource.
     *
     * @return the single floor dungeon of this resource
     */
    Dungeon getSingleFloorDungeon();

}
