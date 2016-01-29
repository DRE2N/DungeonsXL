package io.github.dre2n.dungeonsxl.command;

import io.github.dre2n.dungeonsxl.config.MessageConfig.Messages;
import io.github.dre2n.dungeonsxl.game.Game;
import io.github.dre2n.dungeonsxl.game.GameWorld;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.util.messageutil.MessageUtil;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameCommand extends DCommand {
	
	public GameCommand() {
		setCommand("game");
		setMinArgs(0);
		setMaxArgs(0);
		setHelp(messageConfig.getMessage(Messages.HELP_CMD_GAME));
		setPermission("dxl.game");
		setPlayerCommand(true);
	}
	
	@Override
	public void onExecute(String[] args, CommandSender sender) {
		Player player = (Player) sender;
		DGroup dGroup = DGroup.getByPlayer(player);
		if (dGroup == null) {
			MessageUtil.sendMessage(sender, messageConfig.getMessage(Messages.ERROR_JOIN_GROUP));
			return;
		}
		
		GameWorld gameWorld = dGroup.getGameWorld();
		if (gameWorld == null) {
			MessageUtil.sendMessage(sender, messageConfig.getMessage(Messages.ERROR_NO_GAME));
			return;
		}
		
		Game game = gameWorld.getGame();
		if (game == null) {
			MessageUtil.sendMessage(sender, messageConfig.getMessage(Messages.ERROR_NO_GAME));
			return;
		}
		
		MessageUtil.sendCenteredMessage(sender, "&4&l[ &6Game &4&l]");
		String groups = "";
		for (DGroup group : game.getDGroups()) {
			groups += (group == game.getDGroups().get(0) ? "" : "&b, &e") + group.getName();
		}
		MessageUtil.sendMessage(sender, "&bGroups: &e" + groups);
		MessageUtil.sendMessage(sender, "&bGame type: &e" + game.getType());
		MessageUtil.sendMessage(sender, "&bDungeon: &e" + (dGroup.getDungeonName() == null ? "N/A" : dGroup.getDungeonName()));
		MessageUtil.sendMessage(sender, "&bMap: &e" + (dGroup.getMapName() == null ? "N/A" : dGroup.getMapName()));
	}
	
}
