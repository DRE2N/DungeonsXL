package io.github.dre2n.dungeonsxl.command;

import io.github.dre2n.dungeonsxl.config.MessageConfig.Messages;
import io.github.dre2n.dungeonsxl.dungeon.EditWorld;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.player.DPlayer;
import io.github.dre2n.dungeonsxl.util.messageutil.MessageUtil;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EditCommand extends DCommand {
	
	public EditCommand() {
		setCommand("edit");
		setMinArgs(1);
		setMaxArgs(1);
		setHelp(messageConfig.getMessage(Messages.HELP_CMD_EDIT));
		setPlayerCommand(true);
	}
	
	@Override
	public void onExecute(String[] args, CommandSender sender) {
		Player player = (Player) sender;
		
		String mapName = args[1];
		EditWorld editWorld = EditWorld.load(mapName);
		DGroup dGroup = DGroup.getByPlayer(player);
		DPlayer dPlayer = DPlayer.getByPlayer(player);
		
		if ( !(EditWorld.isInvitedPlayer(mapName, player.getUniqueId(), player.getName()) || player.hasPermission("dxl.edit"))) {
			MessageUtil.sendMessage(player, messageConfig.getMessage(Messages.ERROR_NO_PERMISSIONS));
			return;
		}
		
		if (dPlayer != null) {
			MessageUtil.sendMessage(player, messageConfig.getMessage(Messages.ERROR_LEAVE_DUNGEON));
			return;
		}
		
		if (dGroup != null) {
			MessageUtil.sendMessage(player, messageConfig.getMessage(Messages.ERROR_LEAVE_GROUP));
			return;
		}
		
		if (editWorld == null) {
			MessageUtil.sendMessage(player, messageConfig.getMessage(Messages.ERROR_DUNGEON_NOT_EXIST, mapName));
			return;
		}
		
		if (editWorld.getLobby() == null) {
			new DPlayer(player, editWorld.getWorld(), editWorld.getWorld().getSpawnLocation(), true);
			
		} else {
			new DPlayer(player, editWorld.getWorld(), editWorld.getLobby(), true);
		}
		
	}
	
}
