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
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Daniel Saukel
 */
public class DCommands {

    private CopyOnWriteArrayList<BRCommand> dCommands = new CopyOnWriteArrayList<>();

    // Methods
    public DCommands() {
        // Add Commands
        dCommands.add(new HelpCommand());
        dCommands.add(new BreakCommand());
        dCommands.add(new ChatCommand());
        dCommands.add(new ChatSpyCommand());
        dCommands.add(new CreateCommand());
        dCommands.add(new EditCommand());
        dCommands.add(new EscapeCommand());
        dCommands.add(new GameCommand());
        dCommands.add(new GroupCommand());
        dCommands.add(new InviteCommand());
        dCommands.add(new LeaveCommand());
        dCommands.add(new ListCommand());
        dCommands.add(new LivesCommand());
        dCommands.add(new MainCommand());
        dCommands.add(new UninviteCommand());
        dCommands.add(new MsgCommand());
        dCommands.add(new PlayCommand());
        dCommands.add(new PortalCommand());
        dCommands.add(new DeletePortalCommand());
        dCommands.add(new ReloadCommand());
        dCommands.add(new SaveCommand());
        dCommands.add(new TestCommand());
    }

    /**
     * @param command
     * usually the first command variable
     */
    public BRCommand getDCommand(String command) {
        for (BRCommand dCommand : dCommands) {
            if (dCommand.getCommand().equals(command)) {
                return dCommand;
            }
        }

        return null;
    }

    /**
     * @return the dCommands
     */
    public List<BRCommand> getDCommands() {
        return dCommands;
    }

}
