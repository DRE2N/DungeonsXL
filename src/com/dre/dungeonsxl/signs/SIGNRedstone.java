package com.dre.dungeonsxl.signs;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import com.dre.dungeonsxl.game.GameWorld;

public class SIGNRedstone extends DSign{
	
	public static String name = "Redstone";
	public String buildPermissions = "dxl.sign.redstone";
	public boolean onDungeonInit = false;
	
	//Variables
	private boolean initialized;
	private boolean active;
	private Block block;
	
	public SIGNRedstone(Sign sign, GameWorld gworld) {
		super(sign, gworld);
	}
	
	@Override
	public boolean check() {
		if(isRedstoneTrigger()){
			return false;
		}
		
		return true;
	}

	@Override
	public void onInit() {
		this.block = sign.getBlock();
		this.block.setTypeId(0);
		
		initialized = true;
	}

	@Override
	public void onUpdate(int type, boolean powered) {
		if(initialized){
			setPowered(type, powered);
			if(isPowered()){
				if(!isDistanceTrigger()){
					onTrigger();
				}
			} else {
				active = false;
				block.setTypeId(0);
			}
		}
	}
	
	@Override
	public void onTrigger() {
		if(initialized){
			if(!active){
				block.setTypeId(152);
				active = true;
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

