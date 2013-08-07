package com.dre.dungeonsxl.signs;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import com.dre.dungeonsxl.DPlayer;
import com.dre.dungeonsxl.game.GameWorld;
import com.dre.dungeonsxl.trigger.InteractTrigger;
import com.dre.dungeonsxl.P;

public class SIGNReady extends DSign {

	public static String name = "Ready";
	public String buildPermissions = "dxl.sign.ready";
	public boolean onDungeonInit = true;

	public SIGNReady(Sign sign, GameWorld gworld) {
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
			sign.setLine(1, ChatColor.DARK_GREEN + "Ready");
			sign.setLine(2, "");
			sign.setLine(3, ChatColor.DARK_BLUE + "############");
			sign.update();
		} else {
			sign.getBlock().setTypeId(0);
		}
	}

	@Override
	public boolean onPlayerTrigger(Player player) {
		ready(DPlayer.get(player));
		return true;
	}

	@Override
	public void onTrigger() {
		for (DPlayer dplayer : DPlayer.players) {
			ready(dplayer);
		}
	}

	private void ready(DPlayer dplayer) {
		if (dplayer != null) {
			if (!dplayer.isReady) {
				if (gworld.signClass.isEmpty() || dplayer.dclass != null) {
					dplayer.ready();
					P.p.msg(dplayer.player, p.language.get("Player_Ready"));
					return;
				} else {
					P.p.msg(dplayer.player, p.language.get("Error_Ready"));
				}
			}
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
