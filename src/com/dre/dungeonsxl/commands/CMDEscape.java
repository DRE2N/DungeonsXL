package com.dre.dungeonsxl.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dre.dungeonsxl.DGroup;
import com.dre.dungeonsxl.DPlayer;
import com.dre.dungeonsxl.EditWorld;

public class CMDEscape extends DCommand {

	public CMDEscape() {
		this.command = "escape";
		this.args = 0;
		this.help = p.language.get("Help_Cmd_Escape");
		this.isPlayerCommand = true;
	}

	@Override
	public void onExecute(String[] args, CommandSender sender) {
		Player player = (Player) sender;
		DPlayer dplayer = DPlayer.get(player);
		if (dplayer != null) {

			if (dplayer.isEditing) {
				dplayer.escape();
				
				EditWorld eworld = EditWorld.get(dplayer.world);
				if (eworld != null) {
					if (eworld.world.getPlayers().isEmpty()) {
						eworld.deleteNoSave();
					}
				}
			} else {
				p.msg(player, p.language.get("Error_LeaveDungeon"));
			}

			return;
		} else {
			DGroup dgroup = DGroup.get(player);
			if (dgroup != null) {
				dgroup.removePlayer(player);
				p.msg(player, p.language.get("Cmd_Leave_Success"));
				return;
			}
			p.msg(player, p.language.get("Error_NotInDungeon"));
		}
	}
}
