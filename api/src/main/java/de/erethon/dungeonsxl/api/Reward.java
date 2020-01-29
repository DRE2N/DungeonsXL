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

import org.bukkit.entity.Player;

/**
 * Something players are given when they successfully finish a {@link de.erethon.dungeonsxl.api.dungeon.Dungeon}.
 * <p>
 * @see de.erethon.dungeonsxl.api.player.PlayerGroup#getRewards()
 * @author Daniel Saukel
 */
public interface Reward {

    /**
     * Gives the reward to the given player.
     *
     * @param player the player
     */
    void giveTo(Player player);

}
