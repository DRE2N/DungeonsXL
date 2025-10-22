package com.github.linghun91.dungeonsxl.player;

import com.github.linghun91.dungeonsxl.api.player.*;
import org.bukkit.entity.Player;

import java.util.Optional;

public class DungeonPlayerImpl implements DungeonPlayer {
    private final Player player;
    private PlayerGroup group;
    private boolean ready = false;
    private boolean finished = false;
    private int lives = 3;
    
    public DungeonPlayerImpl(Player player) {
        this.player = player;
    }
    
    @Override
    public Player getPlayer() {
        return player;
    }
    
    @Override
    public Optional<PlayerGroup> getGroup() {
        return Optional.ofNullable(group);
    }
    
    public void setGroup(PlayerGroup group) {
        this.group = group;
    }
    
    @Override
    public boolean isReady() {
        return ready;
    }
    
    @Override
    public void setReady(boolean ready) {
        this.ready = ready;
    }
    
    @Override
    public boolean isFinished() {
        return finished;
    }
    
    @Override
    public void setFinished(boolean finished) {
        this.finished = finished;
    }
    
    @Override
    public int getLives() {
        return lives;
    }
    
    @Override
    public void setLives(int lives) {
        this.lives = lives;
    }
}
