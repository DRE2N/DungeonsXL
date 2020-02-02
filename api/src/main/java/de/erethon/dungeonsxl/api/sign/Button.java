/*
 * Copyright (C) 2014-2020 Daniel Saukel
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

import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.Trigger;
import de.erethon.dungeonsxl.api.world.GameWorld;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * A sign that performs a specific action every time it is triggered. It can have, but typically does not have a state. Consider using {@link Deactivatable} for
 * signs that change themselves when they are triggered.
 * <p>
 * For example, a classes sign with the default interact trigger sets your class every time you punch it.
 *
 * @author Daniel Saukel
 */
public abstract class Button extends AbstractDSign {

    protected Button(DungeonsAPI api, Sign sign, String[] lines, GameWorld gameWorld) {
        super(api, sign, lines, gameWorld);
    }

    /**
     * When the sign is triggered without one particular player being the cause.
     * <p>
     * <b>Note that the default implementation of {@link #push(org.bukkit.entity.Player)} assumes that the sign does not need player specific behavior and
     * simply calls this method, while the default implementation of this method assumes that the sign should perform {@link #push(org.bukkit.entity.Player)}
     * for each player in the game world. This leaves a button sign with a stackoverflow if not one of both methods at least is overriden. Consider using a
     * {@link Passive} sign instead if you need a sign that simply marks places and ignores being triggered.</b>
     */
    public void push() {
        getGameWorld().getPlayers().forEach(p -> push(p.getPlayer()));
    }

    /**
     * When the sign is triggered.
     * <p>
     * This is the default {@link #trigger(org.bukkit.entity.Player)} behavior.
     * <p>
     * <b>Note that the default implementation of this method assumes that the sign does not need player specific behavior and simply calls {@link #push()},
     * while the default implementation of {@link #push()} assumes that the sign should perform {@link #push(org.bukkit.entity.Player)} for each player in the
     * game world. This leaves a button sign with a stackoverflow if not one of both methods at least is overriden. Consider using a {@link Passive} sign
     * instead if you need a sign that simply marks places and ignores being triggered.</b>
     *
     * @param player the player who triggered the sign
     * @return if the action is done successfully
     */
    public boolean push(Player player) {
        push();
        return true;
    }

    @Override
    public void update() {
        if (isErroneous()) {
            return;
        }

        for (Trigger trigger : getTriggers()) {
            if (!trigger.isTriggered()) {
                return;
            }

            if (trigger.getPlayer() == null) {
                continue;
            }

            if (push(trigger.getPlayer())) {
                return;
            }
        }

        push();
    }

    /**
     * This is the same as {@link #push(org.bukkit.entity.Player)}.
     *
     * @param player the player who triggered the sign or null if no one in particular triggered it
     */
    @Override
    public void trigger(Player player) {
        push(player);
    }

}
