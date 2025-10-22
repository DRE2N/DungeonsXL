package com.github.linghun91.dungeonsxl.api.world;

import com.github.linghun91.dungeonsxl.api.dungeon.Game;
import org.bukkit.Location;

/**
 * Represents an active game world instance
 */
public interface GameWorld extends InstanceWorld {
    
    /**
     * Get the game associated with this world
     */
    Game getGame();
    
    /**
     * Get the start location for players
     */
    Location getStartLocation();
    
    /**
     * Set the start location
     */
    void setStartLocation(Location location);
    
    /**
     * Add a protected region
     */
    void addProtectedRegion(Location corner1, Location corner2);
    
    /**
     * Set the floor number
     */
    void setFloorNumber(int floor);
    
    /**
     * Get the floor number
     */
    int getFloorNumber();
    
    /**
     * Check if location is in a protected region
     */
    boolean isProtected(Location location);
}
