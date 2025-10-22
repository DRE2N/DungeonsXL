package com.github.linghun91.dungeonsxl.api;

import org.bukkit.entity.Player;

/**
 * Requirement interface
 * @author linghun91
 */
public interface Requirement {
    String getType();
    boolean check(Player player);
    String getFailureMessage();
    void demand(Player player);
}
