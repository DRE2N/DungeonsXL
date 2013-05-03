package com.dre.dungeonsxl.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dre.dungeonsxl.P;

public abstract class DCommand {
	public P p = P.p;

	public boolean costsMoney;
	public String command;
	public int args;
	public String help;
	public String permissions;
	public boolean isPlayerCommand = false;
	public boolean isConsoleCommand = false;

	// TODO : Add Aliases

	public DCommand() {
		costsMoney = false;
	}

	public void displayHelp(CommandSender sender) {
		p.msg(sender, ChatColor.RED + this.help);
	}

	public boolean playerHasPermissions(Player player) {
		if (this.permissions == null) {
			return true;
		}
		if (p.permission.has(player, this.permissions) || player.isOp()) {
			return true;
		}

		return false;
	}

	// Abstracts
	public abstract void onExecute(String[] args, CommandSender sender);

}
