package com.github.linghun91.dungeonsxl.listener;

import com.github.linghun91.dungeonsxl.DungeonsXL;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {
    private final DungeonsXL plugin;
    
    public PlayerListener(DungeonsXL plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Handle player join
    }
}
