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

import de.erethon.caliburn.item.ExItem;
import de.erethon.caliburn.mob.ExMob;
import de.erethon.caliburn.mob.VanillaMob;
import de.erethon.dungeonsxl.requirement.Requirement;
import de.erethon.dungeonsxl.reward.Reward;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;

/**
 * See {@link de.erethon.dungeonsxl.config.WorldConfig}
 *
 * @author Daniel Saukel
 */
public class GameRuleProvider {

    public static final GameRuleProvider DEFAULT_VALUES = new GameRuleProvider();

    static {
        /* keepInventory */
        DEFAULT_VALUES.keepInventoryOnEnter = false;
        DEFAULT_VALUES.keepInventoryOnEscape = false;
        DEFAULT_VALUES.keepInventoryOnFinish = false;
        DEFAULT_VALUES.keepInventoryOnDeath = true;
        DEFAULT_VALUES.lobbyDisabled = false;

        /* World */
        DEFAULT_VALUES.gameMode = GameMode.SURVIVAL;
        DEFAULT_VALUES.fly = false;
        DEFAULT_VALUES.breakBlocks = false;
        DEFAULT_VALUES.breakPlacedBlocks = false;
        DEFAULT_VALUES.breakWhitelist = null;
        DEFAULT_VALUES.damageProtectedEntities = new HashSet<>(Arrays.asList(
                VanillaMob.ARMOR_STAND,
                VanillaMob.ITEM_FRAME,
                VanillaMob.PAINTING
        ));
        DEFAULT_VALUES.interactionProtectedEntities = new HashSet<>(Arrays.asList(
                VanillaMob.ARMOR_STAND,
                VanillaMob.ITEM_FRAME
        ));
        DEFAULT_VALUES.placeBlocks = false;
        DEFAULT_VALUES.placeWhitelist = null;
        DEFAULT_VALUES.rain = null;
        DEFAULT_VALUES.thunder = null;
        DEFAULT_VALUES.time = null;

        /* Fighting */
        DEFAULT_VALUES.playerVersusPlayer = false;
        DEFAULT_VALUES.friendlyFire = false;
        DEFAULT_VALUES.initialLives = -1;
        DEFAULT_VALUES.initialGroupLives = -1;
        DEFAULT_VALUES.initialScore = 3;
        DEFAULT_VALUES.scoreGoal = -1;

        /* Timer */
        DEFAULT_VALUES.timeLastPlayed = 0;
        DEFAULT_VALUES.timeToNextPlayAfterStart = 0;
        DEFAULT_VALUES.timeToNextPlayAfterFinish = 0;
        DEFAULT_VALUES.timeToNextLoot = 0;
        DEFAULT_VALUES.timeToNextWave = 10;
        DEFAULT_VALUES.timeToFinish = -1;
        DEFAULT_VALUES.timeUntilKickOfflinePlayer = 0;

        /* Requirements and rewards */
        DEFAULT_VALUES.requirements = new ArrayList<>();
        DEFAULT_VALUES.finishedOne = null;
        DEFAULT_VALUES.finishedAll = null;
        DEFAULT_VALUES.rewards = new ArrayList<>();

        /* Commands and permissions */
        DEFAULT_VALUES.gameCommandWhitelist = new ArrayList<>();
        DEFAULT_VALUES.gamePermissions = new ArrayList<>();

        /* Title */
        DEFAULT_VALUES.titleFadeIn = 20;
        DEFAULT_VALUES.titleFadeOut = 20;
        DEFAULT_VALUES.titleShow = 60;

        /* Misc */
        DEFAULT_VALUES.msgs = new HashMap<>();
        DEFAULT_VALUES.secureObjects = new ArrayList<>();
        DEFAULT_VALUES.groupTagEnabled = false;
    }

    /* keepInventory */
    protected Boolean keepInventoryOnEnter;
    protected Boolean keepInventoryOnEscape;
    protected Boolean keepInventoryOnFinish;
    protected Boolean keepInventoryOnDeath;
    protected Boolean lobbyDisabled;

    /* World */
    protected GameMode gameMode;
    protected Boolean fly;
    protected Boolean breakBlocks;
    protected Boolean breakPlacedBlocks;
    protected Map<ExItem, HashSet<ExItem>> breakWhitelist;
    protected Set<ExMob> damageProtectedEntities;
    protected Set<ExMob> interactionProtectedEntities;
    protected Boolean placeBlocks;
    protected Set<ExItem> placeWhitelist;
    protected Boolean rain;
    protected Boolean thunder;
    protected Long time;

    /* Fighting */
    protected Boolean playerVersusPlayer;
    protected Boolean friendlyFire;
    protected Integer initialLives;
    protected Integer initialGroupLives;
    protected Integer initialScore;
    protected Integer scoreGoal;

    /* Timer */
    protected Integer timeLastPlayed;
    protected Integer timeToNextPlayAfterStart;
    protected Integer timeToNextPlayAfterFinish;
    protected Integer timeToNextLoot;
    protected Integer timeToNextWave;
    protected Integer timeToFinish;
    protected Integer timeUntilKickOfflinePlayer;

    /* Requirements and rewards */
    protected List<Requirement> requirements;
    protected List<String> finishedOne;
    protected List<String> finishedAll;
    protected List<Reward> rewards;

    /* Commands and permissions */
    protected List<String> gameCommandWhitelist;
    protected List<String> gamePermissions;

    /* Title */
    protected String title;
    protected String subtitle;
    protected String actionBar;
    protected String chat;
    protected Integer titleFadeIn;
    protected Integer titleFadeOut;
    protected Integer titleShow;

    /* Misc */
    protected Map<Integer, String> msgs;
    protected List<ExItem> secureObjects;
    protected Boolean groupTagEnabled;

    /* Getters and setters */
    // keepInventory
    /**
     * @return if the inventory shall be kept when the player enters the dungeon
     */
    public boolean getKeepInventoryOnEnter() {
        return keepInventoryOnEnter;
    }

    /**
     * @return if the inventory shall be kept when the player leaves the dungeon successlessly
     */
    public boolean getKeepInventoryOnEscape() {
        return keepInventoryOnEscape;
    }

    /**
     * @return if the inventory shall be kept when the player finishs the dungeon
     */
    public boolean getKeepInventoryOnFinish() {
        return keepInventoryOnFinish;
    }

    /**
     * @return if the inventory shall be kept on death
     */
    public boolean getKeepInventoryOnDeath() {
        return keepInventoryOnDeath;
    }

    /**
     * @return if the lobby is disabled
     */
    public boolean isLobbyDisabled() {
        return lobbyDisabled;
    }

    // World
    /**
     * @return the gameMode
     */
    public GameMode getGameMode() {
        return gameMode;
    }

    public boolean canFly() {
        return fly;
    }

    /**
     * @return if all blocks may be destroyed
     */
    public boolean canBreakBlocks() {
        return breakBlocks;
    }

    /**
     * @return if blocks placed in game may be destroyed
     */
    public boolean canBreakPlacedBlocks() {
        return breakPlacedBlocks;
    }

    /**
     * @return the destroyable materials and the materials that may be used to break them or null if any
     */
    public Map<ExItem, HashSet<ExItem>> getBreakWhitelist() {
        return breakWhitelist;
    }

    /**
     * @return a Set of all entity types that cannot be damaged
     */
    public Set<ExMob> getDamageProtectedEntities() {
        return damageProtectedEntities;
    }

    /**
     * @return a Set of all entity types that cannot be damaged
     */
    public Set<ExMob> getInteractionProtectedEntities() {
        return interactionProtectedEntities;
    }

    /**
     * @return if blocks may be placed
     */
    public boolean canPlaceBlocks() {
        return placeBlocks;
    }

    /**
     * @return the placeable materials
     */
    public Set<ExItem> getPlaceWhitelist() {
        return placeWhitelist;
    }

    /**
     * @return if it's raining permanently in this dungeon, null if random
     */
    public Boolean isRaining() {
        return rain;
    }

    /**
     * @param rain set if it's raining permanently in this dungeon
     */
    public void setRaining(Boolean rain) {
        this.rain = rain;
    }

    /**
     * @return You've been... THUNDERSTRUCK!
     */
    public Boolean isThundering() {
        return thunder;
    }

    /**
     * @param thunder You've been... THUNDERSTRUCK!
     */
    public void setThundering(Boolean thunder) {
        this.thunder = thunder;
    }

    /**
     * @return the locked day time in this dungeon, null if not locked
     */
    public Long getTime() {
        return time;
    }

    /**
     * @param time the locked day time to set
     */
    public void setTime(Long time) {
        this.time = time;
    }

    // Fight
    /**
     * @return if players may attack each other
     */
    public boolean isPlayerVersusPlayer() {
        return playerVersusPlayer;
    }

    /**
     * @return if players may attack group members
     */
    public boolean isFriendlyFire() {
        return friendlyFire;
    }

    /**
     * @return the initial amount of lives
     */
    public int getInitialLives() {
        return initialLives;
    }

    /**
     * @return the initial amount of group lives
     */
    public int getInitialGroupLives() {
        return initialGroupLives;
    }

    /**
     * @return the initial score
     */
    public int getInitialScore() {
        return initialScore;
    }

    /**
     * @return the score goal
     */
    public int getScoreGoal() {
        return scoreGoal;
    }

    // Timer
    /**
     * @return the timeLastPlayed
     */
    public int getTimeLastPlayed() {
        return timeLastPlayed;
    }

    /**
     * @return the time until a player can play again after he started the dungeon the last time
     */
    public int getTimeToNextPlayAfterStart() {
        return timeToNextPlayAfterStart;
    }

    /**
     * @return the time until a player can play again after he finished the dungeon the last time
     */
    public int getTimeToNextPlayAfterFinish() {
        return timeToNextPlayAfterFinish;
    }

    /**
     * @return the time until a player can get loot again
     */
    public int getTimeToNextLoot() {
        return timeToNextLoot;
    }

    /**
     * @return the break between two waves
     */
    public int getTimeToNextWave() {
        return timeToNextWave;
    }

    /**
     * @return the time until a player gets kicked from his group
     */
    public int getTimeToFinish() {
        return timeToFinish;
    }

    /**
     * @return the time until a player gets kicked from his group if he is offline
     */
    public int getTimeUntilKickOfflinePlayer() {
        return timeUntilKickOfflinePlayer;
    }

    /**
     * @return if the game is "time is running"
     */
    public boolean isTimeIsRunning() {
        return timeToFinish != -1;
    }

    // Requirements and rewards
    /**
     * @return the requirements
     */
    public List<Requirement> getRequirements() {
        if (requirements == null) {
            requirements = new ArrayList<>();
        }
        return requirements;
    }

    /**
     * @return all maps needed to be finished to play this map
     */
    public List<String> getFinishedAll() {
        if (finishedAll == null) {
            finishedAll = new ArrayList<>();
        }
        return finishedAll;
    }

    /**
     * @return all maps needed to be finished to play this map and a collection of maps of which at least one has to be finished
     */
    public List<String> getFinished() {
        if (finishedAll == null) {
            finishedAll = new ArrayList<>();
        }
        if (finishedOne == null) {
            finishedOne = new ArrayList<>();
        }

        List<String> merge = new ArrayList<>();
        merge.addAll(finishedAll);
        merge.addAll(finishedOne);
        return merge;
    }

    /**
     * @return the rewards
     */
    public List<Reward> getRewards() {
        if (rewards == null) {
            rewards = new ArrayList<>();
        }
        return rewards;
    }

    // Commands and permissions
    /**
     * @return the gameCommandWhitelist
     */
    public List<String> getGameCommandWhitelist() {
        if (gameCommandWhitelist == null) {
            gameCommandWhitelist = new ArrayList<>();
        }
        return gameCommandWhitelist;
    }

    /**
     * @return the gamePermissions
     */
    public List<String> getGamePermissions() {
        if (gamePermissions == null) {
            gamePermissions = new ArrayList<>();
        }
        return gamePermissions;
    }

    // Title
    /**
     * @return the main title string or null for the default one
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param text the text to set
     */
    public void setTitle(String text) {
        title = text;
    }

    /**
     * @return the subtitle string or null for the default one
     */
    public String getSubTitle() {
        return subtitle;
    }

    /**
     * @param text the text to set
     */
    public void setSubTitle(String text) {
        subtitle = text;
    }

    /**
     * @return the action bar string or null for the default one
     */
    public String getActionBar() {
        return actionBar;
    }

    /**
     * @param text the text to set
     */
    public void setActionBar(String text) {
        actionBar = text;
    }

    /**
     * @return the chat message string or null for the default one
     */
    public String getChatText() {
        return chat;
    }

    /**
     * @param text the text to set
     */
    public void setChatText(String text) {
        chat = text;
    }

    /**
     * @return the title fade in time in ticks
     */
    public int getTitleFadeIn() {
        return titleFadeIn;
    }

    /**
     * @param time the time to set
     */
    public void setTitleFadeIn(int time) {
        titleFadeIn = time;
    }

    /**
     * @return the title fade out time in ticks
     */
    public int getTitleFadeOut() {
        return titleFadeOut;
    }

    /**
     * @param time the time to set
     */
    public void setTitleFadeOut(int time) {
        titleFadeOut = time;
    }

    /**
     * @return the time until the title disappears in ticks
     */
    public int getTitleShow() {
        return titleShow;
    }

    /**
     * @param time the time to set
     */
    public void setTitleShow(int time) {
        titleShow = time;
    }

    // Misc
    /**
     * @param id the id of the message
     * @return the message
     */
    public String getMessage(int id) {
        if (msgs == null) {
            msgs = new HashMap<>();
        }
        return msgs.get(id);
    }

    /**
     * @param id  the ID of the message
     * @param msg the message to set
     */
    public void setMessage(int id, String msg) {
        if (msgs == null) {
            msgs = new HashMap<>();
        }
        msgs.put(id, msg);
    }

    /**
     * @return the objects to get passed to another player of the group when this player leaves
     */
    public List<ExItem> getSecureObjects() {
        if (secureObjects == null) {
            secureObjects = new ArrayList<>();
        }
        return secureObjects;
    }

    /**
     * @return if the group tag is enabled. Returns false if HolographicDisplays isn't loaded
     */
    public boolean isGroupTagEnabled() {
        if (!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
            return false;
        }
        return groupTagEnabled;
    }

    /* Actions */
    /**
     * @param defaultValues the GameType that overrides the values that are null.
     */
    public void apply(GameType defaultValues) {
        if (playerVersusPlayer == null) {
            playerVersusPlayer = defaultValues.isPlayerVersusPlayer();
        }

        if (friendlyFire == null) {
            friendlyFire = defaultValues.isFriendlyFire();
        }

        if (timeToFinish == null && defaultValues.getShowTime() != null) {
            timeToFinish = defaultValues.getShowTime() ? null : -1;
        }

        if (breakBlocks == null) {
            breakBlocks = defaultValues.canBreakBlocks();
        }

        if (breakPlacedBlocks == null) {
            breakPlacedBlocks = defaultValues.canBreakPlacedBlocks();
        }

        if (placeBlocks == null) {
            placeBlocks = defaultValues.canPlaceBlocks();
        }

        if (gameMode == null) {
            gameMode = defaultValues.getGameMode();
        }

        if (initialLives == null) {
            if (defaultValues.hasLives() != null) {
                initialLives = defaultValues.hasLives() ? null : -1;
            }
        }
    }

    /**
     * @param defaultValues the GameRules that override the values that are null.
     */
    public void apply(GameRuleProvider defaultValues) {
        /* keepInventory */
        if (keepInventoryOnEnter == null) {
            keepInventoryOnEnter = defaultValues.keepInventoryOnEnter;
        }

        if (keepInventoryOnEscape == null) {
            keepInventoryOnEscape = defaultValues.keepInventoryOnEscape;
        }

        if (keepInventoryOnFinish == null) {
            keepInventoryOnFinish = defaultValues.keepInventoryOnFinish;
        }

        if (keepInventoryOnDeath == null) {
            keepInventoryOnDeath = defaultValues.keepInventoryOnDeath;
        }

        if (lobbyDisabled == null) {
            lobbyDisabled = defaultValues.lobbyDisabled;
        }

        /* World */
        if (gameMode == null) {
            gameMode = defaultValues.gameMode;
        }

        if (fly == null) {
            fly = defaultValues.fly;
        }

        if (breakBlocks == null) {
            breakBlocks = defaultValues.breakBlocks;
        }

        if (breakPlacedBlocks == null) {
            breakPlacedBlocks = defaultValues.breakPlacedBlocks;
        }

        if (breakWhitelist == null) {
            breakWhitelist = defaultValues.breakWhitelist;
        }

        if (damageProtectedEntities == null) {
            // If nothing is specialized for protected entites yet (=> damageProtectedEntites == null and DEFAULT_VALUES are used)
            // and if blocks may be broken, it makes no sense to assume the user wants to have paintings etc. protected.
            if (defaultValues == DEFAULT_VALUES && breakBlocks) {
                damageProtectedEntities = new HashSet<>();
            } else {
                damageProtectedEntities = defaultValues.damageProtectedEntities;
            }
        }

        if (interactionProtectedEntities == null) {
            // If nothing is specialized for protected entites yet (=> interactionProtectedEntites == null and DEFAULT_VALUES are used)
            // and if blocks may be broken, it makes no sense to assume the user wants to have paintings etc. protected.
            if (defaultValues == DEFAULT_VALUES && breakBlocks) {
                interactionProtectedEntities = new HashSet<>();
            } else {
                interactionProtectedEntities = defaultValues.interactionProtectedEntities;
            }
        }

        if (placeBlocks == null) {
            placeBlocks = defaultValues.placeBlocks;
        }

        if (placeWhitelist == null) {
            placeWhitelist = defaultValues.placeWhitelist;
        }

        if (rain == null) {
            rain = defaultValues.rain;
        }

        if (thunder == null) {
            thunder = defaultValues.thunder;
        }

        if (time == null) {
            time = defaultValues.time;
        }

        /* Fighting */
        if (playerVersusPlayer == null) {
            playerVersusPlayer = defaultValues.playerVersusPlayer;
        }

        if (friendlyFire == null) {
            friendlyFire = defaultValues.friendlyFire;
        }

        if (initialLives == null) {
            initialLives = defaultValues.initialLives;
        }

        if (initialGroupLives == null) {
            initialGroupLives = defaultValues.initialGroupLives;
        }

        if (initialScore == null) {
            initialScore = defaultValues.initialScore;
        }

        if (scoreGoal == null) {
            scoreGoal = defaultValues.scoreGoal;
        }

        /* Timer */
        if (timeLastPlayed == null) {
            timeLastPlayed = defaultValues.timeLastPlayed;
        }

        if (timeToNextPlayAfterStart == null) {
            timeToNextPlayAfterStart = defaultValues.timeToNextPlayAfterStart;
        }

        if (timeToNextPlayAfterFinish == null) {
            timeToNextPlayAfterFinish = defaultValues.timeToNextPlayAfterFinish;
        }

        if (timeToNextLoot == null) {
            timeToNextLoot = defaultValues.timeToNextLoot;
        }

        if (timeToNextWave == null) {
            timeToNextWave = defaultValues.timeToNextWave;
        }

        if (timeToFinish == null) {
            timeToFinish = defaultValues.timeToFinish;
        }

        if (timeUntilKickOfflinePlayer == null) {
            timeUntilKickOfflinePlayer = defaultValues.timeUntilKickOfflinePlayer;
        }

        /* Requirements and rewards */
        if (requirements == null) {
            requirements = defaultValues.requirements;
        }

        if (finishedOne == null) {
            finishedOne = defaultValues.finishedOne;
        }

        if (finishedAll == null) {
            finishedAll = defaultValues.finishedAll;
        }

        if (rewards == null) {
            rewards = defaultValues.rewards;
        }

        /* Commands and permissions */
        if (gameCommandWhitelist == null) {
            gameCommandWhitelist = defaultValues.gameCommandWhitelist;
        } else if (defaultValues.gameCommandWhitelist != null) {
            gameCommandWhitelist.addAll(defaultValues.gameCommandWhitelist);
        }

        if (gamePermissions == null) {
            gamePermissions = defaultValues.gamePermissions;
        } else if (defaultValues.gamePermissions != null) {
            gamePermissions.addAll(defaultValues.gamePermissions);
        }

        /* Title */
        if (title == null) {
            title = defaultValues.title;
        }

        if (subtitle == null) {
            subtitle = defaultValues.subtitle;
        }

        if (actionBar == null) {
            actionBar = defaultValues.actionBar;
        }

        if (chat == null) {
            chat = defaultValues.chat;
        }

        if (titleFadeIn == null) {
            titleFadeIn = defaultValues.titleFadeIn;
        }

        if (titleFadeOut == null) {
            titleFadeOut = defaultValues.titleFadeOut;
        }

        if (titleShow == null) {
            titleShow = defaultValues.titleShow;
        }

        /* Misc */
        if (msgs == null) {
            msgs = defaultValues.msgs;
        } else if (defaultValues.msgs != null) {
            msgs.putAll(defaultValues.msgs);
        }

        if (secureObjects == null) {
            secureObjects = defaultValues.secureObjects;
        } else if (defaultValues.secureObjects != null) {
            secureObjects.addAll(defaultValues.secureObjects);
        }

        if (groupTagEnabled == null) {
            groupTagEnabled = defaultValues.groupTagEnabled;
        }
    }

}
