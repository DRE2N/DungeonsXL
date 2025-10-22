package com.github.linghun91.dungeonsxl.dungeon;

import com.github.linghun91.dungeonsxl.DungeonsXL;
import com.github.linghun91.dungeonsxl.api.dungeon.Dungeon;
import com.github.linghun91.dungeonsxl.api.dungeon.Game;
import com.github.linghun91.dungeonsxl.api.player.DungeonPlayer;
import com.github.linghun91.dungeonsxl.api.player.PlayerGroup;
import com.github.linghun91.dungeonsxl.api.world.GameWorld;
import com.github.linghun91.dungeonsxl.manager.TriggerManager;
import com.github.linghun91.dungeonsxl.util.MessageUtil;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Implementation of Game
 */
public class GameImpl implements Game {
    
    private final DungeonsXL plugin;
    private final UUID gameId;
    private final Dungeon dungeon;
    private final GameWorld world;
    private final Set<PlayerGroup> groups;
    private final Set<DungeonPlayer> players;
    private final TriggerManager triggerManager;
    
    private GamePhase phase;
    private int currentFloor;
    private int currentWave;
    private long startTime;
    private boolean started;
    
    public GameImpl(DungeonsXL plugin, Dungeon dungeon, GameWorld world) {
        this.plugin = plugin;
        this.gameId = UUID.randomUUID();
        this.dungeon = dungeon;
        this.world = world;
        this.groups = new HashSet<>();
        this.players = new HashSet<>();
        this.triggerManager = new TriggerManager(plugin, this);
        this.phase = GamePhase.LOBBY;
        this.currentFloor = 0;
        this.currentWave = 0;
        this.started = false;
    }
    
    @Override
    public UUID getId() {
        return gameId;
    }
    
    @Override
    public Dungeon getDungeon() {
        return dungeon;
    }
    
    @Override
    public GameWorld getWorld() {
        return world;
    }
    
    @Override
    public Set<PlayerGroup> getGroups() {
        return new HashSet<>(groups);
    }
    
    @Override
    public void addGroup(PlayerGroup group) {
        groups.add(group);
        group.setGame(this);
        players.addAll(group.getMembers());
    }
    
    @Override
    public void removeGroup(PlayerGroup group) {
        groups.remove(group);
        players.removeAll(group.getMembers());
        group.setGame(null);
    }
    
    @Override
    public Set<DungeonPlayer> getPlayers() {
        return new HashSet<>(players);
    }
    
    @Override
    public void addPlayer(DungeonPlayer player) {
        players.add(player);
    }
    
    @Override
    public void removePlayer(DungeonPlayer player) {
        players.remove(player);
    }
    
    @Override
    public GamePhase getPhase() {
        return phase;
    }
    
    @Override
    public void setPhase(GamePhase phase) {
        this.phase = phase;
    }
    
    @Override
    public void start() {
        if (started) return;
        
        this.phase = GamePhase.GAME;
        this.started = true;
        this.startTime = System.currentTimeMillis();
        
        // Apply game rules
        applyGameRules();
        
        // Broadcast start message
        sendMessage("&aThe dungeon has started!");
        
        // Initialize triggers
        triggerManager.initializeTriggers();
    }
    
    @Override
    public void end(boolean success) {
        this.phase = GamePhase.END;
        
        if (success) {
            sendMessage("&aCongratulations! You completed the dungeon!");
            giveRewards();
        } else {
            sendMessage("&cGame Over! The dungeon has ended.");
        }
        
        // Teleport players out after delay
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            for (DungeonPlayer player : players) {
                player.leave();
            }
        }, 100L); // 5 seconds
    }
    
    @Override
    public boolean isStarted() {
        return started;
    }
    
    @Override
    public long getStartTime() {
        return startTime;
    }
    
    @Override
    public int getCurrentFloor() {
        return currentFloor;
    }
    
    @Override
    public void nextFloor() {
        if (currentFloor < dungeon.getFloorCount() - 1) {
            currentFloor++;
            sendMessage("&eAdvancing to floor " + (currentFloor + 1) + "...");
            // Load next floor
            // This would involve creating a new GameWorld instance for the next floor
        } else {
            // Last floor completed
            end(true);
        }
    }
    
    @Override
    public void startWave(int waveNumber) {
        this.currentWave = waveNumber;
        sendMessage("&6Wave " + waveNumber + " started!");
        triggerManager.activateWave(waveNumber);
    }
    
    @Override
    public int getCurrentWave() {
        return currentWave;
    }
    
    @Override
    public TriggerManager getTriggerManager() {
        return triggerManager;
    }
    
    @Override
    public void sendMessage(String message) {
        players.forEach(player -> {
            if (player.getPlayer() != null && player.getPlayer().isOnline()) {
                MessageUtil.send(player.getPlayer(), message);
            }
        });
    }
    
    private void applyGameRules() {
        // Apply game rules to players and world
        Map<String, Object> rules = dungeon.getGameRules();
        
        Boolean keepInventory = (Boolean) rules.get("keepInventoryOnEnter");
        if (keepInventory != null && !keepInventory) {
            players.forEach(player -> player.getPlayer().getInventory().clear());
        }
        
        Integer lives = (Integer) rules.get("initialLives");
        if (lives != null) {
            groups.forEach(group -> group.setLives(lives));
        }
        
        // Apply more game rules as needed
    }
    
    private void giveRewards() {
        dungeon.getRewards().forEach(reward -> {
            players.forEach(player -> {
                if (player.getPlayer() != null && player.getPlayer().isOnline()) {
                    reward.give(player.getPlayer());
                }
            });
        });
    }
}
