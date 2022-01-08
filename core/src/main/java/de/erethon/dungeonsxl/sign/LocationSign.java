/*
 * Copyright (C) 2012-2022 Frank Baumann
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
import de.erethon.dungeonsxl.api.sign.DungeonSign;
import de.erethon.dungeonsxl.util.commons.misc.BlockUtil;
import org.bukkit.Location;

/**
 * @author Daniel Saukel
 */
public interface LocationSign extends DungeonSign {

    @Override
    default void initialize() {
        double x = getSign().getX() + 0.5;
        double y = getSign().getY();
        double z = getSign().getZ() + 0.5;
        float yaw = BlockUtil.blockFaceToYaw(DungeonsXL.BLOCK_ADAPTER.getFacing(getSign().getBlock()).getOppositeFace());
        float pitch = 0;
        setLocation(new Location(getGameWorld().getWorld(), x, y, z, yaw, pitch));
    }

    Location getLocation();

    void setLocation(Location location);

}
