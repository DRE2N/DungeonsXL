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
package io.github.dre2n.dungeonsxl.command;

import io.github.dre2n.commons.chat.MessageUtil;
import io.github.dre2n.commons.command.DRECommand;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.DMessage;
import io.github.dre2n.dungeonsxl.global.DPortal;
import io.github.dre2n.dungeonsxl.player.DGamePlayer;
import io.github.dre2n.dungeonsxl.player.DGlobalPlayer;
import io.github.dre2n.dungeonsxl.player.DPermission;
import io.github.dre2n.dungeonsxl.util.LegacyUtil;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class PortalCommand extends DRECommand {

    DungeonsXL plugin = DungeonsXL.getInstance();

    public PortalCommand() {
        setCommand("portal");
        setMinArgs(0);
        setMaxArgs(1);
        setHelp(DMessage.HELP_CMD_PORTAL.getMessage());
        setPermission(DPermission.PORTAL.getNode());
        setPlayerCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        DGlobalPlayer dGlobalPlayer = plugin.getDPlayers().getByPlayer(player);

        if (dGlobalPlayer instanceof DGamePlayer) {
            MessageUtil.sendMessage(player, DMessage.ERROR_LEAVE_DUNGEON.getMessage());
            return;
        }

        Material material = null;

        if (args.length == 2) {
            material = Material.matchMaterial(args[1]);
        }

        if (material == null) {
            material = Material.PORTAL;
        }

        DPortal dPortal = dGlobalPlayer.getPortal();

        if (dPortal == null) {
            dPortal = new DPortal(plugin.getGlobalProtections().generateId(DPortal.class, player.getWorld()), player.getWorld(), material, false);
            dGlobalPlayer.setCreatingPortal(dPortal);
            dPortal.setWorld(player.getWorld());
            dGlobalPlayer.setCachedItem(player.getItemInHand());
            player.getInventory().setItemInHand(new ItemStack(LegacyUtil.WOODEN_SWORD));
            MessageUtil.sendMessage(player, DMessage.PLAYER_PORTAL_INTRODUCTION.getMessage());

        } else {
            dPortal.delete();
            dGlobalPlayer.setCreatingPortal(null);
            player.setItemInHand(dGlobalPlayer.getCachedItem());
            dGlobalPlayer.setCachedItem(null);
            MessageUtil.sendMessage(player, DMessage.PLAYER_PORTAL_ABORT.getMessage());
        }
    }

}
