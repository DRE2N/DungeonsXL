package io.github.dre2n.dungeonsxl.command;

import io.github.dre2n.dungeonsxl.DungeonsXL;
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
		
		if (args.length != 2) {
			displayHelp(player);
			return;
		}
		
		String dungeonname = args[1];
		
		if ( !EditWorld.exist(dungeonname)) {
			MessageUtil.sendMessage(player, plugin.getDMessages().get("Error_DungeonNotExist", dungeonname));
			return;
		}
		
		if ( !GameWorld.canPlayDungeon(dungeonname, player)) {
			File file = new File(plugin.getDataFolder() + "/maps/" + dungeonname + "/config.yml");
			
			if (file != null) {
				WorldConfig confReader = new WorldConfig(file);
				
				if (confReader != null) {
					MessageUtil.sendMessage(player, DungeonsXL.getPlugin().getDMessages().get("Error_Cooldown", "" + confReader.getTimeToNextPlay()));
				}
			}
			return;
		}
		
		if ( !GameWorld.checkRequirements(dungeonname, player)) {
			MessageUtil.sendMessage(player, DungeonsXL.getPlugin().getDMessages().get("Error_Requirements"));
		}
		
		if (DGroup.get(player) != null) {
			MessageUtil.sendMessage(player, plugin.getDMessages().get("Error_LeaveGroup"));
			return;
		}
		
		DGroup dgroup = new DGroup(player, dungeonname);
		
		if (dgroup != null) {
			if (dgroup.getGworld() == null) {
				dgroup.setGworld(GameWorld.load(DGroup.get(player).getDungeonname()));
			}
			
			if (dgroup.getGworld().locLobby == null) {
				new DPlayer(player, dgroup.getGworld().world, dgroup.getGworld().world.getSpawnLocation(), false);
				
			} else {
				new DPlayer(player, dgroup.getGworld().world, dgroup.getGworld().locLobby, false);
			}
		}
	}
	
}
