package com.github.linghun91.dungeonsxl.api.sign;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;

/**
 * Dungeon sign interface
 * @author linghun91
 */
public interface DungeonSign {
    String getType();
    Block getBlock();
    Sign getSign();
    boolean setup();
    void trigger();
    void remove();
}
