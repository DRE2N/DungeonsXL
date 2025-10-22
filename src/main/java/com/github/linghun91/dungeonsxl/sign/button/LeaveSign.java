package com.github.linghun91.dungeonsxl.sign.button;

import com.github.linghun91.dungeonsxl.api.world.GameWorld;
import com.github.linghun91.dungeonsxl.sign.AbstractDungeonSign;
import org.bukkit.block.Block;

/**
 * LEAVE sign - leave the dungeon
 * @author linghun91
 */
public class LeaveSign extends AbstractDungeonSign {
    
    public LeaveSign(GameWorld gameWorld, Block block) {
        super(gameWorld, block);
    }
    
    @Override
    public String getType() {
        return "LEAVE";
    }
    
    @Override
    public boolean setup() {
        return true;
    }
    
    @Override
    public void trigger() {
        // Teleport player out
    }
}
