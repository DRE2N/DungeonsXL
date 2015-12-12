package io.github.dre2n.dungeonsxl.command;

import io.github.dre2n.dungeonsxl.global.DPortal;
import io.github.dre2n.dungeonsxl.util.MessageUtil;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeletePortalCommand extends DCommand {
	
	public DeletePortalCommand() {
		setCommand("deleteportal");
		setMinArgs(0);
		setMaxArgs(0);
		setHelp(plugin.getDMessages().get("Help_Cmd_DeletePortal"));
		setPermission("dxl.deleteportal");
		setPlayerCommand(true);
	}
	
	@Override
	public void onExecute(String[] args, CommandSender sender) {
		Player player = (Player) sender;
		DPortal dPortal = DPortal.get(player.getTargetBlock((Set<Material>) null, 20).getLocation());
		
		if (dPortal != null) {
			dPortal.delete();
			MessageUtil.sendMessage(player, plugin.getDMessages().get("Player_PortalDeleted"));
		} else {
			MessageUtil.sendMessage(player, plugin.getDMessages().get("Error_NoPortal"));
		}
	}
	
}
