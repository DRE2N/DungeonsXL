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
 * @author Daniel Saukel
 */
public class GroupPlayerKickEvent extends GroupEvent implements Cancellable {

    public enum Cause {

        COMMAND,
        /**
         * When the player is kicked because he does not have any lives left.
         */
        DEATH,
        /**
         * When a player is kicked from a group to mirror the state of a party plugin.
         *
         * @see de.erethon.dungeonsxl.api.player.GroupAdapter
         */
        GROUP_ADAPTER,
        OFFLINE,
        /**
         * When the time for the group to reach a certain state expired.
         */
        TIME_EXPIRED,
        CUSTOM

    }

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private GlobalPlayer player;
    private Cause cause;

    public GroupPlayerKickEvent(PlayerGroup group, GlobalPlayer player, Cause cause) {
        super(group);
        this.player = player;
        this.cause = cause;
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
     * Returns the cause of the kick.
     *
     * @return the cause of the kick
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
        return getClass().getSimpleName() + "{group=" + group + "; player=" + player + "; cause=" + cause + "}";
    }

}
