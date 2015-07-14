package com.dre.dungeonsxl.commands;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dre.dungeonsxl.DPortal;

public class CMDDeletePortal extends DCommand {

	public CMDDeletePortal() {
		this.command = "deleteportal";
		this.args = 0;
		this.help = p.language.get("Help_Cmd_DeletePortal");
		this.permissions = "dxl.deleteportal";
		this.isPlayerCommand = true;
	}

	@Override
	public void onExecute(String[] args, CommandSender sender) {
		Player player = (Player) sender;
		DPortal dPortal = DPortal.get(player.getTargetBlock((Set<Material>) null, 20).getLocation());

		if (dPortal != null) {
			dPortal.delete();
			p.msg(player, p.language.get("Player_PortalDeleted"));
		} else {
			p.msg(player, p.language.get("Error_NoPortal"));
		}
	}
}
