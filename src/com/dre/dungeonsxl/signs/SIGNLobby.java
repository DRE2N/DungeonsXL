package com.dre.dungeonsxl.signs;

import org.bukkit.block.Sign;
import com.dre.dungeonsxl.game.GameWorld;

public class SIGNLobby extends DSign{
	
	public static String name = "Lobby";
	public static String buildPermissions = "dxl.sign.lobby";
	public static boolean onDungeonInit = true;
	
	public SIGNLobby(Sign sign, GameWorld gworld) {
		super(sign, gworld);
	}
	
	@Override
	public boolean check(Sign sign) {
		// TODO Auto-generated method stub
		
		return false;
	}

	@Override
	public void onInit() {
		gworld.locLobby = sign.getLocation();
		sign.setTypeId(0);
	}
}
