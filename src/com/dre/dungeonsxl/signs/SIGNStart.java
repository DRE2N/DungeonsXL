package com.dre.dungeonsxl.signs;

import org.bukkit.block.Sign;
import com.dre.dungeonsxl.game.GameWorld;

public class SIGNStart extends DSign{
	
	public static String name = "Start";
	public static String buildPermissions = "dxl.sign.start";
	public static boolean onDungeonInit = true;
	
	public SIGNStart(Sign sign, GameWorld gworld) {
		super(sign, gworld);
	}
	
	@Override
	public boolean check(Sign sign) {
		// TODO Auto-generated method stub
		
		return false;
	}

	@Override
	public void onInit() {
		gworld.locStart = sign.getLocation();
		sign.setTypeId(0);
	}
}
