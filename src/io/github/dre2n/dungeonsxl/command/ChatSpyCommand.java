package io.github.dre2n.dungeonsxl.command;

import io.github.dre2n.dungeonsxl.file.DMessages.Messages;
import io.github.dre2n.dungeonsxl.util.messageutil.MessageUtil;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatSpyCommand extends DCommand {
	
	public ChatSpyCommand() {
		setCommand("chatspy");
		setMinArgs(0);
		setMaxArgs(0);
		setHelp(dMessages.getMessage(Messages.HELP_CMD_CHATSPY));
		setPermission("dxl.chatspy");
		setPlayerCommand(true);
	}
	
	@Override
	public void onExecute(String[] args, CommandSender sender) {
		Player player = (Player) sender;
		
		if (plugin.getChatSpyers().contains(player)) {
			plugin.getChatSpyers().remove(player);
			MessageUtil.sendMessage(player, dMessages.getMessage(Messages.CMD_CHATSPY_STOPPED));
			
		} else {
			plugin.getChatSpyers().add(player);
			MessageUtil.sendMessage(player, dMessages.getMessage(Messages.CMD_CHATSPY_START));
		}
	}
	
}
