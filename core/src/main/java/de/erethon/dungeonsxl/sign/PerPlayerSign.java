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
package de.erethon.dungeonsxl.sign;

import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.world.DGameWorld;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public abstract class PerPlayerSign extends DSign {

    private Set<UUID> triggered = new HashSet<>();

    public PerPlayerSign(DungeonsXL plugin, Sign sign, String[] lines, DGameWorld gameWorld) {
        super(plugin, sign, lines, gameWorld);
    }

    @Override
    public boolean onPlayerTrigger(Player player) {
        return triggered.add(player.getUniqueId());
    }

    /**
     * @param player the player to check
     * @return true if the player already triggered the sign
     */
    public boolean isTriggeredByPlayer(Player player) {
        return triggered.contains(player.getUniqueId());
    }

}
