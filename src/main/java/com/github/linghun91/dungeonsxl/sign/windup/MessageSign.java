package com.github.linghun91.dungeonsxl.sign.windup;

import com.github.linghun91.dungeonsxl.api.world.GameWorld;
import com.github.linghun91.dungeonsxl.sign.AbstractDungeonSign;
import com.github.linghun91.dungeonsxl.util.MessageUtil;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

/**
 * Message sign - sends chat message to all players when triggered
 */
public class MessageSign extends AbstractDungeonSign {
    
    private final String message;
    
    public MessageSign(GameWorld gameWorld, Block block, Sign sign) {
        super(gameWorld, block, sign);
        this.message = sign.getLine(1) + " " + sign.getLine(2);
    }
    
    @Override
    public String getType() {
        return "message";
    }
    
    @Override
    public void trigger() {
        gameWorld.getGame().getPlayers().forEach(player -> {
            MessageUtil.send(player.getPlayer(), message);
        });
    }
}
