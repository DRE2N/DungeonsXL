package com.dre.dungeonsxl.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dre.dungeonsxl.DGroup;
import com.dre.dungeonsxl.DPlayer;
import com.dre.dungeonsxl.EditWorld;
import com.dre.dungeonsxl.game.GameWorld;

public class CMDTest extends DCommand {

	public CMDTest() {
		this.command = "test";
		this.args = -1;
		this.help = p.language.get("Help_Cmd_Test");
		this.permissions = "dxl.test";
		this.isPlayerCommand = true;
	}

	@Override
	public void onExecute(String[] args, CommandSender sender) {
		Player player = (Player) sender;
		DPlayer dplayer = DPlayer.get(player);
		String dungeonname;

		if (dplayer == null) {
			if (args.length > 1) {
				dungeonname = args[1];

				if (EditWorld.exist(dungeonname)) {
					DGroup dgroup = new DGroup(player, dungeonname);
					if (dgroup != null) {
						if (dgroup.getGworld() == null) {
							dgroup.setGworld(GameWorld.load(DGroup.get(player).getDungeonname()));
						}

						DPlayer newDPlayer;

						if (dgroup.getGworld().locLobby == null) {
							newDPlayer = new DPlayer(player, dgroup.getGworld().world, dgroup.getGworld().world.getSpawnLocation(), false);
						} else {
							newDPlayer = new DPlayer(player, dgroup.getGworld().world, dgroup.getGworld().locLobby, false);
						}
						newDPlayer.isinTestMode = true;
					}
				} else {
					p.msg(player, p.language.get("Error_DungeonNotExist", dungeonname));
				}
			} else {
				this.displayHelp(player);
			}
		} else {
			p.msg(player, p.language.get("Error_LeaveDungeon"));
		}

	}

}
