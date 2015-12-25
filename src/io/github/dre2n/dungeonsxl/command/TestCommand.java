package io.github.dre2n.dungeonsxl.command;

import io.github.dre2n.dungeonsxl.dungeon.Dungeon;
import io.github.dre2n.dungeonsxl.dungeon.EditWorld;
import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.player.DPlayer;
import io.github.dre2n.dungeonsxl.util.MessageUtil;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestCommand extends DCommand {
	
	public TestCommand() {
		setCommand("test");
		setMinArgs(1);
		setMaxArgs(2);
		setHelp(plugin.getDMessages().get("Help_Cmd_Test"));
		setPermission("dxl.test");
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
		
		boolean multiFloor = false;
		if (args.length == 3) {
			identifier = args[2];
			if (args[1].equalsIgnoreCase("dungeon") || args[1].equalsIgnoreCase("d")) {
				Dungeon dungeon = plugin.getDungeons().getDungeon(args[2]);
				if (dungeon != null) {
					multiFloor = true;
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
		
		if (DGroup.get(player) != null) {
			MessageUtil.sendMessage(player, plugin.getDMessages().get("Error_LeaveGroup"));
			return;
		}
		
		DGroup dGroup = new DGroup(player, identifier, multiFloor);
		
		if (dGroup.getGWorld() == null) {
			dGroup.setGWorld(GameWorld.load(DGroup.get(player).getMapName()));
		}
		
		DPlayer newDPlayer;
		
		if (dGroup.getGWorld().getLocLobby() == null) {
			newDPlayer = new DPlayer(player, dGroup.getGWorld().getWorld(), dGroup.getGWorld().getWorld().getSpawnLocation(), false);
			
		} else {
			newDPlayer = new DPlayer(player, dGroup.getGWorld().getWorld(), dGroup.getGWorld().getLocLobby(), false);
		}
		
		newDPlayer.setIsInTestMode(true);
	}
	
}
