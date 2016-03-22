/*
 * Copyright (C) 2012-2016 Frank Baumann
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
     * @return the playerVersusPlayer
     */
    public boolean isPlayerVersusPlayer();

    /**
     * @param playerVersusPlayer
     * the playerVersusPlayer to set
     */
    public void setPlayerVersusPlayer(boolean playerVersusPlayer);

    /**
     * @return the friendlyFire
     */
    public boolean isFriendlyFire();

    /**
     * @param friendlyFire
     * the friendlyFire to set
     */
    public void setFriendlyFire(boolean friendlyFire);

    /**
     * @return the mobWaves
     */
    public boolean hasMobWaves();

    /**
     * @param mobWaves
     * enable / disable mob waves
     */
    public void setMobWaves(boolean mobWaves);

    /**
     * @return if players get rewards after the dungeon
     */
    public boolean hasRewards();

    /**
     * @param rewards
     * enable / disable rewards
     */
    public void setRewards(boolean rewards);

    /**
     * @return if players shall see how long they play
     */
    public boolean getShowTime();

    /**
     * @param showTime
     * set if players shall see how long they play
     */
    public void setShowTime(boolean showTime);

    /**
     * @return if players can build
     */
    public boolean canBuild();

    /**
     * @param build
     * enable / disable building
     */
    public void setBuild(boolean build);

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
    public boolean hasLives();

    /**
     * @param lives
     * set if the gametype uses player lives
     */
    public void setLives(boolean lives);

}
