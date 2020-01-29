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
package de.erethon.dungeonsxl.api.sign;

import de.erethon.dungeonsxl.api.Trigger;
import de.erethon.dungeonsxl.api.world.GameWorld;
import java.util.Set;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * Interface for all dungeon signs.
 *
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public interface DungeonSign {

    /**
     * Returns the name to identify the sign.
     *
     * @return the name
     */
    String getName();

    /**
     * Returns the permission node that is required to build a sign of this type.
     *
     * @return the build permission
     */
    String getBuildPermission();

    /**
     * Returns if the sign gets initialized when the dungeon is loaded instead of when the game starts.
     *
     * @return if the sign gets initialized when the dungeon is loaded instead of when the game starts
     */
    boolean isOnDungeonInit();

    /**
     * Returns if the sign block is breakable after the initialization.
     *
     * @return if the sign block is breakable after the initialization
     */
    boolean isProtected();

    /**
     * Returns if the block type of the sign is set to air after the initialization.
     *
     * @return if the block type of the sign is set to air after the initialization
     */
    boolean isSetToAir();

    /**
     * Returns the sign that represents event point.
     *
     * @return the sign that represents event point
     */
    Sign getSign();

    /**
     * Returns the raw lines of this sign in an array with 4 elements.
     *
     * @return the raw lines of this sign in an array with 4 elements
     */
    String[] getLines();

    /**
     * Returns the game world this sign is in; null if this is an edit world.
     *
     * @return the game world this sign is in; null if this is an edit world
     */
    GameWorld getGameWorld();

    /**
     * Returns a Set of the triggers registered for this sign.
     *
     * @return a Set of the triggers registered for this sign
     */
    Set<Trigger> getTriggers();

    /**
     * Returns if the sign has triggers.
     *
     * @return if the sign has triggers
     */
    default boolean hasTriggers() {
        return !getTriggers().isEmpty();
    }

    /**
     * Adds a trigger to the sign.
     *
     * @param trigger the trigger
     */
    void addTrigger(Trigger trigger);

    /**
     * Attempts to remove a trigger from the sign.
     *
     * @param trigger the trigger
     */
    void removeTrigger(Trigger trigger);

    /**
     * Makes the sign listen for its triggers if it {@link #hasTriggers()}.
     * <p>
     * {@link #trigger(org.bukkit.entity.Player)}s the sign if it does not have any triggers.
     * (Note that some signs have interaction triggers by default, like ready signs).
     */
    void initialize();

    /**
     * Returns if the sign is {@link #initialize()}d.
     *
     * @return if the sign is {@link #initialize()}d
     */
    boolean isInitialized();

    /**
     * Triggers the sign. The effects are defined by the implementation.
     *
     * @param player the player who triggered the sign or null if no one in particular triggered it
     */
    void trigger(Player player);

    /**
     * Updates the sign.
     */
    void update();

    /**
     * Sets the sign to air if it is not erroneous and if its type requires this.
     * <p>
     * Signs are usually to be set to air upon initialization, but this is not done automatically because some signs need different behavior. Script signs for
     * example are not set to air because this would override whatever a block sign in its script does.
     *
     * @return if the sign type was set to air
     */
    boolean setToAir();

    /**
     * Returns if the sign is valid.
     * <p>
     * A sign is invalid when it lacks needed parameters or if illegal arguments have been entered.
     *
     * @return if the sign is valid
     */
    default boolean validate() {
        return true;
    }

    /**
     * Returns if the sign is erroneous.
     *
     * @return if the sign is erroneous
     */
    boolean isErroneous();

    /**
     * Set a placeholder to show that the sign is setup incorrectly.
     *
     * @param reason the reason why the sign is marked as erroneous
     */
    void markAsErroneous(String reason);

}
