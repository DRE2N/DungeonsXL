package io.github.dre2n.dungeonsxl.command;

import io.github.dre2n.dungeonsxl.config.MessageConfig.Messages;
import io.github.dre2n.dungeonsxl.player.DPlayer;
import io.github.dre2n.dungeonsxl.util.messageutil.MessageUtil;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LivesCommand extends DCommand {
	
	public LivesCommand() {
		setCommand("lives");
		setMinArgs(0);
		setMaxArgs(1);
		setHelp(messageConfig.getMessage(Messages.HELP_CMD_LIVES));
		setPermission("dxl.lives");
		setPlayerCommand(true);
		setConsoleCommand(true);
	}
	
	@Override
	public void onExecute(String[] args, CommandSender sender) {
		Player player = null;
		
		if (args.length == 2) {
			if (Bukkit.getServer().getPlayer(args[1]) != null) {
				player = Bukkit.getServer().getPlayer(args[1]);
			}
			
		} else if (sender instanceof Player) {
			player = (Player) sender;
			
		} else {
			MessageUtil.sendMessage(sender, messageConfig.getMessage(Messages.ERROR_NO_CONSOLE_COMMAND, getCommand()));
			return;
		}
		
		DPlayer dPlayer = DPlayer.getByPlayer(player);
		if (dPlayer != null) {
			MessageUtil.sendMessage(player, messageConfig.getMessage(Messages.CMD_LIVES, player.getName(), String.valueOf(dPlayer.getLives())));
			
		} else {
			MessageUtil.sendMessage(player, messageConfig.getMessage(Messages.ERROR_NOT_IN_DUNGEON));
		}
	}
	
}
