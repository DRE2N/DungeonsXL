package com.github.linghun91.dungeonsxl.world;

import com.github.linghun91.dungeonsxl.DungeonsXL;
import com.github.linghun91.dungeonsxl.api.world.*;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.*;

public class EditWorldImpl implements EditWorld {
    private final DungeonsXL plugin;
    private final ResourceWorld resourceWorld;
    private final int id;
    private World world;
    private Player editor;
    private boolean dirty = false;
    
    public EditWorldImpl(DungeonsXL plugin, ResourceWorld resourceWorld) {
        this.plugin = plugin;
        this.resourceWorld = resourceWorld;
        this.id = plugin.getWorldManager().getNextInstanceId();
        loadWorld();
    }
    
    private void loadWorld() {
        String worldName = "DXL_Edit_" + id;
        world = new WorldCreator(worldName).createWorld();
        plugin.getWorldManager().registerInstance(this);
    }
    
    @Override
    public Player getEditor() {
        return editor;
    }
    
    public void setEditor(Player editor) {
        this.editor = editor;
    }
    
    @Override
    public boolean save() {
        dirty = false;
        return true;
    }
    
    @Override
    public boolean hasUnsavedChanges() {
        return dirty;
    }
    
    @Override
    public void markDirty() {
        dirty = true;
    }
    
    @Override
    public void clearDirty() {
        dirty = false;
    }
    
    @Override
    public int getId() {
        return id;
    }
    
    @Override
    public ResourceWorld getResourceWorld() {
        return resourceWorld;
    }
    
    @Override
    public World getWorld() {
        return world;
    }
    
    @Override
    public Map<Block, Sign> getDungeonSigns() {
        return new HashMap<>();
    }
    
    @Override
    public Collection<Player> getPlayers() {
        return world.getPlayers();
    }
    
    @Override
    public boolean contains(Player player) {
        return player.getWorld().equals(world);
    }
    
    @Override
    public void unload() {
        plugin.getServer().unloadWorld(world, false);
        plugin.getWorldManager().unregisterInstance(this);
    }
    
    @Override
    public boolean isLoaded() {
        return world != null;
    }
    
    @Override
    public InstanceType getType() {
        return InstanceType.EDIT;
    }
}
