package com.github.linghun91.dungeonsxl.api.world;

import com.github.linghun91.dungeonsxl.config.WorldConfig;
import org.bukkit.Location;

import java.io.File;
import java.util.Optional;

/**
 * Represents a resource world - a template dungeon map stored on disk
 * Resource worlds are static blueprints that can be instantiated multiple times
 *
 * @author linghun91
 */
public interface ResourceWorld {

    /**
     * Gets the unique name of this resource world
     *
     * @return World name
     */
    String getName();

    /**
     * Gets the folder where this resource world is stored
     *
     * @return World folder
     */
    File getFolder();

    /**
     * Gets the configuration for this resource world
     *
     * @return World configuration
     */
    WorldConfig getConfig();

    /**
     * Gets the lobby/spawn location for this world
     *
     * @return Lobby location, or empty if not set
     */
    Optional<Location> getLobbyLocation();

    /**
     * Instantiates this resource world as a game world
     *
     * @return New game world instance
     */
    GameWorld instantiateAsGame();

    /**
     * Instantiates this resource world as an edit world
     *
     * @return New edit world instance
     */
    EditWorld instantiateAsEdit();

    /**
     * Checks if this resource world exists on disk
     *
     * @return True if exists
     */
    boolean exists();

    /**
     * Deletes this resource world from disk
     * This cannot be undone!
     *
     * @return True if successfully deleted
     */
    boolean delete();

    /**
     * Creates a backup of this resource world
     *
     * @return Backup file, or empty if failed
     */
    Optional<File> backup();
}
