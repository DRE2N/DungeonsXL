package io.github.dre2n.dungeonsxl.command;

import io.github.dre2n.dungeonsxl.config.MessageConfig.Messages;
import io.github.dre2n.dungeonsxl.game.Game;
import io.github.dre2n.dungeonsxl.game.GameTypeDefault;
import io.github.dre2n.dungeonsxl.game.GameWorld;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.player.DPlayer;
import io.github.dre2n.dungeonsxl.util.messageutil.MessageUtil;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestCommand extends DCommand {
	
	public TestCommand() {
		setCommand("test");
		setMinArgs(0);
		setMaxArgs(0);
		setHelp(messageConfig.getMessage(Messages.HELP_CMD_TEST));
		setPermission("dxl.test");
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
		
		if ( !dGroup.getCaptain().equals(player)) {
			MessageUtil.sendMessage(sender, messageConfig.getMessage(Messages.ERROR_NOT_CAPTAIN));
			return;
		}
		
		GameWorld gameWorld = dGroup.getGameWorld();
		if (gameWorld == null) {
			MessageUtil.sendMessage(sender, messageConfig.getMessage(Messages.ERROR_NOT_IN_DUNGEON));
			return;
		}
		
		Game game = gameWorld.getGame();
		if (game != null) {
			MessageUtil.sendMessage(sender, messageConfig.getMessage(Messages.ERROR_LEAVE_DUNGEON));
			return;
		}
		
		for (Player groupPlayer : dGroup.getPlayers()) {
			DPlayer.getByPlayer(groupPlayer).ready(GameTypeDefault.TEST);
		}
	}
	
}
