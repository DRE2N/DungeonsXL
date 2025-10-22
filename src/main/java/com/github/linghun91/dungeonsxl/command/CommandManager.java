package com.github.linghun91.dungeonsxl.command;

import com.github.linghun91.dungeonsxl.DungeonsXL;

public class CommandManager {
    private final DungeonsXL plugin;
    
    public CommandManager(DungeonsXL plugin) {
        this.plugin = plugin;
    }
    
    public void registerAllCommands() {
        plugin.getLogger().info("Commands registered");
    }
}
