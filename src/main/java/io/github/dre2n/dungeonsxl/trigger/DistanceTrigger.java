/*
 * Copyright (C) 2012-2016 Frank Baumann
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
package io.github.dre2n.dungeonsxl.trigger;

import io.github.dre2n.dungeonsxl.event.trigger.TriggerActionEvent;
import io.github.dre2n.dungeonsxl.world.GameWorld;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class DistanceTrigger extends Trigger {

    private static Map<GameWorld, ArrayList<DistanceTrigger>> triggers = new HashMap<>();

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

        if (event.isCancelled()) {
            return;
        }

        setTriggered(true);
        this.setPlayer(player);
        updateDSigns();
    }

    @Override
    public void register(GameWorld gameWorld) {
        if (!hasTriggers(gameWorld)) {
            ArrayList<DistanceTrigger> list = new ArrayList<>();
            list.add(this);
            triggers.put(gameWorld, list);

        } else {
            triggers.get(gameWorld).add(this);
        }
    }

    @Override
    public void unregister(GameWorld gameWorld) {
        if (hasTriggers(gameWorld)) {
            triggers.get(gameWorld).remove(this);
        }
    }

    @Override
    public TriggerType getType() {
        return type;
    }

    public static void triggerAllInDistance(Player player, GameWorld gameWorld) {
        if (!hasTriggers(gameWorld)) {
            return;
        }

        if (!player.getLocation().getWorld().equals(gameWorld.getWorld())) {
            return;
        }

        for (DistanceTrigger trigger : getTriggersArray(gameWorld)) {
            if (player.getLocation().distance(trigger.loc) < trigger.distance) {
                trigger.onTrigger(player);
            }
        }
    }

    public static boolean hasTriggers(GameWorld gameWorld) {
        return !triggers.isEmpty() && triggers.containsKey(gameWorld);
    }

    public static ArrayList<DistanceTrigger> getTriggers(GameWorld gameWorld) {
        return triggers.get(gameWorld);
    }

    public static DistanceTrigger[] getTriggersArray(GameWorld gameWorld) {
        return getTriggers(gameWorld).toArray(new DistanceTrigger[getTriggers(gameWorld).size()]);
    }

}
