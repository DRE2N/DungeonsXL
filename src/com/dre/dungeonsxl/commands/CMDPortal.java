package com.dre.dungeonsxl.commands;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.dre.dungeonsxl.DPlayer;
import com.dre.dungeonsxl.DPortal;

public class CMDPortal extends DCommand {

	public CMDPortal() {
		this.command = "portal";
		this.args = 0;
		this.help = p.language.get("Help_Cmd_Portal");
		this.permissions = "dxl.portal";
		this.isPlayerCommand = true;
	}

	@Override
	public void onExecute(String[] args, CommandSender sender) {
		Player player = (Player) sender;
		DPlayer dplayer = DPlayer.get(player);
		if (dplayer == null) {
			DPortal dportal = DPortal.get(player);
			if (dportal == null) {
				dportal = new DPortal(false);
				dportal.player = player;
				dportal.world = player.getWorld();
				player.getInventory().setItemInHand(new ItemStack(Material.WOOD_SWORD));
				p.msg(player, p.language.get("Player_PortalIntroduction"));
			} else {
				DPortal.portals.remove(dportal);
				p.msg(player, p.language.get("Player_PortalAbort"));
			}
		} else {
			p.msg(player, p.language.get("Error_LeaveDungeon"));
		}
	}
}
