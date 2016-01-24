package io.github.dre2n.dungeonsxl.command;

import io.github.dre2n.dungeonsxl.config.MessageConfig.Messages;
import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
import io.github.dre2n.dungeonsxl.event.dplayer.DPlayerEscapeEvent;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.player.DPlayer;
import io.github.dre2n.dungeonsxl.util.messageutil.MessageUtil;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaveCommand extends DCommand {
	
	public LeaveCommand() {
		setCommand("leave");
		setMinArgs(0);
		setMaxArgs(0);
		setHelp(messageConfig.getMessage(Messages.HELP_CMD_LEAVE));
		setPermission("dxl.leave");
		setPlayerCommand(true);
	}
	
	@Override
	public void onExecute(String[] args, CommandSender sender) {
		Player player = (Player) sender;
		DPlayer dPlayer = DPlayer.getByPlayer(player);
		
		if (GameWorld.getByWorld(player.getWorld()) != null) {
			if (GameWorld.getByWorld(player.getWorld()).isTutorial()) {
				MessageUtil.sendMessage(player, messageConfig.getMessage(Messages.ERROR_NO_LEAVE_IN_TUTORIAL));
				return;
			}
		}
		
		if (dPlayer != null) {
			DPlayerEscapeEvent event = new DPlayerEscapeEvent(dPlayer);
			
			if (event.isCancelled()) {
				return;
			}
			
			dPlayer.leave();
			MessageUtil.sendMessage(player, messageConfig.getMessage(Messages.CMD_LEAVE_SUCCESS));
			
		} else {
			DGroup dGroup = DGroup.getByPlayer(player);
			if (dGroup != null) {
				dGroup.removePlayer(player);
				MessageUtil.sendMessage(player, messageConfig.getMessage(Messages.CMD_LEAVE_SUCCESS));
				return;
			}
			
			MessageUtil.sendMessage(player, messageConfig.getMessage(Messages.ERROR_NOT_IN_DUNGEON));
		}
	}
	
}
