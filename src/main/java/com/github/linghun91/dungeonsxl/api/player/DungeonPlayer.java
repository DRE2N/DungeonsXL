package com.github.linghun91.dungeonsxl.api.player;

import org.bukkit.entity.Player;
import java.util.Optional;

/**
 * Dungeon player wrapper
 * @author linghun91
 */
public interface DungeonPlayer {
    Player getPlayer();
    Optional<PlayerGroup> getGroup();
    boolean isReady();
    void setReady(boolean ready);
    boolean isFinished();
    void setFinished(boolean finished);
    int getLives();
    void setLives(int lives);
}
