package io.github.dre2n.dungeonsxl.command;

import io.github.dre2n.dungeonsxl.dungeon.EditWorld;
import io.github.dre2n.dungeonsxl.util.MessageUtil;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SaveCommand extends DCommand {
	
	public SaveCommand() {
		setCommand("save");
		setMinArgs(0);
		setMaxArgs(0);
		setHelp(plugin.getDMessages().get("Help_Cmd_Save"));
		setPermission("dxl.save");
		setPlayerCommand(true);
	}
	
	@Override
	public void onExecute(String[] args, CommandSender sender) {
		Player player = (Player) sender;
		EditWorld eworld = EditWorld.get(player.getWorld());
		if (eworld != null) {
			eworld.save();
			MessageUtil.sendMessage(player, plugin.getDMessages().get("Cmd_Save_Success"));
			
		} else {
			MessageUtil.sendMessage(player, plugin.getDMessages().get("Error_NotInDungeon"));
		}
	}
	
}
