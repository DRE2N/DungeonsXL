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
import de.erethon.commons.misc.NumberUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.trigger.SignTrigger;
import de.erethon.dungeonsxl.world.DEditWorld;
import de.erethon.dungeonsxl.world.DGameWorld;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

/**
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class TriggerSign extends DSign {

    // Variables
    private int triggerId;
    private boolean initialized;

    public TriggerSign(DungeonsXL plugin, Sign sign, String[] lines, DGameWorld gameWorld) {
        super(plugin, sign, lines, gameWorld);
    }

    @Override
    public boolean check() {
        Set<Integer> used = new HashSet<>();
        for (Block block : DEditWorld.getByWorld(getSign().getLocation().getWorld()).getSigns()) {
            if (block == null) {
                continue;
            }

            if (!block.getChunk().isLoaded()) {
                block.getChunk().load();
            }

            if (block.getState() instanceof Sign) {
                Sign rsign = (Sign) block.getState();
                if (rsign.getLine(0).equalsIgnoreCase("[" + getType().getName() + "]")) {
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
        getSign().getBlock().setType(VanillaItem.AIR.getMaterial());

        initialized = true;
    }

    @Override
    public void onTrigger() {
        if (!initialized) {
            return;
        }

        SignTrigger trigger = SignTrigger.getById(triggerId, getGameWorld());
        if (trigger != null) {
            trigger.onTrigger(true);
        }
    }

    @Override
    public void onDisable() {
        if (!initialized) {
            return;
        }

        SignTrigger trigger = SignTrigger.getById(triggerId, getGameWorld());
        if (trigger != null) {
            trigger.onTrigger(false);
        }
    }

    @Override
    public DSignType getType() {
        return DSignTypeDefault.TRIGGER;
    }

}
