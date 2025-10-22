package com.github.linghun91.dungeonsxl.requirement;

import com.github.linghun91.dungeonsxl.api.Requirement;
import org.bukkit.entity.Player;

/**
 * Permission requirement
 * @author linghun91
 */
public class PermissionRequirement implements Requirement {
    
    private final String permission;
    
    public PermissionRequirement(String permission) {
        this.permission = permission;
    }
    
    @Override
    public String getType() {
        return "PERMISSION";
    }
    
    @Override
    public boolean check(Player player) {
        return player.hasPermission(permission);
    }
    
    @Override
    public String getFailureMessage() {
        return "You don't have permission: " + permission;
    }
    
    @Override
    public void demand(Player player) {
        // No action needed
    }
}
