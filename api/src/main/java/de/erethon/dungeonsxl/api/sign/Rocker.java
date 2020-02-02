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
import de.erethon.dungeonsxl.api.world.GameWorld;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * A sign that has a <i>deactivated</i> and an <i>activated state</i> and can switch between these two.
 * <p>
 * For example, if a door sign is activated, the door opens - if it is deactivated, the door closes. The state may be set for the whole game world or for the
 * player who triggered the sign depending on the context.
 *
 * @author Daniel Saukel
 */
public abstract class Rocker extends Deactivatable {

    protected Rocker(DungeonsAPI api, Sign sign, String[] lines, GameWorld gameWorld) {
        super(api, sign, lines, gameWorld);
    }

    /**
     * Activates the sign if it is not yet active and deactivates it if it is already active.
     *
     * @param player the player who triggered the sign or null if no one in particular triggered it
     */
    @Override
    public void trigger(Player player) {
        if (!isActive()) {
            activate(player);
        } else {
            deactivate();
        }
    }

}
