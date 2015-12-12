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
				plugin.log(plugin.getDMessages().get("Log_NewDungeon"));
				plugin.log(plugin.getDMessages().get("Log_GenerateNewWorld"));
				
				// Create World
				EditWorld eworld = new EditWorld();
				eworld.generate();
				eworld.dungeonname = name;
				eworld.save();
				eworld.delete();
				
				// MSG Done
				plugin.log(plugin.getDMessages().get("Log_WorldGenerationFinished"));
				
			} else {
				MessageUtil.sendMessage(sender, plugin.getDMessages().get("Error_NameToLong"));
			}
			
		} else if (sender instanceof Player) {
			Player player = (Player) sender;
			
			if (DPlayer.get(player) == null) {
				if (name.length() <= 15) {
					// Msg create
					plugin.log(plugin.getDMessages().get("Log_NewDungeon"));
					plugin.log(plugin.getDMessages().get("Log_GenerateNewWorld"));
					
					// Create World
					EditWorld eworld = new EditWorld();
					eworld.generate();
					eworld.dungeonname = name;
					
					// MSG Done
					plugin.log(plugin.getDMessages().get("Log_WorldGenerationFinished"));
					
					// Tp Player
					if (eworld.lobby == null) {
						new DPlayer(player, eworld.world, eworld.world.getSpawnLocation(), true);
						
					} else {
						new DPlayer(player, eworld.world, eworld.lobby, true);
					}
					
				} else {
					MessageUtil.sendMessage(player, plugin.getDMessages().get("Error_NameToLong"));
				}
				
			} else {
				MessageUtil.sendMessage(player, plugin.getDMessages().get("Error_LeaveDungeon"));
			}
		}
	}
	
}
