package io.github.dre2n.dungeonsxl.command;

import io.github.dre2n.dungeonsxl.dungeon.EditWorld;
import io.github.dre2n.dungeonsxl.file.DMessages.Messages;
import io.github.dre2n.dungeonsxl.util.MessageUtil;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SaveCommand extends DCommand {
	
	public SaveCommand() {
		setCommand("save");
		setMinArgs(0);
		setMaxArgs(0);
		setHelp(dMessages.getMessage(Messages.HELP_CMD_SAVE));
		setPermission("dxl.save");
		setPlayerCommand(true);
	}
	
	@Override
	public void onExecute(String[] args, CommandSender sender) {
		Player player = (Player) sender;
		EditWorld eworld = EditWorld.get(player.getWorld());
		if (eworld != null) {
			eworld.save();
			MessageUtil.sendMessage(player, dMessages.getMessage(Messages.CMD_SAVE_SUCCESS));
			
		} else {
			MessageUtil.sendMessage(player, dMessages.getMessage(Messages.ERROR_NOT_IN_DUNGEON));
		}
	}
	
}
