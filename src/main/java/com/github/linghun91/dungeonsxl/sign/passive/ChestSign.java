package com.github.linghun91.dungeonsxl.sign.passive;

import com.github.linghun91.dungeonsxl.api.world.GameWorld;
import com.github.linghun91.dungeonsxl.sign.AbstractDungeonSign;
import org.bukkit.block.Block;

/**
 * CHEST sign - marks a loot chest
 * @author linghun91
 */
public class ChestSign extends AbstractDungeonSign {
    
    public ChestSign(GameWorld gameWorld, Block block) {
        super(gameWorld, block);
    }
    
    @Override
    public String getType() {
        return "CHEST";
    }
    
    @Override
    public boolean setup() {
        return true;
    }
    
    @Override
    public void trigger() {
    }
}
