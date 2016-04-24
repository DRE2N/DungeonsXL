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
import io.github.dre2n.dungeonsxl.world.GameWorld;
import io.github.dre2n.dungeonsxl.task.MobSpawnTask;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.scheduler.BukkitTask;

/**
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class MobSign extends DSign {

    private DSignType type = DSignTypeDefault.MOB;

    // Variables
    private String mob;
    private int maxinterval = 1;
    private int interval = 0;
    private int amount = 1;
    private boolean initialized;
    private boolean active;
    private BukkitTask task;

    public MobSign(Sign sign, GameWorld gameWorld) {
        super(sign, gameWorld);
    }

    /**
     * @return the mob
     */
    public String getMob() {
        return mob;
    }

    /**
     * @param mob
     * the mob to set
     */
    public void setMob(String mob) {
        this.mob = mob;
    }

    /**
     * @return the maxinterval
     */
    public int getMaxinterval() {
        return maxinterval;
    }

    /**
     * @param maxinterval
     * the maxinterval to set
     */
    public void setMaxinterval(int maxinterval) {
        this.maxinterval = maxinterval;
    }

    /**
     * @return the interval
     */
    public int getInterval() {
        return interval;
    }

    /**
     * @param interval
     * the interval to set
     */
    public void setInterval(int interval) {
        this.interval = interval;
    }

    /**
     * @return the amount
     */
    public int getAmount() {
        return amount;
    }

    /**
     * @param amount
     * the amount to set
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     * @return the initialized
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * @param initialized
     * the initialized to set
     */
    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    /**
     * @return the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active
     * the active to set
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @return the task
     */
    public BukkitTask getTask() {
        return task;
    }

    /**
     * @param task
     * the task to set
     */
    public void setTask(BukkitTask task) {
        this.task = task;
    }

    @Override
    public boolean check() {
        String lines[] = getSign().getLines();
        if (lines[1].isEmpty() || lines[2].isEmpty()) {
            return false;
        }

        if (lines[1] == null) {
            return false;
        }

        String[] attributes = lines[2].split(",");
        if (attributes.length == 2) {
            return true;

        } else {
            return false;
        }
    }

    @Override
    public void onInit() {
        String lines[] = getSign().getLines();
        if (!lines[1].isEmpty() && !lines[2].isEmpty()) {
            String mob = lines[1];
            if (mob != null) {
                String[] attributes = lines[2].split(",");
                if (attributes.length == 2) {
                    this.mob = mob;
                    maxinterval = NumberUtil.parseInt(attributes[0]);
                    amount = NumberUtil.parseInt(attributes[1]);
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

        task = new MobSpawnTask(this).runTaskTimer(plugin, 0L, 20L);

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
        return type;
    }

}
