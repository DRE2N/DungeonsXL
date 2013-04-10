package com.dre.dungeonsxl.signs;

import org.bukkit.block.Sign;

import com.dre.dungeonsxl.game.GameWorld;

public class SIGNTrigger extends DSign{
	public static String name = "Trigger";
	public String buildPermissions = "dxl.sign.trigger";
	public boolean onDungeonInit = false;
	
	//Variables
	private int triggerId;
	private boolean initialized;
	
	public SIGNTrigger(Sign sign, GameWorld gworld) {
		super(sign, gworld);
	}

	@Override
	public boolean check() {
		return true;
	}
	
	@Override
	public void onInit() {
		triggerId = p.parseInt(sign.getLine(1));
		sign.getBlock().setTypeId(0);
		
		initialized = true;
	}

	@Override
	public void onUpdate(int type,boolean powered) {
		if(initialized){
			setPowered(type,powered);
			if(!isDistanceTrigger()){
				for(DSign dsign : this.gworld.dSigns){
					if(dsign != null){
						if(dsign.isSignTrigger()){
							if(triggerId == dsign.getStId()){
								dsign.onUpdate(1,isPowered());
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void onTrigger() {
		if(initialized){
			for(DSign dsign : this.gworld.dSigns){
				if(dsign != null){
					if(dsign.isSignTrigger()){
						if(triggerId == dsign.getStId()){
							dsign.onUpdate(1,true);
						}
					}
				}
			}
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
