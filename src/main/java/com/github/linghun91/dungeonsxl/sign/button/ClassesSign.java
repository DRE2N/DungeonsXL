package com.github.linghun91.dungeonsxl.sign.button;

import com.github.linghun91.dungeonsxl.api.world.GameWorld;
import com.github.linghun91.dungeonsxl.sign.AbstractDungeonSign;
import org.bukkit.block.Block;

/**
 * CLASSES sign - choose player class
 * @author linghun91
 */
public class ClassesSign extends AbstractDungeonSign {
    
    private final String className;
    
    public ClassesSign(GameWorld gameWorld, Block block) {
        super(gameWorld, block);
        this.className = getLine(1);
    }
    
    @Override
    public String getType() {
        return "CLASSES";
    }
    
    @Override
    public boolean setup() {
        return true;
    }
    
    @Override
    public void trigger() {
        // Open class selection GUI or apply class
    }
}
