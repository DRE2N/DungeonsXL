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

import de.erethon.dungeonsxl.mob.DMob;
import de.erethon.dungeonsxl.world.DGameWorld;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class MobSpawnTask extends BukkitRunnable {

    private MobSign sign;

    public MobSpawnTask(MobSign sign) {
        this.sign = sign;
    }

    @Override
    public void run() {
        if (sign.getInterval() <= 0) {
            World world = sign.getSign().getWorld();
            DGameWorld gameWorld = DGameWorld.getByWorld(world);
            if (gameWorld == null) {
                sign.killTask();
                return;
            }

            LivingEntity entity = sign.spawn();
            if (entity != null) {
                new DMob(entity, sign.getGameWorld(), sign.getMob());
            }

            if (sign.getAmount() != -1) {
                if (sign.getAmount() > 1) {
                    sign.setAmount(sign.getAmount() - 1);

                } else {
                    sign.killTask();
                }
            }

            sign.setInterval(sign.getMaxInterval());
        }

        sign.setInterval(sign.getInterval() - 1);
    }

}
