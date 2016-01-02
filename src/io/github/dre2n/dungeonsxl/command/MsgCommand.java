package io.github.dre2n.dungeonsxl.command;

import io.github.dre2n.dungeonsxl.dungeon.WorldConfig;
import io.github.dre2n.dungeonsxl.dungeon.EditWorld;
import io.github.dre2n.dungeonsxl.file.DMessages.Messages;
import io.github.dre2n.dungeonsxl.util.NumberUtil;
import io.github.dre2n.dungeonsxl.util.messageutil.MessageUtil;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MsgCommand extends DCommand {
	
	public MsgCommand() {
		setMinArgs( -1);
		setMaxArgs( -1);
		setCommand("msg");
		setHelp(dMessages.getMessage(Messages.HELP_CMD_MSG));
		setPermission("dxl.msg");
		setPlayerCommand(true);
	}
	
	@Override
	public void onExecute(String[] args, CommandSender sender) {
		Player player = (Player) sender;
		EditWorld editWorld = EditWorld.getByWorld(player.getWorld());
		
		if (editWorld == null) {
			MessageUtil.sendMessage(player, dMessages.getMessage(Messages.ERROR_NOT_IN_DUNGEON));
			return;
		}
		
		if (args.length <= 1) {
			displayHelp(player);
			return;
		}
		
		try {
			int id = NumberUtil.parseInt(args[1]);
			
			WorldConfig confreader = new WorldConfig(new File(plugin.getDataFolder() + "/maps/" + editWorld.getMapName(), "config.yml"));
			
			if (args.length == 2) {
				String msg = confreader.getMsg(id, true);
				
				if (msg != null) {
					MessageUtil.sendMessage(player, ChatColor.WHITE + msg);
					
				} else {
					MessageUtil.sendMessage(player, dMessages.getMessage(Messages.ERROR_MSG_ID_NOT_EXIST, "" + id));
				}
				
			} else {
				String msg = "";
				int i = 0;
				for (String arg : args) {
					i++;
					if (i > 2) {
						msg = msg + " " + arg;
					}
				}
				
				String[] splitMsg = msg.split("\"");
				
				if (splitMsg.length > 1) {
					msg = splitMsg[1];
					String old = confreader.getMsg(id, false);
					if (old == null) {
						MessageUtil.sendMessage(player, dMessages.getMessage(Messages.CMD_MSG_ADDED, "" + id));
						
					} else {
						MessageUtil.sendMessage(player, dMessages.getMessage(Messages.CMD_MSG_UPDATED, "" + id));
					}
					
					confreader.setMsg(msg, id);
					confreader.save();
					
				} else {
					MessageUtil.sendMessage(player, dMessages.getMessage(Messages.ERROR_MSG_FORMAT));
				}
			}
			
		} catch (NumberFormatException e) {
			MessageUtil.sendMessage(player, dMessages.getMessage(Messages.ERROR_MSG_NO_INT));
		}
		
	}
	
}
