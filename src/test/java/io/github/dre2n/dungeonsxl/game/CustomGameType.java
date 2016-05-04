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

import io.github.dre2n.dungeonsxl.game.GameType;
import org.bukkit.GameMode;

/**
 * @author Daniel Saukel
 */
public enum CustomGameType implements GameType {

    GHOST("My awesome game type", "Identifier", false, false, false, false, false, false, GameMode.SPECTATOR, false);

    private String displayName;
    private String signName;
    private boolean playerVersusPlayer;
    private boolean friendlyFire;
    private boolean mobWaves;
    private boolean rewards;
    private boolean showTime;
    private boolean build;
    private GameMode gameMode;
    private boolean lives;

    CustomGameType(String displayName, String signName, boolean playerVersusPlayer, boolean friendlyFire, boolean mobWaves, boolean rewards, boolean showTime, boolean build, GameMode gameMode, boolean lives) {
        this.displayName = displayName;
        this.signName = signName;
        this.playerVersusPlayer = playerVersusPlayer;
        this.friendlyFire = friendlyFire;
        this.mobWaves = mobWaves;
        this.rewards = rewards;
        this.showTime = showTime;
        this.build = build;
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
    public boolean isPlayerVersusPlayer() {
        return playerVersusPlayer;
    }

    @Override
    public void setPlayerVersusPlayer(boolean playerVersusPlayer) {
        this.playerVersusPlayer = playerVersusPlayer;
    }

    @Override
    public boolean isFriendlyFire() {
        return friendlyFire;
    }

    @Override
    public void setFriendlyFire(boolean friendlyFire) {
        this.friendlyFire = friendlyFire;
    }

    @Override
    public boolean hasMobWaves() {
        return mobWaves;
    }

    @Override
    public void setMobWaves(boolean mobWaves) {
        this.mobWaves = mobWaves;
    }

    @Override
    public boolean hasRewards() {
        return rewards;
    }

    @Override
    public void setRewards(boolean rewards) {
        this.rewards = rewards;
    }

    @Override
    public boolean getShowTime() {
        return showTime;
    }

    @Override
    public void setShowTime(boolean showTime) {
        this.showTime = showTime;
    }

    @Override
    public boolean canBuild() {
        return build;
    }

    @Override
    public void setBuild(boolean build) {
        this.build = build;
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
    public boolean hasLives() {
        return lives;
    }

    @Override
    public void setLives(boolean lives) {
        this.lives = lives;
    }

}
