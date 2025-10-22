package com.github.linghun91.dungeonsxl.dungeon;

import com.github.linghun91.dungeonsxl.api.dungeon.GameRule;

/**
 * Game rule implementation
 * @author linghun91
 */
public class GameRuleImpl<T> implements GameRule<T> {
    
    private final String key;
    private final T defaultValue;
    private T value;
    private final Class<T> type;
    
    public GameRuleImpl(String key, T defaultValue, Class<T> type) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.type = type;
    }
    
    @Override
    public String getKey() {
        return key;
    }
    
    @Override
    public T getDefaultValue() {
        return defaultValue;
    }
    
    @Override
    public T getValue() {
        return value;
    }
    
    @Override
    public void setValue(T value) {
        this.value = value;
    }
    
    @Override
    public Class<T> getType() {
        return type;
    }
}
