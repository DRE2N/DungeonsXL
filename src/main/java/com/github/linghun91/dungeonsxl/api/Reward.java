package com.github.linghun91.dungeonsxl.api;

import org.bukkit.entity.Player;

/**
 * Reward interface
 * @author linghun91
 */
public interface Reward {
    String getType();
    void give(Player player);
}
