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
package de.erethon.dungeonsxl.sign.windup;

import de.erethon.dungeonsxl.api.DungeonsAPI;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class MobSpawnTask extends BukkitRunnable {

    private DungeonsAPI api;

    private MobSign sign;
    private int k = 1, n;

    public MobSpawnTask(DungeonsAPI api, MobSign sign, int n) {
        this.api = api;
        this.sign = sign;
        this.n = n;
    }

    @Override
    public void run() {
        if (sign.isWorldFinished()) {
            sign.deactivate();
            return;
        }

        LivingEntity entity = sign.spawn();
        if (entity != null) {
            api.wrapEntity(entity, sign.getGameWorld(), api.getCaliburn().getExMob(entity), sign.getMob());
        }

        if (k < n) {
            k++;
        } else {
            sign.deactivate();
            k = 1;
        }
    }

}
