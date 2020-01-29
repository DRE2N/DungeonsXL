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

import de.erethon.commons.player.PlayerCollection;
import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.Trigger;
import de.erethon.dungeonsxl.api.world.GameWorld;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * A {@link DungeonSign} that changes its state when triggered.
 *
 * @author Daniel Saukel
 */
public abstract class Deactivatable extends AbstractDSign {

    protected boolean active;
    private PlayerCollection playersActivated = new PlayerCollection();

    protected Deactivatable(DungeonsAPI api, Sign sign, String[] lines, GameWorld gameWorld) {
        super(api, sign, lines, gameWorld);
    }

    /**
     * Sets the state to active.
     * <p>
     * This might not be meaningful if the sign uses {@link #activate(org.bukkit.entity.Player)}.
     */
    public void activate() {
        active = true;
    }

    /**
     * Sets the state to active for the given player.
     *
     * @param player the player
     * @return if the action was successful
     */
    public boolean activate(Player player) {
        return playersActivated.add(player);
    }

    /**
     * Sets the state to inactive.
     * <p>
     * This might not be meaningful if the sign uses {@link #deactivate(org.bukkit.entity.Player)}.
     */
    public void deactivate() {
        active = false;
    }

    /**
     * Sets the state to inactive for the given player.
     *
     * @param player the player
     * @return if the action was successful
     */
    public boolean deactivate(Player player) {
        return playersActivated.remove(player);
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
     *
     * @param player the player
     * @return if the sign is activated for the given player
     */
    public boolean isActive(Player player) {
        return playersActivated.contains(player);
    }

    @Override
    public void update() {
        if (isErroneous()) {
            return;
        }

        for (Trigger trigger : getTriggers()) {
            if (!trigger.isTriggered()) {
                deactivate();
                return;
            }

            if (trigger.getPlayer() == null) {
                continue;
            }

            if (activate(trigger.getPlayer())) {
                return;
            }
        }

        activate();
    }

}
