package com.github.linghun91.dungeonsxl.sign.button;

import com.github.linghun91.dungeonsxl.api.world.GameWorld;
import com.github.linghun91.dungeonsxl.sign.AbstractDungeonSign;
import com.github.linghun91.dungeonsxl.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * Teleport sign - teleports player when clicked
 */
public class TeleportSign extends AbstractDungeonSign {
    
    private final Location destination;
    
    public TeleportSign(GameWorld gameWorld, Block block, Sign sign) {
        super(gameWorld, block, sign);
        
        // Parse destination from sign lines
        String coordString = sign.getLine(1) + "," + sign.getLine(2);
        this.destination = LocationUtil.deserialize(coordString, gameWorld.getWorld());
    }
    
    @Override
    public String getType() {
        return "teleport";
    }
    
    @Override
    public void onPlayerClick(Player player) {
        if (destination != null) {
            player.teleport(destination);
        }
    }
}
