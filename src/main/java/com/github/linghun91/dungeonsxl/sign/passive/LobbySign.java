package com.github.linghun91.dungeonsxl.sign.passive;

import com.github.linghun91.dungeonsxl.api.world.GameWorld;
import com.github.linghun91.dungeonsxl.sign.AbstractDungeonSign;
import org.bukkit.block.Block;

/**
 * LOBBY sign - marks lobby/waiting area
 * @author linghun91
 */
public class LobbySign extends AbstractDungeonSign {
    
    public LobbySign(GameWorld gameWorld, Block block) {
        super(gameWorld, block);
    }
    
    @Override
    public String getType() {
        return "LOBBY";
    }
    
    @Override
    public boolean setup() {
        return true;
    }
    
    @Override
    public void trigger() {
    }
}
