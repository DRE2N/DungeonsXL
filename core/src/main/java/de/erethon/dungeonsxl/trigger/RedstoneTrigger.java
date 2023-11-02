/*
 * Copyright (C) 2012-2023 Frank Baumann
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
package de.erethon.dungeonsxl.trigger;

import de.erethon.caliburn.category.Category;
import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.sign.Deactivatable;
import de.erethon.dungeonsxl.api.trigger.AbstractTrigger;
import de.erethon.dungeonsxl.api.trigger.LogicalExpression;
import de.erethon.dungeonsxl.api.trigger.Trigger;
import de.erethon.dungeonsxl.api.trigger.TriggerListener;
import de.erethon.dungeonsxl.api.trigger.TriggerTypeKey;
import de.erethon.dungeonsxl.api.world.GameWorld;
import de.erethon.dungeonsxl.util.BlockUtilCompat;
import org.bukkit.Location;
import org.bukkit.block.Block;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class RedstoneTrigger extends AbstractTrigger {

    private Block rtBlock;

    public RedstoneTrigger(DungeonsAPI api, TriggerListener owner, LogicalExpression expression, String value) {
        super(api, owner, expression, value);

        Location loc = owner.getLocation();
        if (Category.WALL_SIGNS.containsBlock(loc.getBlock())) {
            rtBlock = BlockUtilCompat.getAttachedBlock(loc.getBlock());
        } else {
            rtBlock = loc.getBlock();
        }
    }

    @Override
    public char getKey() {
        return TriggerTypeKey.REDSTONE;
    }

    @Override
    public void onTrigger(boolean switching) {
        if (rtBlock.isBlockPowered()) {
            if (!isTriggered()) {
                setTriggered(true);
            }

        } else if (isTriggered()) {
            setTriggered(false);

            for (TriggerListener listener : getListeners().toArray(TriggerListener[]::new)) {
                if (!(listener instanceof Deactivatable)) {
                    return;
                }
                Deactivatable sign = ((Deactivatable) listener);
                if (sign.isErroneous()) {
                    return;
                }
                for (Trigger trigger : sign.getTriggers()) {
                    if (trigger.isTriggered()) {
                        return;
                    }
                }
                sign.deactivate();
            }
        }
    }

    /* Statics */
    public static void updateAll(GameWorld gameWorld) {
        if (gameWorld == null) {
            return;
        }
        for (Trigger uncasted : gameWorld.getTriggers()) {
            if (!(uncasted instanceof RedstoneTrigger)) {
                continue;
            }
            ((RedstoneTrigger) uncasted).trigger(true, null);
        }
    }

}
