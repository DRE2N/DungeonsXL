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
package de.erethon.dungeonsxl.sign.windup;

import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.mob.ExternalMobProvider;
import de.erethon.dungeonsxl.api.mob.MobSet;
import de.erethon.dungeonsxl.api.sign.Windup;
import de.erethon.dungeonsxl.api.world.InstanceWorld;
import de.erethon.dungeonsxl.player.DPermission;
import de.erethon.xlib.mob.ExMob;
import de.erethon.xlib.util.NumberUtil;
import de.erethon.xlib.util.Registry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
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

    private MobSet typeSet;
    private Collection<MobSet> mobSets = new HashSet<>();
    private ExternalMobProvider provider;
    private Collection<LivingEntity> spawnedMobs = new ArrayList<>();
    private int initialAmount;

    public MobSign(DungeonsAPI api, Sign sign, String[] lines, InstanceWorld instance) {
        super(api, sign, lines, instance);
        providers = api.getExternalMobProviderRegistry();
    }

    public MobSet getTypeSet() {
        return typeSet;
    }

    public Collection<MobSet> getAddedMobSets() {
        return mobSets;
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
        return attributes.length == 2 || attributes.length == 3;
    }

    @Override
    public void initialize() {
        String[] attrAndSets = getLine(2).split("#");
        String[] attributes = attrAndSets[0].split(",");

        interval = NumberUtil.parseDouble(attributes[0]);
        n = NumberUtil.parseInt(attributes[1]);
        initialAmount = n;
        provider = attributes.length == 3 ? providers.get(attributes[2]) : null;

        getGameWorld().getAllMobSet().allocate(n);

        typeSet = getGameWorld().getOrCreateMobSet(getLine(1));
        typeSet.allocate(n);

        if (attrAndSets.length > 1) {
            String[] sets = attrAndSets[1].split(",");
            for (String id : sets) {
                MobSet set = getGameWorld().getOrCreateMobSet(id);
                set.allocate(n);
                mobSets.add(set);
            }
        }

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
            ExMob type = api.getXLib().getExMob(typeSet.getId());
            if (type == null || !type.getSpecies().isAlive()) {
                return null;
            }
            spawned = (LivingEntity) type.toEntity(spawnLoc);

        } else {
            provider.summon(typeSet.getId(), spawnLoc);
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
