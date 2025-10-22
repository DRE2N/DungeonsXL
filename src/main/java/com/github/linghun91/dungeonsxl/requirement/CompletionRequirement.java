package com.github.linghun91.dungeonsxl.requirement;

import com.github.linghun91.dungeonsxl.DungeonsXL;
import com.github.linghun91.dungeonsxl.api.Requirement;
import org.bukkit.entity.Player;

/**
 * Requirement for completing previous dungeons
 */
public class CompletionRequirement implements Requirement {
    
    private final DungeonsXL plugin;
    private final String dungeonName;
    
    public CompletionRequirement(DungeonsXL plugin, String dungeonName) {
        this.plugin = plugin;
        this.dungeonName = dungeonName;
    }
    
    @Override
    public boolean check(Player player) {
        // Check if player has completed the required dungeon
        return plugin.getPlayerManager().hasCompleted(player, dungeonName);
    }
    
    @Override
    public String getType() {
        return "completion";
    }
    
    @Override
    public String getErrorMessage() {
        return "You must complete '" + dungeonName + "' first!";
    }
}
