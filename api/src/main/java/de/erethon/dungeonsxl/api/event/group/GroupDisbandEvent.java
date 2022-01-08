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
 * Fired when a group is disbanded.
 *
 * @author Daniel Saukel
 */
public class GroupDisbandEvent extends GroupEvent implements Cancellable {

    public enum Cause {

        COMMAND,
        DUNGEON_FINISHED,
        GROUP_ADAPTER,
        GROUP_IS_EMPTY,
        LOST,
        CUSTOM

    }

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private GlobalPlayer disbander;
    private Cause cause;

    public GroupDisbandEvent(PlayerGroup group, Cause cause) {
        super(group);
        this.cause = cause;
    }

    public GroupDisbandEvent(PlayerGroup group, GlobalPlayer disbander, Cause cause) {
        super(group);
        this.disbander = disbander;
        this.cause = cause;
    }

    /**
     * The player who disbanded the group.
     * <p>
     * This is null if the cause is {@link Cause#DUNGEON_FINISHED}, {@link Cause#GROUP_ADAPTER}, {@link Cause#LOST} or {@link Cause#CUSTOM}.
     *
     * @return the player who disbanded the group
     */
    public GlobalPlayer getDisbander() {
        return disbander;
    }

    /**
     * Returns the cause for the group deletion.
     *
     * @return the cause for the group deletion
     */
    public Cause getCause() {
        return cause;
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
        return getClass().getSimpleName() + "{group=" + group + "; disbander=" + disbander + "; cause=" + cause + "}";
    }

}
