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
import java.util.Map;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class SignTrigger extends Trigger {

    private static Map<GameWorld, ArrayList<SignTrigger>> triggers = new HashMap<>();

    private TriggerType type = TriggerTypeDefault.SIGN;

    private int stId;

    public SignTrigger(int stId) {
        this.stId = stId;
    }

    public void onTrigger(boolean enable) {
        TriggerActionEvent event = new TriggerActionEvent(this);
        plugin.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        if (enable != isTriggered()) {
            setTriggered(enable);
            updateDSigns();
        }
    }

    @Override
    public void register(GameWorld gameWorld) {
        if (!hasTriggers(gameWorld)) {
            ArrayList<SignTrigger> list = new ArrayList<>();
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

    public static SignTrigger getOrCreate(int id, GameWorld gameWorld) {
        SignTrigger trigger = get(id, gameWorld);
        if (trigger != null) {
            return trigger;
        }
        return new SignTrigger(id);
    }

    public static SignTrigger get(int id, GameWorld gameWorld) {
        if (hasTriggers(gameWorld)) {
            for (SignTrigger trigger : triggers.get(gameWorld)) {
                if (trigger.stId == id) {
                    return trigger;
                }
            }
        }
        return null;
    }

    public static boolean hasTriggers(GameWorld gameWorld) {
        return !triggers.isEmpty() && triggers.containsKey(gameWorld);
    }

}
