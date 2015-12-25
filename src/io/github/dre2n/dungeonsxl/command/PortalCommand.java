package io.github.dre2n.dungeonsxl.command;

import io.github.dre2n.dungeonsxl.global.DPortal;
import io.github.dre2n.dungeonsxl.player.DPlayer;
import io.github.dre2n.dungeonsxl.util.MessageUtil;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PortalCommand extends DCommand {
	
	public PortalCommand() {
		setCommand("portal");
		setMinArgs(0);
		setMaxArgs(0);
		setHelp(plugin.getDMessages().get("Help_Cmd_Portal"));
		setPermission("dxl.portal");
		setPlayerCommand(true);
	}
	
	@Override
	public void onExecute(String[] args, CommandSender sender) {
		Player player = (Player) sender;
		DPlayer dplayer = DPlayer.get(player);
		
		if (dplayer != null) {
			MessageUtil.sendMessage(player, plugin.getDMessages().get("Error_LeaveDungeon"));
		}
		
		DPortal dportal = DPortal.get(player);
		
		if (dportal == null) {
			dportal = new DPortal(false);
			dportal.setPlayer(player);
			dportal.setWorld(player.getWorld());
			player.getInventory().setItemInHand(new ItemStack(Material.WOOD_SWORD));
			MessageUtil.sendMessage(player, plugin.getDMessages().get("Player_PortalIntroduction"));
			
		} else {
			plugin.getDPortals().remove(dportal);
			MessageUtil.sendMessage(player, plugin.getDMessages().get("Player_PortalAbort"));
		}
	}
	
}
