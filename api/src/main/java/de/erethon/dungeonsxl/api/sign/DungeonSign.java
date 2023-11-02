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
package de.erethon.dungeonsxl.api.sign;

import de.erethon.dungeonsxl.api.trigger.TriggerListener;
import de.erethon.dungeonsxl.api.world.EditWorld;
import org.bukkit.block.Sign;

/**
 * Interface for all dungeon signs.
 *
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public interface DungeonSign extends TriggerListener {

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
     * Returns true if the fourth line of the sign that usually contains the trigger is used differently.
     *
     * @deprecated This is overriden by interact signs, but it is strongly advised to stick to the convention to fetch data only from the second and third line.
     *
     * @return true if the fourth line of the sign that usually contains the trigger is used differently
     */
    @Deprecated
    default boolean isTriggerLineDisabled() {
        return false;
    }

    /**
     * Returns if the block type of the sign is set to air after the initialization.
     *
     * @return if the block type of the sign is set to air after the initialization
     */
    boolean isSetToAir();

    /**
     * Returns the sign that represents event point.
     * <p>
     * Use {@link #getLines()} instead to read the raw data of this dungeon sign.
     *
     * @return the sign that represents event point
     */
    Sign getSign();

    /**
     * Returns the raw line of this sign at the given index.
     * <p>
     * These lines might not be the physical lines of {@link #getSign()}.
     *
     * @param index the line index (0-3)
     * @return the raw lines of this sign in an array with 4 elements
     */
    default String getLine(int index) {
        return getLines()[index];
    }

    /**
     * Returns the raw lines of this sign in an array with 4 elements.
     * <p>
     * These lines might not be the physical lines of {@link #getSign()}.
     *
     * @return the raw lines of this sign in an array with 4 elements
     */
    String[] getLines();

    /**
     * Returns the edit world this sign is in; null if this is in a game world.
     *
     * @return the edit world this sign is in; null if this is in a game world
     */
    EditWorld getEditWorld();

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
    boolean validate();

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
