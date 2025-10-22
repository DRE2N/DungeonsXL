package com.github.linghun91.dungeonsxl.sign.passive;

import com.github.linghun91.dungeonsxl.api.world.GameWorld;
import com.github.linghun91.dungeonsxl.sign.AbstractDungeonSign;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

/**
 * Floor sign - marks which floor this is in multi-floor dungeons
 */
public class FloorSign extends AbstractDungeonSign {
    
    private final int floorNumber;
    
    public FloorSign(GameWorld gameWorld, Block block, Sign sign) {
        super(gameWorld, block, sign);
        this.floorNumber = Integer.parseInt(sign.getLine(1));
    }
    
    @Override
    public String getType() {
        return "floor";
    }
    
    @Override
    public void onInitialize() {
        gameWorld.setFloorNumber(floorNumber);
    }
    
    public int getFloorNumber() {
        return floorNumber;
    }
}
