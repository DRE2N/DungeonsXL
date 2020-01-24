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

import de.erethon.dungeonsxl.api.world.EditWorld;

/**
 * Represents a player in an edit instance.
 * <p>
 * All players in an edit world have one wrapper object that is an instance of EditPlayer.
 *
 * @author Daniel Saukel
 */
// Implementation-specific methods: g/s lines copy, poke
public interface EditPlayer extends InstancePlayer {

    /**
     * Returns the {@link de.erethon.dungeonsxl.api.world.EditWorld} the player is editing.
     *
     * @return the {@link de.erethon.dungeonsxl.api.world.EditWorld} the player is editing
     */
    EditWorld getEditWorld();

    /**
     * Returns the lines of a sign the player has copied with a stick tool in an array with the length of four.
     *
     * @return the lines of a sign the player has copied with a stick tool in an array with the length of four
     */
    String[] getCopiedLines();

    /**
     * Sets the memorized sign lines.
     *
     * @param copiedLines the lines
     */
    void setCopiedLines(String[] copiedLines);

    /**
     * Makes the player leave the edit world without saving the progress.
     */
    void escape();

}
