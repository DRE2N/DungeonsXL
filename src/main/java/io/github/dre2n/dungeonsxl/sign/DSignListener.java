/*
 * Copyright (C) 2012-2018 Frank Baumann
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
package io.github.dre2n.dungeonsxl.sign;

import io.github.dre2n.commons.chat.MessageUtil;
import io.github.dre2n.dungeonsxl.config.DMessage;
import io.github.dre2n.dungeonsxl.player.DGamePlayer;
import io.github.dre2n.dungeonsxl.player.DPermission;
import io.github.dre2n.dungeonsxl.trigger.InteractTrigger;
import io.github.dre2n.dungeonsxl.world.DEditWorld;
import io.github.dre2n.dungeonsxl.world.DGameWorld;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class DSignListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }
        DGamePlayer dPlayer = DGamePlayer.getByPlayer(player);
        if (dPlayer == null) {
            return;
        }

        DGameWorld gameWorld = DGameWorld.getByWorld(player.getWorld());
        if (gameWorld == null) {
            return;
        }

        InteractTrigger trigger = InteractTrigger.getByBlock(clickedBlock, gameWorld);
        if (trigger != null) {
            if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                trigger.onTrigger(player);
            }
        }

        for (Sign classSign : gameWorld.getClassesSigns()) {
            if (classSign != null) {
                if (classSign.getLocation().distance(clickedBlock.getLocation()) < 1) {
                    if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        dPlayer.setDClass(ChatColor.stripColor(classSign.getLine(1)));
                    }
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        String[] lines = event.getLines();
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Sign sign = (Sign) block.getState();
        DEditWorld editWorld = DEditWorld.getByWorld(sign.getWorld());
        if (editWorld == null) {
            return;
        }

        if (sign != null) {
            sign.setLine(0, lines[0]);
            sign.setLine(1, lines[1]);
            sign.setLine(2, lines[2]);
            sign.setLine(3, lines[3]);

            DSign dsign = DSign.create(sign, null);

            if (dsign == null) {
                return;
            }

            if (!DPermission.hasPermission(player, dsign.getType().getBuildPermission())) {
                MessageUtil.sendMessage(player, DMessage.ERROR_NO_PERMISSIONS.getMessage());
                return;
            }

            if (dsign.check()) {
                editWorld.registerSign(block);
                editWorld.getSigns().add(block);
                MessageUtil.sendMessage(player, DMessage.PLAYER_SIGN_CREATED.getMessage());

            } else {
                MessageUtil.sendMessage(player, DMessage.ERROR_SIGN_WRONG_FORMAT.getMessage());
            }
        }
    }

}
