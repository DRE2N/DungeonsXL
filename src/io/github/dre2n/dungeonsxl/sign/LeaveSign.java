package io.github.dre2n.dungeonsxl.sign;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
import io.github.dre2n.dungeonsxl.player.DPlayer;
import io.github.dre2n.dungeonsxl.trigger.InteractTrigger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class LeaveSign extends DSign {
	
	private DSignType type = DSignTypeDefault.LEAVE;
	
	public LeaveSign(Sign sign, GameWorld gameWorld) {
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
		getSign().setLine(1, ChatColor.DARK_GREEN + "Leave");
		getSign().setLine(2, "");
		getSign().setLine(3, ChatColor.DARK_BLUE + "############");
		getSign().update();
	}
	
	@Override
	public boolean onPlayerTrigger(Player player) {
		DPlayer dPlayer = DPlayer.getByPlayer(player);
		if (dPlayer != null) {
			dPlayer.leave();
		}
		
		return true;
	}
	
	@Override
	public void onTrigger() {
		for (DPlayer dPlayer : DungeonsXL.getPlugin().getDPlayers()) {
			dPlayer.leave();
		}
	}
	
	@Override
	public DSignType getType() {
		return type;
	}
	
}
