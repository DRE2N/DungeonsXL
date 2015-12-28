package io.github.dre2n.dungeonsxl.command;

import io.github.dre2n.dungeonsxl.dungeon.EditWorld;
import io.github.dre2n.dungeonsxl.file.DMessages.Messages;
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
		setHelp(dMessages.getMessage(Messages.HELP_CMD_EDIT));
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
			MessageUtil.sendMessage(player, dMessages.getMessage(Messages.ERROR_NO_PERMISSIONS));
			return;
		}
		
		if (dplayer != null) {
			MessageUtil.sendMessage(player, dMessages.getMessage(Messages.ERROR_LEAVE_DUNGEON));
			return;
		}
		
		if (dgroup != null) {
			MessageUtil.sendMessage(player, dMessages.getMessage(Messages.ERROR_LEAVE_GROUP));
			return;
		}
		
		if (eworld == null) {
			MessageUtil.sendMessage(player, dMessages.getMessage(Messages.ERROR_DUNGEON_NOT_EXIST, dungeonName));
			return;
		}
		
		if (eworld.getLobby() == null) {
			new DPlayer(player, eworld.getWorld(), eworld.getWorld().getSpawnLocation(), true);
			
		} else {
			new DPlayer(player, eworld.getWorld(), eworld.getLobby(), true);
		}
		
	}
	
}
