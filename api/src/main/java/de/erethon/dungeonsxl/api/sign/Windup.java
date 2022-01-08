/*
 * Copyright (C) 2014-2022 Daniel Saukel
 *
 * This library is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNULesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.erethon.dungeonsxl.api.sign;

import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.world.InstanceWorld;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

/**
 * A sign with an attached task that does actions in a set interval {@link #n} times, like a mob sign that spawns {@link #n} mobs. It is similar to a
 * {@link Rocker} as it expires (=is deactivated).
 *
 * @author Daniel Saukel
 */
public abstract class Windup extends Deactivatable {

    protected double delay = -1;
    protected double interval = -1;
    /**
     * How many times the task is supposed to be executed (unless it is cancelled).
     */
    protected int n;

    private Runnable runnable;
    private BukkitTask task;

    protected Windup(DungeonsAPI api, Sign sign, String[] lines, InstanceWorld instance) {
        super(api, sign, lines, instance);
    }

    /**
     * Returns the delay before the task runs in seconds. If no delay is specified, this uses the interval.
     *
     * @return the delay before the task runs in seconds. If no delay is specified, this uses the interval
     */
    public double getDelaySeconds() {
        return delay != -1 ? delay : interval;
    }

    /**
     * Returns the delay before the task runs in ticks. If no delay is specified, this uses the interval.
     *
     * @return the delay before the task runs in ticks. If no delay is specified, this uses the interval
     */
    public long getDelayTicks() {
        return delay != -1 ? (long) (delay * 20L) : getIntervalTicks();
    }

    /**
     * Returns the task interval in seconds.
     *
     * @return the task interval in seconds
     */
    public double getIntervalSeconds() {
        return interval;
    }

    /**
     * Returns the task interval in ticks.
     *
     * @return the task interval in ticks
     */
    public long getIntervalTicks() {
        return (long) (interval * 20L);
    }

    /**
     * Returns the underlying task if it has started yet or null if not.
     *
     * @return the underlying task if it has started yet or null if not
     */
    public BukkitTask getTask() {
        return task;
    }

    /**
     * Starts the runnable.
     */
    public void startTask() {
        task = Bukkit.getScheduler().runTaskTimer(api, runnable, getDelayTicks(), getIntervalTicks());
    }

    /**
     * Returns the runnable.
     *
     * @return the runnable
     */
    public Runnable getRunnable() {
        return runnable;
    }

    /**
     * Sets the runnable.
     *
     * @param runnable the runnable
     */
    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    /**
     * Returns how many times the task is supposed to be executed (like in SIGMA notation).
     *
     * @return how many times the task is supposed to be executed (like in SIGMA notation)
     */
    public int getN() {
        return n;
    }

    /**
     * Sets how many times the task is supposed to be executed (like in SIGMA notation).
     *
     * @param n the new amount of runs
     */
    public void setN(int n) {
        this.n = n;
    }

    @Override
    public void activate() {
        if (interval <= 0) {
            for (int k = 0; k < n; k++) {
                runnable.run();
            }
        } else {
            active = true;
            startTask();
        }
    }

    /**
     * Cancels the {@link #getTask() task}.
     */
    @Override
    public void deactivate() {
        active = false;
        if (getTask() != null) {
            getTask().cancel();
        }
    }

    /**
     * Activates the sign if it is not yet active and deactivates it if it is already active.
     *
     * @param player the player who triggered the sign or null if no one in particular triggered it
     */
    @Override
    public void trigger(Player player) {
        if (!isActive()) {
            if (player != null) {
                activate(player);
            } else {
                activate();
            }
        }
    }

    /**
     * Use this method to ensure that its world still exists.
     *
     * @return if the world is already finished
     */
    public boolean isWorldFinished() {
        return Bukkit.getWorld(worldName) == null;
    }

}
