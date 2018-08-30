/*
 * Copyright (C) 2012-2018 Frank Baumann
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY), without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.erethon.dungeonsxl.game.rule;

import de.erethon.caliburn.CaliburnAPI;
import de.erethon.caliburn.item.ExItem;
import de.erethon.caliburn.mob.ExMob;
import de.erethon.caliburn.mob.VanillaMob;
import de.erethon.commons.misc.EnumUtil;
import de.erethon.commons.misc.NumberUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.requirement.Requirement;
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
 * @author Daniel Saukel
 */
public enum GameRuleDefault implements GameRule {

    /* keepInventory */
    KEEP_INVENTORY_ON_ENTER("keepInventoryOnEnter", false),
    KEEP_INVENTORY_ON_ESCAPE("keepInventoryOnEscape", false),
    KEEP_INVENTORY_ON_FINISH("keepInventoryOnFinish", false),
    KEEP_INVENTORY_ON_DEATH("keepInventoryOnDeath", true),
    LOBBY_DISABLED("isLobbyDisabled", false),
    /* World */
    GAME_MODE("gameMode", GameMode.SURVIVAL) {
        @Override
        public Object fromConfig(DungeonsXL plugin, CaliburnAPI caliburn, GameRuleProvider provider, ConfigurationSection config) {
            if (config.contains("gameMode")) {
                if (EnumUtil.isValidEnum(GameMode.class, config.getString("gameMode").toUpperCase())) {
                    return GameMode.valueOf(config.getString("gameMode"));
                } else {
                    return GameMode.getByValue(config.getInt("gameMode"));
                }
            } else {
                return null;
            }
        }
    },
    BREAK_BLOCKS("breakBlocks", false),
    BREAK_PLACED_BLOCKS("breakPlacedBlocks", false),
    BREAK_WHITELIST("breakWhitelist", null) {
        @Override
        public Object fromConfig(DungeonsXL plugin, CaliburnAPI caliburn, GameRuleProvider provider, ConfigurationSection config) {
            if (config.contains("breakWhitelist")) {
                Map<ExItem, HashSet<ExItem>> ret = new HashMap<>();
                for (Map.Entry<String, Object> entry : config.getConfigurationSection("breakWhitelist").getValues(true).entrySet()) {
                    ExItem breakable = caliburn.getExItem(entry.getKey());

                    HashSet<ExItem> tools = new HashSet<>();
                    if (entry.getValue() instanceof List) {
                        for (String materialString : (List<String>) entry.getValue()) {
                            ExItem tool = caliburn.getExItem(materialString);
                            if (tool != null) {
                                tools.add(tool);
                            }
                        }
                    }

                    ret.put(breakable, tools);
                }
                return ret;
            } else {
                return null;
            }
        }
    },
    DAMAGE_PROTECTED_ENTITIES("damageProtectedEntities", new HashSet<>(Arrays.asList(
            VanillaMob.ARMOR_STAND,
            VanillaMob.ITEM_FRAME,
            VanillaMob.PAINTING
    ))) {
        @Override
        public Object fromConfig(DungeonsXL plugin, CaliburnAPI caliburn, GameRuleProvider provider, ConfigurationSection config) {
            if (config.contains("damageProtectedEntities")) {
                Set<ExMob> ret = new HashSet<>();
                config.getStringList("damageProtectedEntities").forEach(e -> ret.add(caliburn.getExMob(e)));
                return ret;
            } else {
                return null;
            }
        }
    },
    INTERACTION_PROTECTED_ENTITIES("interactionProtectedEntities", new HashSet<>(Arrays.asList(
            VanillaMob.ARMOR_STAND,
            VanillaMob.ITEM_FRAME
    ))) {
        @Override
        public Object fromConfig(DungeonsXL plugin, CaliburnAPI caliburn, GameRuleProvider provider, ConfigurationSection config) {
            if (config.contains("interactionProtectedEntities")) {
                Set<ExMob> ret = new HashSet<>();
                config.getStringList("interactionProtectedEntities").forEach(e -> ret.add(caliburn.getExMob(e)));
                return ret;
            } else {
                return null;
            }
        }
    },
    PLACE_BLOCKS("placeBlocks", false),
    PLACE_WHITELIST("placeWhitelist", null) {
        @Override
        public Object fromConfig(DungeonsXL plugin, CaliburnAPI caliburn, GameRuleProvider provider, ConfigurationSection config) {
            if (config.contains("placeWhitelist")) {
                Set<ExItem> ret = new HashSet<>();
                for (String materialString : config.getStringList("placeWhitelist")) {
                    ExItem item = caliburn.getExItem(materialString);
                    if (item != null) {
                        ret.add(item);
                    }
                }
                return ret;
            } else {
                return null;
            }
        }
    },
    RAIN("rain", null),
    THUNDER("thunder", null),
    TIME("time", null),
    /* Fighting */
    PLAYER_VERSUS_PLAYER("playerVersusPlayer", false),
    FRIENDLY_FIRE("friendlyFire", false),
    INITIAL_LIVES("initialLives", -1),
    INITIAL_GROUP_LIVES("initialGroupLives", -1),
    INITIAL_SCORE("initialScore", 3),
    SCORE_GOAL("scoreGoal", -1),
    /* Timer */
    TIME_LAST_PLAYED("timeLastPlayed", 0),
    TIME_TO_NEXT_PLAY_AFTER_START("timeToNextPlayAfterStart", 0),
    TIME_TO_NEXT_PLAY("timeToNextPlay", 0),
    TIME_TO_NEXT_PLAY_AFTER_FINISH("timeToNextPlayAfterFinish", 0),
    TIME_TO_NEXT_LOOT("timeToNextLoot", 0),
    TIME_TO_NEXT_WAVE("timeToNextWave", 10),
    TIME_TO_FINISH("timeToFinish", -1),
    TIME_UNTIL_KICK_OFFLINE_PLAYER("timeUntilKickOfflinePlayer", 0),
    /* Requirements and rewards */
    REQUIREMENTS("requirements", new ArrayList<>()) {
        @Override
        public Object fromConfig(DungeonsXL plugin, CaliburnAPI caliburn, GameRuleProvider provider, ConfigurationSection config) {
            if (config.contains("requirements")) {
                List<Requirement> ret = new ArrayList<>();
                ConfigurationSection requirementSection = config.getConfigurationSection("requirements");
                for (String identifier : config.getConfigurationSection("requirements").getKeys(false)) {
                    Requirement requirement = Requirement.create(plugin, plugin.getRequirementTypeCache().getByIdentifier(identifier));
                    requirement.setup(requirementSection);
                    ret.add(requirement);
                }
                return ret;
            } else {
                return null;
            }
        }
    },
    FINISHED_ONE("mustFinishOne", null) {
        @Override
        public Object fromConfig(DungeonsXL plugin, CaliburnAPI caliburn, GameRuleProvider provider, ConfigurationSection config) {
            if (config.contains("mustFinishOne")) {
                return config.getStringList("mustFinishOne");
            } else {
                return new ArrayList<>();
            }
        }
    },
    FINISHED_ALL("mustFinishAll", null) {
        @Override
        public Object fromConfig(DungeonsXL plugin, CaliburnAPI caliburn, GameRuleProvider provider, ConfigurationSection config) {
            if (config.contains("mustFinishAll")) {
                return config.getStringList("mustFinishAll");
            } else {
                return new ArrayList<>();
            }
        }
    },
    REWARDS("rewards", new ArrayList<>()),
    /* Commands and permissions */
    GAME_COMMAND_WHITELIST("gameCommandWhitelist", new ArrayList<>()),
    GAME_PERMISSIONS("gamePermissions", new ArrayList<>()),
    /* Title */
    TITLE("title.title", null),
    SUBTITLE("title.subtitle", null),
    ACTION_BAR("title.actionBar", null),
    CHAT("title.chat", null),
    TITLE_FADE_IN("title.fadeIn", 20) {
        @Override
        public Object fromConfig(DungeonsXL plugin, CaliburnAPI caliburn, GameRuleProvider provider, ConfigurationSection config) {
            if (config.contains("title.fadeIn")) {
                return (int) config.getDouble("title.fadeIn") * 20;
            } else {
                return null;
            }
        }
    },
    TITLE_FADE_OUT("title.fadeOut", 20) {
        @Override
        public Object fromConfig(DungeonsXL plugin, CaliburnAPI caliburn, GameRuleProvider provider, ConfigurationSection config) {
            if (config.contains("title.fadeOut")) {
                return (int) config.getDouble("title.fadeOut") * 20;
            } else {
                return null;
            }
        }
    },
    TITLE_SHOW("title.show", 60) {
        @Override
        public Object fromConfig(DungeonsXL plugin, CaliburnAPI caliburn, GameRuleProvider provider, ConfigurationSection config) {
            if (config.contains("title.show")) {
                return (int) config.getDouble("title.show") * 20;
            } else {
                return null;
            }
        }
    },
    /* Misc */
    SECURE_OBJECTS("secureObjects", new ArrayList<>()) {
        @Override
        public Object fromConfig(DungeonsXL plugin, CaliburnAPI caliburn, GameRuleProvider provider, ConfigurationSection config) {
            if (config.contains("secureObjects")) {
                return caliburn.deserializeExItemList(config, "secureObjects");
            } else {
                return null;
            }
        }
    },
    GROUP_TAG_ENABLED("groupTagEnabled", false);

    private String key;
    private Object defaultValue;

    GameRuleDefault(String key, Object defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public Object getDefaultValue() {
        return defaultValue;
    }

    @Override
    public Object fromConfig(DungeonsXL plugin, CaliburnAPI caliburn, GameRuleProvider provider, ConfigurationSection config) {
        if (config.contains(key)) {
            return config.get(key);
        } else {
            return null;
        }
    }

}
