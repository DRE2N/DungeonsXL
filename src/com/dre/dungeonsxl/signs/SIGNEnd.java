package com.dre.dungeonsxl.signs;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import com.dre.dungeonsxl.game.GameWorld;

public class SIGNEnd extends DSignType{

	@Override
	public boolean check(Sign sign) {
		// TODO Auto-generated method stub
		
		return false;
	}

	@Override
	public void onDungeonInit(Sign sign, GameWorld gworld) {
		gworld.blocksEnd.add(sign.getBlock());
		sign.setLine(0, ChatColor.DARK_BLUE+"############");
		sign.setLine(1, ChatColor.DARK_GREEN+"End");
		sign.setLine(2, "");
		sign.setLine(3, ChatColor.DARK_BLUE+"############");
		sign.update();
	}

	@Override
	public void onTrigger(Sign sign, GameWorld gworld) {
		
	}
}
