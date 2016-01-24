package io.github.dre2n.dungeonsxl.command;

import io.github.dre2n.dungeonsxl.config.MessageConfig.Messages;
import io.github.dre2n.dungeonsxl.dungeon.EditWorld;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.player.DPlayer;
import io.github.dre2n.dungeonsxl.util.messageutil.MessageUtil;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EscapeCommand extends DCommand {
	
	public EscapeCommand() {
		setCommand("escape");
		setMinArgs(0);
		setMaxArgs(0);
		setHelp(messageConfig.getMessage(Messages.HELP_CMD_ESCAPE));
		setPermission("dxl.escape");
		setPlayerCommand(true);
	}
	
	@Override
	public void onExecute(String[] args, CommandSender sender) {
		Player player = (Player) sender;
		DPlayer dPlayer = DPlayer.getByPlayer(player);
		if (dPlayer != null) {
			
			if ( !dPlayer.isEditing()) {
				MessageUtil.sendMessage(player, messageConfig.getMessage(Messages.ERROR_LEAVE_DUNGEON));
				return;
			}
			
			dPlayer.escape();
			
			EditWorld editWorld = EditWorld.getByWorld(dPlayer.getWorld());
			if (editWorld == null) {
				return;
			}
			
			if (editWorld.getWorld().getPlayers().isEmpty()) {
				editWorld.deleteNoSave();
			}
			
		} else {
			DGroup dGroup = DGroup.getByPlayer(player);
			if (dGroup != null) {
				dGroup.removePlayer(player);
				MessageUtil.sendMessage(player, messageConfig.getMessage(Messages.CMD_LEAVE_SUCCESS));
				return;
			}
			MessageUtil.sendMessage(player, messageConfig.getMessage(Messages.ERROR_NOT_IN_DUNGEON));
		}
	}
	
}
