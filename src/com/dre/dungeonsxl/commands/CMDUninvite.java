package com.dre.dungeonsxl.commands;

import org.bukkit.command.CommandSender;

import com.dre.dungeonsxl.EditWorld;
import com.dre.dungeonsxl.util.OfflinePlayerUtil;

public class CMDUninvite extends DCommand {

	public CMDUninvite() {
		this.args = 2;
		this.command = "uninvite";
		this.help = p.language.get("Help_Cmd_Uninvite");
		this.permissions = "dxl.uninvite";
		this.isPlayerCommand = true;
		this.isConsoleCommand = true;
	}

	@Override
	public void onExecute(String[] args, CommandSender sender) {
		if (EditWorld.removeInvitedPlayer(args[2], OfflinePlayerUtil.getUniqueIdFromName(args[1]), args[1])) {
			p.msg(sender, p.language.get("Cmd_Uninvite_Success", args[1], args[2]));
		} else {
			p.msg(sender, p.language.get("Error_DungeonNotExist", args[2]));
		}
	}
}
