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
import de.erethon.dungeonsxl.api.trigger.TriggerListener;
import de.erethon.dungeonsxl.api.trigger.TriggerTypeKey;
import de.erethon.dungeonsxl.world.DResourceWorld;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class ProgressTrigger extends AbstractTrigger {

    private DResourceWorld floor;
    private int floorCount;
    private int waveCount;

    // Unused
    public ProgressTrigger(DungeonsAPI api, TriggerListener owner, LogicalExpression expression, String value) {
        this(api, owner, expression, value, NumberUtil.parseInt(value.split("/")[0]), NumberUtil.parseInt(value.split("/")[1]));
    }

    public ProgressTrigger(DungeonsAPI api, TriggerListener owner, LogicalExpression expression, String value, int floorCount, int waveCount) {
        super(api, owner, expression, value);
        this.floorCount = floorCount;
        this.waveCount = waveCount;
    }

    @Override
    public char getKey() {
        return TriggerTypeKey.PROGRESS;
    }

    /* Getters and setters */
    /**
     * @return the specific floor that must be finished
     */
    public DResourceWorld getFloor() {
        return floor;
    }

    /**
     * @param floor the specific floor to set
     */
    public void setFloor(DResourceWorld floor) {
        this.floor = floor;
    }

    /**
     * @return the floor count to trigger
     */
    public int getFloorCount() {
        return floorCount;
    }

    /**
     * @param floorCount the floor count to set
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
     * @param waveCount the wave count to set
     */
    public void setWaveCount(int waveCount) {
        this.waveCount = waveCount;
    }

    /* Actions */
    @Override
    public void onTrigger(boolean switching) {
        setTriggered(true);
    }

    /* Statics */
    public static ProgressTrigger getOrCreate(DungeonsAPI api, TriggerListener owner, LogicalExpression expression, String value) {
        String[] values = value.split("/");
        int floorCount = NumberUtil.parseInt(values[0]);
        int waveCount = NumberUtil.parseInt(values[1]);
        if (floorCount == 0 & waveCount == 0 || floorCount < 0 || waveCount < 0) {
            return null;
        }
        return new ProgressTrigger(api, owner, expression, value, floorCount, waveCount);
    }

}
