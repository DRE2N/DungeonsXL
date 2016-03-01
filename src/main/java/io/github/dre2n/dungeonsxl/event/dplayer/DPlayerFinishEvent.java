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
package io.github.dre2n.dungeonsxl.event.dplayer;

import io.github.dre2n.dungeonsxl.player.DPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * @author Daniel Saukel
 */
public class DPlayerFinishEvent extends DPlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private boolean first;
    private boolean hasToWait;

    public DPlayerFinishEvent(DPlayer dPlayer, boolean first, boolean hasToWait) {
        super(dPlayer);
        this.first = first;
        this.hasToWait = hasToWait;
    }

    /**
     * @return the first
     */
    public boolean isFirst() {
        return first;
    }

    /**
     * @param first
     * the first to set
     */
    public void setFirst(boolean first) {
        this.first = first;
    }

    /**
     * @return the hasToWait
     */
    public boolean getHasToWait() {
        return hasToWait;
    }

    /**
     * @param hasToWait
     * the hasToWait to set
     */
    public void setHasToWait(boolean hasToWait) {
        this.hasToWait = hasToWait;
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
