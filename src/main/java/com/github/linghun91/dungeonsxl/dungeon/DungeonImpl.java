package com.github.linghun91.dungeonsxl.dungeon;

import com.github.linghun91.dungeonsxl.DungeonsXL;
import com.github.linghun91.dungeonsxl.api.dungeon.Dungeon;
import com.github.linghun91.dungeonsxl.api.dungeon.GameRule;
import com.github.linghun91.dungeonsxl.api.world.ResourceWorld;
import com.github.linghun91.dungeonsxl.api.Requirement;
import com.github.linghun91.dungeonsxl.api.Reward;

import java.util.*;

/**
 * Implementation of Dungeon
 */
public class DungeonImpl implements Dungeon {
    
    private final DungeonsXL plugin;
    private final String name;
    private final List<String> floors; // Resource world names
    private final Map<String, Object> gameRules;
    private final List<Requirement> requirements;
    private final List<Reward> rewards;
    private boolean isMultiFloor;
    private int maxPlayersPerGroup;
    private int minPlayers;
    private int maxGroups;
    
    public DungeonImpl(DungeonsXL plugin, String name) {
        this.plugin = plugin;
        this.name = name;
        this.floors = new ArrayList<>();
        this.gameRules = new HashMap<>();
        this.requirements = new ArrayList<>();
        this.rewards = new ArrayList<>();
        this.isMultiFloor = false;
        this.maxPlayersPerGroup = 4;
        this.minPlayers = 1;
        this.maxGroups = 1;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public List<String> getFloors() {
        return new ArrayList<>(floors);
    }
    
    @Override
    public void addFloor(String resourceWorldName) {
        floors.add(resourceWorldName);
        if (floors.size() > 1) {
            isMultiFloor = true;
        }
    }
    
    @Override
    public String getStartFloor() {
        return floors.isEmpty() ? null : floors.get(0);
    }
    
    @Override
    public String getFloor(int index) {
        if (index >= 0 && index < floors.size()) {
            return floors.get(index);
        }
        return null;
    }
    
    @Override
    public int getFloorCount() {
        return floors.size();
    }
    
    @Override
    public boolean isMultiFloor() {
        return isMultiFloor;
    }
    
    @Override
    public Map<String, Object> getGameRules() {
        return new HashMap<>(gameRules);
    }
    
    @Override
    public <T> T getGameRule(String key, Class<T> type) {
        Object value = gameRules.get(key);
        if (value != null && type.isInstance(value)) {
            return type.cast(value);
        }
        return null;
    }
    
    @Override
    public void setGameRule(String key, Object value) {
        gameRules.put(key, value);
    }
    
    @Override
    public List<Requirement> getRequirements() {
        return new ArrayList<>(requirements);
    }
    
    @Override
    public void addRequirement(Requirement requirement) {
        requirements.add(requirement);
    }
    
    @Override
    public List<Reward> getRewards() {
        return new ArrayList<>(rewards);
    }
    
    @Override
    public void addReward(Reward reward) {
        rewards.add(reward);
    }
    
    @Override
    public int getMaxPlayersPerGroup() {
        return maxPlayersPerGroup;
    }
    
    @Override
    public void setMaxPlayersPerGroup(int max) {
        this.maxPlayersPerGroup = max;
    }
    
    @Override
    public int getMinPlayers() {
        return minPlayers;
    }
    
    @Override
    public void setMinPlayers(int min) {
        this.minPlayers = min;
    }
    
    @Override
    public int getMaxGroups() {
        return maxGroups;
    }
    
    @Override
    public void setMaxGroups(int max) {
        this.maxGroups = max;
    }
    
    @Override
    public boolean canPlayerJoin(org.bukkit.entity.Player player) {
        // Check all requirements
        for (Requirement req : requirements) {
            if (!req.check(player)) {
                return false;
            }
        }
        return true;
    }
}
