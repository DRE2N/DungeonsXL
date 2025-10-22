package com.github.linghun91.dungeonsxl.config;

import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;

public class WorldConfig {
    private final File file;
    private YamlConfiguration config;
    
    public WorldConfig(File worldFolder) {
        this.file = new File(worldFolder, "config.yml");
        if (file.exists()) {
            config = YamlConfiguration.loadConfiguration(file);
        } else {
            config = new YamlConfiguration();
        }
    }
    
    public YamlConfiguration getConfig() {
        return config;
    }
}
