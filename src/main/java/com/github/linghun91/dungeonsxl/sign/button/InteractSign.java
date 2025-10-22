package com.github.linghun91.dungeonsxl.sign.button;

import com.github.linghun91.dungeonsxl.api.world.GameWorld;
import com.github.linghun91.dungeonsxl.sign.AbstractDungeonSign;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * Interact sign - triggers linked triggers when clicked
 */
public class InteractSign extends AbstractDungeonSign {
    
    private final String triggerId;
    
    public InteractSign(GameWorld gameWorld, Block block, Sign sign) {
        super(gameWorld, block, sign);
        this.triggerId = sign.getLine(1);
    }
    
    @Override
    public String getType() {
        return "interact";
    }
    
    @Override
    public void onPlayerClick(Player player) {
        // Activate linked trigger
        gameWorld.getGame().getTriggerManager().activateTrigger(triggerId);
    }
}
