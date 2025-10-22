package com.github.linghun91.dungeonsxl.listener;

import com.github.linghun91.dungeonsxl.DungeonsXL;
import com.github.linghun91.dungeonsxl.api.world.InstanceWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import java.util.Optional;

/**
 * Listener for world events
 */
public class WorldListener implements Listener {
    
    private final DungeonsXL plugin;
    
    public WorldListener(DungeonsXL plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onWorldLoad(WorldLoadEvent event) {
        // Check if this is a dungeon world
        String worldName = event.getWorld().getName();
        if (worldName.startsWith("DXL_")) {
            plugin.getLogger().info("Dungeon world loaded: " + worldName);
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onWorldUnload(WorldUnloadEvent event) {
        // Prevent unloading worlds with active dungeons
        Optional<InstanceWorld> instance = plugin.getWorldManager().getInstance(event.getWorld());
        if (instance.isPresent()) {
            event.setCancelled(true);
            plugin.getLogger().warning("Prevented unloading active dungeon world: " + event.getWorld().getName());
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onWeatherChange(WeatherChangeEvent event) {
        // Control weather in dungeon worlds
        Optional<InstanceWorld> instance = plugin.getWorldManager().getInstance(event.getWorld());
        if (instance.isPresent()) {
            // Check game rule for weather
            if (instance.get() instanceof com.github.linghun91.dungeonsxl.api.world.GameWorld gameWorld) {
                Boolean allowWeather = gameWorld.getGame().getDungeon().getGameRule("allowWeather", Boolean.class);
                if (allowWeather != null && !allowWeather) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
