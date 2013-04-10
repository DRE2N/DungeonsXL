package com.dre.dungeonsxl.signs;

import org.bukkit.block.Sign;

import com.dre.dungeonsxl.DPlayer;
import com.dre.dungeonsxl.P;
import com.dre.dungeonsxl.game.GameWorld;

public class SIGNCheckpoint extends DSign{
	
	public static String name = "Checkpoint";
	private String buildPermissions = "dxl.sign.checkpoint";
	private boolean onDungeonInit = false;
	
	//Variables
	private boolean initialized;
	
	public SIGNCheckpoint(Sign sign, GameWorld gworld) {
		super(sign, gworld);
	}
	
	@Override
	public boolean check() {
		// TODO Auto-generated method stub
		
		return true;
	}

	@Override
	public void onInit() {
		sign.getBlock().setTypeId(0);
		
		initialized = true;
	}

	@Override
	public void onUpdate(int type,boolean powered) {
		if(initialized){
			setPowered(type,powered);
			if(!isDistanceTrigger()){
				if(isPowered()){
					onTrigger();
				}
			}
		}
	}

	@Override
	public void onTrigger() {
		if(initialized){
			for(DPlayer dplayer:DPlayer.get(this.gworld.world)){
				dplayer.setCheckpoint(this.sign.getLocation());
				P.p.msg(dplayer.player, P.p.language.get("Player_CheckpointReached"));
			}
			
			this.gworld.dSigns.remove(this);
		}
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
