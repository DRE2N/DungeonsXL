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

import de.erethon.caliburn.item.VanillaItem;
import de.erethon.commons.misc.BlockUtil;
import de.erethon.commons.misc.NumberUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.world.DGameWorld;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * @author Milan Albrecht, Daniel Saukel
 */
public class TeleportSign extends LocationSign {

    public TeleportSign(DungeonsXL plugin, Sign sign, String[] lines, DGameWorld gameWorld) {
        super(plugin, sign, lines, gameWorld);
    }

    @Override
    public boolean check() {
        for (int i = 1; i <= 2; i++) {
            if (!lines[i].isEmpty()) {
                if (BlockUtil.lettersToYaw(lines[i]) == -1) {
                    String[] loc = lines[i].split(",");
                    if (loc.length != 3) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void onInit() {
        super.onInit();
        for (int i = 1; i <= 2; i++) {
            if (lines[i].isEmpty()) {
                continue;
            }
            Integer yaw = BlockUtil.lettersToYaw(lines[i]);
            if (yaw != null) {
                location.setYaw(yaw);
            } else {
                String[] loc = lines[i].split(",");
                if (loc.length == 3) {
                    double x = NumberUtil.parseDouble(loc[0]);
                    double y = NumberUtil.parseDouble(loc[1]);
                    double z = NumberUtil.parseDouble(loc[2]);

                    // If round number, add 0.5 to tp to middle of block
                    x = NumberUtil.parseInt(loc[0]) + 0.5;
                    z = NumberUtil.parseInt(loc[2]) + 0.5;

                    location.setX(x);
                    location.setY(y);
                    location.setZ(z);
                }
            }
        }
        getSign().getBlock().setType(VanillaItem.AIR.getMaterial());
    }

    @Override
    public void onTrigger() {
        if (location != null) {
            for (Player player : getGameWorld().getWorld().getPlayers()) {
                player.teleport(location);
            }
        }
    }

    @Override
    public boolean onPlayerTrigger(Player player) {
        if (location != null) {
            player.teleport(location);
        }
        return true;
    }

    @Override
    public DSignType getType() {
        return DSignTypeDefault.TELEPORT;
    }

}
