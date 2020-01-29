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
 * A sign that performs a specific action every time it is triggered.
 * <p>
 * For example, a classes sign with the default interact trigger sets your class every time you punch it.
 *
 * @author Daniel Saukel
 */
public abstract class Button extends AbstractDSign {

    protected Button(DungeonsAPI api, Sign sign, String[] lines, GameWorld gameWorld) {
        super(api, sign, lines, gameWorld);
    }

    public void push() {
        getGameWorld().getPlayers().forEach(p -> push(p.getPlayer()));
    }

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
     * @param player the player
     */
    @Override
    public void trigger(Player player) {
        push(player);
    }

}
