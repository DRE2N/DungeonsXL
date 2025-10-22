package com.github.linghun91.dungeonsxl.registry;

import com.github.linghun91.dungeonsxl.api.dungeon.GameRule;
import com.github.linghun91.dungeonsxl.dungeon.GameRuleImpl;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for game rules
 * @author linghun91
 */
public class GameRuleRegistry {
    
    private final Map<String, GameRule<?>> rules = new ConcurrentHashMap<>();
    
    public void registerDefaults() {
        // Player rules
        register("keepInventoryOnEnter", false, Boolean.class);
        register("keepInventoryOnEscape", false, Boolean.class);
        register("keepInventoryOnFinish", true, Boolean.class);
        register("keepInventoryOnDeath", false, Boolean.class);
        
        // Lives
        register("initialLives", 3, Integer.class);
        register("groupLives", false, Boolean.class);
        
        // Time limits
        register("timeToFinish", 0, Integer.class);
        register("timeToNextFloor", 0, Integer.class);
        
        // Difficulty
        register("mobHealthMultiplier", 1.0, Double.class);
        register("mobDamageMultiplier", 1.0, Double.class);
        
        // World rules
        register("breakBlocks", false, Boolean.class);
        register("placeBlocks", false, Boolean.class);
        register("pvp", false, Boolean.class);
        register("hunger", true, Boolean.class);
        register("naturalRegeneration", true, Boolean.class);
        
        // Death
        register("keepExpOnDeath", false, Boolean.class);
        register("keepLevelOnDeath", false, Boolean.class);
        register("respawnEnabled", true, Boolean.class);
        
        // Group rules
        register("requireAllPlayersAlive", false, Boolean.class);
        register("requireGroupSize", 1, Integer.class);
        register("maxGroupSize", 10, Integer.class);
        
        // Rewards
        register("rewardsEnabled", true, Boolean.class);
        register("rewardTime", 60, Integer.class);
        
        // Misc
        register("flyEnabled", false, Boolean.class);
        register("gameMode", "SURVIVAL", String.class);
        register("lobbyDisabled", false, Boolean.class);
        register("announcements", true, Boolean.class);

        // Mob spawning
        register("mobSpawningEnabled", true, Boolean.class);
        register("externalMobs", false, Boolean.class);
        register("mobSpawnDelay", 0, Integer.class);
        register("mobWaveInterval", 60, Integer.class);

        // Damage and combat
        register("damageMultiplier", 1.0, Double.class);
        register("playerDamageMultiplier", 1.0, Double.class);
        register("fallDamage", true, Boolean.class);
        register("fireDamage", true, Boolean.class);
        register("drowningDamage", true, Boolean.class);

        // Experience and levels
        register("expMultiplier", 1.0, Double.class);
        register("levelRequirement", 0, Integer.class);
        register("giveClassItems", true, Boolean.class);

        // Items and inventory
        register("allowDropItems", true, Boolean.class);
        register("allowPickupItems", true, Boolean.class);
        register("allowCrafting", true, Boolean.class);
        register("clearInventoryOnEnter", false, Boolean.class);

        // World interaction
        register("allowBlockBreak", false, Boolean.class);
        register("allowBlockPlace", false, Boolean.class);
        register("allowInteraction", true, Boolean.class);
        register("allowTeleportOut", false, Boolean.class);
        register("allowCommandsInDungeon", false, Boolean.class);

        // Chat and communication
        register("globalChat", true, Boolean.class);
        register("dungeonChatOnly", false, Boolean.class);
        register("showDeathMessages", true, Boolean.class);
        register("showJoinMessages", true, Boolean.class);

        // Weather and time
        register("allowWeather", false, Boolean.class);
        register("lockTime", -1, Integer.class);
        register("thunderEnabled", false, Boolean.class);

        // Economy
        register("feeEnabled", false, Boolean.class);
        register("feeAmount", 0.0, Double.class);
        register("rewardMoney", 0.0, Double.class);

        // Scoring system
        register("scoreEnabled", true, Boolean.class);
        register("killPoints", 10, Integer.class);
        register("deathPenalty", -5, Integer.class);
        register("completionBonus", 100, Integer.class);

        // Checkpoints
        register("checkpointEnabled", true, Boolean.class);
        register("autoCheckpoint", false, Boolean.class);

        // Classes and progression
        register("classesEnabled", false, Boolean.class);
        register("multiFloorProgress", true, Boolean.class);
        register("saveProgress", false, Boolean.class);

        // Triggers and waves
        register("triggersEnabled", true, Boolean.class);
        register("waveSystemEnabled", false, Boolean.class);
        register("maxWaves", 10, Integer.class);

        // Player limits and requirements
        register("minPlayerCount", 1, Integer.class);
        register("maxPlayerCount", 10, Integer.class);
        register("removeOnLogout", true, Boolean.class);
        register("inviteOnly", false, Boolean.class);

        // Difficulty settings
        register("difficulty", "NORMAL", String.class);
        register("hardcoreMode", false, Boolean.class);
        register("permadeath", false, Boolean.class);

        // Rewards timing
        register("instantRewards", false, Boolean.class);
        register("rewardChestsEnabled", true, Boolean.class);

        // Protection
        register("protectedRegionsEnabled", true, Boolean.class);
        register("spawnProtection", true, Boolean.class);
    }
    
    private <T> void register(String key, T defaultValue, Class<T> type) {
        rules.put(key, new GameRuleImpl<>(key, defaultValue, type));
    }
    
    public Optional<GameRule<?>> getRule(String key) {
        return Optional.ofNullable(rules.get(key));
    }
    
    public int getGameRuleCount() {
        return rules.size();
    }
}
