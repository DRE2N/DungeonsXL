package com.github.linghun91.dungeonsxl.sign;

import com.github.linghun91.dungeonsxl.api.sign.DungeonSign;
import com.github.linghun91.dungeonsxl.api.world.GameWorld;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

/**
 * Abstract base class for all dungeon signs
 * @author linghun91
 */
public abstract class AbstractDungeonSign implements DungeonSign {
    
    protected final GameWorld gameWorld;
    protected final Block block;
    protected final Sign sign;
    protected final String[] lines;
    
    public AbstractDungeonSign(GameWorld gameWorld, Block block) {
        this.gameWorld = gameWorld;
        this.block = block;
        this.sign = (Sign) block.getState();
        this.lines = new String[4];
        for (int i = 0; i < 4; i++) {
            this.lines[i] = sign.line(i).toString();
        }
    }
    
    @Override
    public Block getBlock() {
        return block;
    }
    
    @Override
    public Sign getSign() {
        return sign;
    }
    
    protected String getLine(int index) {
        return index >= 0 && index < 4 ? lines[index] : "";
    }
    
    protected void setLine(int index, String text) {
        if (index >= 0 && index < 4) {
            lines[index] = text;
            sign.line(index, net.kyori.adventure.text.Component.text(text));
            sign.update();
        }
    }
    
    @Override
    public void remove() {
        block.breakNaturally();
    }
}
