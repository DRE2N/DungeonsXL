package com.github.linghun91.dungeonsxl.trigger;

import com.github.linghun91.dungeonsxl.api.trigger.TriggerType;
import com.github.linghun91.dungeonsxl.api.world.GameWorld;
import org.bukkit.block.Block;

/**
 * Trigger activated when block is interacted with
 * @author linghun91
 */
public class InteractTrigger extends AbstractTrigger {
    
    private final Block block;
    
    public InteractTrigger(GameWorld gameWorld, String id, Block block) {
        super(gameWorld, id);
        this.block = block;
    }
    
    @Override
    public TriggerType getType() {
        return TriggerType.INTERACT;
    }
    
    @Override
    public void trigger() {
        setTriggered();
    }
    
    public Block getBlock() {
        return block;
    }
}
