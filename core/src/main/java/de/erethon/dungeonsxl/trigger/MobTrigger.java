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

import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.trigger.AbstractTrigger;
import de.erethon.dungeonsxl.api.trigger.LogicalExpression;
import de.erethon.dungeonsxl.api.trigger.Trigger;
import de.erethon.dungeonsxl.api.trigger.TriggerListener;
import de.erethon.dungeonsxl.api.trigger.TriggerTypeKey;
import de.erethon.dungeonsxl.api.world.GameWorld;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class MobTrigger extends AbstractTrigger {

    private String name;

    public MobTrigger(DungeonsAPI api, TriggerListener owner, LogicalExpression expression, String value) {
        super(api, owner, expression, value);
        name = value;
    }

    @Override
    public char getKey() {
        return TriggerTypeKey.MOB;
    }

    @Override
    public void onTrigger(boolean switching) {
        setTriggered(true);
    }

    /* Statics */
    public static MobTrigger getByName(String name, GameWorld gameWorld) {
        if (name == null || gameWorld == null) {
            return null;
        }
        for (Trigger uncasted : gameWorld.getTriggers()) {
            if (!(uncasted instanceof MobTrigger)) {
                continue;
            }
            MobTrigger trigger = (MobTrigger) uncasted;
            if (name.equalsIgnoreCase(trigger.name)) {
                return trigger;
            }
        }
        return null;
    }

}
