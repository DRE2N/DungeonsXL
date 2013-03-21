package com.dre.dungeonsxl.signs;

import org.bukkit.block.Sign;

import com.dre.dungeonsxl.P;
import com.dre.dungeonsxl.game.GameWorld;

public abstract class DSign {
	public static P p = P.p;
	
	public String name;	
	public String permissions;
	public boolean onInit;
	
	public abstract boolean check(Sign sign);
	public abstract void onDungeonInit(Sign sign, GameWorld gworld);
	public abstract void onTrigger(Sign sign, GameWorld gworld);
}
