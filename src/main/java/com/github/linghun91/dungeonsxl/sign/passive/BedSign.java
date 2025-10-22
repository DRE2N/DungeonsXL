package com.github.linghun91.dungeonsxl.sign.passive;

import com.github.linghun91.dungeonsxl.api.world.GameWorld;
import com.github.linghun91.dungeonsxl.sign.AbstractDungeonSign;
import org.bukkit.block.Block;

/**
 * BED sign - marks respawn point
 * @author linghun91
 */
public class BedSign extends AbstractDungeonSign {
    
    public BedSign(GameWorld gameWorld, Block block) {
        super(gameWorld, block);
    }
    
    @Override
    public String getType() {
        return "BED";
    }
    
    @Override
    public boolean setup() {
        return true;
    }
    
    @Override
    public void trigger() {
    }
}
