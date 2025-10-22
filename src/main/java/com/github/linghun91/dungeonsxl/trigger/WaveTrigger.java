package com.github.linghun91.dungeonsxl.trigger;

import com.github.linghun91.dungeonsxl.api.trigger.TriggerType;
import com.github.linghun91.dungeonsxl.api.world.GameWorld;

/**
 * Trigger activated after completing waves
 * @author linghun91
 */
public class WaveTrigger extends AbstractTrigger {
    
    private final int targetWave;
    
    public WaveTrigger(GameWorld gameWorld, String id, int targetWave) {
        super(gameWorld, id);
        this.targetWave = targetWave;
    }
    
    @Override
    public TriggerType getType() {
        return TriggerType.WAVE;
    }
    
    @Override
    public void trigger() {
        if (gameWorld.getWaveCount() >= targetWave) {
            setTriggered();
        }
    }
}
