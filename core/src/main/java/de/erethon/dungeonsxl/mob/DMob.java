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
package de.erethon.dungeonsxl.mob;

import de.erethon.caliburn.mob.ExMob;
import de.erethon.caliburn.mob.VanillaMob;
import de.erethon.caliburn.util.compatibility.Version;
import de.erethon.dungeonsxl.event.dmob.DMobDeathEvent;
import de.erethon.dungeonsxl.event.dmob.DMobSpawnEvent;
import de.erethon.dungeonsxl.game.Game;
import de.erethon.dungeonsxl.trigger.MobTrigger;
import de.erethon.dungeonsxl.trigger.WaveTrigger;
import de.erethon.dungeonsxl.world.DGameWorld;
import java.util.Random;
import java.util.Set;
import org.bukkit.Bukkit;
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
    private ExMob type;

    private String trigger;

    private DMob(LivingEntity entity, DGameWorld gameWorld) {
        gameWorld.addDMob(this);

        this.entity = entity;

        /* Remove DropChance of equipment */
        if (!isExternalMob()) {
            this.entity.getEquipment().setHelmetDropChance(0);
            this.entity.getEquipment().setChestplateDropChance(0);
            this.entity.getEquipment().setLeggingsDropChance(0);
            this.entity.getEquipment().setBootsDropChance(0);
            this.entity.getEquipment().setItemInHandDropChance(0);
            if (Version.isAtLeast(Version.MC1_9)) {
                this.entity.getEquipment().setItemInOffHandDropChance(0);
            }
        }

        DMobSpawnEvent event = new DMobSpawnEvent(this, entity);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            gameWorld.removeDMob(this);
        }
    }

    public DMob(LivingEntity entity, DGameWorld gameWorld, String trigger) {
        this(entity, gameWorld);
        this.trigger = trigger;
    }

    public DMob(LivingEntity entity, DGameWorld gameWorld, ExMob type) {
        this(entity, gameWorld);
        this.type = type;
        this.trigger = type.getId();
    }

    public DMob(LivingEntity entity, DGameWorld gameWorld, ExMob type, String trigger) {
        this(entity, gameWorld);
        this.type = type;
        this.trigger = trigger;
    }

    /* Getters */
    /**
     * @return the represented LivingEntity
     */
    public LivingEntity getEntity() {
        return entity;
    }

    /**
     * @return the DMobType or null if the mob is an external mob
     */
    public ExMob getType() {
        return type;
    }

    /**
     * @return if the mob is spawned by an external plugin
     */
    public boolean isExternalMob() {
        return type == null;
    }

    /**
     * @return the trigger String
     */
    public String getTrigger() {
        return trigger;
    }

    /* Actions */
    public void onDeath(EntityDeathEvent event) {
        LivingEntity victim = event.getEntity();
        DGameWorld gameWorld = DGameWorld.getByWorld(victim.getWorld());
        String name = null;

        if (gameWorld == null) {
            return;
        }

        DMobDeathEvent dMobDeathEvent = new DMobDeathEvent(this, event);
        Bukkit.getServer().getPluginManager().callEvent(dMobDeathEvent);

        if (dMobDeathEvent.isCancelled()) {
            return;
        }

        if (type instanceof DMobType) {
            event.getDrops().clear();

            for (ItemStack itemStack : ((DMobType) type).getDrops().keySet()) {
                Random randomGenerator = new Random();
                int random = randomGenerator.nextInt(100);

                if (((DMobType) type).getDrops().get(itemStack) > random) {
                    event.getDrops().add(itemStack);
                }
            }

            name = type.getId();

        } else if (isExternalMob() && trigger != null) {
            name = trigger;

        } else {
            name = VanillaMob.get(victim.getType()).getId();
        }

        MobTrigger mobTrigger = MobTrigger.getByName(name, gameWorld);
        if (mobTrigger != null) {
            mobTrigger.onTrigger();
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
        DGameWorld gameWorld = DGameWorld.getByWorld(entity.getWorld());

        for (DMob dMob : gameWorld.getDMobs()) {
            if (dMob.entity == entity) {
                return dMob;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{type=" + type + "}";
    }

}
