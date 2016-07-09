/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.dre2n.dungeonsxl.global;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import java.io.File;
import java.util.Collection;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * @author Daniel Saukel
 */
public abstract class GlobalProtection {

    static DungeonsXL plugin = DungeonsXL.getInstance();
    static FileConfiguration config = plugin.getGlobalData().getConfig();
    static GlobalProtections protections = plugin.getGlobalProtections();

    private World world;
    private int id;

    protected GlobalProtection(World world, int id) {
        this.world = world;
        this.id = id;

        protections.addProtection(this);
    }

    /**
     * @return the world
     */
    public World getWorld() {
        return world;
    }

    /**
     * @param world
     * the world to set
     */
    public void setWorld(World world) {
        this.world = world;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Delete this protection.
     */
    public void delete() {
        protections.removeProtection(this);
    }

    /* Abstracts */
    /**
     * Save the data to the default file
     */
    public void save() {
        save(config);
    }

    /**
     * @param file
     * the file to save the protection to
     */
    public void save(File file) {
        save(YamlConfiguration.loadConfiguration(file));
    }

    /**
     * @param config
     * the config to save the protection to
     */
    public abstract void save(FileConfiguration config);

    /**
     * @return a collection of all blocks covered by this protection
     */
    public abstract Collection<Block> getBlocks();

}
