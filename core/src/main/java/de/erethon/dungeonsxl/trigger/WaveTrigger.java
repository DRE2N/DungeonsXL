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

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class WaveTrigger extends AbstractTrigger {

    private double mustKillRate = 1;

    public WaveTrigger(DungeonsAPI api, TriggerListener owner, LogicalExpression expression, String value) {
        super(api, owner, expression, value);
        mustKillRate = NumberUtil.parseDouble(value, mustKillRate);
    }

    @Override
    public char getKey() {
        return TriggerTypeKey.WAVE;
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

    @Override
    public void onTrigger(boolean switching) {
        setTriggered(true);
    }

    @Override
    public void postTrigger() {
        setTriggered(false);
    }

}
