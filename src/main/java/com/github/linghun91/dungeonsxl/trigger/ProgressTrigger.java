package com.github.linghun91.dungeonsxl.trigger;

import com.github.linghun91.dungeonsxl.api.trigger.TriggerType;
import com.github.linghun91.dungeonsxl.api.world.GameWorld;

/**
 * Trigger for progress tracking
 * @author linghun91
 */
public class ProgressTrigger extends AbstractTrigger {
    
    public ProgressTrigger(GameWorld gameWorld, String id) {
        super(gameWorld, id);
    }
    
    @Override
    public TriggerType getType() {
        return TriggerType.PROGRESS;
    }
    
    @Override
    public void trigger() {
        setTriggered();
    }
}
