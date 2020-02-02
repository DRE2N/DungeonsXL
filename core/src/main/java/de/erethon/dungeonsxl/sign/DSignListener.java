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

import de.erethon.commons.chat.MessageUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.player.DGamePlayer;
import de.erethon.dungeonsxl.player.DPermission;
import de.erethon.dungeonsxl.trigger.InteractTrigger;
import de.erethon.dungeonsxl.world.DEditWorld;
import de.erethon.dungeonsxl.world.DGameWorld;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
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

    private DungeonsXL plugin;

    public DSignListener(DungeonsXL plugin) {
        this.plugin = plugin;
    }

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
        BlockState state = block.getState();
        if (!(state instanceof Sign)) {
            return;
        }

        Sign sign = (Sign) state;
        DEditWorld editWorld = DEditWorld.getByWorld(sign.getWorld());
        if (editWorld == null) {
            return;
        }

        if (sign != null) {
            sign.setLine(0, lines[0]);
            sign.setLine(1, lines[1]);
            sign.setLine(2, lines[2]);
            sign.setLine(3, lines[3]);

            for (DSignTypeDefault type : DSignTypeDefault.values()) {
                if (!lines[0].equalsIgnoreCase("[" + type.getName() + "]")) {
                    continue;
                }
                if (type.isLegacy()) {
                    MessageUtil.sendMessage(player, ChatColor.RED + "https://erethon.de/resources/dxl-signs/deprecated.gif");
                    MessageUtil.sendMessage(player, ChatColor.LIGHT_PURPLE + "https://github.com/DRE2N/DungeonsXL/wiki/Legacy-support#updating");
                    return;
                }
            }

            DSign dsign = DSign.create(plugin, sign, null);

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
