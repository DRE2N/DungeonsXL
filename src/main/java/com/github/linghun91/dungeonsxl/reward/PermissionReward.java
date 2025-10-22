package com.github.linghun91.dungeonsxl.reward;

import com.github.linghun91.dungeonsxl.DungeonsXL;
import com.github.linghun91.dungeonsxl.api.Reward;
import org.bukkit.entity.Player;

/**
 * Reward that grants permissions (requires permission plugin)
 */
public class PermissionReward implements Reward {
    
    private final DungeonsXL plugin;
    private final String permission;
    private final boolean temporary;
    private final long duration; // in seconds
    
    public PermissionReward(DungeonsXL plugin, String permission, boolean temporary, long duration) {
        this.plugin = plugin;
        this.permission = permission;
        this.temporary = temporary;
        this.duration = duration;
    }
    
    @Override
    public void give(Player player) {
        // This would integrate with a permission plugin like LuckPerms
        // For now, just log it
        plugin.getLogger().info("Granting permission " + permission + " to " + player.getName());
        
        if (temporary) {
            plugin.getLogger().info("Permission will expire in " + duration + " seconds");
        }
    }
    
    @Override
    public String getType() {
        return "permission";
    }
}
