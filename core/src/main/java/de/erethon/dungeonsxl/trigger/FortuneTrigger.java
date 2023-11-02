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
import java.util.Random;

/**
 * @author Daniel Saukel
 */
public class FortuneTrigger extends AbstractTrigger {

    private double chance = 0;

    public FortuneTrigger(DungeonsAPI api, TriggerListener owner, LogicalExpression expression, String value) {
        super(api, owner, expression, value);
        this.chance = NumberUtil.parseDouble(value, chance);
    }

    @Override
    public char getKey() {
        return TriggerTypeKey.FORTUNE;
    }

    /* Getters and setters */
    /**
     * @return the chance
     */
    public double getChance() {
        return chance;
    }

    /**
     * @param chance the chance to set
     */
    public void setChance(double chance) {
        this.chance = chance;
    }

    /* Actions */
    @Override
    public void onTrigger(boolean switching) {
        int random = new Random().nextInt(100);
        if (chance * 100 >= random) {
            setTriggered(true);
        }
    }

}
