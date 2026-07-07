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
import de.erethon.dungeonsxl.api.world.EditWorld;
import de.erethon.dungeonsxl.api.world.GameWorld;
import de.erethon.dungeonsxl.api.world.InstanceWorld;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.player.DPermission;
import de.erethon.dungeonsxl.util.ParsingUtil;
import de.erethon.xlib.chat.MessageUtil;
import de.erethon.xlib.util.NumberUtil;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class ListCommand extends DCommand {

    public ListCommand(DungeonsXL plugin) {
        super(plugin);
        setCommand("list");
        setMinArgs(0);
        setMaxArgs(3);
        setHelp(DMessage.CMD_LIST_HELP.getMessage());
        setPermission(DPermission.LIST.getNode());
        setPlayerCommand(true);
        setConsoleCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        List<String> stringList = null;
        boolean specified = false;
        boolean loaded = false;
        if (args.length >= 2) {
            if (args[1].equalsIgnoreCase("dungeons") || args[1].equalsIgnoreCase("d")) {
                specified = true;
            }
            if (args[1].equalsIgnoreCase("loaded") || args[1].equalsIgnoreCase("l")) {
                specified = true;
                loaded = true;
                stringList = loaded(args, sender);
            }
        }
        if (stringList == null) {
            stringList = dungeons(args, sender);
        }

        int page = 1;
        if (args.length == 3) {
            page = NumberUtil.parseInt(args[2], 1);
        } else if (args.length == 2 & !specified) {
            page = NumberUtil.parseInt(args[1], 1);
        }
        List<String> toSend = page(sender, stringList, page);

        MessageUtil.sendMessage(sender, loaded ? "&4Loaded map" : "&4Dungeon&7 | &eInvited");
        toSend.forEach(m -> MessageUtil.sendMessage(sender, m));
    }

    public List<String> dungeons(String[] args, CommandSender sender) {
        ArrayList<String> dungeonList = new ArrayList<>();
        for (Dungeon dungeon : plugin.getDungeonRegistry()) {
            String invited = "N/A";
            if (sender instanceof Player) {
                invited = ParsingUtil.getBooleanSymbol(dungeon.isInvitedPlayer((Player) sender));
            }
            dungeonList.add("&b" + dungeon.getName() + "&7 | &e" + invited);
        }
        return dungeonList;
    }

    public List<String> loaded(String[] args, CommandSender sender) {
        ArrayList<String> loadedList = new ArrayList<>();
        for (InstanceWorld editWorld : plugin.getInstanceCache().getAllIf(i -> i instanceof EditWorld)) {
            loadedList.add("&b" + editWorld.getWorld().getWorldFolder().getName() + " / " + editWorld.getName());
        }
        for (InstanceWorld gameWorld : plugin.getInstanceCache().getAllIf(i -> i instanceof GameWorld)) {
            loadedList.add("&b" + gameWorld.getWorld().getWorldFolder().getName() + " / " + gameWorld.getName());
        }
        return loadedList;
    }

    public List<String> page(CommandSender sender, List<String> stringList, int page) {
        List<String> toSend = new ArrayList<>();

        int send = 0;
        int max = 0;
        int min = 0;
        for (String string : stringList) {
            send++;
            if (send >= page * 5 - 4 && send <= page * 5) {
                min = page * 5 - 4;
                max = page * 5;
                toSend.add(string);
            }
        }

        MessageUtil.sendPluginTag(sender, plugin);
        MessageUtil.sendCenteredMessage(sender, "&4&l[ &6" + min + "-" + max + " &4/&6 " + send + " &4|&6 " + page + " &4&l]");
        return toSend;
    }

}
