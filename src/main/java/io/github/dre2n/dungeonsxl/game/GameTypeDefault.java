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
public enum GameTypeDefault implements GameType {

    ADVENTURE("Adventure", "Adventure", false, false, false, true, false, true, GameMode.ADVENTURE, true),
    ADVENTURE_TIME_IS_RUNNING("Adventure - Time is Running", "Adventure TiR", false, false, false, true, true, true, GameMode.ADVENTURE, true),
    APOCALYPSE_LAST_MAN_STANDING("Apocalypse", "Apocalypse LMS", true, true, true, true, false, false, GameMode.SURVIVAL, true),
    APOCALYPSE_LIMITED_MOBS("Apocalypse - Limited Mobs", "Apc Limited", true, true, true, true, false, false, GameMode.SURVIVAL, true),
    APOCALYPSE_TIME_IS_RUNNING("Apocalypse - Time is Running", "Apocalypse TiR", true, true, true, true, true, false, GameMode.SURVIVAL, true),
    PVE_LAST_MAN_STANDING("Player versus Environment - Last Man Standing", "PvE LMS", false, false, true, true, false, false, GameMode.SURVIVAL, true),
    PVE_LIMITED_MOBS("Player versus Environment - Limited Mobs", "PvE Limited", false, false, true, true, false, false, GameMode.SURVIVAL, true),
    PVE_TIME_IS_RUNNING("Player versus Environment - Time is Running", "PvE TiR", false, false, true, true, true, false, GameMode.SURVIVAL, true),
    PVP_FACTIONS_BATTLEFIELD("Player versus Player - Factions Battlefield", "FactionsPvP", true, false, false, false, false, false, GameMode.SURVIVAL, true),
    PVP_LAST_MAN_STANDING("Player versus Player - Last Man Standing", "PvP LMS", true, false, false, false, false, false, GameMode.SURVIVAL, true),
    QUEST("Quest", "Quest", false, false, false, true, false, false, GameMode.SURVIVAL, true),
    QUEST_TIME_IS_RUNNING("Quest - Time is Running", "Quest TiR", false, false, false, true, true, false, GameMode.SURVIVAL, true),
    TEST("Test", "Test", false, false, false, false, true, true, GameMode.SURVIVAL, false),
    TUTORIAL("Tutorial", "Tutorial", false, false, false, true, false, false, GameMode.SURVIVAL, false),
    DEFAULT("Default", "Default", false, false, false, true, false, false, GameMode.SURVIVAL, true);

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

    GameTypeDefault(String displayName, String signName, boolean playerVersusPlayer, boolean friendlyFire, boolean mobWaves, boolean rewards, boolean showTime, boolean build, GameMode gameMode, boolean lives) {
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
