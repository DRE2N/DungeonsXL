package com.github.linghun91.dungeonsxl.sign.windup;

import com.github.linghun91.dungeonsxl.api.world.GameWorld;
import com.github.linghun91.dungeonsxl.sign.AbstractDungeonSign;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;

/**
 * MOB sign - spawn mobs when triggered
 * @author linghun91
 */
public class MobSign extends AbstractDungeonSign {
    
    private final EntityType mobType;
    private final int amount;
    
    public MobSign(GameWorld gameWorld, Block block) {
        super(gameWorld, block);
        
        String mobName = getLine(1);
        this.mobType = parseMobType(mobName);
        
        String amountStr = getLine(2);
        this.amount = parseAmount(amountStr);
    }
    
    @Override
    public String getType() {
        return "MOB";
    }
    
    @Override
    public boolean setup() {
        return true;
    }
    
    @Override
    public void trigger() {
        // Spawn mobs at sign location
        var loc = block.getLocation().add(0.5, 0, 0.5);
        for (int i = 0; i < amount; i++) {
            gameWorld.getWorld().spawnEntity(loc, mobType);
        }
        gameWorld.nextWave();
    }
    
    private EntityType parseMobType(String name) {
        try {
            return EntityType.valueOf(name.toUpperCase());
        } catch (Exception e) {
            return EntityType.ZOMBIE;
        }
    }
    
    private int parseAmount(String str) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return 1;
        }
    }
}
