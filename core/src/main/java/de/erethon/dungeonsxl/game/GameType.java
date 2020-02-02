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

import org.bukkit.GameMode;

/**
 * Implement this to create custom game types.
 *
 * @author Daniel Saukel
 */
public interface GameType {

    /**
     * @return the displayName
     */
    String getDisplayName();

    /**
     * @param displayName the displayName to set
     */
    void setDisplayName(String displayName);

    /**
     * @return the signName
     */
    String getSignName();

    /**
     * @param signName the signName to set
     */
    void setSignName(String signName);

    /**
     * @return the goal of the game
     */
    GameGoal getGameGoal();

    /**
     * @param gameGoal the goal of the game to set
     */
    void setGameGoal(GameGoal gameGoal);

    /**
     * @return the playerVersusPlayer
     */
    Boolean isPlayerVersusPlayer();

    /**
     * @param playerVersusPlayer the playerVersusPlayer to set
     */
    void setPlayerVersusPlayer(Boolean playerVersusPlayer);

    /**
     * @return the friendlyFire
     */
    Boolean isFriendlyFire();

    /**
     * @param friendlyFire the friendlyFire to set
     */
    void setFriendlyFire(Boolean friendlyFire);

    /**
     * @return if players get rewards after the dungeon
     */
    Boolean hasRewards();

    /**
     * @param rewards enable / disable rewards
     */
    void setRewards(Boolean rewards);

    /**
     * @return if players shall see how long they play
     */
    Boolean getShowTime();

    /**
     * @param showTime set if players shall see how long they play
     */
    void setShowTime(Boolean showTime);

    /**
     * @return if all blocks may be destroyed
     */
    Boolean canBreakBlocks();

    /**
     * @param breakBlocks if blocks may be destroyed
     */
    void setBreakBlocks(Boolean breakBlocks);

    /**
     * @return if blocks placed in game may be destroyed
     */
    Boolean canBreakPlacedBlocks();

    /**
     * @param breakPlacedBlocks if placed blocks may be destroyed
     */
    void setBreakPlacedBlocks(Boolean breakPlacedBlocks);

    /**
     * @return if blocks may be placed
     */
    Boolean canPlaceBlocks();

    /**
     * @param placeBlocks if blocks may be placed
     */
    void setPlaceBlocks(Boolean placeBlocks);

    /**
     * @return the gameMode
     */
    GameMode getGameMode();

    /**
     * @param gameMode the gameMode to set
     */
    void setGameMode(GameMode gameMode);

    /**
     * @return if players lose lives
     */
    Boolean hasLives();

    /**
     * @param lives set if the gametype uses player lives
     */
    void setLives(Boolean lives);

}
