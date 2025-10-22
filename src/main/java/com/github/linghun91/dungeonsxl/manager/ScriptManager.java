package com.github.linghun91.dungeonsxl.manager;

import com.github.linghun91.dungeonsxl.DungeonsXL;
import com.github.linghun91.dungeonsxl.api.dungeon.Game;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Manager for custom scripts in dungeons
 */
public class ScriptManager {
    
    private final DungeonsXL plugin;
    private final Map<String, String> scripts;
    private final File scriptsFolder;
    
    public ScriptManager(DungeonsXL plugin) {
        this.plugin = plugin;
        this.scripts = new HashMap<>();
        this.scriptsFolder = new File(plugin.getDataFolder(), "scripts");
        
        if (!scriptsFolder.exists()) {
            scriptsFolder.mkdirs();
        }
    }
    
    /**
     * Load all scripts from the scripts folder
     */
    public void loadScripts() {
        scripts.clear();
        
        File[] files = scriptsFolder.listFiles((dir, name) -> name.endsWith(".txt") || name.endsWith(".script"));
        if (files == null) return;
        
        for (File file : files) {
            try {
                String scriptName = file.getName().replace(".txt", "").replace(".script", "");
                String content = java.nio.file.Files.readString(file.toPath());
                scripts.put(scriptName, content);
                plugin.getLogger().info("Loaded script: " + scriptName);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load script: " + file.getName());
            }
        }
    }
    
    /**
     * Execute a script
     */
    public void executeScript(String scriptName, Game game) {
        String script = scripts.get(scriptName);
        if (script == null) {
            plugin.getLogger().warning("Script not found: " + scriptName);
            return;
        }
        
        // Parse and execute script commands
        String[] lines = script.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            
            // Execute command (simplified implementation)
            if (line.startsWith("/")) {
                String command = line.substring(1);
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command);
            }
        }
    }
    
    public Map<String, String> getScripts() {
        return new HashMap<>(scripts);
    }
}
