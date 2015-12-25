package io.github.dre2n.dungeonsxl.command;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.dungeon.Dungeon;
import io.github.dre2n.dungeonsxl.dungeon.WorldConfig;
import io.github.dre2n.dungeonsxl.dungeon.EditWorld;
import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.player.DPlayer;
import io.github.dre2n.dungeonsxl.util.MessageUtil;

import java.io.File;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayCommand extends DCommand {
	
	public PlayCommand() {
		setCommand("play");
		setMinArgs(1);
		setMaxArgs(2);
		setHelp(plugin.getDMessages().get("Help_Cmd_Play"));
		setPermission("dxl.play");
		setPlayerCommand(true);
	}
	
	@Override
	public void onExecute(String[] args, CommandSender sender) {
		Player player = (Player) sender;
		DPlayer dplayer = DPlayer.get(player);
		
		if (dplayer != null) {
			MessageUtil.sendMessage(player, plugin.getDMessages().get("Error_LeaveDungeon"));
			return;
		}
		
		if ( !(args.length >= 2 && args.length <= 3)) {
			displayHelp(player);
			return;
		}
		
		String identifier = args[1];
		String mapName = identifier;
		
		boolean multiFloor = false;
		if (args.length == 3) {
			identifier = args[2];
			mapName = identifier;
			if (args[1].equalsIgnoreCase("dungeon") || args[1].equalsIgnoreCase("d")) {
				Dungeon dungeon = plugin.getDungeons().getDungeon(args[2]);
				if (dungeon != null) {
					multiFloor = true;
					mapName = dungeon.getConfig().getStartFloor();
				} else {
					displayHelp(player);
					return;
				}
				
			} else if (args[1].equalsIgnoreCase("map") || args[1].equalsIgnoreCase("m")) {
				identifier = args[2];
			}
		}
		
		if ( !multiFloor && !EditWorld.exist(identifier)) {
			MessageUtil.sendMessage(player, plugin.getDMessages().get("Error_DungeonNotExist", identifier));
			return;
		}
		
		if ( !GameWorld.canPlayDungeon(identifier, player)) {
			File file = new File(plugin.getDataFolder() + "/maps/" + identifier + "/config.yml");
			
			if (file != null) {
				WorldConfig confReader = new WorldConfig(file);
				
				if (confReader != null) {
					MessageUtil.sendMessage(player, DungeonsXL.getPlugin().getDMessages().get("Error_Cooldown", "" + confReader.getTimeToNextPlay()));
				}
			}
			return;
		}
		
		if ( !GameWorld.checkRequirements(mapName, player)) {
			MessageUtil.sendMessage(player, DungeonsXL.getPlugin().getDMessages().get("Error_Requirements"));
			return;
		}
		
		if (DGroup.get(player) != null) {
			MessageUtil.sendMessage(player, plugin.getDMessages().get("Error_LeaveGroup"));
			return;
		}
		
		DGroup dGroup = new DGroup(player, identifier, multiFloor);
		
		if (dGroup.getGWorld() == null) {
			dGroup.setGWorld(GameWorld.load(DGroup.get(player).getMapName()));
		}
		
		if (dGroup.getGWorld().getLocLobby() == null) {
			new DPlayer(player, dGroup.getGWorld().getWorld(), dGroup.getGWorld().getWorld().getSpawnLocation(), false);
			
		} else {
			new DPlayer(player, dGroup.getGWorld().getWorld(), dGroup.getGWorld().getLocLobby(), false);
		}
	}
	
}
