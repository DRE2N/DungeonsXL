package com.dre.dungeonsxl.commands;

import org.bukkit.command.CommandSender;

import com.dre.dungeonsxl.EditWorld;
import com.dre.dungeonsxl.util.OfflinePlayerUtil;

public class CMDInvite extends DCommand {

	public CMDInvite() {
		this.args = 2;
		this.command = "invite";
		this.help = p.language.get("Help_Cmd_Invite");
		this.permissions = "dxl.invite";
		this.isPlayerCommand = true;
		this.isConsoleCommand = true;
	}

	@Override
	public void onExecute(String[] args, CommandSender sender) {
		if (EditWorld.addInvitedPlayer(args[2], OfflinePlayerUtil.getUniqueIdFromName(args[1]))) {
			p.msg(sender, p.language.get("Cmd_Invite_Success", args[1], args[2]));
		} else {
			p.msg(sender, p.language.get("Error_DungeonNotExist", args[2]));
		}
	}
}
