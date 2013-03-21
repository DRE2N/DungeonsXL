package com.dre.dungeonsxl.signs;

import org.bukkit.block.Sign;

import com.dre.dungeonsxl.game.GamePlaceableBlock;
import com.dre.dungeonsxl.game.GameWorld;

public class SIGNPlace extends DSign{

	@Override
	public boolean check(Sign sign) {
		// TODO Auto-generated method stub
		
		return false;
	}

	@Override
	public void onDungeonInit(Sign sign, GameWorld gworld) {
		String lines[] = sign.getLines();
		gworld.placeableBlocks.add(new GamePlaceableBlock(sign.getBlock(), lines[2], lines[3]) );
		sign.setTypeId(0);
	}

	@Override
	public void onTrigger(Sign sign, GameWorld gworld) {
		// TODO Auto-generated method stub
		
	}
}
