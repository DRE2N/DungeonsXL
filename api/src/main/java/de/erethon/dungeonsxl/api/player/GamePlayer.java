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
package de.erethon.dungeonsxl.api.player;

import org.bukkit.Location;

/**
 * Represents a player in a game dungeon instance.
 * <p>
 * All players in a game world have one wrapper object that is an instance of GamePlayer.
 *
 * @author Daniel Saukel
 */
// Implementation-specific methods: isInTestMode, setReady, [wolf, group tag, requirement, loot check methods], finishFloor
public interface GamePlayer extends InstancePlayer {

    /**
     * Returns if the player is ready to start the game.
     * <p>
     * This is usually achieved by triggering a ready sign.
     *
     * @return if the player is ready to start the game
     */
    boolean isReady();

    /**
     * Returns if the player finished the game.
     * <p>
     * This is usually achieved by triggering an end sign.
     * <p>
     * It is used for both the end of a whole dungeon and the end of a floor.
     *
     * @return if the player finished the game
     */
    boolean isFinished();

    /**
     * Sets if the player finished their game.
     *
     * @param finished if the player finished the game
     */
    void setFinished(boolean finished);

    /**
     * Returns the player's class or null if they have none.
     *
     * @return the player's class
     */
    PlayerClass getPlayerClass();

    /**
     * Sets and applies the given class.
     *
     * @param playerClass the class
     */
    void setPlayerClass(PlayerClass playerClass);

    /**
     * Returns the location of the last checkpoint the player reached.
     *
     * @return the location of the last checkpoint the player reached
     */
    Location getLastCheckpoint();

    /**
     * Sets the location of the last checkpoint the player reached.
     * <p>
     * This is where the player respawns if they die and have -1 or &gt;0 {@link #getLives() lives} left.
     *
     * @param checkpoint the checkpoint location
     */
    void setLastCheckpoint(Location checkpoint);

    /**
     * Returns the saved time millis from when the player went offline.
     *
     * @return the saved time millis from when the player went offline
     */
    long getOfflineTimeMillis();

    /**
     * Sets the saved time millis from when the player went offline.
     *
     * @param time the time millis
     */
    void setOfflineTimeMillis(long time);

    /**
     * Returns the original amount of lives the player had in the current game or -1 if lives aren't used.
     *
     * @return the original amount of lives the player had in the current game or -1 if lives aren't used
     */
    int getInitialLives();

    /**
     * Sets the original amount of lives the player had in the current game; -1 means lives aren't used.
     *
     * @param lives the amount of lives
     */
    void setInitialLives(int lives);

    /**
     * Returns the lives the player has left or -1 if per player lives aren't used.
     *
     * @return the lives the player has left or -1 if per player lives aren't used
     */
    int getLives();

    /**
     * Sets the lives the player has left.
     * <p>
     * This is not to be used if the dungeon uses group lives.
     *
     * @param lives the lives
     */
    void setLives(int lives);

    /**
     * Returns if the player is stealing another group's flag.
     *
     * @return if the player is stealing another group's flag
     */
    boolean isStealingFlag();

    /**
     * Returns the group whose flag the player robbed; null if the player isn't stealing any.
     *
     * @return the group whose flag the player robbed; null if the player isn't stealing any
     */
    PlayerGroup getRobbedGroup();

    /**
     * Sets the player to be stealing the team flag of the given group.
     *
     * @param group the group
     */
    void setRobbedGroup(PlayerGroup group);

    /**
     * Scores a point.
     */
    void captureFlag();

    /**
     * Makes the player leave his group and dungeon.
     * <p>
     * This sends default messages to the player.
     */
    @Override
    default void leave() {
        leave(true);
    }

    /**
     * Makes the player leave his group and dungeon.
     *
     * @param sendMessages if default messages shall be sent to the player
     */
    void leave(boolean sendMessages);

    /**
     * Treats the player as if they lost their last life and kicks them from the dungeon.
     */
    void kill();

    /**
     * Sets the player to be ready to start the dungeon game, like when a ready sign is triggered.
     * <p>
     * If all other players in the group are already {@link #isReady() ready}, the game is started.
     *
     * @return if the game has been started.
     */
    boolean ready();

    /**
     * Respawns the player. Also teleports DXL pets if there are any.
     */
    void respawn();

    /**
     * The player finishs the current game.
     * <p>
     * This sends default messages to the player.
     */
    default void finish() {
        finish(true);
    }

    /**
     * The player finishs the current game.
     *
     * @param sendMessages if default messages shall be sent to the player
     */
    void finish(boolean sendMessages);

}
