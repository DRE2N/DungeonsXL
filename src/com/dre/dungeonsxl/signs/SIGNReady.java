package com.dre.dungeonsxl.signs;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import com.dre.dungeonsxl.game.GameWorld;

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
		gworld.blocksReady.add(sign.getBlock());
		sign.setLine(0, ChatColor.BLUE + "############");
		sign.setLine(1, ChatColor.DARK_GREEN + "Ready");
		sign.setLine(2, "");
		sign.setLine(3, ChatColor.BLUE + "############");
		sign.update();
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
