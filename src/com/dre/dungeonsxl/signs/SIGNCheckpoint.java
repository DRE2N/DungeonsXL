package com.dre.dungeonsxl.signs;

import org.bukkit.block.Sign;

import com.dre.dungeonsxl.DPlayer;
import com.dre.dungeonsxl.P;
import com.dre.dungeonsxl.game.GameWorld;

public class SIGNCheckpoint extends DSign{
	
	public static String name = "Checkpoint";
	public static String buildPermissions = "dxl.sign.checkpoint";
	public static boolean onDungeonInit = false;
	
	public SIGNCheckpoint(Sign sign, GameWorld gworld) {
		super(sign, gworld);
	}
	
	@Override
	public boolean check(Sign sign) {
		// TODO Auto-generated method stub
		
		return false;
	}

	@Override
	public void onInit() {
		sign.setTypeId(0);
	}

	@Override
	public void onTrigger() {
		for(DPlayer dplayer:DPlayer.get(this.gworld.world)){
			dplayer.setCheckpoint(this.sign.getLocation());
			P.p.msg(dplayer.player, P.p.language.get("Player_CheckpointReached"));
		}
	}
}
