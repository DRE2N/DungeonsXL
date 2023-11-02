/*
 * Copyright (C) 2012-2023 Frank Baumann
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

import de.erethon.bedrock.misc.NumberUtil;
import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.trigger.AbstractTrigger;
import de.erethon.dungeonsxl.api.trigger.LogicalExpression;
import de.erethon.dungeonsxl.api.trigger.Trigger;
import de.erethon.dungeonsxl.api.trigger.TriggerListener;
import de.erethon.dungeonsxl.api.trigger.TriggerTypeKey;
import de.erethon.dungeonsxl.world.DGameWorld;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class DistanceTrigger extends AbstractTrigger {

    private int distance = 5;
    private Location loc;

    public DistanceTrigger(DungeonsAPI api, TriggerListener owner, LogicalExpression expression, String value) {
        super(api, owner, expression, value);

        distance = NumberUtil.parseInt(value, distance);
        if (distance < 2) {
            distance = 2;
        }
        this.loc = owner.getLocation();
    }

    @Override
    public char getKey() {
        return TriggerTypeKey.DISTANCE;
    }

    @Override
    public void onTrigger(boolean switching) {
        setTriggered(true);
        unregisterTrigger();
        getListeners().clear();
        getGameWorld().unregisterTrigger(this);
    }

    /* Statics */
    public static void triggerAllInDistance(Player player, DGameWorld gameWorld) {
        if (!player.getLocation().getWorld().equals(gameWorld.getWorld())) {
            return;
        }

        for (Trigger trigger : gameWorld.getTriggers().toArray(Trigger[]::new)) {
            if (!(trigger instanceof DistanceTrigger)) {
                continue;
            }
            DistanceTrigger distanceTrigger = (DistanceTrigger) trigger;
            if (player.getLocation().distance(distanceTrigger.loc) < distanceTrigger.distance) {
                distanceTrigger.trigger(true, player);
            }
        }
    }

}
