package io.github.dre2n.dungeonsxl.command;

import io.github.dre2n.dungeonsxl.file.DMessages.Messages;
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
		setHelp(dMessages.getMessage(Messages.HELP_CMD_DELETE_PORTAL));
		setPermission("dxl.deleteportal");
		setPlayerCommand(true);
	}
	
	@Override
	public void onExecute(String[] args, CommandSender sender) {
		Player player = (Player) sender;
		DPortal dPortal = DPortal.get(player.getTargetBlock((Set<Material>) null, 20).getLocation());
		
		if (dPortal != null) {
			dPortal.delete();
			MessageUtil.sendMessage(player, dMessages.getMessage(Messages.PLAYER_PORTAL_DELETED));
			
		} else {
			MessageUtil.sendMessage(player, dMessages.getMessage(Messages.ERROR_NO_PORTAL));
		}
	}
	
}
