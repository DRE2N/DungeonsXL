/*
 * Copyright (C) 2016 Daniel Saukel
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
public enum CustomGameType implements GameType {

    GHOST("My awesome game type", "Identifier", false, false, false, false, false, false, false, false, GameMode.SPECTATOR, false);

    private String displayName;
    private String signName;
    private Boolean playerVersusPlayer;
    private Boolean friendlyFire;
    private Boolean mobWaves;
    private Boolean rewards;
    private Boolean showTime;
    private Boolean breakBlocks;
    private Boolean breakPlacedBlocks;
    private Boolean placeBlocks;
    private GameMode gameMode;
    private Boolean lives;

    CustomGameType(String displayName, String signName, Boolean playerVersusPlayer, Boolean friendlyFire, Boolean mobWaves, Boolean rewards,
            Boolean showTime, Boolean breakBlocks, Boolean breakPlacedBlocks, Boolean placeBlocks, GameMode gameMode, Boolean lives) {
        this.displayName = displayName;
        this.signName = signName;
        this.playerVersusPlayer = playerVersusPlayer;
        this.friendlyFire = friendlyFire;
        this.mobWaves = mobWaves;
        this.rewards = rewards;
        this.showTime = showTime;
        this.breakBlocks = breakBlocks;
        this.breakPlacedBlocks = breakPlacedBlocks;
        this.placeBlocks = placeBlocks;
        this.gameMode = gameMode;
        this.lives = lives;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getSignName() {
        return signName;
    }

    @Override
    public void setSignName(String signName) {
        this.signName = signName;
    }

    @Override
    public Boolean isPlayerVersusPlayer() {
        return playerVersusPlayer;
    }

    @Override
    public void setPlayerVersusPlayer(Boolean playerVersusPlayer) {
        this.playerVersusPlayer = playerVersusPlayer;
    }

    @Override
    public Boolean isFriendlyFire() {
        return friendlyFire;
    }

    @Override
    public void setFriendlyFire(Boolean friendlyFire) {
        this.friendlyFire = friendlyFire;
    }

    @Override
    public Boolean hasMobWaves() {
        return mobWaves;
    }

    @Override
    public void setMobWaves(Boolean mobWaves) {
        this.mobWaves = mobWaves;
    }

    @Override
    public Boolean hasRewards() {
        return rewards;
    }

    @Override
    public void setRewards(Boolean rewards) {
        this.rewards = rewards;
    }

    @Override
    public Boolean getShowTime() {
        return showTime;
    }

    @Override
    public void setShowTime(Boolean showTime) {
        this.showTime = showTime;
    }

    @Override
    public Boolean canBreakBlocks() {
        return breakBlocks;
    }

    @Override
    public void setBreakBlocks(Boolean breakBlocks) {
        this.breakBlocks = breakBlocks;
    }

    @Override
    public Boolean canBreakPlacedBlocks() {
        return breakPlacedBlocks;
    }

    @Override
    public void setBreakPlacedBlocks(Boolean breakPlacedBlocks) {
        this.breakPlacedBlocks = breakPlacedBlocks;
    }

    @Override
    public Boolean canPlaceBlocks() {
        return placeBlocks;
    }

    @Override
    public void setPlaceBlocks(Boolean placeBlocks) {
        this.placeBlocks = placeBlocks;
    }

    @Override
    public GameMode getGameMode() {
        return gameMode;
    }

    @Override
    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    @Override
    public Boolean hasLives() {
        return lives;
    }

    @Override
    public void setLives(Boolean lives) {
        this.lives = lives;
    }

}
