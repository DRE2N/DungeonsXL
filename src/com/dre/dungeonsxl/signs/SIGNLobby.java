package com.dre.dungeonsxl.signs;

import org.bukkit.block.Sign;
import com.dre.dungeonsxl.game.GameWorld;

public class SIGNLobby extends DSign{

	@Override
	public boolean check(Sign sign) {
		// TODO Auto-generated method stub
		
		return false;
	}

	@Override
	public void onDungeonInit(Sign sign, GameWorld gworld) {
		gworld.locLobby = sign.getLocation();
		sign.setTypeId(0);
	}

	@Override
	public void onTrigger(Sign sign, GameWorld gworld) {
		
	}
}
