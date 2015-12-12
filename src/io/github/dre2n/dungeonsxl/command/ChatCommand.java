package io.github.dre2n.dungeonsxl.command;

import io.github.dre2n.dungeonsxl.player.DPlayer;
import io.github.dre2n.dungeonsxl.util.MessageUtil;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatCommand extends DCommand {
	
	public ChatCommand() {
		setCommand("chat");
		setMinArgs(0);
		setMaxArgs(0);
		setHelp(plugin.getDMessages().get("Help_Cmd_Chat"));
		setPlayerCommand(true);
	}
	
	@Override
	public void onExecute(String[] args, CommandSender sender) {
		Player player = (Player) sender;
		DPlayer dplayer = DPlayer.get(player);
		
		if (dplayer != null) {
			if (dplayer.isInDungeonChat) {
				dplayer.isInDungeonChat = false;
				MessageUtil.sendMessage(player, plugin.getDMessages().get("Cmd_Chat_NormalChat"));
				
			} else {
				dplayer.isInDungeonChat = true;
				MessageUtil.sendMessage(player, plugin.getDMessages().get("Cmd_Chat_DungeonChat"));
			}
			
		} else {
			MessageUtil.sendMessage(player, plugin.getDMessages().get("Error_NotInDungeon"));
		}
	}
	
}
