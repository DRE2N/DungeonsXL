package io.github.dre2n.dungeonsxl.command;

import io.github.dre2n.dungeonsxl.dungeon.Dungeon;
import io.github.dre2n.dungeonsxl.dungeon.EditWorld;
import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
import io.github.dre2n.dungeonsxl.file.DMessages.Messages;
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
		setHelp(dMessages.getMessage(Messages.HELP_CMD_TEST));
		setPermission("dxl.test");
		setPlayerCommand(true);
	}
	
	@Override
	public void onExecute(String[] args, CommandSender sender) {
		Player player = (Player) sender;
		DPlayer dplayer = DPlayer.get(player);
		
		if (dplayer != null) {
			MessageUtil.sendMessage(player, dMessages.getMessage(Messages.ERROR_LEAVE_DUNGEON));
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
			MessageUtil.sendMessage(player, dMessages.getMessage(Messages.ERROR_DUNGEON_NOT_EXIST, identifier));
			return;
		}
		
		if (DGroup.get(player) != null) {
			MessageUtil.sendMessage(player, dMessages.getMessage(Messages.ERROR_LEAVE_GROUP));
			return;
		}
		
		DGroup dGroup = new DGroup(player, identifier, multiFloor);
		
		if (dGroup.getGameWorld() == null) {
			dGroup.setGameWorld(GameWorld.load(DGroup.get(player).getMapName()));
		}
		
		DPlayer newDPlayer;
		
		if (dGroup.getGameWorld().getLocLobby() == null) {
			newDPlayer = new DPlayer(player, dGroup.getGameWorld().getWorld(), dGroup.getGameWorld().getWorld().getSpawnLocation(), false);
			
		} else {
			newDPlayer = new DPlayer(player, dGroup.getGameWorld().getWorld(), dGroup.getGameWorld().getLocLobby(), false);
		}
		
		newDPlayer.setIsInTestMode(true);
	}
	
}
