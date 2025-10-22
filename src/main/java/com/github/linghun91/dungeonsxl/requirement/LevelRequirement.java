package com.github.linghun91.dungeonsxl.requirement;

import com.github.linghun91.dungeonsxl.api.Requirement;
import org.bukkit.entity.Player;

/**
 * Requirement for minimum player level
 */
public class LevelRequirement implements Requirement {
    
    private final int minLevel;
    
    public LevelRequirement(int minLevel) {
        this.minLevel = minLevel;
    }
    
    @Override
    public boolean check(Player player) {
        return player.getLevel() >= minLevel;
    }
    
    @Override
    public String getType() {
        return "level";
    }
    
    @Override
    public String getErrorMessage() {
        return "You need to be at least level " + minLevel + " to enter!";
    }
}
