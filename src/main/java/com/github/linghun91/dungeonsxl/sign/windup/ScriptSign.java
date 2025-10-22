package com.github.linghun91.dungeonsxl.sign.windup;

import com.github.linghun91.dungeonsxl.api.world.GameWorld;
import com.github.linghun91.dungeonsxl.sign.AbstractDungeonSign;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

/**
 * Script sign - executes custom scripts when triggered
 */
public class ScriptSign extends AbstractDungeonSign {
    
    private final String scriptName;
    
    public ScriptSign(GameWorld gameWorld, Block block, Sign sign) {
        super(gameWorld, block, sign);
        this.scriptName = sign.getLine(1);
    }
    
    @Override
    public String getType() {
        return "script";
    }
    
    @Override
    public void trigger() {
        // Execute script from scripts folder
        gameWorld.getPlugin().getScriptManager().executeScript(scriptName, gameWorld.getGame());
    }
}
