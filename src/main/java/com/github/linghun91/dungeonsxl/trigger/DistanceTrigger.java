package com.github.linghun91.dungeonsxl.trigger;

import com.github.linghun91.dungeonsxl.api.trigger.TriggerType;
import com.github.linghun91.dungeonsxl.api.world.GameWorld;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Trigger activated when player is within distance
 * @author linghun91
 */
public class DistanceTrigger extends AbstractTrigger {
    
    private final Location location;
    private final double distance;
    
    public DistanceTrigger(GameWorld gameWorld, String id, Location location, double distance) {
        super(gameWorld, id);
        this.location = location;
        this.distance = distance;
    }
    
    @Override
    public TriggerType getType() {
        return TriggerType.DISTANCE;
    }
    
    @Override
    public void trigger() {
        for (Player player : gameWorld.getPlayers()) {
            if (player.getLocation().distance(location) <= distance) {
                setTriggered();
                break;
            }
        }
    }
}
