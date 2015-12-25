package io.github.dre2n.dungeonsxl.command;

import io.github.dre2n.dungeonsxl.dungeon.EditWorld;
import io.github.dre2n.dungeonsxl.player.DPlayer;
import io.github.dre2n.dungeonsxl.util.MessageUtil;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class CreateCommand extends DCommand {
	
	public CreateCommand() {
		setMinArgs(1);
		setMaxArgs(1);
		setCommand("create");
		setHelp(plugin.getDMessages().get("Help_Cmd_Create"));
		setPermission("dxl.create");
		setPlayerCommand(true);
		setConsoleCommand(true);
	}
	
	@Override
	public void onExecute(String[] args, CommandSender sender) {
		String name = args[1];
		
		if (sender instanceof ConsoleCommandSender) {
			if (name.length() <= 15) {
				// Msg create
				plugin.getLogger().info(plugin.getDMessages().get("Log_NewDungeon"));
				plugin.getLogger().info(plugin.getDMessages().get("Log_GenerateNewWorld"));
				
				// Create World
				EditWorld editWorld = new EditWorld();
				editWorld.generate();
				editWorld.setMapName(name);
				editWorld.save();
				editWorld.delete();
				
				// MSG Done
				plugin.getLogger().info(plugin.getDMessages().get("Log_WorldGenerationFinished"));
				
			} else {
				MessageUtil.sendMessage(sender, plugin.getDMessages().get("Error_NameToLong"));
			}
			
		} else if (sender instanceof Player) {
			Player player = (Player) sender;
			
			if (DPlayer.get(player) != null) {
				MessageUtil.sendMessage(player, plugin.getDMessages().get("Error_LeaveDungeon"));
				return;
			}
			
			if (name.length() <= 15) {
				// Msg create
				plugin.getLogger().info(plugin.getDMessages().get("Log_NewDungeon"));
				plugin.getLogger().info(plugin.getDMessages().get("Log_GenerateNewWorld"));
				
				// Create World
				EditWorld editWorld = new EditWorld();
				editWorld.generate();
				editWorld.setMapName(name);
				
				// MSG Done
				plugin.getLogger().info(plugin.getDMessages().get("Log_WorldGenerationFinished"));
				
				// Tp Player
				if (editWorld.getLobby() == null) {
					new DPlayer(player, editWorld.getWorld(), editWorld.getWorld().getSpawnLocation(), true);
					
				} else {
					new DPlayer(player, editWorld.getWorld(), editWorld.getLobby(), true);
				}
				
			} else {
				MessageUtil.sendMessage(player, plugin.getDMessages().get("Error_NameToLong"));
			}
		}
	}
	
}
