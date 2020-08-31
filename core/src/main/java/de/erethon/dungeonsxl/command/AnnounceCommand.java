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
package de.erethon.dungeonsxl.command;

import de.erethon.commons.chat.MessageUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.announcer.Announcer;
import de.erethon.dungeonsxl.api.player.GlobalPlayer;
import de.erethon.dungeonsxl.api.player.InstancePlayer;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.player.DGlobalPlayer;
import de.erethon.dungeonsxl.player.DPermission;
import java.util.List;
import org.bukkit.command.CommandSender;

/**
 * @author Goh Wei Wen
 */
public class AnnounceCommand extends DCommand {

    public AnnounceCommand(DungeonsXL plugin) {
        super(plugin);
        setCommand("announce");
        setMinArgs(1);
        setMaxArgs(1);
        setHelp(DMessage.CMD_ANNOUNCE_HELP.getMessage());
        setPermission(DPermission.ANNOUNCE.getNode());
        setPlayerCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        String name = args[1];
        Announcer announcer = plugin.getAnnouncerCache().getByName(name);

        if (announcer == null) {
            MessageUtil.sendMessage(sender, DMessage.ERROR_NO_SUCH_ANNOUNCER.getMessage(name));
            return;
        }

        List<String> worlds = announcer.getWorlds();
        for (GlobalPlayer dPlayer : plugin.getPlayerCache()) {
            if (!(dPlayer instanceof InstancePlayer) && ((DGlobalPlayer) dPlayer).isAnnouncerEnabled()) {
                if (worlds.isEmpty() || worlds.contains(dPlayer.getPlayer().getWorld().getName())) {
                    announcer.send(dPlayer.getPlayer());
                }
            }
        }
    }

}
