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
import io.github.dre2n.dungeonsxl.game.Game;
import io.github.dre2n.dungeonsxl.trigger.MobTrigger;
import io.github.dre2n.dungeonsxl.trigger.WaveTrigger;
import io.github.dre2n.dungeonsxl.world.GameWorld;
import java.util.Random;
import java.util.Set;
import org.bukkit.entity.Entity;
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
        this(entity, gameWorld, type);
        this.trigger = trigger;
    }

    public void onDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }

        LivingEntity victim = event.getEntity();
        GameWorld gameWorld = GameWorld.getByWorld(victim.getWorld());
        String name = null;

        if (gameWorld == null) {
            return;
        }

        DMobDeathEvent dMobDeathEvent = new DMobDeathEvent(this, event);

        if (dMobDeathEvent.isCancelled()) {
            return;
        }

        if (type != null) {
            for (ItemStack itemStack : type.getDrops().keySet()) {
                Random randomGenerator = new Random();
                int random = randomGenerator.nextInt(100);

                if (type.getDrops().get(itemStack) > random) {
                    event.getDrops().add(itemStack);
                }
            }
            name = type.getName();

        } else if (type == null && trigger != null) {// <=MythicMobs mob
            name = trigger;

        } else {
            name = victim.getType().getName();
        }

        MobTrigger mobTriger = MobTrigger.get(name, gameWorld);
        if (mobTriger != null) {
            mobTriger.onTrigger();
        }

        Set<WaveTrigger> waveTriggers = WaveTrigger.getByGameWorld(gameWorld);
        for (WaveTrigger waveTrigger : waveTriggers) {
            if (Game.getByGameWorld(gameWorld).getWaveKills() >= Math.ceil(gameWorld.getMobCount() * waveTrigger.getMustKillRate())) {
                waveTrigger.onTrigger();
            }
        }

        gameWorld.removeDMob(this);
    }

    /* Statics */
    public static DMob getByEntity(Entity entity) {
        GameWorld gameWorld = GameWorld.getByWorld(entity.getWorld());

        for (DMob dMob : gameWorld.getDMobs()) {
            if (dMob.entity == entity) {
                return dMob;
            }
        }

        return null;
    }

}
