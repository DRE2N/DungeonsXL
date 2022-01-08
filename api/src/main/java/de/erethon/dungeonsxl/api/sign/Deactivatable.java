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
package de.erethon.dungeonsxl.api.sign;

import de.erethon.commons.player.PlayerCollection;
import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.world.InstanceWorld;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * A {@link DungeonSign} that changes its state when triggered.
 *
 * @author Daniel Saukel
 */
public abstract class Deactivatable extends AbstractDSign {

    protected boolean active;
    protected PlayerCollection playersActivated = new PlayerCollection();

    protected Deactivatable(DungeonsAPI api, Sign sign, String[] lines, InstanceWorld instance) {
        super(api, sign, lines, instance);
    }

    /**
     * Sets the state to active.
     * <p>
     * <b>Note that the default implementation of {@link #activate(org.bukkit.entity.Player)} assumes that the sign does not need player specific behavior and
     * simply calls this method, while the default implementation of this method assumes that the sign should perform
     * {@link #activate(org.bukkit.entity.Player)} for each player in the game world. This leaves a button sign with a stackoverflow if not one of both methods
     * at least is overriden. Consider using a {@link Passive} sign instead if you need a sign that simply marks places and ignores being triggered. An
     * implementation that does not need player specific behavior should set {@link #active} to true.</b>
     */
    public void activate() {
        getGameWorld().getPlayers().forEach(p -> activate(p.getPlayer()));
    }

    /**
     * Sets the state to active for the given player.
     * <p>
     * <b>Note that the default implementation of this method assumes that the sign does not need player specific behavior and simply calls {@link #activate()},
     * while the default implementation of {@link #activate()} assumes that the sign should perform {@link #activate(org.bukkit.entity.Player)} for each player
     * in the game world. This leaves a deactivatable sign with a stackoverflow if not one of both methods at least is overriden. Consider using a
     * {@link Passive} sign instead if you need a sign that simply marks places and ignores being triggered. An implementation that needs player specific
     * behavior should add the player to the {@link #playersActivated} collection.</b>
     *
     * @param player the player
     * @return if the action was successful
     */
    public boolean activate(Player player) {
        activate();
        return true;
    }

    /**
     * Sets the state to inactive.
     * <p>
     * <b>Note that the default implementation of {@link #deactivate(org.bukkit.entity.Player)} assumes that the sign does not need player specific behavior and
     * simply calls this method, while the default implementation of this method assumes that the sign should perform
     * {@link #deactivate(org.bukkit.entity.Player)} for each player in the game world. This leaves a button sign with a stackoverflow if not one of both
     * methods at least is overriden. Consider using a {@link Passive} sign instead if you need a sign that simply marks places and ignores being triggered. An
     * implementation that does not need player specific behavior should set {@link #active} to false.</b>
     */
    public void deactivate() {
        getGameWorld().getPlayers().forEach(p -> deactivate(p.getPlayer()));
    }

    /**
     * Sets the state to inactive for the given player.
     * <p>
     * <b>Note that the default implementation of this method assumes that the sign does not need player specific behavior and simply calls
     * {@link #deactivate()}, while the default implementation of {@link #deactivate()} assumes that the sign should perform
     * {@link #deactivate(org.bukkit.entity.Player)} for each player in the game world. This leaves a deactivatable sign with a stackoverflow if not one of both
     * methods at least is overriden. Consider using a {@link Passive} sign instead if you need a sign that simply marks places and ignores being triggered. An
     * implementation that needs player specific behavior should remove the player from the {@link #playersActivated} collection.</b>
     *
     * @param player the player
     * @return if the action was successful
     */
    public boolean deactivate(Player player) {
        deactivate();
        return true;
    }

    /**
     * Returns if the sign is currently in its activated state.
     * <p>
     * This might not be meaningful if the sign uses {@link #isActive(org.bukkit.entity.Player)}.
     *
     * @return if the sign is currently in its activated state
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Returns if the sign is activated for the given player.
     * <p>
     * <b>Note that the default implementation of this method assumes that the sign does not need player specific behavior and simply calls {@link #isActive()}.
     * An implementation that needs player specific behavior should check if the {@link #playersActivated} collection contains the player.</b>
     *
     * @param player the player
     * @return if the sign is activated for the given player
     */
    public boolean isActive(Player player) {
        return isActive();
    }

}
