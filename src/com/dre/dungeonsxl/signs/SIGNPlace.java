package com.dre.dungeonsxl.signs;

import org.bukkit.block.Sign;

import com.dre.dungeonsxl.game.GamePlaceableBlock;
import com.dre.dungeonsxl.game.GameWorld;

public class SIGNPlace extends DSign{
	
	public static String name = "Place";
	public static String buildPermissions = "dxl.sign.place";
	public static boolean onDungeonInit = false;
	
	public SIGNPlace(Sign sign, GameWorld gworld) {
		super(sign, gworld);
	}
	
	@Override
	public boolean check(Sign sign) {
		// TODO Auto-generated method stub
		
		return false;
	}

	@Override
	public void onInit() {
		String lines[] = sign.getLines();
		gworld.placeableBlocks.add(new GamePlaceableBlock(sign.getBlock(), lines[1], lines[2]) );
		sign.setTypeId(0);
	}
}
