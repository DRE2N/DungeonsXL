/*
 * Copyright (C) 2012-2018 Frank Baumann
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
package io.github.dre2n.dungeonsxl.game;

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
    public String getDisplayName();

    /**
     * @param displayName
     * the displayName to set
     */
    public void setDisplayName(String displayName);

    /**
     * @return the signName
     */
    public String getSignName();

    /**
     * @param signName
     * the signName to set
     */
    public void setSignName(String signName);

    /**
     * @return the goal of the game
     */
    public GameGoal getGameGoal();

    /**
     * @param gameGoal
     * the goal of the game to set
     */
    public void setGameGoal(GameGoal gameGoal);

    /**
     * @return the playerVersusPlayer
     */
    public Boolean isPlayerVersusPlayer();

    /**
     * @param playerVersusPlayer
     * the playerVersusPlayer to set
     */
    public void setPlayerVersusPlayer(Boolean playerVersusPlayer);

    /**
     * @return the friendlyFire
     */
    public Boolean isFriendlyFire();

    /**
     * @param friendlyFire
     * the friendlyFire to set
     */
    public void setFriendlyFire(Boolean friendlyFire);

    /**
     * @return if players get rewards after the dungeon
     */
    public Boolean hasRewards();

    /**
     * @param rewards
     * enable / disable rewards
     */
    public void setRewards(Boolean rewards);

    /**
     * @return if players shall see how long they play
     */
    public Boolean getShowTime();

    /**
     * @param showTime
     * set if players shall see how long they play
     */
    public void setShowTime(Boolean showTime);

    /**
     * @return if all blocks may be destroyed
     */
    public Boolean canBreakBlocks();

    /**
     * @param breakBlocks
     * if blocks may be destroyed
     */
    public void setBreakBlocks(Boolean breakBlocks);

    /**
     * @return if blocks placed in game may be destroyed
     */
    public Boolean canBreakPlacedBlocks();

    /**
     * @param breakPlacedBlocks
     * if placed blocks may be destroyed
     */
    public void setBreakPlacedBlocks(Boolean breakPlacedBlocks);

    /**
     * @return if blocks may be placed
     */
    public Boolean canPlaceBlocks();

    /**
     * @param placeBlocks
     * if blocks may be placed
     */
    public void setPlaceBlocks(Boolean placeBlocks);

    /**
     * @return the gameMode
     */
    public GameMode getGameMode();

    /**
     * @param gameMode
     * the gameMode to set
     */
    public void setGameMode(GameMode gameMode);

    /**
     * @return if players lose lives
     */
    public Boolean hasLives();

    /**
     * @param lives
     * set if the gametype uses player lives
     */
    public void setLives(Boolean lives);

}
