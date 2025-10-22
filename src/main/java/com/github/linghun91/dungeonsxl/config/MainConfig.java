package com.github.linghun91.dungeonsxl.config;

import com.github.linghun91.dungeonsxl.DungeonsXL;
import org.bukkit.configuration.file.FileConfiguration;

public class MainConfig {
    private final DungeonsXL plugin;
    private FileConfiguration config;
    
    public MainConfig(DungeonsXL plugin) {
        this.plugin = plugin;
    }
    
    public void load() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
        plugin.getLogger().info("Main configuration loaded");
    }
    
    public void save() {
        plugin.saveConfig();
    }
}
