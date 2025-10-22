package com.github.linghun91.dungeonsxl.world;

import com.github.linghun91.dungeonsxl.DungeonsXL;
import com.github.linghun91.dungeonsxl.api.dungeon.Game;
import com.github.linghun91.dungeonsxl.api.mob.DungeonMob;
import com.github.linghun91.dungeonsxl.api.trigger.Trigger;
import com.github.linghun91.dungeonsxl.api.world.*;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GameWorldImpl implements GameWorld {
    private final DungeonsXL plugin;
    private final ResourceWorld resourceWorld;
    private final int id;
    private World world;
    private final List<DungeonMob> mobs = new ArrayList<>();
    private final List<Trigger> triggers = new ArrayList<>();
    private int waveCount = 0;
    private int killCount = 0;
    private boolean gameStarted = false;
    
    public GameWorldImpl(DungeonsXL plugin, ResourceWorld resourceWorld) {
        this.plugin = plugin;
        this.resourceWorld = resourceWorld;
        this.id = plugin.getWorldManager().getNextInstanceId();
        loadWorld();
    }
    
    private void loadWorld() {
        String worldName = "DXL_Game_" + id;
        world = new WorldCreator(worldName).createWorld();
        plugin.getWorldManager().registerInstance(this);
    }
    
    @Override
    public Game getGame() {
        return null;
    }
    
    @Override
    public Collection<DungeonMob> getMobs() {
        return new ArrayList<>(mobs);
    }
    
    @Override
    public Optional<DungeonMob> getMob(LivingEntity entity) {
        return mobs.stream()
            .filter(mob -> mob.getEntity().equals(entity))
            .findFirst();
    }
    
    @Override
    public void addMob(DungeonMob mob) {
        mobs.add(mob);
    }
    
    @Override
    public void removeMob(DungeonMob mob) {
        mobs.remove(mob);
    }
    
    @Override
    public Collection<Trigger> getTriggers() {
        return new ArrayList<>(triggers);
    }
    
    @Override
    public void addTrigger(Trigger trigger) {
        triggers.add(trigger);
    }
    
    @Override
    public void removeTrigger(Trigger trigger) {
        triggers.remove(trigger);
    }
    
    @Override
    public boolean isGameStarted() {
        return gameStarted;
    }
    
    @Override
    public void startGame() {
        gameStarted = true;
    }
    
    @Override
    public int getWaveCount() {
        return waveCount;
    }
    
    @Override
    public void nextWave() {
        waveCount++;
    }
    
    @Override
    public int getKillCount() {
        return killCount;
    }
    
    @Override
    public void incrementKills() {
        killCount++;
    }
    
    @Override
    public int getId() {
        return id;
    }
    
    @Override
    public ResourceWorld getResourceWorld() {
        return resourceWorld;
    }
    
    @Override
    public World getWorld() {
        return world;
    }
    
    @Override
    public Map<Block, Sign> getDungeonSigns() {
        return new HashMap<>();
    }
    
    @Override
    public Collection<Player> getPlayers() {
        return world.getPlayers();
    }
    
    @Override
    public boolean contains(Player player) {
        return player.getWorld().equals(world);
    }
    
    @Override
    public void unload() {
        plugin.getServer().unloadWorld(world, false);
        plugin.getWorldManager().unregisterInstance(this);
    }
    
    @Override
    public boolean isLoaded() {
        return world != null;
    }
    
    @Override
    public InstanceType getType() {
        return InstanceType.GAME;
    }
}
