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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * A container for {@link GameRule}s.
 *
 * @author Daniel Saukel
 */
public class GameRuleContainer {

    /**
     * A container of all rules with their default value. This is used internally as the most subsidiary container that fills missing rules if they are not set.
     */
    public static final GameRuleContainer DEFAULT_VALUES = new GameRuleContainer();

    static {
        for (GameRule rule : GameRule.VALUES) {
            DEFAULT_VALUES.setState(rule, rule.getDefaultValue());
        }
    }

    /**
     * Initializes an emtpy GameRuleContainer.
     */
    public GameRuleContainer() {
    }

    /**
     * Copies a GameRuleContainer.
     *
     * @param container the container to copy
     */
    public GameRuleContainer(GameRuleContainer container) {
        rules = new HashMap<>(container.rules);
    }

    private Map<GameRule<?>, Object> rules = new HashMap<>();

    /**
     * Returns the state of the GameRule or UNDEFINED_STATE if it is not defined
     *
     * @param <V>  the type of the value of the rule
     * @param rule the rule
     * @return the state of the rule
     */
    public <V> V getState(GameRule<V> rule) {
        if (!rules.containsKey(rule)) {
            return null;
        } else {
            return (V) rules.get(rule);
        }
    }

    /**
     * Sets the state of the GameRule.Set it to null to remove the rule from the map sothat a subsidiary provider can set it.
     *
     * @param <V>   the type of the value of the rule
     * @param rule  the rule
     * @param state the new state of the rule in this container
     */
    public <V> void setState(GameRule<V> rule, V state) {
        if (state == null) {
            rules.remove(rule);
        } else if (rule.isValidValue(state)) {
            rules.put(rule, state);
        } else {
            throw new IllegalArgumentException("state is not a valid value for rule " + rule.getKey());
        }
    }

    /**
     * Removes the rule from the map sothat a subsidiary provider can set it.
     *
     * @param rule the GameRule to unset
     */
    public void unsetState(GameRule<?> rule) {
        rules.remove(rule);
    }

    /**
     * Fills the values that are not yet set with values from a subsidiary container.
     *
     * @param subsidiary the GameRules that override the values that are null.
     */
    public void merge(GameRuleContainer subsidiary) {
        rules.entrySet().forEach(e -> e.getKey().merge(this, subsidiary, this));

        // If we are using the last subsidiary rules (the default rules) and if blocks may be broken...
        if (subsidiary != DEFAULT_VALUES || !getState(GameRule.BREAK_BLOCKS)) {
            return;
        }
        // ...then it makes no sense to set *ProtectedEntities to default where several block-like entities (like paintings) are protected.
        if (getState(GameRule.DAMAGE_PROTECTED_ENTITIES) == DEFAULT_VALUES.getState(GameRule.DAMAGE_PROTECTED_ENTITIES)) {
            setState(GameRule.DAMAGE_PROTECTED_ENTITIES, new HashSet<>());
        }
        if (getState(GameRule.INTERACTION_PROTECTED_ENTITIES) == DEFAULT_VALUES.getState(GameRule.INTERACTION_PROTECTED_ENTITIES)) {
            setState(GameRule.INTERACTION_PROTECTED_ENTITIES, new HashSet<>());
        }
    }

}
