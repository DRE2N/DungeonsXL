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
package io.github.dre2n.dungeonsxl.sign;

import io.github.dre2n.commons.util.NumberUtil;
import io.github.dre2n.dungeonsxl.mob.ExternalMobPlugin;
import io.github.dre2n.dungeonsxl.mob.ExternalMobProvider;
import io.github.dre2n.dungeonsxl.task.ExternalMobSpawnTask;
import io.github.dre2n.dungeonsxl.world.GameWorld;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

/**
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class ExternalMobSign extends DSign implements MobSign {

    private DSignType type = DSignTypeDefault.EXTERNAL_MOB;

    // Variables
    private String mob;
    private int maxInterval = 1;
    private int interval = 0;
    private int amount = 1;
    private ExternalMobProvider provider;
    private int initialAmount = 1;
    private boolean initialized;
    private boolean active;
    private BukkitTask task;
    private Location spawnLocation;
    private LivingEntity externalMob;
    private List<Entity> externalMobs = new ArrayList<>();

    public ExternalMobSign(Sign sign, String[] lines, GameWorld gameWorld) {
        super(sign, lines, gameWorld);
    }

    @Override
    public String getMob() {
        return mob;
    }

    @Override
    public void setMob(String mob) {
        this.mob = mob;
    }

    @Override
    public int getMaxInterval() {
        return maxInterval;
    }

    @Override
    public void setMaxInterval(int maxInterval) {
        this.maxInterval = maxInterval;
    }

    @Override
    public int getInterval() {
        return interval;
    }

    @Override
    public void setInterval(int interval) {
        this.interval = interval;
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public int getInitialAmount() {
        return initialAmount;
    }

    @Override
    public void setInitialAmount(int initialAmount) {
        this.initialAmount = initialAmount;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public BukkitTask getTask() {
        return task;
    }

    @Override
    public void setTask(BukkitTask task) {
        this.task = task;
    }

    @Override
    public void initializeTask() {
        task = new ExternalMobSpawnTask(this, provider).runTaskTimer(plugin, 0L, 20L);
    }

    /**
     * @return the spawnLocation
     */
    public Location getSpawnLocation() {
        return spawnLocation;
    }

    /**
     * @param spawnLocation
     * the spawnLocation to set
     */
    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    /**
     * @return the external mob
     */
    public LivingEntity getExternalMob() {
        return externalMob;
    }

    /**
     * @param externalMob
     * the external mob to set
     */
    public void setExternalMob(LivingEntity externalMob) {
        this.externalMob = externalMob;
    }

    /**
     * @return the externalMobs
     */
    public List<Entity> getExternalMobs() {
        return externalMobs;
    }

    /**
     * @param externalMob
     * the externalMob to add
     */
    public void addExternalMob(Entity externalMob) {
        externalMobs.add(externalMob);
    }

    /**
     * @param externalMob
     * the external mob to remove
     */
    public void removeExternalMob(Entity externalMob) {
        externalMobs.remove(externalMob);
    }

    @Override
    public boolean check() {
        if (lines[1].isEmpty() || lines[2].isEmpty()) {
            return false;
        }

        if (lines[1] == null) {
            return false;
        }

        String[] atributes = lines[2].split(",");
        if (atributes.length == 2 || atributes.length == 3) {
            return true;

        } else {
            return false;
        }
    }

    @Override
    public void onInit() {
        String mob = lines[1];
        if (mob != null) {
            String[] attributes = lines[2].split(",");
            if (attributes.length >= 2) {
                this.setMob(mob);
                setMaxInterval(NumberUtil.parseInt(attributes[0]));
                setAmount(NumberUtil.parseInt(attributes[1]));
                initialAmount = amount;

                if (attributes.length == 3) {
                    provider = plugin.getExternalMobProviders().getByIdentifier(attributes[2]);
                } else {
                    provider = ExternalMobPlugin.MYTHIC_MOBS;
                }
            }
        }

        getSign().getBlock().setType(Material.AIR);

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
        setInterval(0);
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

    public void setExternalMobs() {
        for (Entity entity : spawnLocation.getChunk().getEntities()) {
            if (entity.getLocation().getX() >= spawnLocation.getX() - 1 && entity.getLocation().getX() <= spawnLocation.getX() + 1 && entity.getLocation().getY() >= spawnLocation.getY() - 1
                    && entity.getLocation().getY() <= spawnLocation.getY() + 1 && entity.getLocation().getZ() >= spawnLocation.getZ() - 1 && entity.getLocation().getZ() <= spawnLocation.getZ() + 1
                    && !externalMobs.contains(entity) && entity instanceof LivingEntity && !(entity instanceof Player)) {
                setExternalMob((LivingEntity) entity);
                externalMobs.add(entity);
                return;
            }
        }
    }

    @Override
    public DSignType getType() {
        return type;
    }

}
