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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class WaveTrigger extends Trigger {

    private static Map<GameWorld, ArrayList<WaveTrigger>> triggers = new HashMap<>();

    private TriggerType type = TriggerTypeDefault.WAVE;

    private double mustKillRate = 1;

    public WaveTrigger(double mustKillRate) {
        this.mustKillRate = mustKillRate;
    }

    /**
     * @return the minimal mob kill rate to trigger the wave
     */
    public double getMustKillRate() {
        return mustKillRate;
    }

    /**
     * @param mustKillRate
     * the minimal mob kill rate to trigger the wave to set
     */
    public void setMustKillRate(double mustKillRate) {
        this.mustKillRate = mustKillRate;
    }

    public void onTrigger() {
        TriggerActionEvent event = new TriggerActionEvent(this);
        plugin.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        setTriggered(true);
        updateDSigns();
        setTriggered(false);
    }

    @Override
    public void register(GameWorld gameWorld) {
        if (!hasTriggers(gameWorld)) {
            ArrayList<WaveTrigger> list = new ArrayList<>();
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

    /* Statics */
    public static WaveTrigger getOrCreate(double mustKillRate, GameWorld gameWorld) {
        return new WaveTrigger(mustKillRate);
    }

    /**
     * @return the WaveTriggers in the GameWorld
     */
    public static Set<WaveTrigger> getByGameWorld(GameWorld gameWorld) {
        Set<WaveTrigger> toReturn = new HashSet<>();
        for (WaveTrigger trigger : triggers.get(gameWorld)) {
            toReturn.add(trigger);
        }
        return toReturn;
    }

    public static boolean hasTriggers(GameWorld gameWorld) {
        return !triggers.isEmpty() && triggers.containsKey(gameWorld);
    }

}
