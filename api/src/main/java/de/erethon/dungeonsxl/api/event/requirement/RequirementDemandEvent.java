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
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Fired when a {@link Requirement} is demanded from a player.
 * <p>
 * This is fired for all requirements, even for those that do not demand anything from the player.
 *
 * @author Daniel Saukel
 */
public class RequirementDemandEvent extends RequirementEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private Player player;
    private boolean keepInventory;

    public RequirementDemandEvent(Requirement requirement, Dungeon dungeon, Player player, boolean keepInventory) {
        super(requirement, dungeon);
        this.player = player;
        this.keepInventory = keepInventory;
    }

    /**
     * Returns the player who pays the requirement.
     *
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Sets the player who pays the requirement.
     *
     * @param player the player
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * If the player's state - including his inventory, EXP etc. - is kept.
     *
     * @return if the player's state is kept
     */
    public boolean isInventoryKept() {
        return keepInventory;
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
        return getClass().getSimpleName() + "{requirement=" + requirement + "; player=" + player + "; keepInventory=" + keepInventory + "}";
    }

}
