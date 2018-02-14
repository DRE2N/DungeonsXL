/*
 * Copyright (C) 2012-2018 Frank Baumann
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

import io.github.dre2n.dungeonsxl.DungeonsXL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;

/**
 * TriggerType instance manager.
 *
 * @author Daniel Saukel
 */
public class TriggerTypeCache {

    private List<TriggerType> types = new ArrayList<>();

    public TriggerTypeCache() {
        types.addAll(Arrays.asList(TriggerTypeDefault.values()));
        Bukkit.getPluginManager().registerEvents(new TriggerListener(), DungeonsXL.getInstance());
    }

    /**
     * @return the trigger which has the identifier
     */
    public TriggerType getByIdentifier(String identifier) {
        for (TriggerType type : types) {
            if (type.getIdentifier().equalsIgnoreCase(identifier)) {
                return type;
            }
        }

        return null;
    }

    /**
     * @return the trigger types
     */
    public List<TriggerType> getTriggers() {
        return types;
    }

    /**
     * @param type
     * the type to add
     */
    public void addTrigger(TriggerType type) {
        types.add(type);
    }

    /**
     * @param type
     * the type to remove
     */
    public void removeTrigger(TriggerType type) {
        types.remove(type);
    }

}
