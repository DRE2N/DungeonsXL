package com.github.linghun91.dungeonsxl.sign.passive;

import com.github.linghun91.dungeonsxl.api.world.GameWorld;
import com.github.linghun91.dungeonsxl.sign.AbstractDungeonSign;
import org.bukkit.block.Block;

/**
 * START sign - marks the start position of the dungeon
 * @author linghun91
 */
public class StartSign extends AbstractDungeonSign {
    
    public StartSign(GameWorld gameWorld, Block block) {
        super(gameWorld, block);
    }
    
    @Override
    public String getType() {
        return "START";
    }
    
    @Override
    public boolean setup() {
        // Set spawn location for the game world
        var loc = block.getLocation().add(0.5, 0, 0.5);
        // Store this as start location
        return true;
    }
    
    @Override
    public void trigger() {
        // Passive sign, no trigger action
    }
}
