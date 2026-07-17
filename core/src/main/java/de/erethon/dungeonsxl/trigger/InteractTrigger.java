/*
 * Copyright (C) 2012-2013 Frank Baumann; 2015-2026 Daniel Saukel
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

import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.sign.DungeonSign;
import de.erethon.dungeonsxl.api.trigger.AbstractTrigger;
import de.erethon.dungeonsxl.api.trigger.LogicalExpression;
import de.erethon.dungeonsxl.api.trigger.Trigger;
import de.erethon.dungeonsxl.api.trigger.TriggerListener;
import de.erethon.dungeonsxl.api.trigger.TriggerTypeKey;
import de.erethon.dungeonsxl.api.world.GameWorld;
import de.erethon.xlib.util.NumberUtil;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class InteractTrigger extends AbstractTrigger {

    private static int unusedId = Integer.MIN_VALUE;

    private int id;

    public InteractTrigger(DungeonsAPI api, TriggerListener owner, LogicalExpression expression, String value) {
        super(api, owner, expression, value);
        id = NumberUtil.parseInt(value);
    }

    public InteractTrigger(DungeonsAPI api, TriggerListener owner, LogicalExpression expression, int value) {
        super(api, owner, expression, String.valueOf(value));
        id = value;
    }

    public InteractTrigger(DungeonsAPI api, TriggerListener owner) {
        super(api, owner, LogicalExpression.parse("I" + unusedId), String.valueOf(unusedId++));
    }

    @Override
    public char getKey() {
        return TriggerTypeKey.INTERACT;
    }

    @Override
    public boolean isIdentifiableByValue() {
        return true;
    }

    @Override
    public void onTrigger(boolean switching) {
        setTriggered(true);
    }

    /* Statics */
    public static InteractTrigger getByBlock(Block block, GameWorld gameWorld) {
        if (block == null || gameWorld == null) {
            return null;
        }
        DungeonSign dSign = gameWorld.getDungeonSign(block);
        if (dSign == null) {
            return null;
        }

        for (Trigger trigger : dSign.getTriggers()) {
            if (!(trigger instanceof InteractTrigger)) {
                continue;
            }
            return (InteractTrigger) trigger;
        }
        return null;
    }

    public static InteractTrigger getById(int id, GameWorld gameWorld) {
        if (gameWorld == null) {
            return null;
        }
        for (Trigger uncasted : gameWorld.getTriggers()) {
            if (!(uncasted instanceof InteractTrigger)) {
                continue;
            }
            InteractTrigger trigger = (InteractTrigger) uncasted;
            if (id == trigger.id) {
                return trigger;
            }
        }
        return null;
    }

    public static void applyDefaultSignLayout(DungeonSign dungeonSign, String line1, String line2) {
        if (!dungeonSign.getTriggerExpression().isEmpty()) {
            dungeonSign.setToAir();
            return;
        }

        Sign sign = dungeonSign.getSign();
        sign.setLine(0, ChatColor.DARK_BLUE + "############");
        sign.setLine(1, ChatColor.GREEN + line1);
        sign.setLine(2, ChatColor.GREEN + line2);
        sign.setLine(3, ChatColor.DARK_BLUE + "############");
        sign.update();
    }

}
