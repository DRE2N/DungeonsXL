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
package de.erethon.dungeonsxl.api.event.group;

import de.erethon.dungeonsxl.api.dungeon.Dungeon;
import de.erethon.dungeonsxl.api.player.PlayerGroup;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Fired when a group finishs a {@link Dungeon}, which means the end floor of a dungeon.
 * <p>
 * Do not confuse this with {@link de.erethon.dungeonsxl.api.event.player.GamePlayerFinishEvent}. GamePlayerFinishEvent is fired when a player triggers an end
 * sign, while GroupFinishDungeonEvent is triggered when all group members have triggered the end sign and the game actually ends.
 *
 * @author Daniel Saukel
 */
public class GroupFinishDungeonEvent extends GroupEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private Dungeon dungeon;

    public GroupFinishDungeonEvent(PlayerGroup group, Dungeon dungeon) {
        super(group);
        this.dungeon = dungeon;
    }

    /**
     * Returns the dungeon the group was playing.
     *
     * @return the dungeon the group was playing
     */
    public Dungeon getDungeon() {
        return dungeon;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{group=" + group + "; dungeon=" + dungeon + "}";
    }

}
