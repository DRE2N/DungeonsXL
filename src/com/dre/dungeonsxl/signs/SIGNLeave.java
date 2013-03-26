package com.dre.dungeonsxl.signs;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import com.dre.dungeonsxl.game.GameWorld;

public class SIGNLeave extends DSign{
	
	public static String name = "Leave";
	public static String buildPermissions = "dxl.sign.leave";
	public static boolean onDungeonInit = true;
	
	public SIGNLeave(Sign sign, GameWorld gworld) {
		super(sign, gworld);
	}
	
	@Override
	public boolean check(Sign sign) {
		// TODO Auto-generated method stub
		
		return false;
	}

	@Override
	public void onInit() {
		gworld.blocksLeave.add(sign.getBlock());
		sign.setLine(0, ChatColor.BLUE+"############");
		sign.setLine(1, ChatColor.DARK_GREEN+"Leave");
		sign.setLine(2, "");
		sign.setLine(3, ChatColor.BLUE+"############");
		sign.update();
	}
}
