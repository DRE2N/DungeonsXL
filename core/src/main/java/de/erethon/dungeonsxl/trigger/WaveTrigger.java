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
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class WaveTrigger extends Trigger {

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
     * @param mustKillRate the minimal mob kill rate to trigger the wave to set
     */
    public void setMustKillRate(double mustKillRate) {
        this.mustKillRate = mustKillRate;
    }

    public void onTrigger() {
        TriggerActionEvent event = new TriggerActionEvent(this);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        setTriggered(true);
        updateDSigns();
        setTriggered(false);
    }

    @Override
    public TriggerType getType() {
        return type;
    }

    /* Statics */
    public static WaveTrigger getOrCreate(double mustKillRate, DGameWorld gameWorld) {
        return new WaveTrigger(mustKillRate);
    }

    /**
     * @param gameWorld the game world to check
     * @return the WaveTriggers in the DGameWorld
     */
    public static Set<WaveTrigger> getByGameWorld(DGameWorld gameWorld) {
        Set<WaveTrigger> toReturn = new HashSet<>();
        for (Trigger trigger : gameWorld.getTriggers(TriggerTypeDefault.WAVE)) {
            toReturn.add((WaveTrigger) trigger);
        }
        return toReturn;
    }

}
