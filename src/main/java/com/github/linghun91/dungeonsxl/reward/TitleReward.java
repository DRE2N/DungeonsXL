package com.github.linghun91.dungeonsxl.reward;

import com.github.linghun91.dungeonsxl.DungeonsXL;
import com.github.linghun91.dungeonsxl.api.Reward;
import org.bukkit.entity.Player;

/**
 * Reward that grants a player title
 */
public class TitleReward implements Reward {
    
    private final DungeonsXL plugin;
    private final String title;
    
    public TitleReward(DungeonsXL plugin, String title) {
        this.plugin = plugin;
        this.title = title;
    }
    
    @Override
    public void give(Player player) {
        // Store title in player data
        plugin.getPlayerManager().setPlayerTitle(player, title);
        plugin.getLogger().info("Granted title '" + title + "' to " + player.getName());
    }
    
    @Override
    public String getType() {
        return "title";
    }
}
