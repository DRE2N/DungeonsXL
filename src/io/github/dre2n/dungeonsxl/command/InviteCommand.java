package io.github.dre2n.dungeonsxl.command;

import io.github.dre2n.dungeonsxl.dungeon.EditWorld;
import io.github.dre2n.dungeonsxl.util.MessageUtil;
import io.github.dre2n.dungeonsxl.util.UUIDUtil;

import org.bukkit.command.CommandSender;

public class InviteCommand extends DCommand {
	
	public InviteCommand() {
		setMinArgs(2);
		setMaxArgs(2);
		setCommand("invite");
		setHelp(plugin.getDMessages().get("Help_Cmd_Invite"));
		setPermission("dxl.invite");
		setPlayerCommand(true);
		setConsoleCommand(true);
	}
	
	@Override
	public void onExecute(String[] args, CommandSender sender) {
		if (EditWorld.addInvitedPlayer(args[2], UUIDUtil.getUniqueIdFromName(args[1]))) {
			MessageUtil.sendMessage(sender, plugin.getDMessages().get("Cmd_Invite_Success", args[1], args[2]));
		} else {
			MessageUtil.sendMessage(sender, plugin.getDMessages().get("Error_DungeonNotExist", args[2]));
		}
	}
	
}
