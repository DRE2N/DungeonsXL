/*
 * Copyright (C) 2012-2022 Frank Baumann
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

import de.erethon.dungeonsxl.api.sign.DungeonSign;
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
        if (distance >= 2) {
            this.distance = distance;
        } else {
            this.distance = 2;
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
        setPlayer(player);
        updateDSigns();
        DGameWorld gameWorld = null;
        for (DungeonSign sign : getDSigns()) {
            gameWorld = (DGameWorld) sign.getGameWorld();
            removeDSign(sign);
            sign.removeTrigger(this);
        }
        unregister(gameWorld);
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

        for (Trigger trigger : gameWorld.getTriggers().toArray(new Trigger[gameWorld.getTriggers().size()])) {
            if (trigger instanceof DistanceTrigger) {
                DistanceTrigger distanceTrigger = (DistanceTrigger) trigger;
                if (player.getLocation().distance(distanceTrigger.loc) < distanceTrigger.distance) {
                    distanceTrigger.onTrigger(player);
                }
            }
        }
    }

}
