package com.github.linghun91.dungeonsxl.sign.passive;

import com.github.linghun91.dungeonsxl.api.world.GameWorld;
import com.github.linghun91.dungeonsxl.sign.AbstractDungeonSign;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

/**
 * Protection sign - protects an area from modification
 */
public class ProtectionSign extends AbstractDungeonSign {
    
    private final Location corner1;
    private final Location corner2;
    
    public ProtectionSign(GameWorld gameWorld, Block block, Sign sign) {
        super(gameWorld, block, sign);
        
        // Parse coordinates from sign
        String[] parts1 = sign.getLine(1).split(",");
        String[] parts2 = sign.getLine(2).split(",");
        
        this.corner1 = new Location(gameWorld.getWorld(),
            Integer.parseInt(parts1[0]),
            Integer.parseInt(parts1[1]),
            Integer.parseInt(parts1[2]));
        this.corner2 = new Location(gameWorld.getWorld(),
            Integer.parseInt(parts2[0]),
            Integer.parseInt(parts2[1]),
            Integer.parseInt(parts2[2]));
    }
    
    @Override
    public String getType() {
        return "protection";
    }
    
    @Override
    public void onInitialize() {
        // Register protected region
        gameWorld.addProtectedRegion(corner1, corner2);
    }
    
    public boolean isInProtectedArea(Location loc) {
        double minX = Math.min(corner1.getX(), corner2.getX());
        double maxX = Math.max(corner1.getX(), corner2.getX());
        double minY = Math.min(corner1.getY(), corner2.getY());
        double maxY = Math.max(corner1.getY(), corner2.getY());
        double minZ = Math.min(corner1.getZ(), corner2.getZ());
        double maxZ = Math.max(corner1.getZ(), corner2.getZ());
        
        return loc.getX() >= minX && loc.getX() <= maxX &&
               loc.getY() >= minY && loc.getY() <= maxY &&
               loc.getZ() >= minZ && loc.getZ() <= maxZ;
    }
}
