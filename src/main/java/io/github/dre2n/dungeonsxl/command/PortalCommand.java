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
package io.github.dre2n.dungeonsxl.command;

import io.github.dre2n.commons.command.BRCommand;
import io.github.dre2n.commons.util.messageutil.MessageUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.DMessages;
import io.github.dre2n.dungeonsxl.global.DPortal;
import io.github.dre2n.dungeonsxl.player.DGlobalPlayer;
import io.github.dre2n.dungeonsxl.player.DPermissions;
import io.github.dre2n.dungeonsxl.player.DPlayer;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class PortalCommand extends BRCommand {

    protected static DungeonsXL plugin = DungeonsXL.getInstance();

    public PortalCommand() {
        setCommand("portal");
        setMinArgs(0);
        setMaxArgs(0);
        setHelp(DMessages.HELP_CMD_PORTAL.getMessage());
        setPermission(DPermissions.PORTAL.getNode());
        setPlayerCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        DGlobalPlayer dGlobalPlayer = plugin.getDPlayers().getByPlayer(player);

        if (dGlobalPlayer instanceof DPlayer) {
            MessageUtil.sendMessage(player, DMessages.ERROR_LEAVE_DUNGEON.getMessage());
            return;
        }

        DPortal dPortal = dGlobalPlayer.getPortal();

        if (dPortal == null) {
            dPortal = new DPortal(plugin.getGlobalProtections().generateId(DPortal.class, player.getWorld()), player.getWorld(), false);
            dGlobalPlayer.setCreatingPortal(dPortal);
            dPortal.setWorld(player.getWorld());
            player.getInventory().setItemInHand(new ItemStack(Material.WOOD_SWORD));
            MessageUtil.sendMessage(player, DMessages.PLAYER_PORTAL_INTRODUCTION.getMessage());

        } else {
            dPortal.delete();
            dGlobalPlayer.setCreatingPortal(null);
            MessageUtil.sendMessage(player, DMessages.PLAYER_PORTAL_ABORT.getMessage());
        }
    }

}
