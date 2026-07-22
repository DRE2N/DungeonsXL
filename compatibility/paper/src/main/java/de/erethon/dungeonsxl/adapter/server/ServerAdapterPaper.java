/*
 * Copyright (C) 2012-2013 Frank Baumann; 2015-2026 Daniel Saukel
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
package de.erethon.dungeonsxl.adapter.server;

import org.bukkit.GameRules;
import org.bukkit.World;

/**
 * @author Daniel Saukel
 */
public class ServerAdapterPaper implements ServerAdapter {

    @Override
    public void setFireSpreadAroundPlayer(World world, int value) {
        world.setGameRule(GameRules.FIRE_SPREAD_RADIUS_AROUND_PLAYER, value);
    }

}
