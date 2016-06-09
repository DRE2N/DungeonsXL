/*
 * Copyright (C) 2012-2016 Frank Baumann
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
package io.github.dre2n.dungeonsxl.sign;

import io.github.dre2n.commons.util.NumberUtil;
import io.github.dre2n.dungeonsxl.world.GameWorld;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * @author Milan Albrecht
 */
public class TeleportSign extends DSign {

    private DSignType type = DSignTypeDefault.TELEPORT;

    private Location location;

    public TeleportSign(Sign sign, String[] lines, GameWorld gameWorld) {
        super(sign, lines, gameWorld);
    }

    @Override
    public boolean check() {
        String lines[] = getSign().getLines();
        for (int i = 1; i <= 2; i++) {
            if (!lines[i].isEmpty()) {
                if (letterToYaw(lines[i].charAt(0)) == -1) {
                    String[] loc = lines[i].split(",");
                    if (loc.length != 3) {
                        return false;
                    }
                    NumberUtil.parseDouble(loc[0]);
                    NumberUtil.parseDouble(loc[1]);
                    NumberUtil.parseDouble(loc[2]);
                }
            }
        }
        return true;
    }

    @Override
    public void onInit() {
        location = getSign().getLocation().add(0.5, 0, 0.5);
        location.setYaw(letterToYaw(((org.bukkit.material.Sign) getSign().getData()).getFacing().getOppositeFace().name().charAt(0)));
        String lines[] = getSign().getLines();
        for (int i = 1; i <= 2; i++) {
            if (!lines[i].isEmpty()) {
                int yaw = letterToYaw(lines[i].charAt(0));
                if (yaw != -1) {
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
        }
        getSign().getBlock().setType(Material.AIR);
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
        return type;
    }

    public static int letterToYaw(char c) {
        switch (c) {
            case 'S':
            case 's':
                return 0;
            case 'W':
            case 'w':
                return 90;
            case 'N':
            case 'n':
                return 180;
            case 'E':
            case 'e':
                return -90;
            default:
                return -1;
        }
    }

}
