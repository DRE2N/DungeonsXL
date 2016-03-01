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
package io.github.dre2n.dungeonsxl.mob;

import io.github.dre2n.dungeonsxl.event.dmob.DMobDeathEvent;
import io.github.dre2n.dungeonsxl.game.GameWorld;
import io.github.dre2n.dungeonsxl.trigger.MobTrigger;
import java.util.Random;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class DMob {

    // Variables
    private LivingEntity entity;
    private DMobType type;

    private String trigger;

    public DMob(LivingEntity entity, GameWorld gameWorld, DMobType type) {
        gameWorld.addDMob(this);

        this.entity = entity;
        this.type = type;

        /* Remove DropChance of equipment */
        this.entity.getEquipment().setHelmetDropChance(0);
        this.entity.getEquipment().setChestplateDropChance(0);
        this.entity.getEquipment().setLeggingsDropChance(0);
        this.entity.getEquipment().setBootsDropChance(0);
        this.entity.getEquipment().setItemInHandDropChance(0);
    }

    public DMob(LivingEntity entity, GameWorld gameWorld, DMobType type, String trigger) {
        gameWorld.addDMob(this);

        this.entity = entity;
        this.type = type;
        this.trigger = trigger;

        /* Remove DropChance of equipment */
        this.entity.getEquipment().setHelmetDropChance(0);
        this.entity.getEquipment().setChestplateDropChance(0);
        this.entity.getEquipment().setLeggingsDropChance(0);
        this.entity.getEquipment().setBootsDropChance(0);
        this.entity.getEquipment().setItemInHandDropChance(0);
    }

    /* Statics */
    @SuppressWarnings("deprecation")
    public static void onDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }

        LivingEntity victim = event.getEntity();
        GameWorld gameWorld = GameWorld.getByWorld(victim.getWorld());
        String name = null;

        if (gameWorld == null) {
            return;
        }

        for (DMob dMob : gameWorld.getDMobs()) {
            if (dMob.entity != victim) {
                continue;
            }

            DMobDeathEvent dMobDeathEvent = new DMobDeathEvent(dMob, event);

            if (dMobDeathEvent.isCancelled()) {
                return;
            }

            if (dMob.type != null) {
                for (ItemStack itemStack : dMob.type.getDrops().keySet()) {
                    Random randomGenerator = new Random();
                    int random = randomGenerator.nextInt(100);

                    if (dMob.type.getDrops().get(itemStack) > random) {
                        event.getDrops().add(itemStack);
                    }
                }
                name = dMob.type.getName();

            } else if (dMob.type == null && dMob.trigger != null) {// <=MythicMobs mob
                name = dMob.trigger;

            } else {
                name = victim.getType().getName();
            }

            MobTrigger trigger = MobTrigger.get(name, gameWorld);
            if (trigger != null) {
                trigger.onTrigger();
            }

            gameWorld.removeDMob(dMob);
            return;
        }
    }

}
