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
package de.erethon.dungeonsxl.event.dgroup;

import de.erethon.dungeonsxl.player.DGroup;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * @author Daniel Saukel
 */
public class DGroupDisbandEvent extends DGroupEvent implements Cancellable {

    public enum Cause {

        COMMAND,
        DUNGEON_FINISHED,
        GROUP_IS_EMPTY,
        LOST,
        CUSTOM

    }

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private Player disbander;

    private Cause cause;

    public DGroupDisbandEvent(DGroup dGroup, Cause cause) {
        super(dGroup);
        this.cause = cause;
    }

    public DGroupDisbandEvent(DGroup dGroup, Player disbander, Cause cause) {
        super(dGroup);
        this.disbander = disbander;
        this.cause = cause;
    }

    /**
     * @return the disbander
     */
    public Player getDisbander() {
        return disbander;
    }

    /**
     * @param disbander the disbander to set
     */
    public void setDisbander(Player disbander) {
        this.disbander = disbander;
    }

    /**
     * @return the cause
     */
    public Cause getCause() {
        return cause;
    }

    /**
     * @param cause the cause to set
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
