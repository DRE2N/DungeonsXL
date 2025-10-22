package com.github.linghun91.dungeonsxl.api;

import com.github.linghun91.dungeonsxl.dungeon.DungeonManager;
import com.github.linghun91.dungeonsxl.mob.MobManager;
import com.github.linghun91.dungeonsxl.player.PlayerManager;
import com.github.linghun91.dungeonsxl.sign.SignManager;
import com.github.linghun91.dungeonsxl.trigger.TriggerManager;
import com.github.linghun91.dungeonsxl.world.WorldManager;

/**
 * Main API interface for DungeonsXL
 * Provides access to all core managers and systems
 *
 * @author linghun91
 */
public interface DungeonsAPI {

    /**
     * Gets the world manager
     *
     * @return World manager instance
     */
    WorldManager getWorldManager();

    /**
     * Gets the dungeon manager
     *
     * @return Dungeon manager instance
     */
    DungeonManager getDungeonManager();

    /**
     * Gets the player manager
     *
     * @return Player manager instance
     */
    PlayerManager getPlayerManager();

    /**
     * Gets the sign manager
     *
     * @return Sign manager instance
     */
    SignManager getSignManager();

    /**
     * Gets the trigger manager
     *
     * @return Trigger manager instance
     */
    TriggerManager getTriggerManager();

    /**
     * Gets the mob manager
     *
     * @return Mob manager instance
     */
    MobManager getMobManager();
}
