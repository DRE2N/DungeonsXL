package com.github.linghun91.dungeonsxl.sign.windup;

import com.github.linghun91.dungeonsxl.api.world.GameWorld;
import com.github.linghun91.dungeonsxl.sign.AbstractDungeonSign;
import com.github.linghun91.dungeonsxl.util.MessageUtil;
import net.kyori.adventure.title.Title;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import java.time.Duration;

/**
 * Title sign - shows title and subtitle when triggered
 */
public class TitleSign extends AbstractDungeonSign {
    
    private final String titleText;
    private final String subtitleText;
    
    public TitleSign(GameWorld gameWorld, Block block, Sign sign) {
        super(gameWorld, block, sign);
        this.titleText = sign.getLine(1);
        this.subtitleText = sign.getLine(2);
    }
    
    @Override
    public String getType() {
        return "title";
    }
    
    @Override
    public void trigger() {
        Title title = Title.title(
            MessageUtil.parse(titleText),
            MessageUtil.parse(subtitleText),
            Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(3), Duration.ofMillis(500))
        );
        
        gameWorld.getGame().getPlayers().forEach(player -> {
            player.getPlayer().showTitle(title);
        });
    }
}
