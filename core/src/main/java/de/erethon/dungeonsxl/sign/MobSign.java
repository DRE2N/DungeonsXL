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
package de.erethon.dungeonsxl.sign;

import de.erethon.caliburn.item.VanillaItem;
import de.erethon.caliburn.mob.ExMob;
import de.erethon.commons.misc.NumberUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.mob.ExternalMobProvider;
import de.erethon.dungeonsxl.mob.ExternalMobProviderCache;
import de.erethon.dungeonsxl.world.DGameWorld;
import java.util.ArrayList;
import java.util.Collection;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

/**
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class MobSign extends DSign {

    private ExternalMobProviderCache providers;

    private String mob;
    private int maxInterval = 1;
    private int interval = 0;
    private int amount = 1;
    private int initialAmount = 1;
    private boolean initialized;
    private boolean active;
    private ExternalMobProvider provider;
    private BukkitTask task;
    private Collection<LivingEntity> spawnedMobs = new ArrayList<>();

    public MobSign(DungeonsXL plugin, Sign sign, String[] lines, DGameWorld gameWorld) {
        super(plugin, sign, lines, gameWorld);
        providers = plugin.getExternalMobProviderCache();
    }

    /**
     * @return the mob
     */
    public String getMob() {
        return mob;
    }

    /**
     * @param mob the mob to set
     */
    public void setMob(String mob) {
        this.mob = mob;
    }

    /**
     * @return the the maximum interval between mob spawns
     */
    public int getMaxInterval() {
        return maxInterval;
    }

    /**
     * @param maxInterval the maximum interval between mob spawns
     */
    public void setMaxInterval(int maxInterval) {
        this.maxInterval = maxInterval;
    }

    /**
     * @return the spawn interval
     */
    public int getInterval() {
        return interval;
    }

    /**
     * @param interval the spawn interval
     */
    public void setInterval(int interval) {
        this.interval = interval;
    }

    /**
     * @return the amount of mobs
     */
    public int getAmount() {
        return amount;
    }

    /**
     * @param amount the amount of mobs to set
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     * @return the initial amount of mobs
     */
    public int getInitialAmount() {
        return initialAmount;
    }

    /**
     * @param initialAmount the initial amount of mobs to set
     */
    public void setInitialAmount(int initialAmount) {
        this.initialAmount = initialAmount;
    }

    /**
     * @return if the sign is initialized
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * @param initialized set the sign initialized
     */
    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    /**
     * @return if the sign is active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active set the sign active
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @return the spawn task
     */
    public BukkitTask getTask() {
        return task;
    }

    /**
     * @param task the task to set
     */
    public void setTask(BukkitTask task) {
        this.task = task;
    }

    /**
     * Starts a new spawn task.
     */
    public void initializeTask() {
        task = new MobSpawnTask(this).runTaskTimer(plugin, 0L, 20L);
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
            ExMob type = plugin.getCaliburn().getExMob(mob);
            if (type == null || !type.getSpecies().isAlive()) {
                return null;
            }
            spawned = (LivingEntity) type.toEntity(spawnLoc);
            spawned.setRemoveWhenFarAway(false);

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

        spawnedMobs.add(spawned);
        return spawned;
    }

    /**
     * @return a Collection of all spawned mobs.
     */
    public Collection<LivingEntity> getSpawnedMobs() {
        return spawnedMobs;
    }

    @Override
    public boolean check() {
        if (lines[1].isEmpty() || lines[2].isEmpty()) {
            return false;
        }

        if (lines[1] == null) {
            return false;
        }

        String[] attributes = lines[2].split(",");
        if (attributes.length == 2 || attributes.length == 3) {
            return true;

        } else {
            return false;
        }
    }

    @Override
    public void onInit() {
        mob = lines[1];
        String[] attributes = lines[2].split(",");

        maxInterval = NumberUtil.parseInt(attributes[0]);
        amount = NumberUtil.parseInt(attributes[1]);
        initialAmount = amount;
        provider = attributes.length == 3 ? providers.getByIdentifier(attributes[2]) : null;

        getSign().getBlock().setType(VanillaItem.AIR.getMaterial());

        initialized = true;
    }

    @Override
    public void onTrigger() {
        if (!initialized || active) {
            return;
        }

        initializeTask();

        active = true;
    }

    @Override
    public void onDisable() {
        if (!initialized || !active) {
            return;
        }

        killTask();
        interval = 0;
        active = false;
    }

    public void killTask() {
        if (!initialized || !active) {
            return;
        }

        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    @Override
    public DSignType getType() {
        return DSignTypeDefault.MOB;
    }

}
