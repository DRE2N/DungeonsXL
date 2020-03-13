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
import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.world.InstanceWorld;
import de.erethon.dungeonsxl.player.DPermission;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class ChatMessageSign extends MessageSign {

    private List<Player> done = new ArrayList<>();

    public ChatMessageSign(DungeonsAPI api, Sign sign, String[] lines, InstanceWorld instance) {
        super(api, sign, lines, instance);
    }

    @Override
    public String getName() {
        return "MSG";
    }

    @Override
    public String getBuildPermission() {
        return DPermission.SIGN.getNode() + ".msg";
    }

    @Override
    public boolean push(Player player) {
        if (!done.contains(player)) {
            MessageUtil.sendMessage(player, text);
            done.add(player);
        }

        if (done.size() >= getGameWorld().getWorld().getPlayers().size()) {
            getGameWorld().removeDungeonSign(this);
        }

        return true;
    }

    @Override
    public void push() {
        for (Player player : getGameWorld().getWorld().getPlayers()) {
            MessageUtil.sendMessage(player, text);
        }
        getGameWorld().removeDungeonSign(this);
    }

}
