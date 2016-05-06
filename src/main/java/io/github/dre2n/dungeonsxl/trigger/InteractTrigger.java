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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class InteractTrigger extends Trigger {

    private static Map<GameWorld, ArrayList<InteractTrigger>> triggers = new HashMap<>();

    private TriggerType type = TriggerTypeDefault.INTERACT;

    private int interactId;
    private Block interactBlock;

    public InteractTrigger(int id, Block block) {
        interactId = id;
        interactBlock = block;
    }

    public void onTrigger(Player player) {
        TriggerActionEvent event = new TriggerActionEvent(this);
        plugin.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        setTriggered(true);
        this.setPlayer(player);
        updateDSigns();
    }

    @Override
    public void register(GameWorld gameWorld) {
        if (!hasTriggers(gameWorld)) {
            ArrayList<InteractTrigger> list = new ArrayList<>();
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

    public static InteractTrigger getOrCreate(int id, GameWorld gameWorld) {
        if (id == 0) {
            return null;
        }
        InteractTrigger trigger = get(id, gameWorld);
        if (trigger != null) {
            return trigger;
        }
        return new InteractTrigger(id, null);
    }

    public static InteractTrigger getOrCreate(int id, Block block, GameWorld gameWorld) {
        InteractTrigger trigger = get(id, gameWorld);
        if (trigger != null) {
            trigger.interactBlock = block;
            return trigger;
        }
        return new InteractTrigger(id, block);
    }

    public static InteractTrigger get(Block block, GameWorld gameWorld) {
        if (hasTriggers(gameWorld)) {
            for (InteractTrigger trigger : triggers.get(gameWorld)) {
                if (trigger.interactBlock != null) {
                    if (trigger.interactBlock.equals(block)) {
                        return trigger;
                    }
                }
            }
        }
        return null;
    }

    public static InteractTrigger get(int id, GameWorld gameWorld) {
        if (id != 0) {
            if (hasTriggers(gameWorld)) {
                for (InteractTrigger trigger : triggers.get(gameWorld)) {
                    if (trigger.interactId == id) {
                        return trigger;
                    }
                }
            }
        }
        return null;
    }

    public static boolean hasTriggers(GameWorld gameWorld) {
        return !triggers.isEmpty() && triggers.containsKey(gameWorld);
    }

}
