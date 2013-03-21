package com.dre.dungeonsxl.signs;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import com.dre.dungeonsxl.game.GameWorld;

public class SIGNLeave extends DSign{

	@Override
	public boolean check(Sign sign) {
		// TODO Auto-generated method stub
		
		return false;
	}

	@Override
	public void onDungeonInit(Sign sign, GameWorld gworld) {
		gworld.blocksLeave.add(sign.getBlock());
		sign.setLine(0, ChatColor.BLUE+"############");
		sign.setLine(1, ChatColor.DARK_GREEN+"Leave");
		sign.setLine(2, "");
		sign.setLine(3, ChatColor.BLUE+"############");
		sign.update();
	}

	@Override
	public void onTrigger(Sign sign, GameWorld gworld) {
		
	}
}
