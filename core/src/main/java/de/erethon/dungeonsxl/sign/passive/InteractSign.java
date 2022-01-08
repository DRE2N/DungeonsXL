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
package de.erethon.dungeonsxl.sign.passive;

import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.sign.DungeonSign;
import de.erethon.dungeonsxl.api.sign.Passive;
import de.erethon.dungeonsxl.api.world.InstanceWorld;
import de.erethon.dungeonsxl.player.DPermission;
import de.erethon.dungeonsxl.trigger.InteractTrigger;
import de.erethon.dungeonsxl.util.commons.misc.NumberUtil;
import de.erethon.dungeonsxl.world.DGameWorld;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Milan Albrecht, Daniel Saukel
 */
public class InteractSign extends Passive {

    private int id = 0;

    public InteractSign(DungeonsAPI api, Sign sign, String[] lines, InstanceWorld instance) {
        super(api, sign, lines, instance);
    }

    @Override
    public String getName() {
        return "Interact";
    }

    @Override
    public String getBuildPermission() {
        return DPermission.SIGN.getNode() + ".interact";
    }

    @Override
    public boolean isOnDungeonInit() {
        return true;
    }

    @Override
    public boolean isProtected() {
        return true;
    }

    @Override
    public boolean isSetToAir() {
        return false;
    }

    @Override
    public boolean validate() {
        Set<Integer> used = new HashSet<>();
        for (DungeonSign dSign : getEditWorld().getDungeonSigns()) {
            if (dSign instanceof InteractSign) {
                used.add(((InteractSign) dSign).id);
            }
        }

        if (getLine(1).isEmpty()) {
            if (!used.isEmpty()) {
                while (used.contains(id)) {
                    id++;
                }
            }

        } else {
            id = NumberUtil.parseInt(getLine(1));
            if (id == 0 || used.contains(id)) {
                return false;
            } else {
                return true;
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                getSign().setLine(1, String.valueOf(id));
                getSign().update(true);
            }
        }.runTaskLater(api, 1L);
        return true;
    }

    @Override
    public void initialize() {
        InteractTrigger trigger = InteractTrigger.getOrCreate(NumberUtil.parseInt(getSign().getLine(1)), getSign().getBlock(), (DGameWorld) getGameWorld());
        if (trigger != null) {
            trigger.addListener(this);
            addTrigger(trigger);
        }

        getSign().setLine(0, ChatColor.DARK_BLUE + "############");
        getSign().setLine(1, ChatColor.GREEN + getLine(2));
        getSign().setLine(2, ChatColor.GREEN + getLine(3));
        getSign().setLine(3, ChatColor.DARK_BLUE + "############");
        getSign().update();
    }

}
