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

import de.erethon.commons.command.DRECommand;
import de.erethon.commons.command.DRECommandCache;
import de.erethon.dungeonsxl.DungeonsXL;

/**
 * An enumeration of all command instances.
 *
 * @author Daniel Saukel
 */
public class DCommandCache extends DRECommandCache {

    public static final String LABEL = "dungeonsxl";

    public BreakCommand breakCmd;
    public ChatCommand chat;
    public ChatSpyCommand chatSpy;
    public CreateCommand create;
    public EditCommand edit;
    public EnterCommand enter;
    public EscapeCommand escape;
    public DeleteCommand delete;
    public DungeonItemCommand dungeonItem;
    public GameCommand game;
    public GroupCommand group;
    public HelpCommand help;
    public ImportCommand importCmd;
    public InviteCommand invite;
    public JoinCommand join;
    public KickCommand kick;
    public LeaveCommand leave;
    public ListCommand list;
    public LivesCommand lives;
    public MainCommand main;
    public MsgCommand message;
    public PlayCommand play;
    public PortalCommand portal;
    public DRECommand reload;
    public RenameCommand rename;
    public ResourcePackCommand resourcePack;
    public SaveCommand save;
    public StatusCommand status;
    public TestCommand test;
    public UninviteCommand uninvite;

    public DCommandCache(DungeonsXL plugin) {
        super(LABEL, plugin);

        breakCmd = new BreakCommand(plugin);
        chat = new ChatCommand(plugin);
        chatSpy = new ChatSpyCommand(plugin);
        create = new CreateCommand(plugin);
        edit = new EditCommand(plugin);
        enter = new EnterCommand(plugin);
        escape = new EscapeCommand(plugin);
        delete = new DeleteCommand(plugin);
        dungeonItem = new DungeonItemCommand(plugin);
        game = new GameCommand(plugin);
        group = new GroupCommand(plugin);
        help = new HelpCommand(plugin);
        importCmd = new ImportCommand(plugin);
        invite = new InviteCommand(plugin);
        join = new JoinCommand(plugin);
        kick = new KickCommand(plugin);
        leave = new LeaveCommand(plugin);
        list = new ListCommand(plugin);
        lives = new LivesCommand(plugin);
        main = new MainCommand(plugin);
        message = new MsgCommand(plugin);
        play = new PlayCommand(plugin);
        portal = new PortalCommand(plugin);
        reload = new ReloadCommand(plugin);
        rename = new RenameCommand(plugin);
        resourcePack = new ResourcePackCommand(plugin);
        save = new SaveCommand(plugin);
        status = new StatusCommand(plugin);
        test = new TestCommand(plugin);
        uninvite = new UninviteCommand(plugin);

        addCommand(breakCmd);
        addCommand(create);
        addCommand(delete);
        addCommand(dungeonItem);
        addCommand(edit);
        addCommand(enter);
        addCommand(escape);
        addCommand(game);
        addCommand(group);
        addCommand(help);
        addCommand(importCmd);
        addCommand(invite);
        addCommand(join);
        addCommand(kick);
        addCommand(leave);
        addCommand(list);
        addCommand(lives);
        addCommand(main);
        addCommand(message);
        addCommand(play);
        addCommand(portal);
        addCommand(reload);
        addCommand(rename);
        addCommand(resourcePack);
        addCommand(save);
        addCommand(status);
        addCommand(test);
        addCommand(uninvite);
        if (plugin.getMainConfig().isChatEnabled()) {
            addCommand(chat);
            addCommand(chatSpy);
        }
    }

}
