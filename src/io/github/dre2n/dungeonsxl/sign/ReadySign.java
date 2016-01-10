package io.github.dre2n.dungeonsxl.sign;

import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
import io.github.dre2n.dungeonsxl.file.DMessages.Messages;
import io.github.dre2n.dungeonsxl.player.DPlayer;
import io.github.dre2n.dungeonsxl.trigger.InteractTrigger;
import io.github.dre2n.dungeonsxl.util.messageutil.MessageUtil;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class ReadySign extends DSign {
	
	private DSignType type = DSignTypeDefault.READY;
	
	public ReadySign(Sign sign, GameWorld gameWorld) {
		super(sign, gameWorld);
	}
	
	@Override
	public boolean check() {
		return true;
	}
	
	@Override
	public void onInit() {
		if ( !getTriggers().isEmpty()) {
			getSign().getBlock().setType(Material.AIR);
			return;
		}
		
		InteractTrigger trigger = InteractTrigger.getOrCreate(0, getSign().getBlock(), getGameWorld());
		if (trigger != null) {
			trigger.addListener(this);
			addTrigger(trigger);
		}
		
		getSign().setLine(0, ChatColor.DARK_BLUE + "############");
		getSign().setLine(1, ChatColor.DARK_GREEN + "Ready");
		getSign().setLine(2, "");
		getSign().setLine(3, ChatColor.DARK_BLUE + "############");
		getSign().update();
	}
	
	@Override
	public boolean onPlayerTrigger(Player player) {
		ready(DPlayer.getByPlayer(player));
		return true;
	}
	
	@Override
	public void onTrigger() {
		for (DPlayer dPlayer : plugin.getDPlayers()) {
			ready(dPlayer);
		}
	}
	
	private void ready(DPlayer dPlayer) {
		if (dPlayer == null) {
			return;
		}
		
		if (dPlayer.isReady()) {
			return;
		}
		
		if (getGameWorld().getSignClass().isEmpty() || dPlayer.getDClass() != null) {
			dPlayer.ready();
			MessageUtil.sendMessage(dPlayer.getPlayer(), plugin.getDMessages().getMessage(Messages.PLAYER_READY));
			return;
			
		} else {
			MessageUtil.sendMessage(dPlayer.getPlayer(), plugin.getDMessages().getMessage(Messages.ERROR_READY));
		}
	}
	
	@Override
	public DSignType getType() {
		return type;
	}
	
}
