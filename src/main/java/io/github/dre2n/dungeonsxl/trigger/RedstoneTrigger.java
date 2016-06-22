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
package io.github.dre2n.dungeonsxl.trigger;

import io.github.dre2n.dungeonsxl.event.trigger.TriggerActionEvent;
import io.github.dre2n.dungeonsxl.world.GameWorld;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
        plugin.getServer().getPluginManager().callEvent(event);

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
            updateDSigns();
        }
    }

    @Override
    public TriggerType getType() {
        return type;
    }

    /* Statics */
    public static RedstoneTrigger getOrCreate(Sign sign, GameWorld gameWorld) {
        Block rtBlock = null;
        if (sign.getBlock().getType() == Material.WALL_SIGN) {
            switch (sign.getData().getData()) {
                case 5:
                    rtBlock = sign.getBlock().getRelative(BlockFace.WEST);
                    break;
                case 4:
                    rtBlock = sign.getBlock().getRelative(BlockFace.EAST);
                    break;
                case 3:
                    rtBlock = sign.getBlock().getRelative(BlockFace.NORTH);
                    break;
                case 2:
                    rtBlock = sign.getBlock().getRelative(BlockFace.SOUTH);
                    break;
            }

        } else {
            rtBlock = sign.getBlock().getRelative(BlockFace.DOWN);
        }

        if (rtBlock != null) {
            for (Trigger uncasted : gameWorld.getTriggers(TriggerTypeDefault.REDSTONE)) {
                RedstoneTrigger trigger = (RedstoneTrigger) uncasted;
                if (trigger.rtBlock.equals(rtBlock)) {
                    return trigger;
                }
            }
            return new RedstoneTrigger(rtBlock);
        }
        return null;
    }

    public static void updateAll(GameWorld gameWorld) {
        for (Trigger trigger : gameWorld.getTriggers(TriggerTypeDefault.REDSTONE)) {
            ((RedstoneTrigger) trigger).onTrigger();
        }
    }

}
