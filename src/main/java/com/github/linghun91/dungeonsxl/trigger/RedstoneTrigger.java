package com.github.linghun91.dungeonsxl.trigger;

import com.github.linghun91.dungeonsxl.api.trigger.TriggerType;
import com.github.linghun91.dungeonsxl.api.world.GameWorld;
import org.bukkit.block.Block;

/**
 * Trigger activated by redstone signal
 * @author linghun91
 */
public class RedstoneTrigger extends AbstractTrigger {
    
    private final Block block;
    
    public RedstoneTrigger(GameWorld gameWorld, String id, Block block) {
        super(gameWorld, id);
        this.block = block;
    }
    
    @Override
    public TriggerType getType() {
        return TriggerType.REDSTONE;
    }
    
    @Override
    public void trigger() {
        if (block.isBlockPowered() || block.isBlockIndirectlyPowered()) {
            setTriggered();
        }
    }
}
