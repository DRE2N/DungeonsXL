package com.dre.dungeonsxl.signs;

import org.bukkit.block.Sign;

import com.dre.dungeonsxl.game.GameCheckpoint;
import com.dre.dungeonsxl.game.GameWorld;

public class SIGNCheckpoint extends DSign{
	
	public static String name = "Checkpoint";
	public static String buildPermissions = "dxl.sign.checkpoint";
	public static boolean onDungeonInit = false;
	
	public SIGNCheckpoint(Sign sign, GameWorld gworld) {
		super(sign, gworld);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean check(Sign sign) {
		// TODO Auto-generated method stub
		
		return false;
	}

	@Override
	public void onInit() {
		String lines[] = sign.getLines();
		int radius = 0;
		
		if(lines[1] != null ){
			if(lines[1].length() > 0){
				radius = p.parseInt(lines[1]);
			}
		}

		new GameCheckpoint(gworld, sign.getLocation(), radius);
		sign.setTypeId(0);
	}

	@Override
	public void onTrigger() {
		// TODO Auto-generated method stub
		
	}
}
