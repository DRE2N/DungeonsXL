package com.github.linghun91.dungeonsxl.world;

import com.github.linghun91.dungeonsxl.DungeonsXL;
import com.github.linghun91.dungeonsxl.api.world.*;
import org.bukkit.World;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages all dungeon worlds
 * @author linghun91
 */
public class WorldManager {
    private final DungeonsXL plugin;
    private final Map<String, ResourceWorld> resourceWorlds = new ConcurrentHashMap<>();
    private final Map<Integer, InstanceWorld> instances = new ConcurrentHashMap<>();
    private int nextInstanceId = 1;

    public WorldManager(DungeonsXL plugin) {
        this.plugin = plugin;
        loadResourceWorlds();
    }

    private void loadResourceWorlds() {
        File mapsFolder = new File(plugin.getDataFolder(), "maps");
        if (!mapsFolder.exists()) {
            mapsFolder.mkdirs();
        }

        File[] worldFolders = mapsFolder.listFiles(File::isDirectory);
        if (worldFolders != null) {
            for (File folder : worldFolders) {
                try {
                    ResourceWorld world = new ResourceWorldImpl(plugin, folder);
                    resourceWorlds.put(world.getName(), world);
                } catch (Exception e) {
                    plugin.getLogger().warning("Failed to load resource world: " + folder.getName());
                }
            }
        }

        plugin.getLogger().info("Loaded " + resourceWorlds.size() + " resource worlds");
    }

    public Optional<ResourceWorld> getResourceWorld(String name) {
        return Optional.ofNullable(resourceWorlds.get(name));
    }

    public Collection<ResourceWorld> getResourceWorlds() {
        return new ArrayList<>(resourceWorlds.values());
    }

    public Optional<InstanceWorld> getInstance(int id) {
        return Optional.ofNullable(instances.get(id));
    }

    public Optional<InstanceWorld> getInstance(World world) {
        return instances.values().stream()
            .filter(instance -> instance.getWorld().equals(world))
            .findFirst();
    }

    public Collection<InstanceWorld> getInstances() {
        return new ArrayList<>(instances.values());
    }

    public void registerInstance(InstanceWorld instance) {
        instances.put(instance.getId(), instance);
    }

    public void unregisterInstance(InstanceWorld instance) {
        instances.remove(instance.getId());
    }

    public void unloadAllInstances() {
        new ArrayList<>(instances.values()).forEach(InstanceWorld::unload);
    }

    public int getNextInstanceId() {
        return nextInstanceId++;
    }
}
