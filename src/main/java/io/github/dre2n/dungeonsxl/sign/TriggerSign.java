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
import io.github.dre2n.dungeonsxl.trigger.SignTrigger;
import io.github.dre2n.dungeonsxl.world.EditWorld;
import io.github.dre2n.dungeonsxl.world.GameWorld;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

/**
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class TriggerSign extends DSign {

    private DSignType type = DSignTypeDefault.TRIGGER;

    // Variables
    private int triggerId;
    private boolean initialized;

    public TriggerSign(Sign sign, String[] lines, GameWorld gameWorld) {
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
            if (used.contains(id)) {
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
        triggerId = NumberUtil.parseInt(getSign().getLine(1));
        getSign().getBlock().setType(Material.AIR);

        initialized = true;
    }

    @Override
    public void onTrigger() {
        if (!initialized) {
            return;
        }

        SignTrigger trigger = SignTrigger.get(triggerId, getGameWorld());
        if (trigger != null) {
            trigger.onTrigger(true);
        }
    }

    @Override
    public void onDisable() {
        if (!initialized) {
            return;
        }

        SignTrigger trigger = SignTrigger.get(triggerId, getGameWorld());
        if (trigger != null) {
            trigger.onTrigger(false);
        }
    }

    @Override
    public DSignType getType() {
        return type;
    }

}
