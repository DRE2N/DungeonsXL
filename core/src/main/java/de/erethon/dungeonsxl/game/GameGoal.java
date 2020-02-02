/*
 * Copyright (C) 2012-2020 Frank Baumann
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.erethon.dungeonsxl.game;

/**
 * A game goal defines what the players have to do in order to finish the game.
 *
 * @author Daniel Saukel
 */
public enum GameGoal {

    /**
     * The default goal. The game ends when the end is reached.
     */
    END,
    /**
     * The game does not end. Instead, the goal is to survive as long as possible to beat a highscore.
     */
    HIGHSCORE,
    /**
     * The game ends when a player dies and only one group is left.
     */
    LAST_MAN_STANDING,
    /**
     * The game ends when a group reachs a specific score.
     */
    REACH_SCORE,
    /**
     * The game ends after a specific time. The goal is to get the highest score until then.
     */
    TIME_SCORE,
    /**
     * The game ends after a specific time. The goal is to survive until then.
     */
    TIME_SURVIVAL;

}
