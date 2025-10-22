package com.github.linghun91.dungeonsxl.sign.windup;

import com.github.linghun91.dungeonsxl.api.world.GameWorld;
import com.github.linghun91.dungeonsxl.sign.AbstractDungeonSign;
import org.bukkit.block.Block;
import org.bukkit.block.data.Powerable;

/**
 * REDSTONE sign - activate redstone when triggered
 * @author linghun91
 */
public class RedstoneSign extends AbstractDungeonSign {
    
    public RedstoneSign(GameWorld gameWorld, Block block) {
        super(gameWorld, block);
    }
    
    @Override
    public String getType() {
        return "REDSTONE";
    }
    
    @Override
    public boolean setup() {
        return true;
    }
    
    @Override
    public void trigger() {
        // Power nearby redstone
        var blockData = block.getBlockData();
        if (blockData instanceof Powerable powerable) {
            powerable.setPowered(true);
            block.setBlockData(powerable);
        }
    }
}
