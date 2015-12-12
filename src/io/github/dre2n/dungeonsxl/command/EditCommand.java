package io.github.dre2n.dungeonsxl.command;

import io.github.dre2n.dungeonsxl.dungeon.EditWorld;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.player.DPlayer;
import io.github.dre2n.dungeonsxl.util.MessageUtil;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EditCommand extends DCommand {
	
	public EditCommand() {
		setCommand("edit");
		setMinArgs(1);
		setMaxArgs(1);
		setHelp(plugin.getDMessages().get("Help_Cmd_Edit"));
		setPlayerCommand(true);
	}
	
	@Override
	public void onExecute(String[] args, CommandSender sender) {
		Player player = (Player) sender;
		
		String dungeonName = args[1];
		EditWorld eworld = EditWorld.load(dungeonName);
		DGroup dgroup = DGroup.get(player);
		DPlayer dplayer = DPlayer.get(player);
		
		if (EditWorld.isInvitedPlayer(dungeonName, player.getUniqueId(), player.getName()) || player.hasPermission("dxl.edit")) {
			if (dplayer == null) {
				if (dgroup == null) {
					if (eworld != null) {
						if (eworld.lobby == null) {
							new DPlayer(player, eworld.world, eworld.world.getSpawnLocation(), true);
						} else {
							new DPlayer(player, eworld.world, eworld.lobby, true);
						}
					} else {
						MessageUtil.sendMessage(player, plugin.getDMessages().get("Error_DungeonNotExist", dungeonName));
					}
				} else {
					MessageUtil.sendMessage(player, plugin.getDMessages().get("Error_LeaveGroup"));
				}
			} else {
				MessageUtil.sendMessage(player, plugin.getDMessages().get("Error_LeaveDungeon"));
			}
		} else {
			MessageUtil.sendMessage(player, plugin.getDMessages().get("Error_NoPermission"));
		}
		
	}
	
}
