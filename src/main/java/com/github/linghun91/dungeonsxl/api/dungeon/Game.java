package com.github.linghun91.dungeonsxl.api.dungeon;

import com.github.linghun91.dungeonsxl.api.player.DungeonPlayer;
import com.github.linghun91.dungeonsxl.api.player.PlayerGroup;
import com.github.linghun91.dungeonsxl.api.world.GameWorld;
import com.github.linghun91.dungeonsxl.manager.TriggerManager;

import java.util.Set;
import java.util.UUID;

/**
 * Represents an active dungeon game session
 */
public interface Game {
    
    enum GamePhase {
        LOBBY,
        GAME,
        END
    }
    
    UUID getId();
    
    Dungeon getDungeon();
    
    GameWorld getWorld();
    
    Set<PlayerGroup> getGroups();
    
    void addGroup(PlayerGroup group);
    
    void removeGroup(PlayerGroup group);
    
    Set<DungeonPlayer> getPlayers();
    
    void addPlayer(DungeonPlayer player);
    
    void removePlayer(DungeonPlayer player);
    
    GamePhase getPhase();
    
    void setPhase(GamePhase phase);
    
    void start();
    
    void end(boolean success);
    
    boolean isStarted();
    
    long getStartTime();
    
    int getCurrentFloor();
    
    void nextFloor();
    
    void startWave(int waveNumber);
    
    int getCurrentWave();
    
    TriggerManager getTriggerManager();
    
    void sendMessage(String message);
}
