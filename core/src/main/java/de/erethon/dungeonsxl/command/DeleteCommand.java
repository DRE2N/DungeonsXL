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
import de.erethon.commons.misc.FileUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.player.DPermission;
import de.erethon.dungeonsxl.world.DEditWorld;
import de.erethon.dungeonsxl.world.DResourceWorld;
import java.io.File;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class DeleteCommand extends DCommand {

    public DeleteCommand(DungeonsXL plugin) {
        super(plugin);
        setCommand("delete");
        setMinArgs(1);
        setMaxArgs(2);
        setHelp(DMessage.CMD_DELETE_HELP.getMessage());
        setPermission(DPermission.DELETE.getNode());
        setPlayerCommand(true);
        setConsoleCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        DResourceWorld resource = instances.getResourceByName(args[1]);
        if (resource == null) {
            MessageUtil.sendMessage(sender, DMessage.ERROR_NO_SUCH_MAP.getMessage(args[1]));
            return;
        }

        if (args.length == 2 && sender instanceof Player) {
            ClickEvent onClickConfirm = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dungeonsxl delete " + args[1] + " true");
            TextComponent confirm = new TextComponent(DMessage.MISC_YES.getMessage());
            confirm.setClickEvent(onClickConfirm);

            ClickEvent onClickDeny = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dungeonsxl delete " + args[1] + " false");
            TextComponent deny = new TextComponent(DMessage.MISC_NO.getMessage());
            deny.setClickEvent(onClickDeny);

            MessageUtil.sendMessage(sender, DMessage.CMD_DELETE_BACKUPS.getMessage());
            MessageUtil.sendMessage(sender, confirm, new TextComponent(" "), deny);

            return;
        }

        for (DEditWorld editWorld : instances.getEditWorlds()) {
            if (editWorld.getResource().equals(resource)) {
                editWorld.delete(false);
            }
        }
        instances.removeResource(resource);
        FileUtil.removeDir(resource.getFolder());

        if (args[2].equalsIgnoreCase("true")) {
            for (File file : DungeonsXL.BACKUPS.listFiles()) {
                if (file.getName().startsWith(resource.getName() + "-")) {
                    FileUtil.removeDir(file);
                }
            }
        }

        MessageUtil.sendMessage(sender, DMessage.CMD_DELETE_SUCCESS.getMessage(args[1]));
    }

}
