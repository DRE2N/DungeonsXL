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
package io.github.dre2n.dungeonsxl.event.dgroup;

import io.github.dre2n.dungeonsxl.player.DGroup;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * @author Daniel Saukel
 */
public class DGroupCreateEvent extends DGroupEvent implements Cancellable {

    public enum Cause {

        ANNOUNCER,
        COMMAND,
        GROUP_SIGN,
        CUSTOM

    }

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private Player creator;

    private Cause cause;

    public DGroupCreateEvent(DGroup dGroup, Player creator, Cause cause) {
        super(dGroup);
        this.creator = creator;
        this.cause = cause;
    }

    /**
     * @return the creator
     */
    public Player getCreator() {
        return creator;
    }

    /**
     * @param creator
     * the creator to set
     */
    public void setCreator(Player creator) {
        this.creator = creator;
    }

    /**
     * @return the cause
     */
    public Cause getCause() {
        return cause;
    }

    /**
     * @param cause
     * the cause to set
     */
    public void setCause(Cause cause) {
        this.cause = cause;
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

}
