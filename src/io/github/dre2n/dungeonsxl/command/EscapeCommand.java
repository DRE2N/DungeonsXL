package io.github.dre2n.dungeonsxl.command;

import io.github.dre2n.dungeonsxl.dungeon.EditWorld;
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
		setHelp(plugin.getDMessages().get("Help_Cmd_Escape"));
		setPlayerCommand(true);
	}
	
	@Override
	public void onExecute(String[] args, CommandSender sender) {
		Player player = (Player) sender;
		DPlayer dplayer = DPlayer.get(player);
		if (dplayer != null) {
			
			if ( !dplayer.isEditing()) {
				MessageUtil.sendMessage(player, plugin.getDMessages().get("Error_LeaveDungeon"));
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
				MessageUtil.sendMessage(player, plugin.getDMessages().get("Cmd_Leave_Success"));
				return;
			}
			MessageUtil.sendMessage(player, plugin.getDMessages().get("Error_NotInDungeon"));
		}
	}
	
}
