package io.github.dre2n.dungeonsxl.command;

import io.github.dre2n.dungeonsxl.dungeon.EditWorld;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.player.DPlayer;
import io.github.dre2n.dungeonsxl.util.MessageUtil;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EditCommand extends DCommand {
	
	public EditCommand() {
		setCommand("edit");
		setMinArgs(1);
		setMaxArgs(1);
		setHelp(plugin.getDMessages().get("Help_Cmd_Edit"));
		setPlayerCommand(true);
	}
	
	@Override
	public void onExecute(String[] args, CommandSender sender) {
		Player player = (Player) sender;
		
		String dungeonName = args[1];
		EditWorld eworld = EditWorld.load(dungeonName);
		DGroup dgroup = DGroup.get(player);
		DPlayer dplayer = DPlayer.get(player);
		
		if ( !(EditWorld.isInvitedPlayer(dungeonName, player.getUniqueId(), player.getName()) || player.hasPermission("dxl.edit"))) {
			MessageUtil.sendMessage(player, plugin.getDMessages().get("Error_NoPermission"));
			return;
		}
		
		if (dplayer != null) {
			MessageUtil.sendMessage(player, plugin.getDMessages().get("Error_LeaveDungeon"));
			return;
		}
		
		if (dgroup != null) {
			MessageUtil.sendMessage(player, plugin.getDMessages().get("Error_LeaveGroup"));
			return;
		}
		
		if (eworld == null) {
			MessageUtil.sendMessage(player, plugin.getDMessages().get("Error_DungeonNotExist", dungeonName));
			return;
		}
		
		if (eworld.getLobby() == null) {
			new DPlayer(player, eworld.getWorld(), eworld.getWorld().getSpawnLocation(), true);
			
		} else {
			new DPlayer(player, eworld.getWorld(), eworld.getLobby(), true);
		}
		
	}
	
}
