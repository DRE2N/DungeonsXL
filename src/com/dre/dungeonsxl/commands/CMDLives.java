package com.dre.dungeonsxl.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dre.dungeonsxl.P;

public class CMDLives extends DCommand {

	public CMDLives() {
		this.command = "lives";
		this.args = 1;
		this.help = p.language.get("Help_Cmd_Lives");
		this.isPlayerCommand = true;
	}

	@Override
	public void onExecute(String[] args, CommandSender sender) {
		Player player = (Player) sender;
		String lives = "";
		if (P.lives.containsKey(player)) {
			lives = String.valueOf(P.lives.get(player));
			p.msg(player, p.language.get("Cmd_Lives").replaceAll("v1", player.getName()).replaceAll("v2", lives));
		} else {
			p.msg(player, p.language.get("Error_NotInDungeon"));
		}
	}

}
