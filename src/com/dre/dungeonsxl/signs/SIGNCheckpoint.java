package com.dre.dungeonsxl.signs;

import org.bukkit.block.Sign;

import com.dre.dungeonsxl.game.GameCheckpoint;
import com.dre.dungeonsxl.game.GameWorld;

public class SIGNCheckpoint extends DSign{

	@Override
	public boolean check(Sign sign) {
		// TODO Auto-generated method stub
		
		return false;
	}

	@Override
	public void onDungeonInit(Sign sign, GameWorld gworld) {
		String lines[] = sign.getLines();
		int radius = 0;
		
		if(lines[2] != null ){
			if(lines[2].length() > 0){
				radius = p.parseInt(lines[2]);
			}
		}

		new GameCheckpoint(gworld, sign.getLocation(), radius);
		sign.setTypeId(0);
	}

	@Override
	public void onTrigger(Sign sign, GameWorld gworld) {
		// TODO Auto-generated method stub
		
	}
}
