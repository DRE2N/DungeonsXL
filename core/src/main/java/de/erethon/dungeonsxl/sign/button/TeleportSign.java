/*
 * Copyright (C) 2012-2023 Frank Baumann
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
package de.erethon.dungeonsxl.sign.button;

import de.erethon.bedrock.misc.BlockUtil;
import de.erethon.bedrock.misc.NumberUtil;
import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.sign.Button;
import de.erethon.dungeonsxl.api.world.InstanceWorld;
import de.erethon.dungeonsxl.player.DPermission;
import de.erethon.dungeonsxl.sign.LocationSign;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * @author Milan Albrecht, Daniel Saukel
 */
public class TeleportSign extends Button implements LocationSign {

    private Location targetLocation;

    public TeleportSign(DungeonsAPI api, Sign sign, String[] lines, InstanceWorld instance) {
        super(api, sign, lines, instance);
    }

    @Override
    public Location getTargetLocation() {
        return targetLocation;
    }

    @Override
    public void setTargetLocation(Location location) {
        this.targetLocation = location;
    }

    @Override
    public String getName() {
        return "Teleport";
    }

    @Override
    public String getBuildPermission() {
        return DPermission.SIGN.getNode() + ".teleport";
    }

    @Override
    public boolean isOnDungeonInit() {
        return false;
    }

    @Override
    public boolean isProtected() {
        return false;
    }

    @Override
    public boolean isSetToAir() {
        return true;
    }

    @Override
    public boolean validate() {
        for (int i = 1; i <= 2; i++) {
            if (!getLine(i).isEmpty()) {
                if (BlockUtil.lettersToYaw(getLine(i)) == -1) {
                    String[] loc = getLine(i).split(",");
                    if (loc.length != 3) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void initialize() {
        LocationSign.super.initialize();
        for (int i = 1; i <= 2; i++) {
            if (getLine(i).isEmpty()) {
                continue;
            }
            Integer yaw = BlockUtil.lettersToYaw(getLine(i));
            if (yaw != null) {
                targetLocation.setYaw(yaw);
            } else {
                String[] loc = getLine(i).split(",");
                if (loc.length == 3) {
                    double x = NumberUtil.parseDouble(loc[0]);
                    double y = NumberUtil.parseDouble(loc[1]);
                    double z = NumberUtil.parseDouble(loc[2]);

                    // If number is even, add +0.5 to teleport to the middle of the block
                    if (!loc[0].contains(".")) {
                        x += 0.5;
                    }
                    if (!loc[2].contains(".")) {
                        z += 0.5;
                    }

                    targetLocation.setX(x);
                    targetLocation.setY(y);
                    targetLocation.setZ(z);
                }
            }
        }
    }

    @Override
    public boolean push(Player player) {
        if (targetLocation != null) {
            player.teleport(targetLocation);
            return true;
        } else {
            return false;
        }
    }

}
