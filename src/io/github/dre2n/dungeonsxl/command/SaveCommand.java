package io.github.dre2n.dungeonsxl.command;

import io.github.dre2n.dungeonsxl.config.MessageConfig.Messages;
import io.github.dre2n.dungeonsxl.dungeon.EditWorld;
import io.github.dre2n.dungeonsxl.util.messageutil.MessageUtil;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SaveCommand extends DCommand {
	
	public SaveCommand() {
		setCommand("save");
		setMinArgs(0);
		setMaxArgs(0);
		setHelp(messageConfig.getMessage(Messages.HELP_CMD_SAVE));
		setPermission("dxl.save");
		setPlayerCommand(true);
	}
	
	@Override
	public void onExecute(String[] args, CommandSender sender) {
		Player player = (Player) sender;
		EditWorld editWorld = EditWorld.getByWorld(player.getWorld());
		if (editWorld != null) {
			editWorld.save();
			MessageUtil.sendMessage(player, messageConfig.getMessage(Messages.CMD_SAVE_SUCCESS));
			
		} else {
			MessageUtil.sendMessage(player, messageConfig.getMessage(Messages.ERROR_NOT_IN_DUNGEON));
		}
	}
	
}
