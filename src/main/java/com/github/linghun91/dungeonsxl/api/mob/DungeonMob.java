package com.github.linghun91.dungeonsxl.api.mob;

import org.bukkit.entity.LivingEntity;
import java.util.Optional;

/**
 * Dungeon mob
 * @author linghun91
 */
public interface DungeonMob {
    LivingEntity getEntity();
    Optional<String> getTriggerId();
    boolean isDead();
}
