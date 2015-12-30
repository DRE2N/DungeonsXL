package io.github.dre2n.dungeonsxl.command;

import io.github.dre2n.dungeonsxl.dungeon.Dungeon;
import io.github.dre2n.dungeonsxl.dungeon.DungeonConfig;
import io.github.dre2n.dungeonsxl.dungeon.EditWorld;
import io.github.dre2n.dungeonsxl.dungeon.WorldConfig;
import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
import io.github.dre2n.dungeonsxl.file.DMessages.Messages;
import io.github.dre2n.dungeonsxl.util.IntegerUtil;
import io.github.dre2n.dungeonsxl.util.messageutil.MessageUtil;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.command.CommandSender;

public class ListCommand extends DCommand {
	
	public ListCommand() {
		setCommand("list");
		setMinArgs(0);
		setMaxArgs(3);
		setHelp(dMessages.getMessage(Messages.HELP_CMD_LIST));
		setPermission("dxl.list");
		setPlayerCommand(true);
		setConsoleCommand(true);
	}
	
	@Override
	public void onExecute(String[] args, CommandSender sender) {
		File dungeonFolder = new File(plugin.getDataFolder() + "/dungeons");
		File mapFolder = new File(plugin.getDataFolder() + "/maps");
		ArrayList<String> dungeonList = new ArrayList<String>();
		for (Dungeon dungeon : plugin.getDungeons().getDungeons()) {
			dungeonList.add(dungeon.getName());
		}
		ArrayList<String> mapList = new ArrayList<String>();
		for (File file : mapFolder.listFiles()) {
			mapList.add(file.getName());
		}
		ArrayList<String> loadedList = new ArrayList<String>();
		for (EditWorld editWorld : plugin.getEditWorlds()) {
			loadedList.add(editWorld.getWorld().getWorldFolder().getName());
		}
		for (GameWorld gameWorld : plugin.getGameWorlds()) {
			loadedList.add(gameWorld.getWorld().getWorldFolder().getName());
		}
		ArrayList<String> toSend = new ArrayList<String>();
		
		ArrayList<String> stringList = mapList;
		boolean specified = false;
		byte listType = 0;
		if (args.length >= 2) {
			if (args[1].equalsIgnoreCase("dungeons") || args[1].equalsIgnoreCase("d")) {
				if (args.length >= 3) {
					Dungeon dungeon = plugin.getDungeons().getDungeon(args[2]);
					if (dungeon != null) {
						MessageUtil.sendPluginTag(sender, plugin);
						MessageUtil.sendCenteredMessage(sender, "&4&l[ &6" + dungeon.getName() + " &4&l]");
						MessageUtil.sendMessage(sender, "&eFloors: &o" + dungeon.getConfig().getFloors());
						MessageUtil.sendMessage(sender, "&estartFloor: &o[" + dungeon.getConfig().getStartFloor() + "]");
						MessageUtil.sendMessage(sender, "&eendFloor: &o[" + dungeon.getConfig().getEndFloor() + "]");
						MessageUtil.sendMessage(sender, "&efloorCount: &o[" + dungeon.getConfig().getFloorCount() + "]");
						MessageUtil.sendMessage(sender, "&eremoveWhenPlayed: &o[" + dungeon.getConfig().getRemoveWhenPlayed() + "]");
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
			page = IntegerUtil.parseInt(args[2], 1);
			
		} else if (args.length == 2 & !specified) {
			page = IntegerUtil.parseInt(args[1], 1);
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
		
		if (listType == 0) {
			MessageUtil.sendMessage(sender, "&4Map&7 | &eInvited");
			
			for (String map : toSend) {
				WorldConfig worldConfig = new WorldConfig(new File(mapFolder + "/" + map, "config.yml"));
				MessageUtil.sendMessage(sender, "&b" + map + "&7 | &e" + worldConfig.getInvitedPlayers().contains(sender));
			}
			
		} else if (listType == 1) {
			MessageUtil.sendMessage(sender, "&4Dungeon&7 | &eMap count");
			
			for (String dungeon : toSend) {
				DungeonConfig dungeonConfig = new DungeonConfig(new File(dungeonFolder, dungeon + ".yml"));
				int count = dungeonConfig.getFloors().size() + 2;
				MessageUtil.sendMessage(sender, "&b" + dungeon + "&7 | &e" + count);
			}
			
		} else if (listType == 2) {
			MessageUtil.sendMessage(sender, "&4Loaded map");
			for (String map : toSend) {
				MessageUtil.sendMessage(sender, "&b" + map);
			}
		}
	}
	
}
