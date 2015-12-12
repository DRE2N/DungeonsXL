package io.github.dre2n.dungeonsxl.command;

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
		
		if (args.length != 2) {
			displayHelp(player);
			return;
		}
		
		String dungeonname = args[1];
		
		if ( !EditWorld.exist(dungeonname)) {
			MessageUtil.sendMessage(player, plugin.getDMessages().get("Error_DungeonNotExist", dungeonname));
			return;
		}
		
		if (DGroup.get(player) != null) {
			MessageUtil.sendMessage(player, plugin.getDMessages().get("Error_LeaveGroup"));
			return;
		}
		
		if (DGroup.get(player) != null) {
			MessageUtil.sendMessage(player, plugin.getDMessages().get("Error_LeaveGroup"));
			return;
		}
		
		DGroup dgroup = new DGroup(player, dungeonname);
		
		if (dgroup.getGworld() == null) {
			dgroup.setGworld(GameWorld.load(DGroup.get(player).getDungeonname()));
		}
		
		DPlayer newDPlayer;
		
		if (dgroup.getGworld().locLobby == null) {
			newDPlayer = new DPlayer(player, dgroup.getGworld().world, dgroup.getGworld().world.getSpawnLocation(), false);
			
		} else {
			newDPlayer = new DPlayer(player, dgroup.getGworld().world, dgroup.getGworld().locLobby, false);
		}
		
		newDPlayer.isinTestMode = true;
	}
	
}
