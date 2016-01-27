package io.github.dre2n.dungeonsxl.command;

import io.github.dre2n.dungeonsxl.config.MessageConfig.Messages;
import io.github.dre2n.dungeonsxl.player.DPlayer;
import io.github.dre2n.dungeonsxl.util.messageutil.MessageUtil;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatCommand extends DCommand {
	
	public ChatCommand() {
		setCommand("chat");
		setMinArgs(0);
		setMaxArgs(0);
		setHelp(messageConfig.getMessage(Messages.HELP_CMD_CHAT));
		setPermission("dxl.chat");
		setPlayerCommand(true);
	}
	
	@Override
	public void onExecute(String[] args, CommandSender sender) {
		Player player = (Player) sender;
		DPlayer dplayer = DPlayer.getByPlayer(player);
		
		if (dplayer == null) {
			MessageUtil.sendMessage(player, messageConfig.getMessage(Messages.ERROR_JOIN_GROUP));
			return;
		}
		
		if (dplayer.isInDungeonChat()) {
			dplayer.setInDungeonChat(false);
			MessageUtil.sendMessage(player, messageConfig.getMessage(Messages.CMD_CHAT_NORMAL_CHAT));
			
		} else {
			dplayer.setInDungeonChat(true);
			MessageUtil.sendMessage(player, messageConfig.getMessage(Messages.CMD_CHAT_DUNGEON_CHAT));
		}
	}
	
}
