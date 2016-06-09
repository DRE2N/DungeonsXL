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
import io.github.dre2n.dungeonsxl.task.SignUpdateTask;
import io.github.dre2n.dungeonsxl.trigger.InteractTrigger;
import io.github.dre2n.dungeonsxl.world.EditWorld;
import io.github.dre2n.dungeonsxl.world.GameWorld;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * @author Milan Albrecht, Daniel Saukel
 */
public class InteractSign extends DSign {

    private DSignType type = DSignTypeDefault.INTERACT;

    public InteractSign(Sign sign, String[] lines, GameWorld gameWorld) {
        super(sign, lines, gameWorld);
    }

    @Override
    public boolean check() {
        Set<Integer> used = new HashSet<>();
        for (Block block : EditWorld.getByWorld(getSign().getLocation().getWorld()).getSigns()) {
            if (block == null) {
                continue;
            }

            if (!block.getChunk().isLoaded()) {
                block.getChunk().load();
            }

            if (block.getState() instanceof Sign) {
                Sign rsign = (Sign) block.getState();
                if (rsign.getLine(0).equalsIgnoreCase("[" + type.getName() + "]")) {
                    used.add(NumberUtil.parseInt(rsign.getLine(1)));
                }
            }
        }

        int id = 1;
        if (getSign().getLine(1).isEmpty()) {
            if (!used.isEmpty()) {
                while (used.contains(id)) {
                    id++;
                }
            }

        } else {
            id = NumberUtil.parseInt(getSign().getLine(1));
            if (id == 0 || used.contains(id)) {
                return false;

            } else {
                return true;
            }
        }

        getSign().setLine(1, id + "");

        new SignUpdateTask(getSign()).runTaskLater(plugin, 2L);

        return true;
    }

    @Override
    public void onInit() {
        InteractTrigger trigger = InteractTrigger.getOrCreate(NumberUtil.parseInt(getSign().getLine(1)), getSign().getBlock(), getGameWorld());
        if (trigger != null) {
            trigger.addListener(this);
            addTrigger(trigger);
        }

        getSign().setLine(0, ChatColor.DARK_BLUE + "############");
        getSign().setLine(1, ChatColor.GREEN + getSign().getLine(2));
        getSign().setLine(2, ChatColor.GREEN + getSign().getLine(3));
        getSign().setLine(3, ChatColor.DARK_BLUE + "############");
        getSign().update();
    }

    @Override
    public boolean onPlayerTrigger(Player player) {
        return true;
    }

    @Override
    public DSignType getType() {
        return type;
    }

}
