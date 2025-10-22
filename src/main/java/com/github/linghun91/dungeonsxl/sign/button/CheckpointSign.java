package com.github.linghun91.dungeonsxl.sign.button;

import com.github.linghun91.dungeonsxl.api.world.GameWorld;
import com.github.linghun91.dungeonsxl.sign.AbstractDungeonSign;
import org.bukkit.block.Block;

/**
 * CHECKPOINT sign - set respawn checkpoint
 * @author linghun91
 */
public class CheckpointSign extends AbstractDungeonSign {
    
    public CheckpointSign(GameWorld gameWorld, Block block) {
        super(gameWorld, block);
    }
    
    @Override
    public String getType() {
        return "CHECKPOINT";
    }
    
    @Override
    public boolean setup() {
        return true;
    }
    
    @Override
    public void trigger() {
        // Set checkpoint for triggering player
    }
}
