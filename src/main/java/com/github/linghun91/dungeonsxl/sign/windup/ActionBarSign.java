package com.github.linghun91.dungeonsxl.sign.windup;

import com.github.linghun91.dungeonsxl.api.world.GameWorld;
import com.github.linghun91.dungeonsxl.sign.AbstractDungeonSign;
import com.github.linghun91.dungeonsxl.util.MessageUtil;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

/**
 * ActionBar sign - sends action bar message when triggered
 */
public class ActionBarSign extends AbstractDungeonSign {
    
    private final String message;
    
    public ActionBarSign(GameWorld gameWorld, Block block, Sign sign) {
        super(gameWorld, block, sign);
        this.message = sign.getLine(1) + " " + sign.getLine(2);
    }
    
    @Override
    public String getType() {
        return "actionbar";
    }
    
    @Override
    public void trigger() {
        gameWorld.getGame().getPlayers().forEach(player -> {
            player.getPlayer().sendActionBar(MessageUtil.parse(message));
        });
    }
}
