package io.github.dre2n.dungeonsxl.command;

import io.github.dre2n.dungeonsxl.config.MessageConfig.Messages;
import io.github.dre2n.dungeonsxl.dungeon.EditWorld;
import io.github.dre2n.dungeonsxl.player.DPlayer;
import io.github.dre2n.dungeonsxl.util.messageutil.MessageUtil;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class CreateCommand extends DCommand {
	
	public CreateCommand() {
		setMinArgs(1);
		setMaxArgs(1);
		setCommand("create");
		setHelp(messageConfig.getMessage(Messages.HELP_CMD_CREATE));
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
				plugin.getLogger().info(messageConfig.getMessage(Messages.LOG_NEW_DUNGEON));
				plugin.getLogger().info(messageConfig.getMessage(Messages.LOG_GENERATE_NEW_WORLD));
				
				// Create World
				EditWorld editWorld = new EditWorld();
				editWorld.generate();
				editWorld.setMapName(name);
				editWorld.save();
				editWorld.delete();
				
				// MSG Done
				plugin.getLogger().info(messageConfig.getMessage(Messages.LOG_WORLD_GENERATION_FINISHED));
				
			} else {
				MessageUtil.sendMessage(sender, messageConfig.getMessage(Messages.ERROR_NAME_TO_LONG));
			}
			
		} else if (sender instanceof Player) {
			Player player = (Player) sender;
			
			if (DPlayer.getByPlayer(player) != null) {
				MessageUtil.sendMessage(player, messageConfig.getMessage(Messages.ERROR_LEAVE_DUNGEON));
				return;
			}
			
			if (name.length() <= 15) {
				// Msg create
				plugin.getLogger().info(messageConfig.getMessage(Messages.LOG_NEW_DUNGEON));
				plugin.getLogger().info(messageConfig.getMessage(Messages.LOG_GENERATE_NEW_WORLD));
				
				// Create World
				EditWorld editWorld = new EditWorld();
				editWorld.generate();
				editWorld.setMapName(name);
				
				// MSG Done
				plugin.getLogger().info(messageConfig.getMessage(Messages.LOG_WORLD_GENERATION_FINISHED));
				
				// Tp Player
				if (editWorld.getLobby() == null) {
					new DPlayer(player, editWorld.getWorld(), editWorld.getWorld().getSpawnLocation(), true);
					
				} else {
					new DPlayer(player, editWorld.getWorld(), editWorld.getLobby(), true);
				}
				
			} else {
				MessageUtil.sendMessage(player, messageConfig.getMessage(Messages.ERROR_NAME_TO_LONG));
			}
		}
	}
	
}
