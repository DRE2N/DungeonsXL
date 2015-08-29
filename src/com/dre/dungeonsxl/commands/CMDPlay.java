package com.dre.dungeonsxl.commands;

import java.io.File;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dre.dungeonsxl.DConfig;
import com.dre.dungeonsxl.DGroup;
import com.dre.dungeonsxl.DPlayer;
import com.dre.dungeonsxl.EditWorld;
import com.dre.dungeonsxl.P;
import com.dre.dungeonsxl.game.GameWorld;

public class CMDPlay extends DCommand {

	public CMDPlay() {
		this.command = "play";
		this.args = -1;
		this.help = p.language.get("Help_Cmd_Play");
		this.permissions = "dxl.play";
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
					if (GameWorld.canPlayDungeon(dungeonname, player)) {
						if (GameWorld.checkRequirements(dungeonname, player)) {
							if (DGroup.get(player) == null) {
								DGroup dgroup = new DGroup(player, dungeonname);
								if (dgroup != null) {
									if (dgroup.getGworld() == null) {
										dgroup.setGworld(GameWorld.load(DGroup.get(player).getDungeonname()));
									}
									if (dgroup.getGworld().locLobby == null) {
										new DPlayer(player, dgroup.getGworld().world, dgroup.getGworld().world.getSpawnLocation(), false);
									} else {
										new DPlayer(player, dgroup.getGworld().world, dgroup.getGworld().locLobby, false);
									}
								}
							} else {
								p.msg(player, p.language.get("Error_LeaveGroup"));
							}
						} else {
							P.p.msg(player, P.p.language.get("Error_Requirements"));
						}
					} else {
						File file = new File(p.getDataFolder() + "/dungeons/" + dungeonname + "/config.yml");
						if (file != null) {
							DConfig confReader = new DConfig(file);
							if (confReader != null) {
								P.p.msg(player, P.p.language.get("Error_Cooldown", "" + confReader.getTimeToNextPlay()));
							}
						}
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
