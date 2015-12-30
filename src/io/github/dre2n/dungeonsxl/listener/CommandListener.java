package io.github.dre2n.dungeonsxl.listener;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.util.messageutil.MessageUtil;
import io.github.dre2n.dungeonsxl.command.DCommand;
import io.github.dre2n.dungeonsxl.file.DMessages;
import io.github.dre2n.dungeonsxl.file.DMessages.Messages;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class CommandListener implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd_notused, String arg, String[] args) {
		DungeonsXL plugin = DungeonsXL.getPlugin();
		
		if (args.length > 0) {
			DMessages dMessages = plugin.getDMessages();
			DCommand dCommand = plugin.getDCommands().getDCommand(args[0]);
			
			if (dCommand != null) {
				if (sender instanceof ConsoleCommandSender) {
					if ( !dCommand.isConsoleCommand()) {
						MessageUtil.sendMessage(sender, dMessages.getMessage(Messages.LOG_ERROR_NO_CONSOLE_COMMAND, dCommand.getCommand()));
						return false;
					}
				}
				
				if (sender instanceof Player) {
					Player player = (Player) sender;
					
					if ( !dCommand.isPlayerCommand()) {
						MessageUtil.sendMessage(player, dMessages.getMessage(Messages.ERROR_NO_PLAYER_COMMAND, dCommand.getCommand()));
						return false;
						
					} else {
						if (dCommand.getPermission() != null) {
							if ( !dCommand.playerHasPermissions(player)) {
								MessageUtil.sendMessage(player, dMessages.getMessage(Messages.ERROR_NO_PERMISSIONS));
								return false;
							}
						}
					}
				}
				
				if (dCommand.getMinArgs() <= args.length - 1 & dCommand.getMaxArgs() >= args.length - 1 || dCommand.getMinArgs() == -1) {
					dCommand.onExecute(args, sender);
					return true;
					
				} else {
					dCommand.displayHelp(sender);
					return true;
				}
			}
		}
		
		plugin.getDCommands().getDCommand("main").onExecute(null, sender);
		
		return false;
	}
	
}
