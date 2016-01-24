package io.github.dre2n.dungeonsxl.command;

import io.github.dre2n.dungeonsxl.config.MessageConfig.Messages;
import io.github.dre2n.dungeonsxl.util.messageutil.MessageUtil;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BreakCommand extends DCommand {
	
	public BreakCommand() {
		setCommand("break");
		setMinArgs(0);
		setMaxArgs(0);
		setHelp(messageConfig.getMessage(Messages.HELP_CMD_BREAK));
		setPermission("dxl.break");
		setPlayerCommand(true);
	}
	
	@Override
	public void onExecute(String[] args, CommandSender sender) {
		Player player = (Player) sender;
		if ( !plugin.getInBreakMode().contains(player)) {
			plugin.getInBreakMode().add(player);
			MessageUtil.sendMessage(sender, messageConfig.getMessage(Messages.CMD_BREAK_BREAK_MODE));
			
		} else {
			plugin.getInBreakMode().remove(player);
			MessageUtil.sendMessage(sender, messageConfig.getMessage(Messages.CMD_BREAK_PROTECTED_MODE));
		}
	}
	
}
