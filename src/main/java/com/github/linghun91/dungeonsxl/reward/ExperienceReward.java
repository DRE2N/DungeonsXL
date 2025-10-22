package com.github.linghun91.dungeonsxl.reward;

import com.github.linghun91.dungeonsxl.api.Reward;
import org.bukkit.entity.Player;

/**
 * Reward that gives experience points
 */
public class ExperienceReward implements Reward {
    
    private final int expAmount;
    private final boolean levels; // true = levels, false = exp points
    
    public ExperienceReward(int expAmount, boolean levels) {
        this.expAmount = expAmount;
        this.levels = levels;
    }
    
    @Override
    public void give(Player player) {
        if (levels) {
            player.setLevel(player.getLevel() + expAmount);
        } else {
            player.giveExp(expAmount);
        }
    }
    
    @Override
    public String getType() {
        return "experience";
    }
}
