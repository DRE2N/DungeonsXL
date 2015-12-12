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
	
	public static String name = "Leave";
	public String buildPermissions = "dxl.sign.leave";
	public boolean onDungeonInit = true;
	
	public LeaveSign(Sign sign, GameWorld gWorld) {
		super(sign, gWorld);
	}
	
	@Override
	public boolean check() {
		// TODO Auto-generated method stub
		
		return true;
	}
	
	@Override
	public void onInit() {
		if (getTriggers().isEmpty()) {
			InteractTrigger trigger = InteractTrigger.getOrCreate(0, getSign().getBlock(), getGWorld());
			if (trigger != null) {
				trigger.addListener(this);
				addTrigger(trigger);
			}
			getSign().setLine(0, ChatColor.DARK_BLUE + "############");
			getSign().setLine(1, ChatColor.DARK_GREEN + "Leave");
			getSign().setLine(2, "");
			getSign().setLine(3, ChatColor.DARK_BLUE + "############");
			getSign().update();
		} else {
			getSign().getBlock().setType(Material.AIR);
		}
	}
	
	@Override
	public boolean onPlayerTrigger(Player player) {
		DPlayer dplayer = DPlayer.get(player);
		if (dplayer != null) {
			dplayer.leave();
		}
		return true;
	}
	
	@Override
	public void onTrigger() {
		for (DPlayer dplayer : DungeonsXL.getPlugin().getDPlayers()) {
			dplayer.leave();
		}
	}
	
	@Override
	public String getPermissions() {
		return buildPermissions;
	}
	
	@Override
	public boolean isOnDungeonInit() {
		return onDungeonInit;
	}
}
