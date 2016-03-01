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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Daniel Saukel
 */
public class Triggers {

    private List<TriggerType> triggers = new ArrayList<>();

    public Triggers() {
        triggers.addAll(Arrays.asList(TriggerTypeDefault.values()));
    }

    /**
     * @return the trigger which has the identifier
     */
    public TriggerType getByIdentifier(String identifier) {
        for (TriggerType trigger : triggers) {
            if (trigger.getIdentifier().equalsIgnoreCase(identifier)) {
                return trigger;
            }
        }

        return null;
    }

    /**
     * @return the triggers
     */
    public List<TriggerType> getTriggers() {
        return triggers;
    }

    /**
     * @param trigger
     * the triggers to add
     */
    public void addTrigger(TriggerType trigger) {
        triggers.add(trigger);
    }

    /**
     * @param trigger
     * the trigger to remove
     */
    public void removeTrigger(TriggerType trigger) {
        triggers.remove(trigger);
    }

}
