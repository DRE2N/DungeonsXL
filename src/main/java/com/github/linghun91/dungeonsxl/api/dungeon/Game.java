package com.github.linghun91.dungeonsxl.api.dungeon;

import com.github.linghun91.dungeonsxl.api.player.PlayerGroup;
import com.github.linghun91.dungeonsxl.api.world.GameWorld;
import java.util.Collection;

/**
 * Represents an active game session
 * @author linghun91
 */
public interface Game {
    Dungeon getDungeon();
    GameWorld getWorld();
    Collection<PlayerGroup> getGroups();
    boolean isStarted();
    void start();
    int getFloorCount();
    int getWaveCount();
}
