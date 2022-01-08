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
package de.erethon.dungeonsxl.trigger;

import de.erethon.caliburn.category.Category;
import de.erethon.dungeonsxl.api.sign.Deactivatable;
import de.erethon.dungeonsxl.api.sign.DungeonSign;
import de.erethon.dungeonsxl.event.trigger.TriggerActionEvent;
import de.erethon.dungeonsxl.util.commons.misc.BlockUtil;
import de.erethon.dungeonsxl.world.DGameWorld;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class RedstoneTrigger extends Trigger {

    private TriggerType type = TriggerTypeDefault.REDSTONE;

    private Block rtBlock;

    public RedstoneTrigger(Block block) {
        rtBlock = block;
    }

    public void onTrigger() {
        TriggerActionEvent event = new TriggerActionEvent(this);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        if (rtBlock.isBlockPowered()) {
            if (!isTriggered()) {
                setTriggered(true);
                updateDSigns();
            }

        } else if (isTriggered()) {
            setTriggered(false);

            for (DungeonSign dSign : getDSigns().toArray(new DungeonSign[getDSigns().size()])) {
                if (!(dSign instanceof Deactivatable)) {
                    return;
                }
                if (dSign.isErroneous()) {
                    return;
                }
                for (de.erethon.dungeonsxl.api.Trigger trigger : dSign.getTriggers()) {
                    if (trigger.isTriggered()) {
                        return;
                    }
                }
                ((Deactivatable) dSign).deactivate();
            }
        }
    }

    @Override
    public TriggerType getType() {
        return type;
    }

    /* Statics */
    public static RedstoneTrigger getOrCreate(Sign sign, DGameWorld gameWorld) {
        Block rtBlock;
        if (Category.WALL_SIGNS.containsBlock(sign.getBlock())) {
            rtBlock = BlockUtil.getAttachedBlock(sign.getBlock());
        } else {
            rtBlock = sign.getBlock();
        }
        if (rtBlock == null) {
            return null;
        }
        for (Trigger uncasted : gameWorld.getTriggers(TriggerTypeDefault.REDSTONE)) {
            RedstoneTrigger trigger = (RedstoneTrigger) uncasted;
            if (trigger.rtBlock.equals(rtBlock)) {
                return trigger;
            }
        }
        return new RedstoneTrigger(rtBlock);
    }

    public static void updateAll(DGameWorld gameWorld) {
        for (Trigger trigger : gameWorld.getTriggers(TriggerTypeDefault.REDSTONE)) {
            ((RedstoneTrigger) trigger).onTrigger();
        }
    }

}
