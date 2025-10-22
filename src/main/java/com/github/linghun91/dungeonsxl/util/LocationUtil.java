package com.github.linghun91.dungeonsxl.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Optional;

/**
 * Utility class for location handling
 * @author linghun91
 */
public class LocationUtil {
    
    /**
     * Serialize location to string format: world,x,y,z,yaw,pitch
     */
    public static String serialize(Location loc) {
        if (loc == null || loc.getWorld() == null) return null;
        
        return String.format("%s,%.2f,%.2f,%.2f,%.2f,%.2f",
            loc.getWorld().getName(),
            loc.getX(),
            loc.getY(),
            loc.getZ(),
            loc.getYaw(),
            loc.getPitch()
        );
    }
    
    /**
     * Deserialize location from string
     */
    public static Optional<Location> deserialize(String str) {
        if (str == null || str.isEmpty()) return Optional.empty();
        
        try {
            String[] parts = str.split(",");
            if (parts.length < 4) return Optional.empty();
            
            World world = Bukkit.getWorld(parts[0]);
            if (world == null) return Optional.empty();
            
            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);
            double z = Double.parseDouble(parts[3]);
            float yaw = parts.length > 4 ? Float.parseFloat(parts[4]) : 0f;
            float pitch = parts.length > 5 ? Float.parseFloat(parts[5]) : 0f;
            
            return Optional.of(new Location(world, x, y, z, yaw, pitch));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    /**
     * Save location to config section
     */
    public static void save(ConfigurationSection section, String path, Location loc) {
        if (loc == null || loc.getWorld() == null) return;
        
        section.set(path + ".world", loc.getWorld().getName());
        section.set(path + ".x", loc.getX());
        section.set(path + ".y", loc.getY());
        section.set(path + ".z", loc.getZ());
        section.set(path + ".yaw", loc.getYaw());
        section.set(path + ".pitch", loc.getPitch());
    }
    
    /**
     * Load location from config section
     */
    public static Optional<Location> load(ConfigurationSection section, String path) {
        if (!section.contains(path + ".world")) return Optional.empty();
        
        String worldName = section.getString(path + ".world");
        World world = Bukkit.getWorld(worldName);
        if (world == null) return Optional.empty();
        
        double x = section.getDouble(path + ".x");
        double y = section.getDouble(path + ".y");
        double z = section.getDouble(path + ".z");
        float yaw = (float) section.getDouble(path + ".yaw");
        float pitch = (float) section.getDouble(path + ".pitch");
        
        return Optional.of(new Location(world, x, y, z, yaw, pitch));
    }
    
    /**
     * Calculate distance between two locations (ignoring world)
     */
    public static double distance(Location loc1, Location loc2) {
        if (loc1 == null || loc2 == null) return Double.MAX_VALUE;
        return loc1.distance(loc2);
    }
    
    /**
     * Check if location is safe for teleport
     */
    public static boolean isSafe(Location loc) {
        if (loc == null || loc.getWorld() == null) return false;
        
        // Check if block at location is safe
        var block = loc.getBlock();
        var below = loc.clone().subtract(0, 1, 0).getBlock();
        var above = loc.clone().add(0, 1, 0).getBlock();
        
        return !block.getType().isSolid() && 
               !above.getType().isSolid() && 
               below.getType().isSolid();
    }
    
    /**
     * Get center of block location
     */
    public static Location getBlockCenter(Location loc) {
        if (loc == null) return null;
        return new Location(
            loc.getWorld(),
            loc.getBlockX() + 0.5,
            loc.getBlockY(),
            loc.getBlockZ() + 0.5,
            loc.getYaw(),
            loc.getPitch()
        );
    }
}
