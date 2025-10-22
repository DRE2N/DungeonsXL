package com.github.linghun91.dungeonsxl.requirement;

import com.github.linghun91.dungeonsxl.DungeonsXL;
import com.github.linghun91.dungeonsxl.api.Requirement;
import com.github.linghun91.dungeonsxl.api.player.DungeonPlayer;
import org.bukkit.entity.Player;

/**
 * Requirement for group size
 */
public class GroupSizeRequirement implements Requirement {
    
    private final DungeonsXL plugin;
    private final int minSize;
    private final int maxSize;
    
    public GroupSizeRequirement(DungeonsXL plugin, int minSize, int maxSize) {
        this.plugin = plugin;
        this.minSize = minSize;
        this.maxSize = maxSize;
    }
    
    @Override
    public boolean check(Player player) {
        DungeonPlayer dPlayer = plugin.getPlayerManager().getDungeonPlayer(player);
        if (dPlayer == null || dPlayer.getGroup() == null) {
            return minSize <= 1; // Solo player
        }
        
        int groupSize = dPlayer.getGroup().getMembers().size();
        return groupSize >= minSize && groupSize <= maxSize;
    }
    
    @Override
    public String getType() {
        return "groupSize";
    }
    
    @Override
    public String getErrorMessage() {
        return "Your group size must be between " + minSize + " and " + maxSize + "!";
    }
}
