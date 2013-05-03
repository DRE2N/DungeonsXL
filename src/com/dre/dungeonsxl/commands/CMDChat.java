package com.dre.dungeonsxl.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dre.dungeonsxl.DPlayer;

public class CMDChat extends DCommand {

	public CMDChat() {
		this.command = "chat";
		this.args = 0;
		this.help = p.language.get("Help_Cmd_Chat");
		this.isPlayerCommand = true;
	}

	@Override
	public void onExecute(String[] args, CommandSender sender) {
		Player player = (Player) sender;
		DPlayer dplayer = DPlayer.get(player);

		if (dplayer != null) {
			if (dplayer.isInDungeonChat) {
				dplayer.isInDungeonChat = false;
				p.msg(player, p.language.get("Cmd_Chat_NormalChat"));
			} else {
				dplayer.isInDungeonChat = true;
				p.msg(player, p.language.get("Cmd_Chat_DungeonChat"));
			}
		} else {
			p.msg(player, p.language.get("Error_NotInDungeon"));
		}
	}

}
