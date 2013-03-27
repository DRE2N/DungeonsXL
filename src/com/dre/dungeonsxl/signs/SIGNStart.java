package com.dre.dungeonsxl.signs;

import org.bukkit.block.Sign;
import com.dre.dungeonsxl.game.GameWorld;

public class SIGNStart extends DSign{
	
	public static String name = "Start";
	public String buildPermissions = "dxl.sign.start";
	public boolean onDungeonInit = true;
	
	public SIGNStart(Sign sign, GameWorld gworld) {
		super(sign, gworld);
	}
	
	@Override
	public boolean check() {
		// TODO Auto-generated method stub
		
		return true;
	}

	@Override
	public void onInit() {
		gworld.locStart = sign.getLocation();
		sign.getBlock().setTypeId(0);
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
