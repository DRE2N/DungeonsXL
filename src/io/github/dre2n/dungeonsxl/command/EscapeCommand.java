package io.github.dre2n.dungeonsxl.command;

import io.github.dre2n.dungeonsxl.dungeon.EditWorld;
import io.github.dre2n.dungeonsxl.file.DMessages.Messages;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.player.DPlayer;
import io.github.dre2n.dungeonsxl.util.MessageUtil;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EscapeCommand extends DCommand {
	
	public EscapeCommand() {
		setCommand("escape");
		setMinArgs(0);
		setMaxArgs(0);
		setHelp(dMessages.getMessage(Messages.HELP_CMD_ESCAPE));
		setPlayerCommand(true);
	}
	
	@Override
	public void onExecute(String[] args, CommandSender sender) {
		Player player = (Player) sender;
		DPlayer dplayer = DPlayer.get(player);
		if (dplayer != null) {
			
			if ( !dplayer.isEditing()) {
				MessageUtil.sendMessage(player, dMessages.getMessage(Messages.ERROR_LEAVE_DUNGEON));
				return;
			}
			
			dplayer.escape();
			
			EditWorld eworld = EditWorld.get(dplayer.getWorld());
			if (eworld == null) {
				return;
			}
			
			if (eworld.getWorld().getPlayers().isEmpty()) {
				eworld.deleteNoSave();
			}
			
		} else {
			DGroup dgroup = DGroup.get(player);
			if (dgroup != null) {
				dgroup.removePlayer(player);
				MessageUtil.sendMessage(player, dMessages.getMessage(Messages.CMD_LEAVE_SUCCESS));
				return;
			}
			MessageUtil.sendMessage(player, dMessages.getMessage(Messages.ERROR_NOT_IN_DUNGEON));
		}
	}
	
}
