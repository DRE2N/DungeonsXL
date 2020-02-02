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
import org.bukkit.Bukkit;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class MobTrigger extends Trigger {

    private TriggerType type = TriggerTypeDefault.MOB;

    private String name;

    public MobTrigger(String name) {
        this.name = name;
    }

    public void onTrigger() {
        TriggerActionEvent event = new TriggerActionEvent(this);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        setTriggered(true);
        updateDSigns();
    }

    @Override
    public TriggerType getType() {
        return type;
    }

    /* Statics */
    public static MobTrigger getOrCreate(String name, DGameWorld gameWorld) {
        MobTrigger trigger = getByName(name, gameWorld);
        if (trigger != null) {
            return trigger;
        }
        return new MobTrigger(name);
    }

    public static MobTrigger getByName(String name, DGameWorld gameWorld) {
        for (Trigger uncasted : gameWorld.getTriggers(TriggerTypeDefault.MOB)) {
            MobTrigger trigger = (MobTrigger) uncasted;
            if (trigger.name.equalsIgnoreCase(name)) {
                return trigger;
            }
        }
        return null;
    }

}
