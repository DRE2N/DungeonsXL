package com.github.linghun91.dungeonsxl.api.dungeon;

import java.util.Optional;

/**
 * Container for game rules
 * @author linghun91
 */
public interface GameRuleContainer {
    <T> Optional<GameRule<T>> getRule(String key);
    <T> T getValue(String key, T defaultValue);
    <T> void setValue(String key, T value);
}
