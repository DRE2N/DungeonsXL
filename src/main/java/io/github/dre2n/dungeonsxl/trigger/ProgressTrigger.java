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
public class ProgressTrigger extends Trigger {

    private static Map<GameWorld, ArrayList<ProgressTrigger>> triggers = new HashMap<>();

    private TriggerType type = TriggerTypeDefault.PROGRESS;

    private String floor;
    private int floorCount;
    private int waveCount;

    public ProgressTrigger(int floorCount, int waveCount) {
        this.floorCount = floorCount;
        this.waveCount = waveCount;
    }

    public ProgressTrigger(String floor) {
        this.floor = floor;
    }

    /* Getters and setters */
    /**
     * @return the specific floor that must be finished
     */
    public String getFloor() {
        return floor;
    }

    /**
     * @param floor
     * the specific floor to set
     */
    public void setFloor(String floor) {
        this.floor = floor;
    }

    /**
     * @return the floor count to trigger
     */
    public int getFloorCount() {
        return floorCount;
    }

    /**
     * @param floorCount
     * the floor count to set
     */
    public void setFloorCount(int floorCount) {
        this.floorCount = floorCount;
    }

    /**
     * @return the wave count to trigger
     */
    public int getWaveCount() {
        return waveCount;
    }

    /**
     * @param waveCount
     * the wave count to set
     */
    public void setWaveCount(int waveCount) {
        this.waveCount = waveCount;
    }

    /* Actions */
    public void onTrigger() {
        TriggerActionEvent event = new TriggerActionEvent(this);

        if (event.isCancelled()) {
            return;
        }

        setTriggered(true);
        updateDSigns();
    }

    @Override
    public void register(GameWorld gameWorld) {
        if (!hasTriggers(gameWorld)) {
            ArrayList<ProgressTrigger> list = new ArrayList<>();
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

    public static ProgressTrigger getOrCreate(int floorCount, int waveCount, GameWorld gameWorld) {
        if (floorCount == 0 & waveCount == 0 || floorCount < 0 || waveCount < 0) {
            return null;
        }
        return new ProgressTrigger(floorCount, waveCount);
    }

    public static ProgressTrigger getOrCreate(String floor, GameWorld gameWorld) {
        return new ProgressTrigger(floor);
    }

    public static Set<ProgressTrigger> getByGameWorld(GameWorld gameWorld) {
        Set<ProgressTrigger> toReturn = new HashSet<>();
        for (ProgressTrigger trigger : triggers.get(gameWorld)) {
            toReturn.add(trigger);
        }
        return toReturn;
    }

    public static boolean hasTriggers(GameWorld gameWorld) {
        return !triggers.isEmpty() && triggers.containsKey(gameWorld);
    }

}
