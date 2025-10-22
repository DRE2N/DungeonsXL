package com.github.linghun91.dungeonsxl.sign.windup;

import com.github.linghun91.dungeonsxl.api.world.GameWorld;
import com.github.linghun91.dungeonsxl.sign.AbstractDungeonSign;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

/**
 * Sound sign - plays sound when triggered
 */
public class SoundSign extends AbstractDungeonSign {
    
    private final Sound sound;
    private final float volume;
    private final float pitch;
    
    public SoundSign(GameWorld gameWorld, Block block, Sign sign) {
        super(gameWorld, block, sign);
        
        try {
            this.sound = Sound.valueOf(sign.getLine(1).toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid sound: " + sign.getLine(1));
        }
        
        this.volume = sign.getLine(2).isEmpty() ? 1.0f : Float.parseFloat(sign.getLine(2));
        this.pitch = sign.getLine(3).isEmpty() ? 1.0f : Float.parseFloat(sign.getLine(3));
    }
    
    @Override
    public String getType() {
        return "sound";
    }
    
    @Override
    public void trigger() {
        Location loc = block.getLocation();
        gameWorld.getWorld().playSound(loc, sound, volume, pitch);
    }
}
