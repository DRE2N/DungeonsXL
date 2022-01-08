/*
 * Copyright (C) 2014-2022 Daniel Saukel
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
import de.erethon.dungeonsxl.api.dungeon.Game;
import de.erethon.dungeonsxl.api.dungeon.GameRuleContainer;
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
// Implementation-specific methods: getSignData, generate
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
     * Returns the {@link de.erethon.dungeonsxl.api.dungeon.GameRule}s of this world.
     * <p>
     * Note that these are only the rules that are specific to the map itself. They are not the rules that are actually used in a game instance instantiated
     * from this resource world as these ones may be supplemented or overriden by other rules taken from the main config, dungeon config or the
     * {@link de.erethon.dungeonsxl.api.dungeon.GameRule#DEFAULT_VALUES}.
     *
     * @return the {@link de.erethon.dungeonsxl.api.dungeon.GameRule}s of this world
     */
    GameRuleContainer getRules();

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
     * Removes a player from the list of players that are invited to edit the resource.
     *
     * @param player the player
     * @return if the action was successful
     */
    boolean removeInvitedPlayer(OfflinePlayer player);

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
     * Returns the loaded edit instance of this world or null if none exists.
     *
     * @return the loaded edit instance of this world or null if none exists
     */
    EditWorld getEditWorld();

    /**
     * Returns the loaded edit instance of this world or generates a new one if none exists.
     *
     * @param ignoreLimit if the instance limit set in the main config shall be ignored
     * @return the loaded edit instance of this world or generates a new one if none exists
     */
    EditWorld getOrInstantiateEditWorld(boolean ignoreLimit);

    /**
     * Returns a new game instance of this resource.
     *
     * @see de.erethon.dungeonsxl.api.dungeon.Game#ensureWorldIsLoaded(boolean)
     * @param game        the game the instance belongs to
     * @param ignoreLimit if the instance limit set in the main config shall be ignored
     * @return a new game instance of this resource
     */
    GameWorld instantiateGameWorld(Game game, boolean ignoreLimit);

    /**
     * Returns the single floor dungeon of this resource.
     *
     * @return the single floor dungeon of this resource
     */
    Dungeon getSingleFloorDungeon();

}
