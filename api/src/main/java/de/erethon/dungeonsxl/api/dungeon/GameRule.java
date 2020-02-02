/*
 * Copyright (C) 2014-2020 Daniel Saukel
 *
 * This library is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNULesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.erethon.dungeonsxl.api.dungeon;

import de.erethon.caliburn.CaliburnAPI;
import de.erethon.caliburn.item.ExItem;
import de.erethon.caliburn.mob.ExMob;
import de.erethon.caliburn.mob.VanillaMob;
import de.erethon.commons.misc.EnumUtil;
import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.Requirement;
import de.erethon.dungeonsxl.api.Reward;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.GameMode;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Represents a game rule for a {@link Game}.
 *
 * @param <V> the type of the game rule value
 * @author Daniel Saukel
 */
// TODO: These values aren't properly fetched from config yet: values that involve Caliburn, requirements & rewards; maybe messages.
// Check special merging rules for damageProtectedEntities and interactionProtectedEntities.
public class GameRule<V> {

    /**
     * Shall players play the dungeon with their own items or do you want to use classes?
     */
    public static final GameRule<Boolean> KEEP_INVENTORY_ON_ENTER = new GameRule<>(Boolean.class, "keepInventoryOnEnter", false);
    /**
     * Shall players keep their inventory when they leave the dungeon without succeeding?
     */
    public static final GameRule<Boolean> KEEP_INVENTORY_ON_ESCAPE = new GameRule<>(Boolean.class, "keepInventoryOnEscape", false);
    /**
     * Shall players keep their inventory when they finish the dungeon?
     */
    public static final GameRule<Boolean> KEEP_INVENTORY_ON_FINISH = new GameRule<>(Boolean.class, "keepInventoryOnFinish", false);
    /**
     * Shall players lose their items when they die (do not mix up this with "onEscape"!)?
     */
    public static final GameRule<Boolean> KEEP_INVENTORY_ON_DEATH = new GameRule<>(Boolean.class, "keepInventoryOnDeath", true);
    /**
     * If the Lobby is disabled. This applies only to Dungeons that have to be solved alone and where there are no classes to choose from.
     */
    public static final GameRule<Boolean> IS_LOBBY_DISABLED = new GameRule<>(Boolean.class, "isLobbyDisabled", false);
    /**
     * The game mode.
     */
    public static final GameRule<GameMode> GAME_MODE = new GameRule<>(GameMode.class, "gameMode", GameMode.SURVIVAL);
    /**
     * If players may fly.
     */
    public static final GameRule<Boolean> FLY = new GameRule<>(Boolean.class, "fly", false);
    /**
     * If players can build and destroy blocks in this world.
     */
    public static final GameRule<Boolean> BREAK_BLOCKS = new GameRule<>(Boolean.class, "breakBlocks", false);
    /**
     * If players may destroy blocks they placed themselves.
     */
    public static final GameRule<Boolean> BREAK_PLACED_BLOCKS = new GameRule<>(Boolean.class, "breakPlacedBlocks", false);
    /**
     * A whitelist of breakable blocks. breakBlocks is supposed to be set to "true" if this should be used.
     */
    public static final GameRule<Map<ExItem, HashSet<ExItem>>> BREAK_WHITELIST = new MapGameRule<>(Map.class, "breakWhitelist", null);
    /**
     * A list of all entity types that shall be protected from damage. If this is left out AND if breakBlocks is false, armor stands, paintings and item frames
     * will be protected by default. If this is left out and if breakBlocks is true, nothing will be protected by default.
     */
    public static final GameRule<Set<ExMob>> DAMAGE_PROTECTED_ENTITIES = new CollectionGameRule<>(Set.class, "damageProtectedEntities", new HashSet<>(Arrays.asList(
            VanillaMob.ARMOR_STAND,
            VanillaMob.ITEM_FRAME,
            VanillaMob.PAINTING
    )));
    /**
     * If this is left out AND if breakBlocks is false, armor stands and item frames will be protected by default. If this is left out and if breakBlocks is
     * true, nothing will be protected by default.
     */
    public static final GameRule<Set<ExMob>> INTERACTION_PROTECTED_ENTITIES = new CollectionGameRule<>(Set.class, "interactionProtectedEntities", new HashSet<>(Arrays.asList(
            VanillaMob.ARMOR_STAND,
            VanillaMob.ITEM_FRAME
    )));
    /**
     * If blocks may be placed.
     */
    public static final GameRule<Boolean> PLACE_BLOCKS = new GameRule<>(Boolean.class, "placeBlocks", false);
    /**
     * A whitelist of placeable blocks. placeBlocks is supposed to be set to "true" if this should be used.
     */
    public static final GameRule<Set<ExItem>> PLACE_WHITELIST = new CollectionGameRule<>(Set.class, "placeWhitelist", null);
    /**
     * If it should rain permanently in the dungeon.
     * <p>
     * true = permanent rain; false = permanent sun; leaving this out = random weather like in vanilla Minecraft
     */
    public static final GameRule<Boolean> RAIN = new GameRule<>(Boolean.class, "rain", null);
    /**
     * Thunderstorms.
     *
     * @see #RAIN
     */
    public static final GameRule<Boolean> THUNDER = new GameRule<>(Boolean.class, "thunder", null);
    /**
     * The time ticks (to be used like in the vanilla /time command).
     */
    public static final GameRule<Long> TIME = new GameRule<>(Long.class, "time", null);
    /**
     * PvP
     */
    public static final GameRule<Boolean> PLAYER_VERSUS_PLAYER = new GameRule<>(Boolean.class, "playerVersusPlayer", false);
    /**
     * Friendly fire refers just to members of the same group.
     */
    public static final GameRule<Boolean> FRIENDLY_FIRE = new GameRule<>(Boolean.class, "friendlyFire", false);
    /**
     * Amount of lives a player initially has when he enters a dungeon.
     */
    public static final GameRule<Integer> INITIAL_LIVES = new GameRule<>(Integer.class, "initialLives", -1);
    /**
     * Alternatively to {@link #INITIAL_LIVES player lives}, you can use group lives.
     */
    public static final GameRule<Integer> INITIAL_GROUP_LIVES = new GameRule<>(Integer.class, "initialGroupLives", -1);
    /**
     * Score used for capture the flag and similar game types.
     */
    public static final GameRule<Integer> INITIAL_SCORE = new GameRule<>(Integer.class, "initialScore", 3);
    /**
     * The amount of goals to score before the game ends. -1 = not used.
     */
    public static final GameRule<Integer> SCORE_GOAL = new GameRule<>(Integer.class, "scoreGoal", -1);
    /**
     * Time in hours when the game may be played again after it has been started.
     */
    public static final GameRule<Integer> TIME_TO_NEXT_PLAY_AFTER_START = new GameRule<>(Integer.class, "timeToNextPlayAfterStart", 0);
    /**
     * When the game may be played again after it has been finished.
     */
    public static final GameRule<Integer> TIME_TO_NEXT_PLAY_AFTER_FINISH = new GameRule<>(Integer.class, "timeToNextPlayAfterFinish", 0);
    /**
     * When loot may be taken away out of the dungeon again.
     */
    public static final GameRule<Integer> TIME_TO_NEXT_LOOT = new GameRule<>(Integer.class, "timeToNextLoot", 0);
    /**
     * The cooldown between two mob waves.
     */
    public static final GameRule<Integer> TIME_TO_NEXT_WAVE = new GameRule<>(Integer.class, "timeToNextWave", 10);
    /**
     * The time left to finish the game; -1 if no timer is used.
     */
    public static final GameRule<Integer> TIME_TO_FINISH = new GameRule<>(Integer.class, "timeToFinish", -1);
    /**
     * Time until a player is kicked out of a group after he leaves the server.
     */
    public static final GameRule<Integer> TIME_UNTIL_KICK_OFFLINE_PLAYER = new GameRule<>(Integer.class, "timeUntilKickOfflinePlayer", 0);
    /**
     * A list of requirements. Note that requirements will be ignored if the player has the dxl.ignorerequirements permission node.
     */
    public static final GameRule<List<Requirement>> REQUIREMENTS = new CollectionGameRule<>(List.class, "requirements", new ArrayList<>());
    /**
     * One of these Dungeons must be finished ("any" for any dungeon).
     */
    public static final GameRule<List<String>> MUST_FINISH_ONE = new CollectionGameRule<>(List.class, "mustFinishOne", null);
    /**
     * All of these Dungeons must be finished. If you do not want any, leave this empty.
     */
    public static final GameRule<List<String>> MUST_FINISH_ALL = new CollectionGameRule<>(List.class, "mustFinishAll", null);
    /**
     * This can be used to give rewards. The default implementation does not do this at the moment.
     */
    public static final GameRule<List<Reward>> REWARDS = new CollectionGameRule<>(List.class, "rewards", new ArrayList<>());
    /**
     * These commands can be used by all players if they are in the dungeon. DXL commands like /dxl leavecan be used by default.
     */
    public static final GameRule<List<String>> GAME_COMMAND_WHITELIST = new CollectionGameRule<>(List.class, "gameCommandWhitelist", new ArrayList<>());
    /**
     * A list of permissions players get while they play the game. The permissions get removed as soon as the player leaves the game. Requires Vault and a
     * permissions plugin like PermissionsEx.
     */
    public static final GameRule<List<String>> GAME_PERMISSIONS = new CollectionGameRule<>(List.class, "gamePermissions", new ArrayList<>());
    /**
     * Use this to replace the default ready / new floor message. If titles are deactivated in the main config, this is not going to work.
     */
    public static final GameRule<String> TITLE = new GameRule<>(String.class, "title", null);
    /**
     * Use this to replace the default ready / new floor message. If titles are deactivated in the main config, this is not going to work.
     */
    public static final GameRule<String> SUBTITLE = new GameRule<>(String.class, "subtitle", null);
    /**
     * Use this to replace the default ready / new floor message. If titles are deactivated in the main config, this is not going to work.
     */
    public static final GameRule<String> ACTION_BAR = new GameRule<>(String.class, "actionBar", null);
    /**
     * Use this to replace the default ready / new floor message. If titles are deactivated in the main config, this is not going to work.
     */
    public static final GameRule<String> CHAT = new GameRule<>(String.class, "chat", null);
    /**
     * Use this to replace the default ready / new floor message. If titles are deactivated in the main config, this is not going to work.
     */
    public static final GameRule<Integer> TITLE_FADE_IN = new GameRule<>(Integer.class, "titleFadeIn", 20);
    /**
     * Use this to replace the default ready / new floor message. If titles are deactivated in the main config, this is not going to work.
     */
    public static final GameRule<Integer> TITLE_FADE_OUT = new GameRule<>(Integer.class, "titleFadeOut", 20);
    /**
     * Use this to replace the default ready / new floor message. If titles are deactivated in the main config, this is not going to work.
     */
    public static final GameRule<Integer> TITLE_SHOW = new GameRule<>(Integer.class, "titleShow", 60);
    /**
     * Messages; also to be created with /dxl msg
     */
    public static final GameRule<Map<Integer, String>> MESSAGES = new MapGameRule<>(Map.class, "msgs", new HashMap<>());
    /**
     * Items you cannot drop or destroy.
     */
    public static final GameRule<List<ExItem>> SECURE_OBJECTS = new CollectionGameRule<>(List.class, "secureObjects", new ArrayList<>());
    /**
     * If group tags are used.
     */
    public static final GameRule<Boolean> GROUP_TAG_ENABLED = new GameRule<>(Boolean.class, "groupTagEnabled", false);

    protected Class<V> type;
    private String key;
    private V defaultValue;

    public GameRule(Class<V> type, String key, V defaultValue) {
        this.type = type;
        this.key = key;
        this.defaultValue = defaultValue;
    }

    /**
     * Returns the configuration key of the game rule.
     *
     * @return the configuration key of the game rule
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns the value used if nothing is specified by a game rule provider.
     *
     * @return the value used if nothing is specified by a game rule provider
     */
    public V getDefaultValue() {
        return defaultValue;
    }

    /**
     * Returns if the given value is an instance of {@link V}.
     *
     * @param value the value
     * @return if the given value is an instance of {@link V}
     */
    public boolean isValidValue(Object value) {
        return type.isInstance(value);
    }

    /**
     * Returns the state of the game rule fetched from the config.
     * <p>
     * If the type of this game rule is an enum, Strings as config values that are the {@link Enum#name()} of an enum value are converted
     * automatically.
     *
     * @param api       the API instance
     * @param caliburn  the CaliburnAPI instance
     * @param container the game rule container whose state is to be set
     * @param config    the config to fetch the value from
     * @return the value
     */
    public V fromConfig(DungeonsAPI api, CaliburnAPI caliburn, GameRuleContainer container, ConfigurationSection config) {
        Object value = config.get(getKey());

        if (Enum.class.isAssignableFrom(type)) {
            if (!(value instanceof String)) {
                return null;
            }
            value = EnumUtil.getEnumIgnoreCase((Class<? extends Enum>) type, (String) value);
        }

        return isValidValue(value) ? (V) value : null;
    }

    /**
     * Compares the state attached to the game rule of two GameRuleContainers.
     * <p>
     * This may be overriden if necessary, for example if the value is a {@link java.util.Collection} and the desired behavior is to merge the values instead of
     * keeping the overriding one.
     *
     * @param overriding the state of this container will by default be copied to the "writeTo" container if it is not null
     * @param subsidiary the state of this container will by default be copied to the "writeTo" container if the state of the "overriding" container is null
     * @param writeTo    the state of the game rule will be set to the one of either "overriding" or "subsidiary". This container may be == to one of the
     *                   others.
     */
    public void merge(GameRuleContainer overriding, GameRuleContainer subsidiary, GameRuleContainer writeTo) {
        V overridingValue = overriding.getState(this);
        V subsidiaryValue = subsidiary.getState(this);
        writeTo.setState(this, overridingValue != null ? overridingValue : subsidiaryValue);
    }

}
