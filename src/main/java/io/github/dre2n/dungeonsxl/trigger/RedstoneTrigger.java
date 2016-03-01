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
import io.github.dre2n.dungeonsxl.game.GameWorld;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class RedstoneTrigger extends Trigger {

    private static Map<GameWorld, ArrayList<RedstoneTrigger>> triggers = new HashMap<>();

    private TriggerType type = TriggerTypeDefault.REDSTONE;

    private Block rtBlock;

    public RedstoneTrigger(Block block) {
        rtBlock = block;
    }

    public void onTrigger() {
        TriggerActionEvent event = new TriggerActionEvent(this);

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
    public void register(GameWorld gameWorld) {
        if (!hasTriggers(gameWorld)) {
            ArrayList<RedstoneTrigger> list = new ArrayList<>();
            list.add(this);
            triggers.put(gameWorld, list);

        } else {
            triggers.get(gameWorld).add(this);
        }
    }

    @Override
    public void unregister(GameWorld gameWorld) {
        if (hasTriggers(gameWorld)) {
            triggers.get(gameWorld).remove(this);
        }
    }

    @Override
    public TriggerType getType() {
        return type;
    }

    @SuppressWarnings("deprecation")
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
            if (hasTriggers(gameWorld)) {
                for (RedstoneTrigger trigger : getTriggers(gameWorld)) {
                    if (trigger.rtBlock.equals(rtBlock)) {
                        return trigger;
                    }
                }
            }
            return new RedstoneTrigger(rtBlock);
        }
        return null;
    }

    public static void updateAll(GameWorld gameWorld) {
        if (hasTriggers(gameWorld)) {
            for (RedstoneTrigger trigger : getTriggersArray(gameWorld)) {
                trigger.onTrigger();
            }
        }
    }

    public static boolean hasTriggers(GameWorld gameWorld) {
        return !triggers.isEmpty() && triggers.containsKey(gameWorld);
    }

    public static ArrayList<RedstoneTrigger> getTriggers(GameWorld gameWorld) {
        return triggers.get(gameWorld);
    }

    public static RedstoneTrigger[] getTriggersArray(GameWorld gameWorld) {
        return getTriggers(gameWorld).toArray(new RedstoneTrigger[getTriggers(gameWorld).size()]);
    }

}
