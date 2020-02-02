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

import de.erethon.dungeonsxl.world.DGameWorld;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class DelayedPowerTask extends BukkitRunnable {

    private RedstoneSign sign;
    private boolean enable;

    public DelayedPowerTask(RedstoneSign sign, boolean enable) {
        this.sign = sign;
        this.enable = enable;
    }

    @Override
    public void run() {
        if (DGameWorld.getByWorld(sign.getBlock().getWorld()) == null) {
            sign.getEnableTask().cancel();
            sign.getDisableTask().cancel();
            return;
        }

        if (enable) {
            sign.power();
            if (sign.getRepeatsToDo() == 1) {
                sign.getEnableTask().cancel();
            }

        } else {
            sign.unpower();
            if (sign.getRepeatsToDo() == 1) {
                sign.getDisableTask().cancel();
            }
            sign.setRepeatsToDo(sign.getRepeatsToDo() - 1);
        }
    }

}
