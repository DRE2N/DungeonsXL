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

import de.erethon.caliburn.mob.ExMob;
import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.mob.ExternalMobProvider;
import de.erethon.dungeonsxl.api.sign.Windup;
import de.erethon.dungeonsxl.api.world.InstanceWorld;
import de.erethon.dungeonsxl.player.DPermission;
import de.erethon.dungeonsxl.util.commons.misc.NumberUtil;
import de.erethon.dungeonsxl.util.commons.misc.Registry;
import java.util.ArrayList;
import java.util.Collection;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class MobSign extends Windup {

    private Registry<String, ExternalMobProvider> providers;

    private String mob;
    private ExternalMobProvider provider;
    private Collection<LivingEntity> spawnedMobs = new ArrayList<>();
    private int initialAmount;

    public MobSign(DungeonsAPI api, Sign sign, String[] lines, InstanceWorld instance) {
        super(api, sign, lines, instance);
        providers = api.getExternalMobProviderRegistry();
    }

    public String getMob() {
        return mob;
    }

    public void setMob(String mob) {
        this.mob = mob;
    }

    /**
     * Returns the initial amount of mobs to spawn - this value may increase with waves.
     *
     * @return the initial amount of mobs to spawn - this value may increase with waves
     */
    public int getInitialAmount() {
        return initialAmount;
    }

    public Collection<LivingEntity> getSpawnedMobs() {
        return spawnedMobs;
    }

    @Override
    public String getName() {
        return "Mob";
    }

    @Override
    public String getBuildPermission() {
        return DPermission.SIGN.getNode() + ".mob";
    }

    @Override
    public boolean isOnDungeonInit() {
        return false;
    }

    @Override
    public boolean isProtected() {
        return false;
    }

    @Override
    public boolean isSetToAir() {
        return true;
    }

    @Override
    public boolean validate() {
        if (getLine(1).isEmpty() || getLine(2).isEmpty()) {
            return false;
        }

        String[] attributes = getLine(2).split(",");
        if (attributes.length == 2 || attributes.length == 3) {
            return true;

        } else {
            return false;
        }
    }

    @Override
    public void initialize() {
        mob = getLine(1);
        String[] attributes = getLine(2).split(",");

        interval = NumberUtil.parseDouble(attributes[0]);
        n = NumberUtil.parseInt(attributes[1]);
        initialAmount = n;
        provider = attributes.length == 3 ? providers.get(attributes[2]) : null;

        setRunnable(new MobSpawnTask(api, this, n));
    }

    /**
     * Spawns the mob.
     *
     * @return the spawned mob
     */
    public LivingEntity spawn() {
        Location spawnLoc = getSign().getLocation().add(0.5, 0, 0.5);
        LivingEntity spawned = null;

        if (provider == null) {
            ExMob type = api.getCaliburn().getExMob(mob);
            if (type == null || !type.getSpecies().isAlive()) {
                return null;
            }
            spawned = (LivingEntity) type.toEntity(spawnLoc);

        } else {
            provider.summon(mob, spawnLoc);
            for (Entity entity : spawnLoc.getChunk().getEntities()) {
                Location entityLoc = entity.getLocation();
                if (entityLoc.getX() >= spawnLoc.getX() - 1 && entityLoc.getX() <= spawnLoc.getX() + 1 && entityLoc.getY() >= spawnLoc.getY() - 1
                        && entityLoc.getY() <= spawnLoc.getY() + 1 && entityLoc.getZ() >= spawnLoc.getZ() - 1 && entityLoc.getZ() <= spawnLoc.getZ() + 1
                        && entity instanceof LivingEntity && !spawnedMobs.contains((LivingEntity) entity) && !(entity instanceof Player)) {
                    spawned = (LivingEntity) entity;
                }
            }
        }

        if (spawned == null) {
            return null;
        }

        spawned.setRemoveWhenFarAway(false);
        spawnedMobs.add(spawned);
        return spawned;
    }

}
