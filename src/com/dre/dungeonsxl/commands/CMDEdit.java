package com.dre.dungeonsxl.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dre.dungeonsxl.DGroup;
import com.dre.dungeonsxl.DPlayer;
import com.dre.dungeonsxl.EditWorld;

public class CMDEdit extends DCommand {

	public CMDEdit() {
		this.command = "edit";
		this.args = 1;
		this.help = p.language.get("Help_Cmd_Edit");
		this.isPlayerCommand = true;
	}

	@Override
	public void onExecute(String[] args, CommandSender sender) {
		Player player = (Player) sender;

		String dungeonName = args[1];
		EditWorld eworld = EditWorld.load(dungeonName);
		DGroup dgroup = DGroup.get(player);
		DPlayer dplayer = DPlayer.get(player);

		if (EditWorld.isInvitedPlayer(dungeonName, player.getName()) || p.permission.has(player, "dxl.edit") || player.isOp()) {
			if (dplayer == null) {
				if (dgroup == null) {
					if (eworld != null) {
						if (eworld.lobby == null) {
							new DPlayer(player, eworld.world, eworld.world.getSpawnLocation(), true);
						} else {
							new DPlayer(player, eworld.world, eworld.lobby, true);
						}
					} else {
						p.msg(player, p.language.get("Error_DungeonNotExist", dungeonName));
					}
				} else {
					p.msg(player, p.language.get("Error_LeaveGroup"));
				}
			} else {
				p.msg(player, p.language.get("Error_LeaveDungeon"));
			}
		}

	}

}
