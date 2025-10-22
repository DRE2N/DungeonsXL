package com.github.linghun91.dungeonsxl.api.dungeon;

/**
 * Represents a game rule
 * @author linghun91
 */
public interface GameRule<T> {
    String getKey();
    T getDefaultValue();
    T getValue();
    void setValue(T value);
    Class<T> getType();
}
