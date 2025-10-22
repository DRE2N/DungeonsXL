package com.github.linghun91.dungeonsxl.world;

import com.github.linghun91.dungeonsxl.DungeonsXL;
import com.github.linghun91.dungeonsxl.api.world.*;
import com.github.linghun91.dungeonsxl.config.WorldConfig;
import org.bukkit.Location;

import java.io.File;
import java.util.Optional;

public class ResourceWorldImpl implements ResourceWorld {
    private final DungeonsXL plugin;
    private final File folder;
    private final String name;
    private final WorldConfig config;
    
    public ResourceWorldImpl(DungeonsXL plugin, File folder) {
        this.plugin = plugin;
        this.folder = folder;
        this.name = folder.getName();
        this.config = new WorldConfig(folder);
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public File getFolder() {
        return folder;
    }
    
    @Override
    public WorldConfig getConfig() {
        return config;
    }
    
    @Override
    public Optional<Location> getLobbyLocation() {
        return Optional.empty();
    }
    
    @Override
    public GameWorld instantiateAsGame() {
        return new GameWorldImpl(plugin, this);
    }
    
    @Override
    public EditWorld instantiateAsEdit() {
        return new EditWorldImpl(plugin, this);
    }
    
    @Override
    public boolean exists() {
        return folder.exists();
    }
    
    @Override
    public boolean delete() {
        return false;
    }
    
    @Override
    public Optional<File> backup() {
        return Optional.empty();
    }
}
