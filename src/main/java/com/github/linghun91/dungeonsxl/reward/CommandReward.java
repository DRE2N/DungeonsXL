package com.github.linghun91.dungeonsxl.reward;

import com.github.linghun91.dungeonsxl.DungeonsXL;
import com.github.linghun91.dungeonsxl.api.Reward;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Reward that executes commands
 */
public class CommandReward implements Reward {
    
    private final DungeonsXL plugin;
    private final String command;
    private final boolean asConsole;
    
    public CommandReward(DungeonsXL plugin, String command, boolean asConsole) {
        this.plugin = plugin;
        this.command = command;
        this.asConsole = asConsole;
    }
    
    @Override
    public void give(Player player) {
        String parsedCommand = command.replace("%player%", player.getName());
        
        if (asConsole) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsedCommand);
        } else {
            player.performCommand(parsedCommand);
        }
    }
    
    @Override
    public String getType() {
        return "command";
    }
}
