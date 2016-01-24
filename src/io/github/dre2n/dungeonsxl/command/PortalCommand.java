package io.github.dre2n.dungeonsxl.command;

import io.github.dre2n.dungeonsxl.config.MessageConfig.Messages;
import io.github.dre2n.dungeonsxl.global.DPortal;
import io.github.dre2n.dungeonsxl.player.DPlayer;
import io.github.dre2n.dungeonsxl.util.messageutil.MessageUtil;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PortalCommand extends DCommand {
	
	public PortalCommand() {
		setCommand("portal");
		setMinArgs(0);
		setMaxArgs(0);
		setHelp(messageConfig.getMessage(Messages.HELP_CMD_PORTAL));
		setPermission("dxl.portal");
		setPlayerCommand(true);
	}
	
	@Override
	public void onExecute(String[] args, CommandSender sender) {
		Player player = (Player) sender;
		DPlayer dPlayer = DPlayer.getByPlayer(player);
		
		if (dPlayer != null) {
			MessageUtil.sendMessage(player, messageConfig.getMessage(Messages.ERROR_LEAVE_DUNGEON));
		}
		
		DPortal dPortal = DPortal.getByPlayer(player);
		
		if (dPortal == null) {
			dPortal = new DPortal(false);
			dPortal.setPlayer(player);
			dPortal.setWorld(player.getWorld());
			player.getInventory().setItemInHand(new ItemStack(Material.WOOD_SWORD));
			MessageUtil.sendMessage(player, messageConfig.getMessage(Messages.PLAYER_PORTAL_INTRODUCTION));
			
		} else {
			plugin.getDPortals().remove(dPortal);
			MessageUtil.sendMessage(player, messageConfig.getMessage(Messages.PLAYER_PORTAL_ABORT));
		}
	}
	
}
