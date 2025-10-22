package com.github.linghun91.dungeonsxl.config;

import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;

public class DungeonConfig {
    private final File file;
    private YamlConfiguration config;
    
    public DungeonConfig(File file) {
        this.file = file;
        if (file.exists()) {
            config = YamlConfiguration.loadConfiguration(file);
        } else {
            config = new YamlConfiguration();
        }
    }
}
