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

import org.bukkit.scheduler.BukkitTask;

/**
 * @author Daniel Saukel
 */
public interface MobSign {

    /**
     * @return the mob
     */
    public String getMob();

    /**
     * @param mob
     * the mob to set
     */
    public void setMob(String mob);

    /**
     * @return the the maximum interval between mob spawns
     */
    public int getMaxInterval();

    /**
     * @param maxInterval
     * the maximum interval between mob spawns
     */
    public void setMaxInterval(int maxInterval);

    /**
     * @return the spawn interval
     */
    public int getInterval();

    /**
     * @param interval
     * the spawn interval
     */
    public void setInterval(int interval);

    /**
     * @return the amount of mobs
     */
    public int getAmount();

    /**
     * @param amount
     * the amount of mobs to set
     */
    public void setAmount(int amount);

    /**
     * @return the initial amount of mobs
     */
    public int getInitialAmount();

    /**
     * @param amount
     * the amount of mobs to set
     */
    public void setInitialAmount(int initialAmount);

    /**
     * @return if the sign is initialized
     */
    public boolean isInitialized();

    /**
     * @param initialized
     * set the sign initialized
     */
    public void setInitialized(boolean initialized);

    /**
     * @return if the sign is active
     */
    public boolean isActive();

    /**
     * @param active
     * set the sign active
     */
    public void setActive(boolean active);

    /**
     * @return the spawn task
     */
    public BukkitTask getTask();

    /**
     * @param task
     * the task to set
     */
    public void setTask(BukkitTask task);

    /**
     * Start a new spawn task.
     */
    public void initializeTask();

}
