package com.github.linghun91.dungeonsxl.sign.windup;

import com.github.linghun91.dungeonsxl.api.world.GameWorld;
import com.github.linghun91.dungeonsxl.sign.AbstractDungeonSign;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;

/**
 * CMD sign - execute command when triggered
 * @author linghun91
 */
public class CommandSign extends AbstractDungeonSign {
    
    private final String command;
    
    public CommandSign(GameWorld gameWorld, Block block) {
        super(gameWorld, block);
        this.command = getLine(1) + " " + getLine(2) + " " + getLine(3);
    }
    
    @Override
    public String getType() {
        return "CMD";
    }
    
    @Override
    public boolean setup() {
        return true;
    }
    
    @Override
    public void trigger() {
        if (command != null && !command.trim().isEmpty()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.trim());
        }
    }
}
