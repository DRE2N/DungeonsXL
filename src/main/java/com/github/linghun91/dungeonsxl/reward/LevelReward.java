package com.github.linghun91.dungeonsxl.reward;

import com.github.linghun91.dungeonsxl.api.Reward;
import org.bukkit.entity.Player;

/**
 * Experience level reward
 * @author linghun91
 */
public class LevelReward implements Reward {
    
    private final int levels;
    
    public LevelReward(int levels) {
        this.levels = levels;
    }
    
    @Override
    public String getType() {
        return "LEVEL";
    }
    
    @Override
    public void give(Player player) {
        player.giveExpLevels(levels);
        player.sendMessage("You received " + levels + " experience levels!");
    }
}
