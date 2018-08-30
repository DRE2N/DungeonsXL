/*
 * Copyright (C) 2012-2018 Frank Baumann
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
package de.erethon.dungeonsxl.game.rule;

import de.erethon.dungeonsxl.game.GameType;
import static de.erethon.dungeonsxl.game.rule.GameRuleDefault.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * See {@link de.erethon.dungeonsxl.config.WorldConfig}
 *
 * @author Daniel Saukel
 */
public class GameRuleProvider {

    public static final String UNDEFINED_STATE = "UNDEFINED_STATE";

    private Map<GameRule, Object> rules = new HashMap<>();

    /**
     * Returns the state of the GameRule or UNDEFINED_STATE if it is not defined
     *
     * @param rule the rule
     * @return the state of the rule
     */
    public Object getState(GameRule rule) {
        if (!rules.containsKey(rule)) {
            return UNDEFINED_STATE;
        } else {
            return rules.get(rule);
        }
    }

    public Boolean getBooleanState(GameRule rule) {
        return (Boolean) rules.get(rule);
    }

    public Integer getIntState(GameRule rule) {
        return (Integer) rules.get(rule);
    }

    public String getStringState(GameRule rule) {
        return (String) rules.get(rule);
    }

    /**
     * Sets the state of the GameRule. Set it to UNDEFINED_STATE to remove the rule from the map sothat a subsidiary provider can set it.
     *
     * @param rule
     * @param state
     */
    public void setState(GameRule rule, Object state) {
        if (state == UNDEFINED_STATE) {
            rules.remove(rule);
        } else {
            rules.put(rule, state);
        }
    }

    /**
     * Removes the rule from the map sothat a subsidiary provider can set it.
     *
     * @param rule the GameRule to unset
     */
    public void unsetState(GameRule rule) {
        rules.remove(rule);
    }

    /**
     * @return all maps needed to be finished to play this map and a collection of maps of which at least one has to be finished
     */
    public List<String> getFinished() {
        List<String> finishedAll = (List<String>) getState(GameRuleDefault.FINISHED_ALL);
        List<String> finishedOne = (List<String>) getState(GameRuleDefault.FINISHED_ONE);
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

    /* Actions */
    /**
     * @param defaultValues the GameType that overrides the values that are null.
     */
    public void apply(GameType defaultValues) {
        if (getState(PLAYER_VERSUS_PLAYER) == UNDEFINED_STATE) {
            setState(PLAYER_VERSUS_PLAYER, defaultValues.isPlayerVersusPlayer());
        }

        if (getState(FRIENDLY_FIRE) == UNDEFINED_STATE) {
            setState(FRIENDLY_FIRE, defaultValues.isFriendlyFire());
        }

        if (getState(TIME_TO_FINISH) == UNDEFINED_STATE && defaultValues.getShowTime() != null) {
            setState(TIME_TO_FINISH, defaultValues.getShowTime() ? null : -1);
        }

        if (getState(BREAK_BLOCKS) == UNDEFINED_STATE) {
            setState(BREAK_BLOCKS, defaultValues.canBreakBlocks());
        }

        if (getState(BREAK_PLACED_BLOCKS) == UNDEFINED_STATE) {
            setState(BREAK_PLACED_BLOCKS, defaultValues.canBreakPlacedBlocks());
        }

        if (getState(PLACE_BLOCKS) == UNDEFINED_STATE) {
            setState(PLACE_BLOCKS, defaultValues.canPlaceBlocks());
        }

        if (getState(GAME_MODE) == UNDEFINED_STATE) {
            setState(GAME_MODE, defaultValues.getGameMode());
        }

        if (getState(INITIAL_LIVES) == UNDEFINED_STATE) {
            if (defaultValues.hasLives() != null) {
                setState(INITIAL_LIVES, defaultValues.hasLives() ? null : -1);
            }
        }
    }

    /**
     * @param defaultValues the GameRules that override the values that are null.
     */
    public void apply(GameRuleProvider defaultValues) {
        for (Entry<GameRule, Object> entry : defaultValues.rules.entrySet()) {
            if (rules.containsKey(entry.getKey())) {
                if (entry.getKey() == GAME_COMMAND_WHITELIST) {
                    ((Collection) getState(GAME_COMMAND_WHITELIST)).addAll((Collection) entry.getValue());
                } else if (entry.getKey() == GAME_PERMISSIONS) {
                    ((Collection) getState(GAME_PERMISSIONS)).addAll((Collection) entry.getValue());
                } else if (entry.getKey() == SECURE_OBJECTS) {
                    ((Collection) getState(SECURE_OBJECTS)).addAll((Collection) entry.getValue());
                } else if (entry.getKey() == MESSAGES) {
                    ((Map) getState(MESSAGES)).putAll((Map) entry.getValue());
                }
                continue;
            }

            if (entry.getKey() == DAMAGE_PROTECTED_ENTITIES) {
                // If nothing is specialized for protected entites yet (=> damageProtectedEntites == null and DEFAULT_VALUES are used)
                // and if blocks may be broken, it makes no sense to assume the user wants to have paintings etc. protected.
                if (defaultValues == DEFAULT_VALUES && getBooleanState(BREAK_BLOCKS)) {
                    setState(DAMAGE_PROTECTED_ENTITIES, new HashSet<>());
                } else {
                    setState(DAMAGE_PROTECTED_ENTITIES, entry.getValue());
                }

            } else if (entry.getKey() == INTERACTION_PROTECTED_ENTITIES) {
                // If nothing is specialized for protected entites yet (=> interactionProtectedEntites == null and DEFAULT_VALUES are used)
                // and if blocks may be broken, it makes no sense to assume the user wants to have paintings etc. protected.
                if (defaultValues == DEFAULT_VALUES && getBooleanState(BREAK_BLOCKS)) {
                    setState(INTERACTION_PROTECTED_ENTITIES, new HashSet<>());
                } else {
                    setState(INTERACTION_PROTECTED_ENTITIES, entry.getValue());
                }

            } else {
                rules.put(entry.getKey(), entry.getValue());
            }
        }
    }

}
