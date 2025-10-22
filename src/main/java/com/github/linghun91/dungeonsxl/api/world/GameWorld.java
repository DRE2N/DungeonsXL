package com.github.linghun91.dungeonsxl.api.world;

import com.github.linghun91.dungeonsxl.api.dungeon.Game;
import com.github.linghun91.dungeonsxl.api.mob.DungeonMob;
import com.github.linghun91.dungeonsxl.api.trigger.Trigger;
import org.bukkit.entity.LivingEntity;

import java.util.Collection;
import java.util.Optional;

/**
 * Represents a game world instance
 *
 * @author linghun91
 */
public interface GameWorld extends InstanceWorld {

    Game getGame();
    Collection<DungeonMob> getMobs();
    Optional<DungeonMob> getMob(LivingEntity entity);
    void addMob(DungeonMob mob);
    void removeMob(DungeonMob mob);
    Collection<Trigger> getTriggers();
    void addTrigger(Trigger trigger);
    void removeTrigger(Trigger trigger);
    boolean isGameStarted();
    void startGame();
    int getWaveCount();
    void nextWave();
    int getKillCount();
    void incrementKills();
}
