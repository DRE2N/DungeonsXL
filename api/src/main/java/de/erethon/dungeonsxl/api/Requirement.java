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
package de.erethon.dungeonsxl.api;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * Something a player needs to fulfill in order to be allowed to start the game (= trigger a ready sign).
 *
 * @author Daniel Saukel
 */
public interface Requirement {

    /**
     * Sets up the requirement from the given requirements {@link de.erethon.dungeonsxl.api.dungeon.GameRule} section.
     *
     * @param config the requirements config section
     */
    void setup(ConfigurationSection config);

    /**
     * Returns if the given player fulfills the requirements. If true, this lets him start the game (= trigger a ready sign).
     *
     * @param player the player
     * @return if the given player fulfills the requirements
     */
    boolean check(Player player);

    /**
     * This is fired after the {@link #check(Player)} has been accepted. It demands the requirement from the given player. This may be empty for a "key" or may
     * take something away for a "fee" requirement.
     *
     * @param player the player
     */
    void demand(Player player);

}
