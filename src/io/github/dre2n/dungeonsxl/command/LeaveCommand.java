package io.github.dre2n.dungeonsxl.command;

import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
import io.github.dre2n.dungeonsxl.file.DMessages.Messages;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.player.DPlayer;
import io.github.dre2n.dungeonsxl.util.MessageUtil;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaveCommand extends DCommand {
	
	public LeaveCommand() {
		setCommand("leave");
		setMinArgs(0);
		setMaxArgs(0);
		setHelp(dMessages.getMessage(Messages.HELP_CMD_LEAVE));
		setPlayerCommand(true);
	}
	
	@Override
	public void onExecute(String[] args, CommandSender sender) {
		Player player = (Player) sender;
		DPlayer dplayer = DPlayer.get(player);
		
		if (GameWorld.get(player.getWorld()) != null) {
			if (GameWorld.get(player.getWorld()).isTutorial()) {
				MessageUtil.sendMessage(player, dMessages.getMessage(Messages.ERROR_NO_LEAVE_IN_TUTORIAL));
				return;
			}
		}
		
		if (dplayer != null) {
			dplayer.leave();
			MessageUtil.sendMessage(player, dMessages.getMessage(Messages.CMD_LEAVE_SUCCESS));
			
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
