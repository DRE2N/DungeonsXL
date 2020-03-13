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
package de.erethon.dungeonsxl.sign.windup;

import de.erethon.caliburn.item.VanillaItem;
import de.erethon.commons.misc.NumberUtil;
import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.sign.Windup;
import de.erethon.dungeonsxl.api.world.InstanceWorld;
import de.erethon.dungeonsxl.player.DPermission;
import org.bukkit.block.Sign;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class RedstoneSign extends Windup {

    public RedstoneSign(DungeonsAPI api, Sign sign, String[] lines, InstanceWorld instance) {
        super(api, sign, lines, instance);
    }

    @Override
    public String getName() {
        return "Redstone";
    }

    @Override
    public String getBuildPermission() {
        return DPermission.SIGN.getNode() + ".redstone";
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
        return true;
    }

    @Override
    public void initialize() {
        interval = NumberUtil.parseDouble(getLine(1), 0);
        n = NumberUtil.parseInt(getLine(2), -1);
        setRunnable(new BukkitRunnable() {
            @Override
            public void run() {
                if (getGameWorld() == null) {
                    cancel();
                    return;
                }
                power(!isPowered());
                if (k < n) {
                    k++;
                } else {
                    cancel();
                }
            }
        });
    }

    @Override
    public void activate() {
        if (active) {
            return;
        }

        if (delay > 0) {
            startTask();
        } else {
            power(true);
        }

        active = true;
    }

    public boolean isPowered() {
        return getSign().getBlock().getType() == VanillaItem.REDSTONE_BLOCK.getMaterial();
    }

    public void power(boolean power) {
        getSign().getBlock().setType((power ? VanillaItem.REDSTONE_BLOCK : VanillaItem.AIR).getMaterial());
    }

}
