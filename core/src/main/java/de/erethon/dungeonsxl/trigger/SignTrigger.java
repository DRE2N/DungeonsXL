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
import de.erethon.dungeonsxl.api.world.GameWorld;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class SignTrigger extends AbstractTrigger {

    private int id;

    public SignTrigger(DungeonsAPI api, TriggerListener owner, LogicalExpression expression, String value) {
        super(api, owner, expression, value);
        char key = expression.getText().charAt(0);
        int i = Character.toUpperCase(key) == getKey() ? 1 : 0;
        id = NumberUtil.parseInt(expression.getText().substring(i));
    }

    @Override
    public char getKey() {
        return TriggerTypeKey.GENERIC;
    }

    @Override
    public void onTrigger(boolean switching) {
        if (switching != isTriggered()) {
            setTriggered(switching);
        }
    }

    /* Statics */
    public static SignTrigger getById(int id, GameWorld gameWorld) {
        if (gameWorld == null) {
            return null;
        }
        for (Trigger uncasted : gameWorld.getTriggers()) {
            if (!(uncasted instanceof SignTrigger)) {
                continue;
            }
            SignTrigger trigger = (SignTrigger) uncasted;
            if (id == trigger.id) {
                return trigger;
            }
        }
        return null;
    }

}
