package com.github.linghun91.dungeonsxl.sign.button;

import com.github.linghun91.dungeonsxl.api.world.GameWorld;
import com.github.linghun91.dungeonsxl.sign.AbstractDungeonSign;
import org.bukkit.block.Block;

/**
 * READY sign - players click to ready up
 * @author linghun91
 */
public class ReadySign extends AbstractDungeonSign {
    
    public ReadySign(GameWorld gameWorld, Block block) {
        super(gameWorld, block);
    }
    
    @Override
    public String getType() {
        return "READY";
    }
    
    @Override
    public boolean setup() {
        return true;
    }
    
    @Override
    public void trigger() {
        // Check if all players are ready, then start game
        if (!gameWorld.isGameStarted()) {
            // Count ready players
            boolean allReady = true; // TODO: implement proper check
            if (allReady) {
                gameWorld.startGame();
            }
        }
    }
}
