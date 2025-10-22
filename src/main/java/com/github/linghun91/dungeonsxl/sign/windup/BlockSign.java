package com.github.linghun91.dungeonsxl.sign.windup;

import com.github.linghun91.dungeonsxl.api.world.GameWorld;
import com.github.linghun91.dungeonsxl.sign.AbstractDungeonSign;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

/**
 * Block sign - places or removes blocks when triggered
 */
public class BlockSign extends AbstractDungeonSign {
    
    private final Location targetLocation;
    private final Material material;
    private final boolean place; // true = place, false = remove
    
    public BlockSign(GameWorld gameWorld, Block block, Sign sign) {
        super(gameWorld, block, sign);
        
        // Parse coordinates
        String[] coords = sign.getLine(1).split(",");
        this.targetLocation = new Location(
            gameWorld.getWorld(),
            Integer.parseInt(coords[0]),
            Integer.parseInt(coords[1]),
            Integer.parseInt(coords[2])
        );
        
        String materialStr = sign.getLine(2);
        if (materialStr.isEmpty() || materialStr.equalsIgnoreCase("AIR")) {
            this.material = Material.AIR;
            this.place = false;
        } else {
            this.material = Material.valueOf(materialStr.toUpperCase());
            this.place = true;
        }
    }
    
    @Override
    public String getType() {
        return "block";
    }
    
    @Override
    public void trigger() {
        Block targetBlock = targetLocation.getBlock();
        if (place) {
            targetBlock.setType(material);
        } else {
            targetBlock.setType(Material.AIR);
        }
    }
}
