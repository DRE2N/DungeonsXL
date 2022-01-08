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
package de.erethon.dungeonsxl.mob;

import de.erethon.caliburn.mob.ExMob;
import de.erethon.caliburn.mob.VanillaMob;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.api.dungeon.GameRule;
import de.erethon.dungeonsxl.api.event.mob.DungeonMobDeathEvent;
import de.erethon.dungeonsxl.api.event.mob.DungeonMobSpawnEvent;
import de.erethon.dungeonsxl.api.mob.DungeonMob;
import de.erethon.dungeonsxl.api.world.GameWorld;
import de.erethon.dungeonsxl.dungeon.DGame;
import de.erethon.dungeonsxl.trigger.MobTrigger;
import de.erethon.dungeonsxl.trigger.WaveTrigger;
import de.erethon.dungeonsxl.util.commons.compatibility.Version;
import de.erethon.dungeonsxl.world.DGameWorld;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;

/**
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class DMob implements DungeonMob {

    private LivingEntity entity;
    private ExMob type;

    private String trigger;

    public DMob(LivingEntity entity, GameWorld gameWorld, ExMob type, String trigger) {
        this.entity = entity;
        this.type = type != null ? type : VanillaMob.get(entity.getType());

        if (this.type != VanillaMob.PLAYER && !getDrops(gameWorld.getDungeon().getRules().getState(GameRule.MOB_ITEM_DROPS))) {
            entity.getEquipment().setHelmetDropChance(0);
            entity.getEquipment().setChestplateDropChance(0);
            entity.getEquipment().setLeggingsDropChance(0);
            entity.getEquipment().setBootsDropChance(0);
            if (Version.isAtLeast(Version.MC1_9)) {
                entity.getEquipment().setItemInMainHandDropChance(0);
                entity.getEquipment().setItemInOffHandDropChance(0);
            } else {
                entity.getEquipment().setItemInHandDropChance(0);
            }
        }

        DungeonMobSpawnEvent event = new DungeonMobSpawnEvent(this);
        Bukkit.getPluginManager().callEvent(event);
        this.trigger = trigger;
        gameWorld.addMob(this);
    }

    /* Getters */
    @Override
    public LivingEntity getEntity() {
        return entity;
    }

    @Override
    public ExMob getType() {
        return type;
    }

    @Override
    public String getTriggerId() {
        return trigger;
    }

    /* Actions */
    public void onDeath(DungeonsXL plugin, EntityDeathEvent event) {
        LivingEntity victim = event.getEntity();
        DGameWorld gameWorld = (DGameWorld) plugin.getGameWorld(victim.getWorld());
        if (gameWorld == null) {
            return;
        }

        DungeonMobDeathEvent dMobDeathEvent = new DungeonMobDeathEvent(this);
        Bukkit.getServer().getPluginManager().callEvent(dMobDeathEvent);
        if (dMobDeathEvent.isCancelled()) {
            return;
        }

        if (!getDrops(gameWorld.getDungeon().getRules().getState(GameRule.MOB_ITEM_DROPS))) {
            event.getDrops().clear();
        }
        if (!getDrops(gameWorld.getDungeon().getRules().getState(GameRule.MOB_EXP_DROPS))) {
            event.setDroppedExp(0);
        }

        MobTrigger mobTrigger = MobTrigger.getByName(trigger, gameWorld);
        if (mobTrigger != null) {
            mobTrigger.onTrigger();
        }

        Set<WaveTrigger> waveTriggers = WaveTrigger.getByGameWorld(gameWorld);
        for (WaveTrigger waveTrigger : waveTriggers) {
            if (((DGame) gameWorld.getGame()).getWaveKills() >= Math.ceil(gameWorld.getMobCount() * waveTrigger.getMustKillRate())) {
                waveTrigger.onTrigger();
            }
        }

        gameWorld.removeMob(this);
    }

    private boolean getDrops(Object drops) {
        if (drops instanceof Boolean) {
            return (Boolean) drops;
        } else if (drops instanceof Set) {
            for (ExMob whitelisted : (Set<ExMob>) drops) {
                if (type.isSubsumableUnder(whitelisted)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{type=" + type + "}";
    }

}
