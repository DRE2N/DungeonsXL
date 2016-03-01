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
     * the mobWaves to set
     */
    public void setMobWaves(boolean mobWaves);

    /**
     * @return the rewards
     */
    public boolean hasRewards();

    /**
     * @param rewards
     * the rewards to set
     */
    public void setRewards(boolean rewards);

    /**
     * @return the showTime
     */
    public boolean getShowTime();

    /**
     * @param showTime
     * the showTime to set
     */
    public void setShowTime(boolean showTime);

    /**
     * @return the build
     */
    public boolean canBuild();

    /**
     * @param build
     * the build to set
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

}
