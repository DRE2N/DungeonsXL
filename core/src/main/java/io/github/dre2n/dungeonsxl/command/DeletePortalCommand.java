/*
 * Copyright (C) 2012-2017 Frank Baumann
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
import io.github.dre2n.dungeonsxl.config.DMessages;
import io.github.dre2n.dungeonsxl.global.DPortal;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Daniel Saukel
 * @deprecated Use BreakCommand instead.
 */
@Deprecated
public class DeletePortalCommand extends BRCommand {

    public DeletePortalCommand() {
        setCommand("deleteportal");
        setMinArgs(0);
        setMaxArgs(0);
        setHelp("/dxl deleteportal - Deletes the portal you are looking at");
        setPermission("dxl.portal");
        setPlayerCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        DPortal dPortal = DPortal.getByLocation(player.getTargetBlock((Set<Material>) null, 20).getLocation());

        if (dPortal != null) {
            dPortal.delete();
            MessageUtil.sendMessage(player, DMessages.PLAYER_PROTECTED_BLOCK_DELETED.getMessage());

        } else {
            MessageUtil.sendMessage(player, DMessages.ERROR_NO_PROTECTED_BLOCK.getMessage());
        }
    }

}
