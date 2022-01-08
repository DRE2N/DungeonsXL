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

import de.erethon.caliburn.item.VanillaItem;
import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.sign.Rocker;
import de.erethon.dungeonsxl.api.world.InstanceWorld;
import de.erethon.dungeonsxl.player.DPermission;
import de.erethon.dungeonsxl.util.commons.misc.NumberUtil;
import org.bukkit.block.Sign;
import org.bukkit.scheduler.BukkitTask;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class RedstoneSign extends Rocker {

    private BukkitTask enableTask;
    private BukkitTask disableTask;
    private long delay = 0;
    private long offDelay = 0;
    private int repeat = 1;
    private int repeatsToDo = 1;

    public RedstoneSign(DungeonsAPI api, Sign sign, String[] lines, InstanceWorld instance) {
        super(api, sign, lines, instance);
    }

    public BukkitTask getEnableTask() {
        return enableTask;
    }

    public void setEnableTask(BukkitTask enableTask) {
        this.enableTask = enableTask;
    }

    public BukkitTask getDisableTask() {
        return disableTask;
    }

    public void setDisableTask(BukkitTask disableTask) {
        this.disableTask = disableTask;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public long getOffDelay() {
        return offDelay;
    }

    public void setOffDelay(long offDelay) {
        this.offDelay = offDelay;
    }

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public int getRepeatsToDo() {
        return repeatsToDo;
    }

    public void setRepeatsToDo(int repeatsToDo) {
        this.repeatsToDo = repeatsToDo;
    }

    @Override
    public String getName() {
        return "Redstone";
    }

    @Override
    public String getBuildPermission() {
        return DPermission.SIGN.getNode() + ".redstone";
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
        return true;
    }

    @Override
    public void initialize() {
        int line1 = 0;
        int line11 = 0;
        if (!getLine(1).isEmpty()) {
            String line[] = getLine(1).split(",");
            line1 = NumberUtil.parseInt(line[0]);
            if (line.length > 1) {
                line11 = NumberUtil.parseInt(line[1]);
            }
        }

        int line2 = 1;
        if (!getLine(2).isEmpty()) {
            line2 = NumberUtil.parseInt(getLine(2));
        }

        if (line1 > 0) {
            delay = (long) line1 * 2;
            if (line11 > 0) {
                offDelay = (long) line11 * 2;
            } else {
                offDelay = delay;
            }
            if (line2 >= 0) {
                repeat = line2;
            }
        }
    }

    @Override
    public void activate() {
        if (active) {
            return;
        }

        if (delay > 0) {
            enableTask = new DelayedPowerTask(this, true).runTaskTimer(api, delay, delay + offDelay);

            if (repeat != 1) {
                repeatsToDo = repeat;
                disableTask = new DelayedPowerTask(this, false).runTaskTimer(api, delay + offDelay, delay + offDelay);
            }

        } else {
            power();
        }

        active = true;
    }

    @Override
    public void deactivate() {
        if (!active) {
            return;
        }

        unpower();

        if (enableTask != null) {
            enableTask.cancel();
        }
        if (disableTask != null) {
            disableTask.cancel();
        }

        active = false;
    }

    public void power() {
        getSign().getBlock().setType(VanillaItem.REDSTONE_BLOCK.getMaterial());
    }

    public void unpower() {
        setToAir();
    }

}
