package com.github.linghun91.dungeonsxl.integration;

import com.github.linghun91.dungeonsxl.DungeonsXL;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPIIntegration extends PlaceholderExpansion {
    private final DungeonsXL plugin;
    private boolean enabled = false;
    
    public PlaceholderAPIIntegration(DungeonsXL plugin) {
        this.plugin = plugin;
    }
    
    public boolean register() {
        enabled = super.register();
        return enabled;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    @Override
    public @NotNull String getIdentifier() {
        return "dungeonsxl";
    }
    
    @Override
    public @NotNull String getAuthor() {
        return "linghun91";
    }
    
    @Override
    public @NotNull String getVersion() {
        return "2.0.0";
    }
    
    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        return null;
    }
}
