/*
 * Copyright (C) 2012-2022 Frank Baumann
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
import de.erethon.dungeonsxl.api.world.ResourceWorld;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.dungeon.DungeonConfig;
import de.erethon.dungeonsxl.player.DPermission;
import de.erethon.dungeonsxl.util.commons.chat.MessageUtil;
import de.erethon.dungeonsxl.util.commons.misc.NumberUtil;
import de.erethon.dungeonsxl.world.DResourceWorld;
import java.io.File;
import java.util.ArrayList;
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
        ArrayList<String> dungeonList = new ArrayList<>();
        for (Dungeon dungeon : plugin.getDungeonRegistry()) {
            dungeonList.add(dungeon.getName());
        }
        ArrayList<String> mapList = new ArrayList<>();
        for (File file : DungeonsXL.MAPS.listFiles()) {
            if (!file.equals(DResourceWorld.RAW)) {
                mapList.add(file.getName());
            }
        }
        ArrayList<String> loadedList = new ArrayList<>();
        for (InstanceWorld editWorld : plugin.getInstanceCache().getAllIf(i -> i instanceof EditWorld)) {
            loadedList.add(editWorld.getWorld().getWorldFolder().getName() + " / " + editWorld.getName());
        }
        for (InstanceWorld gameWorld : plugin.getInstanceCache().getAllIf(i -> i instanceof GameWorld)) {
            loadedList.add(gameWorld.getWorld().getWorldFolder().getName() + " / " + gameWorld.getName());
        }
        ArrayList<String> toSend = new ArrayList<>();

        ArrayList<String> stringList = mapList;
        boolean specified = false;
        byte listType = 0;
        if (args.length >= 2) {
            if (args[1].equalsIgnoreCase("dungeons") || args[1].equalsIgnoreCase("d")) {
                if (args.length >= 3) {
                    Dungeon dungeon = plugin.getDungeonRegistry().get(args[2]);
                    if (dungeon != null) {
                        MessageUtil.sendPluginTag(sender, plugin);
                        MessageUtil.sendCenteredMessage(sender, "&4&l[ &6" + dungeon.getName() + " &4&l]");
                        StringBuilder floorList = new StringBuilder();
                        for (int i = 0; i < dungeon.getFloors().size(); i++) {
                            if (i != 0) {
                                floorList.append(", ");
                            }
                            floorList.append(dungeon.getFloors().get(i).getName());
                        }
                        MessageUtil.sendMessage(sender, "&eFloors: &o" + floorList.toString());
                        MessageUtil.sendMessage(sender, "&estartFloor: &o[" + dungeon.getStartFloor().getName() + "]");
                        MessageUtil.sendMessage(sender, "&eendFloor: &o[" + dungeon.getEndFloor().getName() + "]");
                        MessageUtil.sendMessage(sender, "&efloorCount: &o[" + dungeon.getFloorCount() + "]");
                        MessageUtil.sendMessage(sender, "&eremoveWhenPlayed: &o[" + dungeon.getRemoveWhenPlayed() + "]");
                        return;
                    }
                }
                specified = true;
                stringList = dungeonList;
                listType = 1;

            } else if (args[1].equalsIgnoreCase("maps") || args[1].equalsIgnoreCase("m")) {
                specified = true;

            } else if (args[1].equalsIgnoreCase("loaded") || args[1].equalsIgnoreCase("l")) {
                specified = true;
                stringList = loadedList;
                listType = 2;
            }
        }

        int page = 1;
        if (args.length == 3) {
            page = NumberUtil.parseInt(args[2], 1);

        } else if (args.length == 2 & !specified) {
            page = NumberUtil.parseInt(args[1], 1);
        }

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

        switch (listType) {
            case 0:
                MessageUtil.sendMessage(sender, "&4Map&7 | &eInvited");
                for (String map : toSend) {
                    boolean invited = false;
                    if (sender instanceof Player) {
                        ResourceWorld resource = plugin.getMapRegistry().get(map);
                        if (resource != null) {
                            invited = resource.isInvitedPlayer((Player) sender);
                        }
                    }

                    MessageUtil.sendMessage(sender, "&b" + map + "&7 | &e" + invited);
                }
                break;
            case 1:
                MessageUtil.sendMessage(sender, "&4Dungeon&7 | &eMap count");
                for (String dungeon : toSend) {
                    DungeonConfig dungeonConfig = new DungeonConfig(plugin, new File(DungeonsXL.DUNGEONS, dungeon + ".yml"));
                    int count = dungeonConfig.getFloors().size() + 2;
                    MessageUtil.sendMessage(sender, "&b" + dungeon + "&7 | &e" + count);
                }
                break;
            case 2:
                MessageUtil.sendMessage(sender, "&4Loaded map");
                for (String map : toSend) {
                    MessageUtil.sendMessage(sender, "&b" + map);
                }
                break;
            default:
                break;
        }
    }

}
