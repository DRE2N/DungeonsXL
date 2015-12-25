package io.github.dre2n.dungeonsxl.command;

import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
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
		setHelp(plugin.getDMessages().get("Help_Cmd_Leave"));
		setPlayerCommand(true);
	}
	
	@Override
	public void onExecute(String[] args, CommandSender sender) {
		Player player = (Player) sender;
		DPlayer dplayer = DPlayer.get(player);
		
		if (GameWorld.get(player.getWorld()) != null) {
			if (GameWorld.get(player.getWorld()).isTutorial()) {
				MessageUtil.sendMessage(player, plugin.getDMessages().get("Error_NoLeaveInTutorial"));
				return;
			}
		}
		
		if (dplayer != null) {
			dplayer.leave();
			MessageUtil.sendMessage(player, plugin.getDMessages().get("Cmd_Leave_Success"));
			
		} else {
			DGroup dgroup = DGroup.get(player);
			if (dgroup != null) {
				dgroup.removePlayer(player);
				MessageUtil.sendMessage(player, plugin.getDMessages().get("Cmd_Leave_Success"));
				return;
			}
			
			MessageUtil.sendMessage(player, plugin.getDMessages().get("Error_NotInDungeon"));
		}
	}
	
}
