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
package de.erethon.dungeonsxl.api.dungeon;

import de.erethon.dungeonsxl.api.player.PlayerGroup;
import de.erethon.dungeonsxl.api.world.GameWorld;
import de.erethon.dungeonsxl.api.world.ResourceWorld;
import java.util.Collection;
import java.util.List;
import org.bukkit.entity.Player;

/**
 * Handles the rules of playing in a dungeon.
 * <p>
 * Tracks the progress of groups in a dungeon and handles their interaction with each other.
 *
 * @author Daniel Saukel
 */
// Implementation-specific methods: wave and kill counter methods
public interface Game {

    /**
     * Returns if this is a tutorial game.
     *
     * @return if this is a tutorial game
     */
    boolean isTutorial();

    /**
     * Sets if this is a tutorial game
     *
     * @param tutorial if this is a tutorial game
     */
    void setTutorial(boolean tutorial);

    /**
     * Returns a read-only List of the groups that are playing this game.
     *
     * @return a read-only List of the groups that are playing this game
     */
    List<PlayerGroup> getGroups();

    /**
     * Adds the given group to this game.
     *
     * @param group the group
     */
    void addGroup(PlayerGroup group);

    /**
     * Removes the given group from this game.
     *
     * @param group the group
     */
    void removeGroup(PlayerGroup group);

    /**
     * Returns if the group has started (=if the ready sign has been triggered).
     *
     * @return if the group has started
     */
    boolean hasStarted();

    /**
     * Sets the status of the game to have started / not yet started.
     *
     * @param started if the game has started
     */
    void setStarted(boolean started);

    /**
     * Returns the game instance in which this game takes place.
     *
     * @return the game instance in which this game takes place
     */
    GameWorld getWorld();

    /**
     * Sets the game instance in which this game takes place.
     *
     * @param gameWorld the game instance in which this game takes place
     */
    void setWorld(GameWorld gameWorld);

    /**
     * Returns if the game has rewards.
     *
     * @return if the game has rewards
     */
    boolean hasRewards();

    /**
     * Sets if the game has rewards.
     *
     * @param enabled if the game has rewards
     */
    void setRewards(boolean enabled);

    /**
     * Returns the rules of the dungeon of this game.
     * <p>
     * This is not necessarily represented 1:1 by a config file because it is usually merged together through {@link Dungeon#setupRules()}.
     *
     * @return the rules of the dungeon of this game
     */
    default GameRuleContainer getRules() {
        return getDungeon().getRules();
    }

    /**
     * Returns a read-only List of the remaining floors to play.
     *
     * @return a read-only List of the remaining floors to play
     */
    List<ResourceWorld> getUnplayedFloors();

    /**
     * Adds a floor to the list of floors to play.
     *
     * @param unplayedFloor the resource world of the floor
     * @return if the addition was successful
     */
    boolean addUnplayedFloor(ResourceWorld unplayedFloor);

    /**
     * Removes a floor from the list of floors to play.
     *
     * @param unplayedFloor the resource world of the floor
     * @param force         if the floor shall be removed even if the {@link #getDungeon() dungeon}'s floors are not to be
     *                      {@link Dungeon#getRemoveWhenPlayed() removed when played.}
     * @return if the removal was successful
     */
    boolean removeUnplayedFloor(ResourceWorld unplayedFloor, boolean force);

    /**
     * Returns the resource of the next floor to play.
     *
     * @return the resource of the next floor to play
     */
    ResourceWorld getNextFloor();

    /**
     * Sets the next floor to play.
     *
     * @param floor the resource world of the floor
     */
    void setNextFloor(ResourceWorld floor);

    /**
     * Returns the amount of played floors in this game.
     *
     * @return the amount of played floors in this game
     */
    int getFloorCount();

    /**
     * Returns the dungeon that "hosts" this game.
     *
     * @return the dungeon that "hosts" this game
     */
    Dungeon getDungeon();

    /**
     * Returns the players playing the game.
     *
     * @return the players playing the game
     */
    Collection<Player> getPlayers();

    /**
     * Returns true if there are no groups in this game; false if not.
     *
     * @return true if there are no groups in this game; false if not
     */
    boolean isEmpty();

    /**
     * Returns and, if necessary, instantiates the game world.
     *
     * @param ignoreLimit if the instance limit set in the main config shall be ignored
     * @return the game world
     */
    GameWorld ensureWorldIsLoaded(boolean ignoreLimit);

    /**
     * Starts the game. This is what happens when the ready sign is triggered by everyone.
     *
     * @return if the game has started correctly
     */
    boolean start();

    /**
     * Deletes this game.
     */
    void delete();

    /**
     * Returns true if all groups of the game have finished it; false if not.
     *
     * @return true if all groups of the game have finished it; false if not
     */
    default boolean isFinished() {
        return getGroups().stream().allMatch(PlayerGroup::isFinished);
    }

    /**
     * Sends a message to each player in each group.
     *
     * @param message the message. Supports color codes
     */
    default void sendMessage(String message) {
        getGroups().forEach(g -> g.sendMessage(message));
    }

}
