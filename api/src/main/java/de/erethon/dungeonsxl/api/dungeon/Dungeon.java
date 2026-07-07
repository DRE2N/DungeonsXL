/*
 * Copyright (C) 2015-2026 Daniel Saukel
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
package de.erethon.dungeonsxl.api.dungeon;

import de.erethon.dungeonsxl.api.world.EditWorld;
import de.erethon.dungeonsxl.api.world.GameWorld;
import java.io.File;
import org.bukkit.OfflinePlayer;
import org.bukkit.World.Environment;

/**
 * A stored world that can be instantiated as an {@link EditWorld} or as a {@link GameWorld}.
 * <p>
 * In the default implementation, these are saved under "plugins/DungeonsXL/maps/".
 *
 * @author Daniel Saukel
 */
// Implementation-specific methods: getSignData, generate
public interface Dungeon {

    /**
     * Returns the name of this dungeon.
     * <p>
     * Equals {@link #getFolder()}{@link File#getName() #getName()}.
     *
     * @return name of this dungeon
     */
    String getName();

    /**
     * Returns the folder where this dungeon is stored.
     *
     * @return the folder where this dungeon is stored
     */
    File getFolder();

    /**
     * Returns the {@link de.erethon.dungeonsxl.api.dungeon.GameRule}s that apply to this dungeon.
     * <p>
     * This is not necessarily represented 1:1 by a config file because it is usually merged together through {@link #setupRules()}. The raw values from the
     * dungeon's configuration can be retrieved through {@link #getConfig()}.
     *
     * @return the {@link de.erethon.dungeonsxl.api.dungeon.GameRule}s that apply to this dungeon
     */
    GameRuleContainer getRules();

    /**
     * Returns the {@link de.erethon.dungeonsxl.api.dungeon.GameRule}s specified in the configuration of this dungeon.
     * <p>
     * Note that these are only the raw rules that are specified in the file. They are not the rules that are actually used in a game instance instantiated
     * from this dungeon as these ones may be supplemented or overriden by other rules taken from the main config or the
     * {@link de.erethon.dungeonsxl.api.dungeon.GameRule#DEFAULT_VALUES}.
     *
     * @return the {@link de.erethon.dungeonsxl.api.dungeon.GameRule}s specified in the configuration of this world
     */
    GameRuleContainer getConfig();

    /**
     * Sets up the rules with the following priority: 1. World config 2. Main config: Default values 3. Internal default values
     */
    void setupRules();

    /**
     * Returns the environment of the world as defined in the config or {@link org.bukkit.World.Environment#NORMAL} if nothing is set.
     *
     * @return the environment of the world as defined in the config or {@link org.bukkit.World.Environment#NORMAL} if nothing is set
     */
    Environment getWorldEnvironment();

    /**
     * Adds the player to the list of players that are invited to edit the dungeon.
     *
     * @param player the player
     */
    void addInvitedPlayer(OfflinePlayer player);

    /**
     * Removes a player from the list of players that are invited to edit the dungeon.
     *
     * @param player the player
     * @return if the action was successful
     */
    boolean removeInvitedPlayer(OfflinePlayer player);

    /**
     * Returns if the player is invited to edit the dungeon.
     *
     * @param player the player
     * @return if the player is invited to edit the dungeon
     */
    boolean isInvitedPlayer(OfflinePlayer player);

    /**
     * Returns false if there are errors in the setup; true if not.
     *
     * @return false if there are errors in the setup; true if not
     */
    boolean isSetupCorrect();

    /**
     * Creates a backup of the dungeon.
     */
    void backup();

    /**
     * Returns the loaded edit instance of this world or null if none exists.
     *
     * @return the loaded edit instance of this world or null if none exists
     */
    EditWorld getEditWorld();

    /**
     * Returns the loaded edit instance of this dungeon or generates a new one if none exists.
     *
     * @param ignoreLimit if the instance limit set in the main config shall be ignored
     * @return the loaded edit instance of this dungeon or generates a new one if none exists
     */
    EditWorld getOrInstantiateEditWorld(boolean ignoreLimit);

    /**
     * Returns a new game instance of this dungeon.
     *
     * @see de.erethon.dungeonsxl.api.dungeon.Game#ensureWorldIsLoaded(boolean)
     * @param game        the game the instance belongs to
     * @param ignoreLimit if the instance limit set in the main config shall be ignored
     * @return a new game instance of this dungeon
     */
    GameWorld instantiateGameWorld(Game game, boolean ignoreLimit);

}
