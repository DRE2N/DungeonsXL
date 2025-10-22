package com.github.linghun91.dungeonsxl.trigger;

import com.github.linghun91.dungeonsxl.api.trigger.TriggerType;
import com.github.linghun91.dungeonsxl.api.world.GameWorld;

/**
 * Trigger activated when mobs are killed
 * @author linghun91
 */
public class MobTrigger extends AbstractTrigger {
    
    private final int requiredKills;
    private int currentKills = 0;
    
    public MobTrigger(GameWorld gameWorld, String id, int requiredKills) {
        super(gameWorld, id);
        this.requiredKills = requiredKills;
    }
    
    @Override
    public TriggerType getType() {
        return TriggerType.MOB;
    }
    
    @Override
    public void trigger() {
        currentKills++;
        if (currentKills >= requiredKills) {
            setTriggered();
        }
    }
    
    @Override
    public void reset() {
        super.reset();
        currentKills = 0;
    }
}
