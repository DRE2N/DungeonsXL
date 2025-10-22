package com.github.linghun91.dungeonsxl.sign.windup;

import com.github.linghun91.dungeonsxl.api.world.GameWorld;
import com.github.linghun91.dungeonsxl.sign.AbstractDungeonSign;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

/**
 * Wave sign - starts a specific wave when triggered
 */
public class WaveSign extends AbstractDungeonSign {
    
    private final int waveNumber;
    
    public WaveSign(GameWorld gameWorld, Block block, Sign sign) {
        super(gameWorld, block, sign);
        this.waveNumber = Integer.parseInt(sign.getLine(1));
    }
    
    @Override
    public String getType() {
        return "wave";
    }
    
    @Override
    public void trigger() {
        gameWorld.getGame().startWave(waveNumber);
    }
}
