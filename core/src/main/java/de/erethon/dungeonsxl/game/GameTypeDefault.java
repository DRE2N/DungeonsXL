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

import static de.erethon.dungeonsxl.game.GameGoal.*;
import org.bukkit.GameMode;

/**
 * Default implementation of {@link de.erethon.dungeonsxl.game.GameType}.
 *
 * @author Daniel Saukel
 */
public enum GameTypeDefault implements GameType {

    ADVENTURE("Adventure", "Adventure", END, false, false, true, false, true, true, true, GameMode.ADVENTURE, true),
    ADVENTURE_TIME_IS_RUNNING("Adventure - Time is Running", "Adventure TiR", TIME_SCORE, false, false, true, true, true, true, true, GameMode.ADVENTURE, true),
    APOCALYPSE("Apocalypse", "Apocalypse", HIGHSCORE, true, true, true, false, false, false, false, GameMode.SURVIVAL, true),
    APOCALYPSE_LAST_MAN_STANDING("Apocalypse", "Apocalypse LMS", LAST_MAN_STANDING, true, true, true, false, false, false, false, GameMode.SURVIVAL, true),
    APOCALYPSE_LIMITED_MOBS("Apocalypse - Limited Mobs", "Apc Limited", END, true, true, true, false, false, false, false, GameMode.SURVIVAL, true),
    APOCALYPSE_TIME_IS_RUNNING("Apocalypse - Time is Running", "Apocalypse TiR", TIME_SURVIVAL, true, true, true, true, false, false, false, GameMode.SURVIVAL, true),
    BEDWARS("Bedwars", "Bedwars", LAST_MAN_STANDING, true, false, false, false, false, true, true, GameMode.SURVIVAL, false),
    PVE_LAST_MAN_STANDING("Player versus Environment - Last Man Standing", "PvE LMS", LAST_MAN_STANDING, false, false, true, false, false, false, false, GameMode.SURVIVAL, true),
    PVE_LIMITED_MOBS("Player versus Environment - Limited Mobs", "PvE Limited", END, false, false, true, false, false, false, false, GameMode.SURVIVAL, true),
    PVE_TIME_IS_RUNNING("Player versus Environment - Time is Running", "PvE TiR", TIME_SURVIVAL, false, false, true, true, false, false, false, GameMode.SURVIVAL, true),
    PVP_FACTIONS_BATTLEFIELD("Player versus Player - Factions Battlefield", "FactionsPvP", LAST_MAN_STANDING, true, false, false, false, false, false, false, GameMode.SURVIVAL, true),
    PVP_LAST_MAN_STANDING("Player versus Player - Last Man Standing", "PvP LMS", LAST_MAN_STANDING, true, false, false, false, false, false, false, GameMode.SURVIVAL, true),
    QUEST("Quest", "Quest", END, false, false, true, false, false, false, false, GameMode.SURVIVAL, true),
    QUEST_TIME_IS_RUNNING("Quest - Time is Running", "Quest TiR", END, false, false, true, true, false, false, false, GameMode.SURVIVAL, true),
    TEST("Test", "Test", HIGHSCORE, false, false, false, true, true, true, true, GameMode.SURVIVAL, false),
    DEFAULT("Default", "Default", END, false, false, true, false, false, false, false, GameMode.SURVIVAL, true),
    CUSTOM("Custom", "Custom");

    private String displayName;
    private String signName;
    private GameGoal gameGoal;
    private Boolean playerVersusPlayer;
    private Boolean friendlyFire;
    private Boolean rewards;
    private Boolean showTime;
    private Boolean breakBlocks;
    private Boolean breakPlacedBlocks;
    private Boolean placeBlocks;
    private GameMode gameMode;
    private Boolean lives;

    GameTypeDefault(String displayName, String signName, GameGoal gameGoal, Boolean playerVersusPlayer, Boolean friendlyFire, Boolean rewards,
            Boolean showTime, Boolean breakBlocks, Boolean breakPlacedBlocks, Boolean placeBlocks, GameMode gameMode, Boolean lives) {
        this.displayName = displayName;
        this.signName = signName;
        this.gameGoal = gameGoal;
        this.playerVersusPlayer = playerVersusPlayer;
        this.friendlyFire = friendlyFire;
        this.rewards = rewards;
        this.showTime = showTime;
        this.breakBlocks = breakBlocks;
        this.breakPlacedBlocks = breakPlacedBlocks;
        this.placeBlocks = placeBlocks;
        this.gameMode = gameMode;
        this.lives = lives;
    }

    GameTypeDefault(String displayName, String signName) {
        this.displayName = displayName;
        this.signName = signName;
    }

    @Override
    public GameGoal getGameGoal() {
        return gameGoal;
    }

    @Override
    public void setGameGoal(GameGoal gameGoal) {
        this.gameGoal = gameGoal;
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
