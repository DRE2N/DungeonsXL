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
package de.erethon.dungeonsxl.event.dsign;

import de.erethon.dungeonsxl.sign.DSign;
import de.erethon.dungeonsxl.world.DGameWorld;
import org.bukkit.block.Sign;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * @author Daniel Saukel
 */
public class DSignRegistrationEvent extends DSignEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private Sign sign;
    private DGameWorld gameWorld;

    public DSignRegistrationEvent(Sign sign, DGameWorld gameWorld, DSign dSign) {
        super(dSign);
        this.sign = sign;
        this.gameWorld = gameWorld;
    }

    /**
     * @return the sign
     */
    public Sign getSign() {
        return sign;
    }

    /**
     * @param sign the sign to set
     */
    public void setSign(Sign sign) {
        this.sign = sign;
    }

    /**
     * @return the gameWorld
     */
    public DGameWorld getGameWorld() {
        return gameWorld;
    }

    /**
     * @param gameWorld the gameWorld to set
     */
    public void setGameWorld(DGameWorld gameWorld) {
        this.gameWorld = gameWorld;
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
