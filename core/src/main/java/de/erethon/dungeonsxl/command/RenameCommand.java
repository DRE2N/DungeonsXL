/*
 * Copyright (C) 2012-2013 Frank Baumann; 2015-2026 Daniel Saukel
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

import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.api.dungeon.Dungeon;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.global.GlobalProtection;
import de.erethon.dungeonsxl.global.JoinSign;
import de.erethon.dungeonsxl.player.DPermission;
import de.erethon.dungeonsxl.dungeon.DDungeon;
import de.erethon.xlib.chat.MessageUtil;
import java.util.Set;
import org.bukkit.command.CommandSender;

/**
 * @author Daniel Saukel
 */
public class RenameCommand extends DCommand {

    public RenameCommand(DungeonsXL plugin) {
        super(plugin);
        setCommand("rename");
        setMinArgs(2);
        setMaxArgs(2);
        setHelp(DMessage.CMD_RENAME_HELP.getMessage());
        setPermission(DPermission.RENAME.getNode());
        setPlayerCommand(true);
        setConsoleCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        DDungeon dungeon = (DDungeon) plugin.getDungeonRegistry().get(args[1]);
        if (dungeon == null) {
            MessageUtil.sendMessage(sender, DMessage.ERROR_NO_SUCH_MAP.getMessage(args[1]));
            return;
        }

        if (plugin.getDungeonRegistry().get(args[2]) == null) {
            MessageUtil.sendMessage(sender, DMessage.ERROR_NAME_IN_USE.getMessage(args[2]));
            return;
        }

        if (dungeon.getEditWorld() != null) {
            dungeon.getEditWorld().delete(true);
        }

        dungeon.setName(args[2]);

        boolean changed = false;
        Set<GlobalProtection> protections = plugin.getGlobalProtectionCache().getProtections();
        for (GlobalProtection protection : protections.toArray(new GlobalProtection[protections.size()])) {
            if (!(protection instanceof JoinSign)) {
                continue;
            }
            Dungeon pDungeon = ((JoinSign) protection).getDungeon();
            if (pDungeon == null) {
                protection.delete();
                continue;
            }
            if (pDungeon.getName().equals(args[1])) {
                // TODO: Why necessary?
                // pDungeon.setName(args[2]);
                changed = true;
            }
        }

        if (changed) {
            plugin.getGlobalProtectionCache().saveAll();
        }

        MessageUtil.sendMessage(sender, DMessage.CMD_RENAME_SUCCESS.getMessage(args[1], args[2]));
    }

}
