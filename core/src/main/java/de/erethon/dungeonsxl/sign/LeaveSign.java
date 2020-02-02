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
package de.erethon.dungeonsxl.sign;

import de.erethon.caliburn.item.VanillaItem;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.event.dplayer.instance.game.DGamePlayerEscapeEvent;
import de.erethon.dungeonsxl.player.DGamePlayer;
import de.erethon.dungeonsxl.trigger.InteractTrigger;
import de.erethon.dungeonsxl.world.DGameWorld;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class LeaveSign extends DSign {

    public LeaveSign(DungeonsXL plugin, Sign sign, String[] lines, DGameWorld gameWorld) {
        super(plugin, sign, lines, gameWorld);
    }

    @Override
    public boolean check() {
        return true;
    }

    @Override
    public void onInit() {
        if (!getTriggers().isEmpty()) {
            getSign().getBlock().setType(VanillaItem.AIR.getMaterial());
            return;
        }

        InteractTrigger trigger = InteractTrigger.getOrCreate(0, getSign().getBlock(), getGameWorld());
        if (trigger != null) {
            trigger.addListener(this);
            addTrigger(trigger);
        }
        getSign().setLine(0, ChatColor.DARK_BLUE + "############");
        getSign().setLine(1, DMessage.SIGN_LEAVE.getMessage());
        getSign().setLine(2, "");
        getSign().setLine(3, ChatColor.DARK_BLUE + "############");
        getSign().update();
    }

    @Override
    public boolean onPlayerTrigger(Player player) {
        DGamePlayer dPlayer = DGamePlayer.getByPlayer(player);
        if (dPlayer != null) {
            DGamePlayerEscapeEvent event = new DGamePlayerEscapeEvent(dPlayer);
            Bukkit.getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                return false;
            }

            dPlayer.leave();
        }

        return true;
    }

    @Override
    public void onTrigger() {
        for (DGamePlayer dPlayer : plugin.getDPlayerCache().getDGamePlayers()) {
            DGamePlayerEscapeEvent event = new DGamePlayerEscapeEvent(dPlayer);
            Bukkit.getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                return;
            }

            dPlayer.leave();
        }
    }

    @Override
    public DSignType getType() {
        return DSignTypeDefault.LEAVE;
    }

}
