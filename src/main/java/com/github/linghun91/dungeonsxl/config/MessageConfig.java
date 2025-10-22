package com.github.linghun91.dungeonsxl.config;

import com.github.linghun91.dungeonsxl.DungeonsXL;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class MessageConfig {
    private final DungeonsXL plugin;
    private YamlConfiguration messages;
    
    public MessageConfig(DungeonsXL plugin) {
        this.plugin = plugin;
    }
    
    public void load() {
        File file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(file);
        plugin.getLogger().info("Message configuration loaded");
    }
    
    public String getMessage(String key) {
        return messages.getString(key, key);
    }
}
