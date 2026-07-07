/*
 * Copyright (C) 2012-2013 Frank Baumann; 2015-2026 Daniel Saukel
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

import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.api.dungeon.GameRule;
import de.erethon.dungeonsxl.api.event.mob.DungeonMobDeathEvent;
import de.erethon.dungeonsxl.api.event.mob.DungeonMobSpawnEvent;
import de.erethon.dungeonsxl.api.mob.DungeonMob;
import de.erethon.dungeonsxl.api.mob.MobSet;
import de.erethon.dungeonsxl.api.world.GameWorld;
import de.erethon.dungeonsxl.world.DGameWorld;
import de.erethon.xlib.compatibility.Version;
import de.erethon.xlib.mob.ExMob;
import de.erethon.xlib.mob.VanillaMob;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;

/**
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class DMob implements DungeonMob {

    private GameWorld gameWorld;

    private LivingEntity entity;
    private ExMob type;

    private MobSet typeSet;
    private Set<MobSet> mobSets;

    public DMob(LivingEntity entity, GameWorld gameWorld, ExMob type, MobSet typeSet, Collection<MobSet> mobSets) {
        this.gameWorld = gameWorld;
        this.entity = entity;
        this.type = type != null ? type : VanillaMob.get(entity.getType());

        if (this.type.getSpecies().isAlive() && this.type != VanillaMob.ARMOR_STAND && this.type != VanillaMob.PLAYER
                && !getDrops(gameWorld.getDungeon().getRules().getState(GameRule.MOB_ITEM_DROPS))) {
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
        gameWorld.addMob(this);
        this.typeSet = typeSet;
        this.mobSets = new HashSet<>();
        this.mobSets.add(gameWorld.getAllMobSet());
        this.mobSets.add(typeSet);
        if (mobSets != null) {
            this.mobSets.addAll(mobSets);
        }
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

        mobSets.forEach(s -> s.kill(victim));

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

    @Override
    public MobSet getTypeMobSet() {
        return typeSet;
    }

    @Override
    public Collection<MobSet> getMobSets() {
        return new HashSet<>(mobSets);
    }

    @Override
    public boolean addMobSet(MobSet mobSet) {
        if (!gameWorld.getMobSets().contains(mobSet)) {
            throw new IllegalArgumentException("MobSet not registered for GameWorld");
        }
        return mobSets.add(mobSet);
    }

    @Override
    public boolean removeMobSet(MobSet mobSet) {
        return mobSets.remove(mobSet);
    }

}
