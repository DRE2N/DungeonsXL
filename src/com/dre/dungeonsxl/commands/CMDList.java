package com.dre.dungeonsxl.commands;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CMDList extends DCommand {

	public CMDList() {
		this.command = "list";
		this.args = 0;
		this.help = p.language.get("Help_Cmd_List");
		this.isPlayerCommand = true;
		this.isConsoleCommand = true;
	}

	@Override
	public void onExecute(String[] args, CommandSender sender) {

		File dungeonsfolder = new File(p.getDataFolder() + "/dungeons");
		if (dungeonsfolder.exists()) {
			p.msg(sender, ChatColor.DARK_GREEN + "-----[ " + ChatColor.GOLD + "Dungeons " + ChatColor.RED + dungeonsfolder.list().length + ChatColor.DARK_GREEN + "]-----");

			for (String dungeon : dungeonsfolder.list()) {
				p.msg(sender, dungeon);
			}
		}
	}

}
