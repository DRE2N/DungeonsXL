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
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Fired when it is checked if a player fulfills a {@link Requirement} realized through the Requirement API.
 * <p>
 * Note that this is usually called twice per player: When he tries to enter a dungeon and when he tries to start a game.
 *
 * @author Daniel Saukel
 */
public class RequirementCheckEvent extends RequirementEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private Player player;
    private boolean keepInventory;
    private BaseComponent[] checkMessage;

    public RequirementCheckEvent(Requirement requirement, Dungeon dungeon, Player player, boolean keepInventory) {
        super(requirement, dungeon);
        this.player = player;
        this.keepInventory = keepInventory;
        checkMessage = requirement.getCheckMessage(player);
    }

    /**
     * Returns the checked player.
     *
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Sets the checked player.
     *
     * @param player the player
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Returns the message that will be sent to the player to inform him what he needs in order to fulfill the requirement if there is a requirement that he
     * does not fulfill.
     *
     * @return the message that will be sent to the player to inform him what he needs in order to fulfill the requirement if there is a requirement that he
     *         does not fulfill
     */
    public BaseComponent[] getCheckMessage() {
        return checkMessage;
    }

    /**
     * Sets the message that will be sent to the player to inform him what he needs in order to fulfill the requirement if there is a a requirement that he does
     * not fulfill.
     *
     * @param checkMessage the message component array
     */
    public void setCheckMessage(BaseComponent[] checkMessage) {
        this.checkMessage = checkMessage;
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
