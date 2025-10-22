package com.github.linghun91.dungeonsxl.world;

import com.github.linghun91.dungeonsxl.DungeonsXL;
import com.github.linghun91.dungeonsxl.api.dungeon.Game;
import com.github.linghun91.dungeonsxl.api.world.GameWorld;
import com.github.linghun91.dungeonsxl.api.world.ResourceWorld;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of GameWorld
 */
public class GameWorldImpl extends InstanceWorldImpl implements GameWorld {
    
    private final Game game;
    private Location startLocation;
    private int floorNumber;
    private final List<ProtectedRegion> protectedRegions;
    
    public GameWorldImpl(DungeonsXL plugin, ResourceWorld resourceWorld, World world, Game game) {
        super(plugin, resourceWorld, world);
        this.game = game;
        this.floorNumber = 0;
        this.protectedRegions = new ArrayList<>();
    }
    
    @Override
    public Game getGame() {
        return game;
    }
    
    @Override
    public Location getStartLocation() {
        if (startLocation == null) {
            startLocation = world.getSpawnLocation();
        }
        return startLocation;
    }
    
    @Override
    public void setStartLocation(Location location) {
        this.startLocation = location;
    }
    
    @Override
    public void addProtectedRegion(Location corner1, Location corner2) {
        protectedRegions.add(new ProtectedRegion(corner1, corner2));
    }
    
    @Override
    public void setFloorNumber(int floor) {
        this.floorNumber = floor;
    }
    
    @Override
    public int getFloorNumber() {
        return floorNumber;
    }
    
    @Override
    public boolean isProtected(Location location) {
        for (ProtectedRegion region : protectedRegions) {
            if (region.contains(location)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Inner class for protected regions
     */
    private static class ProtectedRegion {
        private final double minX, maxX, minY, maxY, minZ, maxZ;
        
        public ProtectedRegion(Location corner1, Location corner2) {
            this.minX = Math.min(corner1.getX(), corner2.getX());
            this.maxX = Math.max(corner1.getX(), corner2.getX());
            this.minY = Math.min(corner1.getY(), corner2.getY());
            this.maxY = Math.max(corner1.getY(), corner2.getY());
            this.minZ = Math.min(corner1.getZ(), corner2.getZ());
            this.maxZ = Math.max(corner1.getZ(), corner2.getZ());
        }
        
        public boolean contains(Location loc) {
            return loc.getX() >= minX && loc.getX() <= maxX &&
                   loc.getY() >= minY && loc.getY() <= maxY &&
                   loc.getZ() >= minZ && loc.getZ() <= maxZ;
        }
    }
}
