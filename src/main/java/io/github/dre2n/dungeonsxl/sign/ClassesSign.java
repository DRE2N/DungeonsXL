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

import io.github.dre2n.dungeonsxl.global.GroupSign;
import io.github.dre2n.dungeonsxl.player.DClass;
import io.github.dre2n.dungeonsxl.world.GameWorld;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class ClassesSign extends DSign {

    private DSignType type = DSignTypeDefault.CLASSES;

    public ClassesSign(Sign sign, GameWorld gameWorld) {
        super(sign, gameWorld);
    }

    @Override
    public boolean check() {
        return true;
    }

    @Override
    public void onInit() {
        if (getGame().getRules().isLobbyDisabled()) {
            getSign().getBlock().setType(Material.AIR);
            return;
        }

        int[] direction = GroupSign.getDirection(getSign().getBlock().getData());
        int directionX = direction[0];
        int directionZ = direction[1];

        int xx = 0, zz = 0;
        for (DClass dclass : getGame().getRules().getClasses()) {

            // Check existing signs
            boolean isContinued = true;
            for (Sign isusedsign : getGameWorld().getSignClass()) {
                if (dclass.getName().equalsIgnoreCase(ChatColor.stripColor(isusedsign.getLine(1)))) {
                    isContinued = false;
                }
            }

            if (!isContinued) {
                continue;
            }

            Block classBlock = getSign().getBlock().getRelative(xx, 0, zz);

            if (classBlock.getData() == getSign().getData().getData() && classBlock.getType() == Material.WALL_SIGN && classBlock.getState() instanceof Sign) {
                Sign classSign = (Sign) classBlock.getState();

                classSign.setLine(0, ChatColor.DARK_BLUE + "############");
                classSign.setLine(1, ChatColor.DARK_GREEN + dclass.getName());
                classSign.setLine(2, "");
                classSign.setLine(3, ChatColor.DARK_BLUE + "############");
                classSign.update();

                getGameWorld().getSignClass().add(classSign);

            } else {
                break;
            }

            xx = xx + directionX;
            zz = zz + directionZ;
        }
    }

    @Override
    public DSignType getType() {
        return type;
    }

}
