package com.dre.dungeonsxl.signs;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import com.dre.dungeonsxl.game.GameWorld;

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
		gworld.blocksEnd.add(sign.getBlock());
		sign.setLine(0, ChatColor.DARK_BLUE + "############");
		sign.setLine(1, ChatColor.DARK_GREEN + "End");
		sign.setLine(2, "");
		sign.setLine(3, ChatColor.DARK_BLUE + "############");
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
