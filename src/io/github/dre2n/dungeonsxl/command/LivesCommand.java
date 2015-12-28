package io.github.dre2n.dungeonsxl.command;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.player.DPlayer;
import io.github.dre2n.dungeonsxl.util.MessageUtil;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LivesCommand extends DCommand {
	
	public LivesCommand() {
		setCommand("lives");
		setMinArgs(0);
		setMaxArgs(1);
		setHelp(plugin.getDMessages().get("Help_Cmd_Lives"));
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
			MessageUtil.sendMessage(sender, DungeonsXL.getPlugin().getDMessages().get("Error_NoConsoleCommand", getCommand()));
			return;
		}
		
		DPlayer dPlayer = DPlayer.get(player);
		if (dPlayer != null) {
			MessageUtil.sendMessage(player, plugin.getDMessages().get("Cmd_Lives").replaceAll("v1", player.getName()).replaceAll("v2", String.valueOf(dPlayer.getLives())));
			
		} else {
			MessageUtil.sendMessage(player, plugin.getDMessages().get("Error_NotInDungeon"));
		}
	}
	
}
