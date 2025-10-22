package com.github.linghun91.dungeonsxl.trigger;

import com.github.linghun91.dungeonsxl.api.trigger.Trigger;
import com.github.linghun91.dungeonsxl.api.trigger.TriggerType;
import com.github.linghun91.dungeonsxl.api.world.GameWorld;

/**
 * Abstract base trigger
 * @author linghun91
 */
public abstract class AbstractTrigger implements Trigger {
    
    protected final GameWorld gameWorld;
    protected final String id;
    protected boolean triggered = false;
    
    public AbstractTrigger(GameWorld gameWorld, String id) {
        this.gameWorld = gameWorld;
        this.id = id;
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public boolean isTriggered() {
        return triggered;
    }
    
    @Override
    public void reset() {
        triggered = false;
    }
    
    protected void setTriggered() {
        this.triggered = true;
    }
}
