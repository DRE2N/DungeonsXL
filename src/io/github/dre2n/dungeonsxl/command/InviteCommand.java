package io.github.dre2n.dungeonsxl.command;

import io.github.dre2n.dungeonsxl.config.MessageConfig.Messages;
import io.github.dre2n.dungeonsxl.dungeon.EditWorld;
import io.github.dre2n.dungeonsxl.util.UUIDUtil;
import io.github.dre2n.dungeonsxl.util.messageutil.MessageUtil;

import org.bukkit.command.CommandSender;

public class InviteCommand extends DCommand {
	
	public InviteCommand() {
		setMinArgs(2);
		setMaxArgs(2);
		setCommand("invite");
		setHelp(messageConfig.getMessage(Messages.HELP_CMD_INVITE));
		setPermission("dxl.invite");
		setPlayerCommand(true);
		setConsoleCommand(true);
	}
	
	@Override
	public void onExecute(String[] args, CommandSender sender) {
		if (EditWorld.addInvitedPlayer(args[2], UUIDUtil.getUniqueIdFromName(args[1]))) {
			MessageUtil.sendMessage(sender, messageConfig.getMessage(Messages.CMD_INVITE_SUCCESS, args[1], args[2]));
			
		} else {
			MessageUtil.sendMessage(sender, messageConfig.getMessage(Messages.ERROR_DUNGEON_NOT_EXIST, args[2]));
		}
	}
	
}
