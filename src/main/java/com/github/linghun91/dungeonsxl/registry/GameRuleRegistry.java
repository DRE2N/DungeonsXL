package com.github.linghun91.dungeonsxl.registry;

import com.github.linghun91.dungeonsxl.api.dungeon.GameRule;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GameRuleRegistry {
    private final Map<String, GameRule<?>> rules = new ConcurrentHashMap<>();
    
    public void registerDefaults() {
        // Register default game rules
    }
    
    public int getGameRuleCount() {
        return rules.size();
    }
}
