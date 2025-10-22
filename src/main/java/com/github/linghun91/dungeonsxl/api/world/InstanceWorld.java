package com.github.linghun91.dungeonsxl.api.world;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;

/**
 * Represents an instance world - a runtime copy of a resource world
 * Instance worlds are temporary and are cleaned up when no longer needed
 *
 * @author linghun91
 */
public interface InstanceWorld {

    int getId();
    ResourceWorld getResourceWorld();
    World getWorld();
    Map<Block, Sign> getDungeonSigns();
    Collection<Player> getPlayers();
    boolean contains(Player player);
    void unload();
    boolean isLoaded();
    InstanceType getType();

    enum InstanceType {
        GAME, EDIT
    }
}
