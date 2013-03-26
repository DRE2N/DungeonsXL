package com.dre.dungeonsxl.signs;

import org.bukkit.block.Sign;

import com.dre.dungeonsxl.P;
import com.dre.dungeonsxl.game.GameWorld;

public abstract class DSign {
	protected static P p = P.p;
	
	public static String name = "";	
	public static String buildPermissions = "";
	public static boolean onDungeonInit = false;
	
	protected Sign sign;
	protected GameWorld gworld;
	
	public DSign(Sign sign, GameWorld gworld){
		this.sign = sign;
		this.gworld = gworld;
	}
	
	public abstract boolean check(Sign sign);
	
	public void onInit(){
		
	}
	
	public void onTrigger(){
		
	}
	
	public static DSign create(Sign sign, GameWorld gworld){
		String[] lines = sign.getLines();
		DSign dSign = null;
		
		if(lines[0].equalsIgnoreCase("["+SIGNCheckpoint.name+"]")) {
			dSign = new SIGNCheckpoint(sign, gworld);
		} else if (lines[0].equalsIgnoreCase("["+SIGNChest.name+"]")) {
			dSign = new SIGNChest(sign, gworld);
		} else if (lines[0].equalsIgnoreCase("["+SIGNChunkUpdater.name+"]")) {
			dSign = new SIGNChunkUpdater(sign, gworld);
		} else if (lines[0].equalsIgnoreCase("["+SIGNClasses.name+"]")) {
			dSign = new SIGNClasses(sign, gworld);
		} else if (lines[0].equalsIgnoreCase("["+SIGNEnd.name+"]")) {
			dSign = new SIGNEnd(sign, gworld);
		} else if (lines[0].equalsIgnoreCase("["+SIGNLeave.name+"]")) {
			dSign = new SIGNLeave(sign, gworld);
		} else if (lines[0].equalsIgnoreCase("["+SIGNLobby.name+"]")) {
			dSign = new SIGNLobby(sign, gworld);
		} else if (lines[0].equalsIgnoreCase("["+SIGNMob.name+"]")) {
			dSign = new SIGNMob(sign, gworld);
		} else if (lines[0].equalsIgnoreCase("["+SIGNMsg.name+"]")) {
			dSign = new SIGNMsg(sign, gworld);
		} else if (lines[0].equalsIgnoreCase("["+SIGNPlace.name+"]")) {
			dSign = new SIGNPlace(sign, gworld);
		} else if (lines[0].equalsIgnoreCase("["+SIGNReady.name+"]")) {
			dSign = new SIGNReady(sign, gworld);
		} else if (lines[0].equalsIgnoreCase("["+SIGNSoundMsg.name+"]")) {
			dSign = new SIGNSoundMsg(sign, gworld);
		} else if (lines[0].equalsIgnoreCase("["+SIGNStart.name+"]")) {
			dSign = new SIGNStart(sign, gworld);
		} 
		
		return dSign;
	}
}
