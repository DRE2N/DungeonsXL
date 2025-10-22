package com.github.linghun91.dungeonsxl.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Utility class for location and geometry operations.
 *
 * @author linghun91
 */
public final class LocationUtil {

    private static final BlockFace[] CARDINAL_DIRECTIONS = {
            BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST
    };

    private LocationUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Serializes a location to a string.
     * Format: world,x,y,z,yaw,pitch
     *
     * @param location The location to serialize
     * @return The serialized string
     */
    public static String serialize(Location location) {
        if (location == null || location.getWorld() == null) {
            return "";
        }
        return String.format("%s,%.2f,%.2f,%.2f,%.2f,%.2f",
                location.getWorld().getName(),
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch()
        );
    }

    /**
     * Deserializes a location from a string.
     *
     * @param serialized The serialized string
     * @return The location, or empty if invalid
     */
    public static Optional<Location> deserialize(String serialized) {
        if (serialized == null || serialized.isEmpty()) {
            return Optional.empty();
        }

        try {
            String[] parts = serialized.split(",");
            if (parts.length < 4) {
                return Optional.empty();
            }

            World world = Bukkit.getWorld(parts[0]);
            if (world == null) {
                return Optional.empty();
            }

            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);
            double z = Double.parseDouble(parts[3]);
            float yaw = parts.length > 4 ? Float.parseFloat(parts[4]) : 0;
            float pitch = parts.length > 5 ? Float.parseFloat(parts[5]) : 0;

            return Optional.of(new Location(world, x, y, z, yaw, pitch));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Gets the block location (integer coordinates).
     *
     * @param location The location
     * @return The block location
     */
    public static Location getBlockLocation(Location location) {
        return new Location(
                location.getWorld(),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ()
        );
    }

    /**
     * Gets the center of a block.
     *
     * @param location The location
     * @return The centered location
     */
    public static Location getBlockCenter(Location location) {
        return new Location(
                location.getWorld(),
                location.getBlockX() + 0.5,
                location.getBlockY() + 0.5,
                location.getBlockZ() + 0.5,
                location.getYaw(),
                location.getPitch()
        );
    }

    /**
     * Checks if two locations are in the same block.
     *
     * @param loc1 First location
     * @param loc2 Second location
     * @return true if same block
     */
    public static boolean isSameBlock(Location loc1, Location loc2) {
        if (loc1.getWorld() != loc2.getWorld()) {
            return false;
        }
        return loc1.getBlockX() == loc2.getBlockX()
                && loc1.getBlockY() == loc2.getBlockY()
                && loc1.getBlockZ() == loc2.getBlockZ();
    }

    /**
     * Gets the distance between two locations.
     *
     * @param loc1 First location
     * @param loc2 Second location
     * @return The distance, or -1 if different worlds
     */
    public static double distance(Location loc1, Location loc2) {
        if (loc1.getWorld() != loc2.getWorld()) {
            return -1;
        }
        return loc1.distance(loc2);
    }

    /**
     * Gets all blocks in a cuboid region.
     *
     * @param corner1 First corner
     * @param corner2 Opposite corner
     * @return List of blocks
     */
    public static List<Block> getBlocksInRegion(Location corner1, Location corner2) {
        List<Block> blocks = new ArrayList<>();

        if (corner1.getWorld() != corner2.getWorld()) {
            return blocks;
        }

        int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int minY = Math.min(corner1.getBlockY(), corner2.getBlockY());
        int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());

        int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int maxY = Math.max(corner1.getBlockY(), corner2.getBlockY());
        int maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

        World world = corner1.getWorld();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    blocks.add(world.getBlockAt(x, y, z));
                }
            }
        }

        return blocks;
    }

    /**
     * Gets the direction a player is facing.
     *
     * @param location The location with yaw
     * @return The cardinal block face
     */
    public static BlockFace getCardinalDirection(Location location) {
        double rotation = (location.getYaw() - 180) % 360;
        if (rotation < 0) {
            rotation += 360.0;
        }

        if (45 <= rotation && rotation < 135) {
            return BlockFace.WEST;
        } else if (135 <= rotation && rotation < 225) {
            return BlockFace.NORTH;
        } else if (225 <= rotation && rotation < 315) {
            return BlockFace.EAST;
        } else {
            return BlockFace.SOUTH;
        }
    }

    /**
     * Gets the opposite block face.
     *
     * @param face The block face
     * @return The opposite face
     */
    public static BlockFace getOpposite(BlockFace face) {
        return face.getOppositeFace();
    }

    /**
     * Checks if a location is within a spherical radius.
     *
     * @param center The center location
     * @param location The location to check
     * @param radius The radius
     * @return true if within radius
     */
    public static boolean isWithinRadius(Location center, Location location, double radius) {
        if (center.getWorld() != location.getWorld()) {
            return false;
        }
        return center.distanceSquared(location) <= radius * radius;
    }

    /**
     * Adds a vector to a location and returns a new location.
     *
     * @param location The base location
     * @param vector The vector to add
     * @return The new location
     */
    public static Location add(Location location, Vector vector) {
        return location.clone().add(vector);
    }

    /**
     * Gets a random location within a radius.
     *
     * @param center The center location
     * @param radius The radius
     * @return The random location
     */
    public static Location randomLocation(Location center, double radius) {
        double angle = Math.random() * 2 * Math.PI;
        double distance = Math.random() * radius;
        double x = center.getX() + distance * Math.cos(angle);
        double z = center.getZ() + distance * Math.sin(angle);
        return new Location(center.getWorld(), x, center.getY(), z);
    }

    /**
     * Finds a safe location to teleport to (on ground, with air above).
     *
     * @param location The starting location
     * @return The safe location, or the original if none found
     */
    public static Location findSafeLocation(Location location) {
        World world = location.getWorld();
        if (world == null) {
            return location;
        }

        int x = location.getBlockX();
        int z = location.getBlockZ();

        // Search downward first
        for (int y = location.getBlockY(); y >= world.getMinHeight(); y--) {
            Block block = world.getBlockAt(x, y, z);
            if (block.getType().isSolid()) {
                Block above = world.getBlockAt(x, y + 1, z);
                Block above2 = world.getBlockAt(x, y + 2, z);
                if (above.getType().isAir() && above2.getType().isAir()) {
                    return new Location(world, x + 0.5, y + 1, z + 0.5);
                }
            }
        }

        // Search upward
        for (int y = location.getBlockY(); y < world.getMaxHeight(); y++) {
            Block block = world.getBlockAt(x, y, z);
            if (block.getType().isSolid()) {
                Block above = world.getBlockAt(x, y + 1, z);
                Block above2 = world.getBlockAt(x, y + 2, z);
                if (above.getType().isAir() && above2.getType().isAir()) {
                    return new Location(world, x + 0.5, y + 1, z + 0.5);
                }
            }
        }

        return location;
    }

    /**
     * Gets all cardinal directions.
     *
     * @return Array of cardinal block faces
     */
    public static BlockFace[] getCardinalDirections() {
        return CARDINAL_DIRECTIONS.clone();
    }
}
