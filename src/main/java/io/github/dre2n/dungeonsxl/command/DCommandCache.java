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

import io.github.dre2n.commons.command.DRECommand;
import io.github.dre2n.commons.command.DRECommandCache;
import io.github.dre2n.commons.compatibility.CompatibilityHandler;
import io.github.dre2n.commons.javaplugin.DREPlugin;
import io.github.dre2n.dungeonsxl.DungeonsXL;

/**
 * An enumeration of all command instances.
 *
 * @author Daniel Saukel
 */
public class DCommandCache extends DRECommandCache {

    public static BreakCommand BREAK = new BreakCommand();
    public static ChatCommand CHAT = new ChatCommand();
    public static ChatSpyCommand CHAT_SPY = new ChatSpyCommand();
    public static CreateCommand CREATE = new CreateCommand();
    public static EditCommand EDIT = new EditCommand();
    public static EnterCommand ENTER = new EnterCommand();
    public static EscapeCommand ESCAPE = new EscapeCommand();
    public static DeleteCommand DELETE = new DeleteCommand();
    public static GameCommand GAME = new GameCommand();
    public static GroupCommand GROUP = new GroupCommand();
    public static HelpCommand HELP = new HelpCommand();
    public static ImportCommand IMPORT = new ImportCommand();
    public static InviteCommand INVITE = new InviteCommand();
    public static JoinCommand JOIN = new JoinCommand();
    public static KickCommand KICK = new KickCommand();
    public static LeaveCommand LEAVE = new LeaveCommand();
    public static ListCommand LIST = new ListCommand();
    public static LivesCommand LIVES = new LivesCommand();
    public static MainCommand MAIN = new MainCommand();
    public static MsgCommand MESSAGE = new MsgCommand();
    public static PlayCommand PLAY = new PlayCommand();
    public static PortalCommand PORTAL = new PortalCommand();
    public static DRECommand RELOAD = CompatibilityHandler.getInstance().isSpigot() ? new ReloadCommand() : new ReloadCommandNoSpigot();
    public static RenameCommand RENAME = new RenameCommand();
    public static ResourcePackCommand RESOURCE_PACK = new ResourcePackCommand();
    public static SaveCommand SAVE = new SaveCommand();
    public static StatusCommand STATUS = new StatusCommand();
    public static TestCommand TEST = new TestCommand();
    public static UninviteCommand UNINVITE = new UninviteCommand();

    public DCommandCache(DREPlugin plugin) {
        super("dungeonsxl", plugin,
                BREAK,
                CREATE,
                DELETE,
                EDIT,
                ENTER,
                ESCAPE,
                GAME,
                GROUP,
                HELP,
                IMPORT,
                INVITE,
                JOIN,
                KICK,
                LEAVE,
                LIST,
                LIVES,
                MAIN,
                MESSAGE,
                PLAY,
                PORTAL,
                RELOAD,
                RENAME,
                RESOURCE_PACK,
                SAVE,
                STATUS,
                TEST,
                UNINVITE,
                new DeletePortalCommand()
        );
        if (DungeonsXL.getInstance().getMainConfig().isChatEnabled()) {
            addCommand(CHAT);
            addCommand(CHAT_SPY);
        }
    }

}
