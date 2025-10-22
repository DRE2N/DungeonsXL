package com.github.linghun91.dungeonsxl.sign.windup;

import com.github.linghun91.dungeonsxl.api.world.GameWorld;
import com.github.linghun91.dungeonsxl.sign.AbstractDungeonSign;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

/**
 * Particle sign - spawns particles when triggered
 */
public class ParticleSign extends AbstractDungeonSign {
    
    private final Particle particle;
    private final int count;
    
    public ParticleSign(GameWorld gameWorld, Block block, Sign sign) {
        super(gameWorld, block, sign);
        
        try {
            this.particle = Particle.valueOf(sign.getLine(1).toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid particle: " + sign.getLine(1));
        }
        
        this.count = sign.getLine(2).isEmpty() ? 10 : Integer.parseInt(sign.getLine(2));
    }
    
    @Override
    public String getType() {
        return "particle";
    }
    
    @Override
    public void trigger() {
        Location loc = block.getLocation().add(0.5, 0.5, 0.5);
        gameWorld.getWorld().spawnParticle(particle, loc, count);
    }
}
