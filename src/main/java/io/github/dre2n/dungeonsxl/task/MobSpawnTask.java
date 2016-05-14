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
package io.github.dre2n.dungeonsxl.task;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.mob.DMob;
import io.github.dre2n.dungeonsxl.mob.DMobType;
import io.github.dre2n.dungeonsxl.sign.DMobSign;
import io.github.dre2n.dungeonsxl.world.GameWorld;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class MobSpawnTask extends BukkitRunnable {

    private DMobSign sign;

    public MobSpawnTask(DMobSign sign) {
        this.sign = sign;
    }

    @Override
    public void run() {
        if (sign.getInterval() <= 0) {
            World world = sign.getSign().getWorld();
            GameWorld gameWorld = GameWorld.getByWorld(world);

            if (gameWorld != null) {
                Location spawnLoc = sign.getSign().getLocation().add(0.5, 0, 0.5);

                // Check normal mobs
                if (EntityType.fromName(sign.getMob()) != null) {
                    if (EntityType.fromName(sign.getMob()).isAlive()) {
                        LivingEntity entity = (LivingEntity) world.spawnEntity(spawnLoc, EntityType.fromName(sign.getMob()));

                        // Add Bow to normal Skeletons
                        if (entity.getType() == EntityType.SKELETON) {
                            Skeleton skeleton = (Skeleton) entity;
                            if (skeleton.getSkeletonType() == SkeletonType.NORMAL) {
                                skeleton.getEquipment().setItemInHand(new ItemStack(Material.BOW));
                            }
                        }

                        // Disable Despawning
                        entity.setRemoveWhenFarAway(false);

                        new DMob(entity, sign.getGameWorld(), null);
                    }
                }

                // Check custom mobs
                DMobType mobType = DungeonsXL.getInstance().getDMobTypes().getByName(sign.getMob());

                if (mobType != null) {
                    mobType.spawn(GameWorld.getByWorld(world), spawnLoc);
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
