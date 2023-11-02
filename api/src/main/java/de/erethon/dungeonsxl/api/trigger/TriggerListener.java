/*
 * Copyright (C) 2014-2023 Daniel Saukel
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
package de.erethon.dungeonsxl.api.trigger;

import de.erethon.dungeonsxl.api.dungeon.Game;
import de.erethon.dungeonsxl.api.world.GameWorld;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public interface TriggerListener {

    /**
     * Returns a {@link LogicalExpression} of the triggers registered for this listener.
     *
     * @return a a {@link LogicalExpression} of the triggers registered for this listener
     */
    LogicalExpression getTriggerExpression();

    /**
     * Returns a deep List of triggers of this listener.
     * <p>
     * WARNING: Do not iterate and check if each is satisfied to get if the sign should fire. Use {@link #getTriggerExpression()} instead.
     *
     * @return a deep List of triggers of this listener
     */
    List<Trigger> getTriggers();

    /**
     * Returns if the listener has triggers.
     *
     * @return if the listener has triggers
     */
    default boolean hasTriggers() {
        return getTriggerExpression() != null;
    }

    /**
     * The location of this listener.
     *
     * @return the location of this listener
     */
    Location getLocation();

    /**
     * Returns the game world this listener is in; null if this is in an edit world.
     *
     * @return the game world this listener is in; null if this is in an edit world
     */
    GameWorld getGameWorld();

    /**
     * Returns the game played in the world of this listener.
     *
     * @return the game played in the world of this listener
     */
    default Game getGame() {
        if (getGameWorld() == null) {
            return null;
        }
        return getGameWorld().getGame();
    }

    /**
     * Makes the listener listen for its triggers if it {@link #hasTriggers()}.
     * <p>
     * {@link #trigger(org.bukkit.entity.Player)}s the listener if it does not have any triggers. (Note that some signs have interaction triggers by default,
     * like ready signs).
     */
    void initialize();

    /**
     * Returns if the listener is {@link #initialize()}d.
     *
     * @return if the listener is {@link #initialize()}d
     */
    boolean isInitialized();

    /**
     * Triggers the listener. The effects are defined by the implementation.
     *
     * @param player the player who triggered the listener or null if no one in particular triggered it
     */
    void trigger(Player player);

    /**
     * Checks if the triggers of the listener have been triggered. If they all are, the listener itself is triggered.
     *
     * @param lastFired the last trigger that has been triggered
     */
    void updateTriggers(Trigger lastFired);

}
