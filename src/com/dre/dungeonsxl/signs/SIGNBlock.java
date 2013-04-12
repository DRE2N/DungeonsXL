package com.dre.dungeonsxl.signs;

import org.bukkit.block.Sign;
import org.bukkit.block.Block;

import com.dre.dungeonsxl.game.GameWorld;

public class SIGNBlock extends DSign{
	
	public static String name = "Block";
	public String buildPermissions = "dxl.sign.block";
	public boolean onDungeonInit = false;
	
	//Variables
	private boolean initialized;
	private boolean active;
	private byte side;
	private int offBlock = 0;
	private int onBlock = 0;

	
	public SIGNBlock(Sign sign, GameWorld gworld) {
		super(sign, gworld);
	}
	
	@Override
	public boolean check() {
		// TODO Auto-generated method stub
		
		return true;
	}

	@Override
	public void onInit() {
		String lines[] = sign.getLines();
		offBlock = p.parseInt(lines[1]);
		onBlock = p.parseInt(lines[2]);
		sign.getBlock().setTypeId(offBlock);
		initialized = true;
	}

	@Override
	public void onUpdate(int type,boolean powered) {
		if(initialized){
			setPowered(type,powered);
			if(isPowered()){
				if(!isDistanceTrigger()){
					onTrigger();
				}
			} else {
				active = false;
				sign.getBlock().setTypeId(offBlock);
			}
		}
	}


	@Override
	public void onTrigger() {
		if(initialized){
			if(!active){
				sign.getBlock().setTypeId(onBlock);
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
