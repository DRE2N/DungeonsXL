/*
 * Copyright (C) 2014-2022 Daniel Saukel
 *
 * This library is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNULesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.erethon.dungeonsxl.api.event.requirement;

import de.erethon.dungeonsxl.api.Requirement;
import de.erethon.dungeonsxl.api.dungeon.Dungeon;
import org.bukkit.event.Event;

/**
 * Superclass for events involving {@link Requirement}s.
 *
 * @author Daniel Saukel
 */
public abstract class RequirementEvent extends Event {

    protected Requirement requirement;
    protected Dungeon dungeon;

    public RequirementEvent(Requirement requirement, Dungeon dungeon) {
        this.requirement = requirement;
        this.dungeon = dungeon;
    }

    /**
     * Returns the dungeon involved in this event.
     *
     * @return the dungeon involved in this event
     */
    public Dungeon getDungeon() {
        return dungeon;
    }

    /**
     * Returns the requirement involved in this event.
     *
     * @return the requirement involved in this event
     */
    public Requirement getRequirement() {
        return requirement;
    }

    /**
     * Sets the requirement involved in this event.
     *
     * @param requirement the requirement
     */
    public void setRequirement(Requirement requirement) {
        this.requirement = requirement;
    }

}
