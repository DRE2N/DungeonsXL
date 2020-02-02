/*
 * Copyright (C) 2012-2020 Frank Baumann
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.erethon.dungeonsxl.trigger;

import de.erethon.dungeonsxl.event.trigger.TriggerActionEvent;
import de.erethon.dungeonsxl.world.DGameWorld;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class DistanceTrigger extends Trigger {

    private TriggerType type = TriggerTypeDefault.DISTANCE;

    private int distance = 5;
    private Location loc;

    public DistanceTrigger(int distance, Location loc) {
        if (distance >= 0) {
            this.distance = distance;
        }
        this.loc = loc;
    }

    public DistanceTrigger(Location loc) {
        this.loc = loc;
    }

    public void onTrigger(Player player) {
        TriggerActionEvent event = new TriggerActionEvent(this);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        setTriggered(true);
        this.setPlayer(player);
        updateDSigns();
    }

    @Override
    public TriggerType getType() {
        return type;
    }

    /* Statics */
    public static void triggerAllInDistance(Player player, DGameWorld gameWorld) {
        if (!player.getLocation().getWorld().equals(gameWorld.getWorld())) {
            return;
        }

        for (Trigger uncasted : gameWorld.getTriggers(TriggerTypeDefault.DISTANCE)) {
            DistanceTrigger trigger = (DistanceTrigger) uncasted;
            if (player.getLocation().distance(trigger.loc) < trigger.distance) {
                trigger.onTrigger(player);
            }
        }
    }

}
