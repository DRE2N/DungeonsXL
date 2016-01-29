package io.github.dre2n.dungeonsxl.command;

import io.github.dre2n.dungeonsxl.config.DungeonConfig;
import io.github.dre2n.dungeonsxl.config.MessageConfig.Messages;
import io.github.dre2n.dungeonsxl.dungeon.Dungeon;
import io.github.dre2n.dungeonsxl.dungeon.EditWorld;
import io.github.dre2n.dungeonsxl.event.dgroup.DGroupCreateEvent;
import io.github.dre2n.dungeonsxl.game.GameWorld;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.player.DPlayer;
import io.github.dre2n.dungeonsxl.util.messageutil.MessageUtil;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestCommand extends DCommand {
	
	public TestCommand() {
		setCommand("test");
		setMinArgs(1);
		setMaxArgs(2);
		setHelp(messageConfig.getMessage(Messages.HELP_CMD_TEST));
		setPermission("dxl.test");
		setPlayerCommand(true);
	}
	
	@Override
	public void onExecute(String[] args, CommandSender sender) {
		Player player = (Player) sender;
		DPlayer dplayer = DPlayer.getByPlayer(player);
		
		if (dplayer != null) {
			MessageUtil.sendMessage(player, messageConfig.getMessage(Messages.ERROR_LEAVE_DUNGEON));
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
		
		if ( !multiFloor && !EditWorld.exists(identifier)) {
			MessageUtil.sendMessage(player, messageConfig.getMessage(Messages.ERROR_DUNGEON_NOT_EXIST, identifier));
			return;
		}
		
		DGroup dGroup = DGroup.getByPlayer(player);
		
		if (dGroup != null) {
			if ( !dGroup.getCaptain().equals(player) && !player.hasPermission("dxl.bypass")) {
				MessageUtil.sendMessage(player, messageConfig.getMessage(Messages.ERROR_NOT_CAPTAIN));
			}
			
			if (dGroup.getMapName() == null) {
				if ( !multiFloor) {
					dGroup.setMapName(identifier);
					
				} else {
					dGroup.setDungeonName(identifier);
					Dungeon dungeon = plugin.getDungeons().getDungeon(identifier);
					
					if (dungeon != null) {
						DungeonConfig config = dungeon.getConfig();
						
						if (config != null) {
							dGroup.setMapName(config.getStartFloor());
						}
					}
				}
				
			} else {
				MessageUtil.sendMessage(player, messageConfig.getMessage(Messages.ERROR_LEAVE_GROUP));
				return;
			}
			
		} else {
			dGroup = new DGroup(player, identifier, multiFloor);
		}
		
		DGroupCreateEvent event = new DGroupCreateEvent(dGroup, player, DGroupCreateEvent.Cause.COMMAND);
		
		if (event.isCancelled()) {
			plugin.getDGroups().remove(dGroup);
			dGroup = null;
		}
		
		if (dGroup == null) {
			return;
		}
		
		if (dGroup.getGameWorld() == null) {
			dGroup.setGameWorld(GameWorld.load(DGroup.getByPlayer(player).getMapName()));
		}
		
		if (dGroup.getGameWorld() == null) {
			MessageUtil.sendMessage(player, messageConfig.getMessage(Messages.ERROR_NOT_SAVED, DGroup.getByPlayer(player).getMapName()));
			dGroup.remove();
			return;
		}
		if (dGroup.getGameWorld().getLocLobby() == null) {
			for (Player groupPlayer : dGroup.getPlayers()) {
				new DPlayer(groupPlayer, dGroup.getGameWorld().getWorld(), dGroup.getGameWorld().getWorld().getSpawnLocation(), false).setInTestMode(true);
			}
			
		} else {
			for (Player groupPlayer : dGroup.getPlayers()) {
				new DPlayer(groupPlayer, dGroup.getGameWorld().getWorld(), dGroup.getGameWorld().getLocLobby(), false).setInTestMode(true);
			}
		}
	}
	
}
