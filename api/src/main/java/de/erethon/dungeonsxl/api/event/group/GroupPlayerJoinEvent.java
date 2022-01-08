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

import de.erethon.dungeonsxl.api.player.GlobalPlayer;
import de.erethon.dungeonsxl.api.player.PlayerGroup;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Fired when a player joins a DungeonsXL group.
 *
 * @author Daniel Saukel
 */
public class GroupPlayerJoinEvent extends GroupEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private GlobalPlayer player;
    private boolean creator;

    public GroupPlayerJoinEvent(PlayerGroup group, GlobalPlayer player, boolean creator) {
        super(group);
        this.player = player;
        this.creator = creator;
    }

    /**
     * Returns the player who is joining the group.
     *
     * @return the player who is joining the group
     */
    public GlobalPlayer getPlayer() {
        return player;
    }

    /**
     * Returns if the player is the creator of the group.
     *
     * @return if the player is the creator of the group
     */
    public boolean isCreator() {
        return creator;
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
        return getClass().getSimpleName() + "{group=" + group + "; player=" + player + "; creator=" + creator + "}";
    }

}
