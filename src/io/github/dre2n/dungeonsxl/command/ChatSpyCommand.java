package io.github.dre2n.dungeonsxl.command;

import io.github.dre2n.dungeonsxl.util.MessageUtil;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatSpyCommand extends DCommand {
	
	public ChatSpyCommand() {
		setCommand("chatspy");
		setMinArgs(0);
		setMaxArgs(0);
		setHelp(plugin.getDMessages().get("Help_Cmd_Chatspy"));
		setPermission("dxl.chatspy");
		setPlayerCommand(true);
	}
	
	@Override
	public void onExecute(String[] args, CommandSender sender) {
		Player player = (Player) sender;
		
		if (plugin.getChatSpyers().contains(player)) {
			plugin.getChatSpyers().remove(player);
			MessageUtil.sendMessage(player, plugin.getDMessages().get("Cmd_Chatspy_Stopped"));
			
		} else {
			plugin.getChatSpyers().add(player);
			MessageUtil.sendMessage(player, plugin.getDMessages().get("Cmd_Chatspy_Start"));
		}
	}
	
}
