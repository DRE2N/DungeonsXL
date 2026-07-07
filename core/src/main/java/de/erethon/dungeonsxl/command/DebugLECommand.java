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
import de.erethon.dungeonsxl.api.trigger.LogicalExpression;
import de.erethon.xlib.chat.MessageUtil;
import org.bukkit.command.CommandSender;

/**
 * @author Daniel Saukel
 */
public class DebugLECommand extends DCommand {

    public DebugLECommand(DungeonsXL plugin) {
        super(plugin);
        setCommand("dle");
        setMinArgs(1);
        setMaxArgs(1);
        setHelp("Logical expression debug command");
        setPlayerCommand(true);
        setConsoleCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        LogicalExpression le = LogicalExpression.parse(args[1]);
        MessageUtil.sendMessage(sender, "toString: " + le.toString());
        MessageUtil.sendMessage(sender, "Atomic: " + le.isAtomic());
        MessageUtil.sendMessage(sender, "Contents: " + le.getContents(false));
        MessageUtil.sendMessage(sender, "Contents deep: " + le.getContents(true));
    }

}
