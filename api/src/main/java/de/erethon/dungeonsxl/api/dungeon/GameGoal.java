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

import de.erethon.commons.misc.EnumUtil;
import org.bukkit.configuration.ConfigurationSection;

/**
 * A game goal defines what the players have to do in order to finish the game.
 *
 * @author Daniel Saukel
 */
public class GameGoal extends GameRuleContainer {

    /**
     * Score used for capture the flag and similar game types.
     */
    public static final GameRule<Integer> INITIAL_SCORE = new GameRule<>(Integer.class, "initialScore", 3);
    /**
     * The amount of goals to score before the game ends. -1 = not used.
     */
    public static final GameRule<Integer> SCORE_GOAL = new GameRule<>(Integer.class, "scoreGoal", -1);
    /**
     * The time left to finish the game; -1 if no timer is used.
     */
    public static final GameRule<Integer> TIME_TO_FINISH = new GameRule<>(Integer.class, "timeToFinish", -1);

    /**
     * The default game goal: {@link Type#END} without TIME_TO_FINISH
     */
    public static final GameGoal DEFAULT = new GameGoal(Type.END);

    static {
        DEFAULT.setState(TIME_TO_FINISH, TIME_TO_FINISH.getDefaultValue());
    }

    /**
     * The reader to deserialize a game goal from a configuration.
     */
    public static final ConfigReader<GameGoal> READER = (api, value) -> {
        if (!(value instanceof ConfigurationSection)) {
            return DEFAULT;
        }
        ConfigurationSection config = (ConfigurationSection) value;
        Type type = EnumUtil.getEnumIgnoreCase(Type.class, config.getString("type", "END"));
        GameGoal goal = new GameGoal(type);
        for (GameRule rule : type.getComponents()) {
            rule.fromConfig(api, goal, config);
            if (!goal.rules.containsKey(rule)) {
                goal.setState(rule, rule.getDefaultValue());
            }
        }
        return goal;
    };

    private Type type;

    public GameGoal(Type type) {
        this.type = type;
    }

    /**
     * Returns the type of the game goal.
     *
     * @return the type
     */
    public Type getType() {
        return type;
    }

    /**
     * Determines the behavior of the game goal and which settings apply to it.
     */
    public enum Type {
        /**
         * The default goal. The game ends when the end is reached.
         */
        END(TIME_TO_FINISH),
        /**
         * The game ends when a player dies and only one group is left.
         */
        LAST_MAN_STANDING,
        /**
         * SCORE_GOAL = -1: The game does not end. Instead, the goal is to survive as long as possible to beat a highscore.
         * <p>
         * SCORE_GOAL > 0: The game ends when a group reachs a specific score.
         * <p>
         * TIME_TO_FINISH != -1: The game ends after a specific time. The goal is to get the highest score until then.
         */
        SCORE(INITIAL_SCORE, SCORE_GOAL, TIME_TO_FINISH),
        /**
         * The game ends after a specific time. The goal is to survive until then.
         */
        TIME_SURVIVAL(TIME_TO_FINISH);

        private GameRule[] components;

        Type(GameRule... components) {
            this.components = components;
        }

        /**
         * Returns an array of the game rules that apply to game goals of this type.
         *
         * @return an array of the game rules that apply to game goals of this type
         */
        public GameRule[] getComponents() {
            return components;
        }

        /**
         * Returns whether the given game rule applies to game goals of this type.
         *
         * @param component the game rule
         * @return whether the given game rule applies to game goals of this type
         */
        public boolean hasComponent(GameRule component) {
            for (GameRule c : components) {
                if (c == component) {
                    return true;
                }
            }
            return false;
        }
    }

}
