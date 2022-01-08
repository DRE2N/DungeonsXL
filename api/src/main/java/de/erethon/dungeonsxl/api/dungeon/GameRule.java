/*
 * Copyright (C) 2014-2022 Daniel Saukel
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

import de.erethon.caliburn.item.ExItem;
import de.erethon.caliburn.mob.ExMob;
import de.erethon.commons.chat.MessageUtil;
import de.erethon.commons.misc.EnumUtil;
import de.erethon.commons.misc.NumberUtil;
import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.Requirement;
import de.erethon.dungeonsxl.api.Reward;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Represents a game rule for a {@link Game}.
 *
 * @param <V> the type of the game rule value
 * @author Daniel Saukel
 */
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
     * Shall players reset their inventory to their chosen class when respawning?
     */
    public static final GameRule<Boolean> RESET_CLASS_INVENTORY_ON_RESPAWN = new GameRule<>(Boolean.class, "resetClassInventoryOnRespawn", false);
    /**
     * The location where the players spawn when they leave the dungeon without succeeding.
     */
    public static final GameRule<String> ESCAPE_LOCATION = new GameRule<>(String.class, "escapeLocation", null);
    /**
     * The location where the players spawn when they finish the dungeon.
     */
    public static final GameRule<String> FINISH_LOCATION = new GameRule<>(String.class, "finishLocation", null);
    /**
     * The goal of the game that defines what makes it end.
     */
    public static final GameRule<GameGoal> GAME_GOAL = new GameRule<>(GameGoal.class, "gameGoal", GameGoal.DEFAULT, GameGoal.READER);
    /**
     * The Vanilla game mode.
     */
    public static final GameRule<GameMode> GAME_MODE = new GameRule<>(GameMode.class, "gameMode", GameMode.SURVIVAL);
    /**
     * The Vanilla difficulty.
     */
    public static final GameRule<Difficulty> DIFFICULTY = new GameRule<>(Difficulty.class, "difficulty", Difficulty.NORMAL);
    /**
     * If the food levels of the players change.
     */
    public static final GameRule<Boolean> FOOD_LEVEL = new GameRule<>(Boolean.class, "foodLevel", true);
    /**
     * Sets if death screens are enabled. If false, players that would have died are healed and teleported to the respawn location;
     * their inventory and experience are dropped if {@link #KEEP_INVENTORY_ON_DEATH} is set to false.
     */
    public static final GameRule<Boolean> DEATH_SCREEN = new GameRule<>(Boolean.class, "deathScreen", false);
    /**
     * If players may fly.
     */
    public static final GameRule<Boolean> FLY = new GameRule<>(Boolean.class, "fly", false);
    /**
     * If players can build and destroy blocks in this world.
     */
    public static final GameRule<BuildMode> BREAK_BLOCKS = new GameRule<>(BuildMode.class, "breakBlocks", BuildMode.FALSE, ConfigReader.BUILD_MODE_READER);
    /**
     * A blacklist of block types players cannot interact with.
     */
    public static final GameRule<Map<ExItem, HashSet<ExItem>>> INTERACTION_BLACKLIST
            = new MapGameRule<>("interactionBlacklist", new HashMap<>(), ConfigReader.TOOL_BLOCK_MAP_READER, HashMap::new);
    /**
     * A list of all entity types that shall be protected from damage.
     */
    public static final GameRule<Set<ExMob>> DAMAGE_PROTECTED_ENTITIES = new CollectionGameRule<>("damageProtectedEntities", new HashSet<>(), ConfigReader.EX_MOB_SET_READER, HashSet::new);
    /**
     * A list of all entity types that shall be protected from interaction.
     */
    public static final GameRule<Set<ExMob>> INTERACTION_PROTECTED_ENTITIES = new CollectionGameRule<>("interactionProtectedEntities", new HashSet<>(), ConfigReader.EX_MOB_SET_READER, HashSet::new);
    /**
     * If blocks may be placed.
     */
    public static final GameRule<BuildMode> PLACE_BLOCKS = new GameRule<>(BuildMode.class, "placeBlocks", BuildMode.FALSE, ConfigReader.BUILD_MODE_READER);
    /**
     * A set of blocks that do not fade.
     *
     * @see <a href="https://hub.spigotmc.org/javadocs/spigot/org/bukkit/event/block/BlockFadeEvent.html">org.bukkit.event.block.BlockFadeEvent</a>
     */
    public static final GameRule<Set<ExItem>> BLOCK_FADE_DISABLED = new CollectionGameRule<>("blockFadeDisabled", new HashSet<>(), ConfigReader.EX_ITEM_SET_READER, HashSet::new);
    /**
     * This does what the doFireTick Vanilla game rule does.
     */
    public static final GameRule<Boolean> FIRE_TICK = new GameRule<>(Boolean.class, "fireTick", false);
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
     * When loot may be taken away out of the dungeon again.
     */
    public static final GameRule<Integer> TIME_TO_NEXT_LOOT = new GameRule<>(Integer.class, "timeToNextLoot", 0);
    /**
     * The cooldown between two mob waves.
     */
    public static final GameRule<Integer> TIME_TO_NEXT_WAVE = new GameRule<>(Integer.class, "timeToNextWave", 10);
    /**
     * Time until a player is kicked out of a group after he leaves the server.
     */
    public static final GameRule<Integer> TIME_UNTIL_KICK_OFFLINE_PLAYER = new GameRule<>(Integer.class, "timeUntilKickOfflinePlayer", 0);
    /**
     * A list of requirements. Note that requirements will be ignored if the player has the dxl.ignorerequirements permission node.
     */
    public static final GameRule<List<Requirement>> REQUIREMENTS = new CollectionGameRule<>("requirements", new ArrayList<>(), (api, value) -> {
        if (!(value instanceof ConfigurationSection)) {
            return null;
        }
        ConfigurationSection section = (ConfigurationSection) value;
        List<Requirement> requirements = new ArrayList<>();
        for (String key : section.getValues(false).keySet()) {
            Class<? extends Requirement> clss = api.getRequirementRegistry().get(key);
            if (clss == null) {
                MessageUtil.log(api, "&4Could not find requirement named \"" + key + "\".");
                continue;
            }
            try {
                Constructor constructor = clss.getConstructor(DungeonsAPI.class);
                if (constructor == null) {
                    MessageUtil.log(api, "&4Requirement \"" + key + "\" is not implemented properly with a (DungeonsAPI) constructor.");
                    continue;
                }
                Requirement requirement = (Requirement) constructor.newInstance(api);
                requirement.setup(section);
                requirements.add(requirement);
            } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException exception) {
                MessageUtil.log(api, "&4Requirement \"" + key + "\" is not implemented properly with a (DungeonsAPI) constructor.");
            }
        }
        return requirements;
    }, ArrayList::new);
    /**
     * This can be used to give rewards. The default implementation does not do this at the moment.
     */
    public static final GameRule<List<Reward>> REWARDS = new CollectionGameRule<Reward, List<Reward>>("rewards", new ArrayList<>(), (api, value) -> {
        if (!(value instanceof ConfigurationSection)) {
            return null;
        }
        ConfigurationSection section = (ConfigurationSection) value;
        List<Reward> rewards = new ArrayList<>();
        for (String key : section.getValues(false).keySet()) {
            Class<? extends Reward> clss = api.getRewardRegistry().get(key);
            if (clss == null) {
                MessageUtil.log(api, "&4Could not find reward named \"" + key + "\".");
                continue;
            }
            try {
                Constructor constructor = clss.getConstructor(DungeonsAPI.class);
                if (constructor == null) {
                    MessageUtil.log(api, "&4Reward \"" + key + "\" is not implemented properly with a (DungeonsAPI) constructor.");
                    continue;
                }
                Reward reward = (Reward) constructor.newInstance(api);
                // reward.setup();
                rewards.add(reward);
            } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException exception) {
                MessageUtil.log(api, "&4Reward \"" + key + "\" is not implemented properly with a (DungeonsAPI) constructor.");
            }
        }
        return rewards;
    }, ArrayList::new);
    /**
     * These commands can be used by all players if they are in the dungeon. DXL commands like /dxl leavecan be used by default.
     */
    public static final GameRule<List<String>> GAME_COMMAND_WHITELIST = new CollectionGameRule<>("gameCommandWhitelist", new ArrayList<>(), ArrayList::new);
    /**
     * A list of permissions players get while they play the game. The permissions get removed as soon as the player leaves the game. Requires Vault and a
     * permissions plugin like PermissionsEx.
     */
    public static final GameRule<List<String>> GAME_PERMISSIONS = new CollectionGameRule<>("gamePermissions", new ArrayList<>(), ArrayList::new);
    /**
     * Use this to replace the default ready / new floor message. If titles are deactivated in the main config, this is not going to work.
     */
    public static final GameRule<String> TITLE = new GameRule<>(String.class, "title.title", null);
    /**
     * Use this to replace the default ready / new floor message. If titles are deactivated in the main config, this is not going to work.
     */
    public static final GameRule<String> SUBTITLE = new GameRule<>(String.class, "title.subtitle", null);
    /**
     * Use this to replace the default ready / new floor message. If titles are deactivated in the main config, this is not going to work.
     */
    public static final GameRule<String> ACTION_BAR = new GameRule<>(String.class, "title.actionBar", null);
    /**
     * Use this to replace the default ready / new floor message. If titles are deactivated in the main config, this is not going to work.
     */
    public static final GameRule<String> CHAT = new GameRule<>(String.class, "title.chat", null);
    /**
     * Use this to replace the default ready / new floor message. If titles are deactivated in the main config, this is not going to work.
     */
    public static final GameRule<Integer> TITLE_FADE_IN = new GameRule<>(Integer.class, "title.fadeIn", 20);
    /**
     * Use this to replace the default ready / new floor message. If titles are deactivated in the main config, this is not going to work.
     */
    public static final GameRule<Integer> TITLE_FADE_OUT = new GameRule<>(Integer.class, "title.fadeOut", 20);
    /**
     * Use this to replace the default ready / new floor message. If titles are deactivated in the main config, this is not going to work.
     */
    public static final GameRule<Integer> TITLE_SHOW = new GameRule<>(Integer.class, "title.show", 60);
    /**
     * Messages; also to be created with /dxl msg
     */
    public static final GameRule<Map<Integer, String>> MESSAGES = new MapGameRule<>("messages", new HashMap<>(), (api, value) -> {
        if (!(value instanceof ConfigurationSection)) {
            return null;
        }
        ConfigurationSection section = (ConfigurationSection) value;
        Map<Integer, String> map = new HashMap<>();
        for (Map.Entry<String, Object> entry : section.getValues(false).entrySet()) {
            int id = NumberUtil.parseInt(entry.getKey(), -1);
            if (id == -1) {
                continue;
            }
            if (!(entry.getValue() instanceof String)) {
                continue;
            }
            map.put(id, (String) entry.getValue());
        }
        return map;
    }, HashMap::new);
    /**
     * Items you cannot drop or destroy.
     */
    public static final GameRule<Set<ExItem>> SECURE_OBJECTS = new CollectionGameRule<>("secureObjects", new HashSet<>(), ConfigReader.EX_ITEM_SET_READER, HashSet::new);
    /**
     * If group tags are used.
     */
    public static final GameRule<Boolean> GROUP_TAG_ENABLED = new GameRule<>(Boolean.class, "groupTagEnabled", false);
    /**
     * If Citizens NPCs should be copied to the native registry.
     */
    public static final GameRule<Boolean> USE_NATIVE_CITIZENS_REGISTRY = new GameRule<>(Boolean.class, "useNativeCitizensRegistry", false);
    /**
     * If mobs shall drop experience or a whitelist of mobs that drop experience, while all others do not.
     */
    public static final GameRule<Object> MOB_EXP_DROPS = new GameRule(Object.class, "mobExpDrops", false,
            (api, value) -> value instanceof Boolean ? value : ConfigReader.EX_MOB_SET_READER.read(api, value)
    );
    /**
     * If mobs shall drop items or a whitelist of mobs that drop items, while all others do not.
     */
    public static final GameRule<Object> MOB_ITEM_DROPS = new GameRule(Object.class, "mobItemDrops", false,
            (api, value) -> value instanceof Boolean ? value : ConfigReader.EX_MOB_SET_READER.read(api, value)
    );

    /**
     * An array of all game rules that exist natively in DungeonsXL.
     */
    public static final GameRule[] VALUES = values();
    /**
     * A container of all rules with their default value. This is used internally as the most subsidiary container that fills missing rules if they are not set.
     */
    public static final GameRuleContainer DEFAULT_VALUES = new GameRuleContainer();

    static {
        for (GameRule rule : VALUES) {
            DEFAULT_VALUES.setState(rule, rule.getDefaultValue());
        }
    }

    private static GameRule[] values() {
        Field[] fields = GameRule.class.getFields();
        GameRule[] values = new GameRule[fields.length - 2];
        int i = 0;
        for (Field field : fields) {
            try {
                Object object = field.get(null);
                if (object instanceof GameRule) {
                    values[i++] = (GameRule) object;
                }
            } catch (IllegalArgumentException | IllegalAccessException exception) {
                exception.printStackTrace();
            }
        }
        return values;
    }

    protected Class<V> type;
    protected ConfigReader<V> reader;
    private String key;
    private V defaultValue;

    /**
     * @param type         the class of V
     * @param key          the configuration key of the game rule
     * @param defaultValue the default value that is used when nothing is set
     */
    public GameRule(Class<V> type, String key, V defaultValue) {
        this.type = type;
        this.key = key;
        this.defaultValue = defaultValue;
    }

    /**
     * @param type         the class of V
     * @param key          the configuration key of the game rule
     * @param defaultValue the default value that is used when nothing is set
     * @param reader       a functional interface that loads the value from config
     */
    public GameRule(Class<V> type, String key, V defaultValue, ConfigReader<V> reader) {
        this(type, key, defaultValue);
        this.reader = reader;
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
     * If the type of this game rule is an enum, Strings as config values that are the {@link Enum#name()} of an enum value are converted automatically.
     *
     * @param api       the API instance
     * @param container the game rule container whose state is to be set
     * @param config    the config to fetch the value from
     * @return the value
     */
    public V fromConfig(DungeonsAPI api, GameRuleContainer container, ConfigurationSection config) {
        Object value = config.get(getKey());
        if (reader != null) {
            V v = reader.read(api, value);
            container.setState(this, v);
            return v;
        }

        if (Enum.class.isAssignableFrom(type)) {
            if (!(value instanceof String)) {
                return null;
            }
            value = EnumUtil.getEnumIgnoreCase((Class<? extends Enum>) type, (String) value);
        }

        if (isValidValue(value)) {
            container.setState(this, (V) value);
            return (V) value;
        } else {
            return null;
        }
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

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{key=" + key + "}";
    }

}
