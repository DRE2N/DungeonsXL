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
package de.erethon.dungeonsxl.sign.button;

import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.dungeon.GameRule;
import de.erethon.dungeonsxl.api.world.InstanceWorld;
import de.erethon.dungeonsxl.player.DPermission;
import de.erethon.dungeonsxl.util.commons.chat.MessageUtil;
import de.erethon.dungeonsxl.util.commons.misc.NumberUtil;
import java.util.Map;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class TitleSign extends MessageSign {

    private String title, subtitle;
    private int fadeIn = 10, stay = 70, fadeOut = 20;

    public TitleSign(DungeonsAPI api, Sign sign, String[] lines, InstanceWorld instance) {
        super(api, sign, lines, instance);
    }

    @Override
    public String getName() {
        return "Title";
    }

    @Override
    public String getBuildPermission() {
        return DPermission.SIGN.getNode() + ".title";
    }

    @Override
    public void initialize() {
        String[] line1 = getLine(1).split(",");
        Map<Integer, String> messages = getGameWorld().getDungeon().getRules().getState(GameRule.MESSAGES);
        int id0 = NumberUtil.parseInt(line1[0], -1);
        title = messages.get(id0);
        if (title == null) {
            markAsErroneous("Unknown message, ID: " + getLine(1));
            return;
        }
        if (line1.length > 1) {
            int id1 = NumberUtil.parseInt(line1[1], -1);
            subtitle = messages.get(id1);
            if (subtitle == null) {
                markAsErroneous("Unknown message, ID: " + getLine(1));
                return;
            }
        }
        if (subtitle == null) {
            subtitle = "";
        }

        if (getLine(2).isEmpty()) {
            return;
        }
        String[] line2 = getLine(2).split(",");
        if (line2.length != 3) {
            return;
        }
        fadeIn = NumberUtil.parseInt(line2[0], fadeIn);
        stay = NumberUtil.parseInt(line2[1], stay);
        fadeOut = NumberUtil.parseInt(line2[2], fadeOut);
    }

    @Override
    public void sendMessage(Player player) {
        MessageUtil.sendTitleMessage(player, title, subtitle, fadeIn, stay, fadeOut);
    }

}
