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

import de.erethon.dungeonsxl.api.player.InstancePlayer;
import de.erethon.dungeonsxl.api.sign.DungeonSign;
import java.io.File;
import java.util.Collection;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Super interface for worlds that are instantiated by DungeonsXL.
 * <p>
 * An instance world is not equal to a {@link de.erethon.dungeonsxl.api.dungeon.Dungeon}.
 *
 * @author Daniel Saukel
 */
// Implementation-specific methods: getConfig, exists, setWeather
public interface InstanceWorld {

    /**
     * Returns the name of the resource world of this instance.
     * <p>
     * Use {@link #getWorld()}{@link org.bukkit.World#getName() #getName()} to get the name of the instantiated world (like e.g. DXL_Game_1).
     *
     * @return the name of the resource world of this instance
     */
    String getName();

    /**
     * Returns the saved map this instance was loaded from.
     *
     * @return the saved map this instance was loaded from
     */
    ResourceWorld getResource();

    /**
     * Returns the world folder.
     *
     * @return the world folder
     */
    File getFolder();

    /**
     * Returns the wrapped Bukkit world.
     *
     * @return the wrapped Bukkit world
     */
    World getWorld();

    /**
     * Returns the ID. This is usually the number in the map name.
     *
     * @return the ID
     */
    int getId();

    /**
     * Returns a collection of the signs in this instance.
     *
     * @return a collection of the signs in this instance
     */
    Collection<DungeonSign> getDungeonSigns();

    /**
     * Adds a dungeon sign to this instance.
     *
     * @param sign the sign
     */
    void addDungeonSign(DungeonSign sign);

    /**
     * Removes a dungeon sign from this instance.
     *
     * @param sign the sign
     */
    void removeDungeonSign(DungeonSign sign);

    /**
     * Returns the location of the lobby where players spawn by default when they are teleported into the dungeon.
     *
     * @return the location of the lobby where players spawn by default when they are teleported into the dungeon
     */
    Location getLobbyLocation();

    /**
     * Sets the default spawn location of the instance.
     * <p>
     * This is not persistent and does not create a lobby sign.
     *
     * @param location the location
     */
    void setLobbyLocation(Location location);

    /**
     * Returns the players in the instance.
     *
     * @return the players in the instance
     */
    Collection<InstancePlayer> getPlayers();

    /**
     * Sends a message to all players in the instance.
     *
     * @param message the message to send
     */
    void sendMessage(String message);

    /**
     * Makes all players leave the world. Attempts to let them leave properly if they are correct DInstancePlayers; teleports them to the spawn if they are not.
     */
    void kickAllPlayers();

    /**
     * Deletes this instance.
     */
    void delete();

}
