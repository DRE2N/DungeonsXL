/*
 * Copyright (C) 2012-2013 Frank Baumann; 2015-2026 Daniel Saukel
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

import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.mob.MobSet;
import de.erethon.dungeonsxl.api.trigger.AbstractTrigger;
import de.erethon.dungeonsxl.api.trigger.LogicalExpression;
import de.erethon.dungeonsxl.api.trigger.TriggerListener;
import de.erethon.dungeonsxl.api.trigger.TriggerTypeKey;
import de.erethon.xlib.util.NumberUtil;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class MobTrigger extends AbstractTrigger {

    private Double modifier;
    private MobSet mobSet;

    public MobTrigger(DungeonsAPI api, TriggerListener owner, LogicalExpression expression, String value) {
        super(api, owner, expression, value);
        String[] values = value.split("@");
        int i = 0;
        if (values.length == 2) {
            i++;
            modifier = NumberUtil.parseDouble(values[0], 1.0);
        }
        mobSet = getGameWorld().getOrCreateMobSet(values[i]);
    }

    @Override
    public char getKey() {
        return TriggerTypeKey.MOB;
    }

    @Override
    public void onTrigger(boolean switching) {
        setTriggered(true);
    }

}
