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
package de.erethon.dungeonsxl.sign.button;

import de.erethon.commons.chat.MessageUtil;
import de.erethon.commons.misc.NumberUtil;
import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.world.InstanceWorld;
import de.erethon.dungeonsxl.player.DPermission;
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
        super.initialize();

        String[] line1 = text.split("/");
        title = line1[0];
        if (line1.length > 1) {
            subtitle = line1[1];
        } else {
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
    public boolean push(Player player) {
        MessageUtil.sendTitleMessage(player, title, subtitle, fadeIn, stay, fadeOut);
        return true;
    }

}
