package com.dre.dungeonsxl.signs;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.block.Sign;
import org.bukkit.block.Block;

import com.dre.dungeonsxl.game.GameWorld;
import com.dre.dungeonsxl.EditWorld;

public class SIGNTrigger extends DSign{
	public static String name = "Trigger";
	public String buildPermissions = "dxl.sign.trigger";
	public boolean onDungeonInit = false;
	public static Set<Integer> used = new HashSet<Integer>();
	
	//Variables
	private int triggerId;
	private boolean initialized;
	
	public SIGNTrigger(Sign sign, GameWorld gworld) {
		super(sign, gworld);
	}

	@Override
	public boolean check() {
		used.clear();
		for(Block block : EditWorld.get(sign.getLocation().getWorld()).sign) {
			if(block.getState() instanceof Sign){
				Sign rsign = (Sign) block.getState();
				if(rsign != null){
					if(rsign.getLine(0).equalsIgnoreCase("["+name+"]")){
						used.add(p.parseInt(rsign.getLine(1)));
					}
				}
			}
		}

		int id = 1;
		if(sign.getLine(1).equalsIgnoreCase("")){
			if(used.size() != 0){
				while(used.contains(id)){
					id++;
				}
			}
		} else {
			id = p.parseInt(sign.getLine(1));
			if(used.contains(id)){
				return false;
			} else {
				return true;
			}
		}

		sign.setLine(1, id+"");
		p.getServer().getScheduler().scheduleSyncDelayedTask(p, new UpdateTask(), 2);
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
			if(!isDistanceTrigger() || !isPowered()){
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

	public class UpdateTask implements Runnable  {

		public UpdateTask() {
	    }
	 
	 	@Override
	    public void run() {
	    	sign.update();
	    }
	}
}
