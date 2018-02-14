/*
 * Copyright (C) 2012-2018 Frank Baumann
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
package io.github.dre2n.dungeonsxl.sign.mob;

import io.github.dre2n.dungeonsxl.mob.DMob;
import io.github.dre2n.dungeonsxl.mob.ExternalMobProvider;
import io.github.dre2n.dungeonsxl.world.DGameWorld;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class ExternalMobSpawnTask extends BukkitRunnable {

    private ExternalMobSign sign;
    private ExternalMobProvider provider;

    public ExternalMobSpawnTask(ExternalMobSign sign, ExternalMobProvider provider) {
        this.sign = sign;
        this.provider = provider;
    }

    @Override
    public void run() {
        if (sign.getInterval() <= 0) {
            World world = sign.getSign().getWorld();
            DGameWorld gameWorld = DGameWorld.getByWorld(world);

            if (gameWorld != null) {
                sign.setSpawnLocation(sign.getSign().getLocation().add(0.5, 0, 0.5));

                provider.summon(sign.getMob(), sign.getSpawnLocation());

                sign.setExternalMobs();
                if (sign.getExternalMob() != null) {
                    new DMob(sign.getExternalMob(), sign.getGameWorld(), null, sign.getMob());
                }

                // Set the amount
                if (sign.getAmount() != -1) {
                    if (sign.getAmount() > 1) {
                        sign.setAmount(sign.getAmount() - 1);

                    } else {
                        sign.killTask();
                    }
                }

                sign.setInterval(sign.getMaxInterval());

            } else {
                sign.killTask();
            }
        }

        sign.setInterval(sign.getInterval() - 1);
    }

}
