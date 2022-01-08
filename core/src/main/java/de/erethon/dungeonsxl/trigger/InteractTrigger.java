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

import de.erethon.dungeonsxl.event.trigger.TriggerActionEvent;
import de.erethon.dungeonsxl.world.DGameWorld;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class InteractTrigger extends Trigger {

    private TriggerType type = TriggerTypeDefault.INTERACT;

    private int interactId;
    private Block interactBlock;

    public InteractTrigger(int id, Block block) {
        interactId = id;
        interactBlock = block;
    }

    public void onTrigger(Player player) {
        TriggerActionEvent event = new TriggerActionEvent(this);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        setTriggered(true);
        this.setPlayer(player);
        updateDSigns();
    }

    @Override
    public TriggerType getType() {
        return type;
    }

    /* Statics */
    public static InteractTrigger getOrCreate(int id, DGameWorld gameWorld) {
        if (id == 0) {
            return null;
        }
        InteractTrigger trigger = getById(id, gameWorld);
        if (trigger != null) {
            return trigger;
        }
        return new InteractTrigger(id, null);
    }

    public static InteractTrigger getOrCreate(int id, Block block, DGameWorld gameWorld) {
        InteractTrigger trigger = getById(id, gameWorld);
        if (trigger != null) {
            trigger.interactBlock = block;
            return trigger;
        }
        return new InteractTrigger(id, block);
    }

    public static InteractTrigger getByBlock(Block block, DGameWorld gameWorld) {
        for (Trigger uncasted : gameWorld.getTriggers(TriggerTypeDefault.INTERACT)) {
            InteractTrigger trigger = (InteractTrigger) uncasted;
            if (trigger.interactBlock != null) {
                if (trigger.interactBlock.equals(block)) {
                    return trigger;
                }
            }
        }
        return null;
    }

    public static InteractTrigger getById(int id, DGameWorld gameWorld) {
        if (id != 0) {
            for (Trigger uncasted : gameWorld.getTriggers(TriggerTypeDefault.INTERACT)) {
                InteractTrigger trigger = (InteractTrigger) uncasted;
                if (trigger.interactId == id) {
                    return trigger;
                }
            }
        }
        return null;
    }

}
