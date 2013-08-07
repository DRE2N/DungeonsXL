package com.dre.dungeonsxl.signs;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import com.dre.dungeonsxl.DPlayer;
import com.dre.dungeonsxl.game.GameWorld;
import com.dre.dungeonsxl.trigger.InteractTrigger;

public class SIGNEnd extends DSign {

	public static String name = "End";
	public String buildPermissions = "dxl.sign.end";
	public boolean onDungeonInit = false;

	public SIGNEnd(Sign sign, GameWorld gworld) {
		super(sign, gworld);
	}

	@Override
	public boolean check() {
		// TODO Auto-generated method stub

		return true;
	}

	@Override
	public void onInit() {
		if (triggers.isEmpty()) {
			InteractTrigger trigger = InteractTrigger.getOrCreate(0, sign.getBlock(), gworld);
			if (trigger != null) {
				trigger.addListener(this);
				this.triggers.add(trigger);
			}
			sign.setLine(0, ChatColor.DARK_BLUE + "############");
			sign.setLine(1, ChatColor.DARK_GREEN + "End");
			sign.setLine(2, "");
			sign.setLine(3, ChatColor.DARK_BLUE + "############");
			sign.update();
		} else {
			sign.getBlock().setTypeId(0);
		}
	}

	@Override
	public boolean onPlayerTrigger(Player player) {
		DPlayer dplayer = DPlayer.get(player);
		if (dplayer != null) {
			if (!dplayer.isFinished) {
				dplayer.finish();
			}
		}
		return true;
	}

	@Override
	public void onTrigger() {
		for (DPlayer dplayer : DPlayer.players) {
			dplayer.finish();
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
