package com.dre.dungeonsxl.signs;

import org.bukkit.block.Sign;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitTask;

import com.dre.dungeonsxl.P;
import com.dre.dungeonsxl.DPlayer;
import com.dre.dungeonsxl.game.GameWorld;

public class SIGNRedstone extends DSign{
	
	public static String name = "Redstone";
	public String buildPermissions = "dxl.sign.redstone";
	public boolean onDungeonInit = false;
	
	//Variables
	private boolean initialized;
	private boolean active;
	private byte side;
	private BukkitTask task = null;
	
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
		if(sign.getBlock().getType() == Material.WALL_SIGN){
			switch(sign.getData().getData()){
				case 5:
					side = 0x1;	//west
					break;
				case 4:
					side = 0x2;	//east
					break;
				case 3:
					side = 0x3;	//north
					break;
				case 2:
					side = 0x4;	//south
					break;
			}
		} else {
			side = 0x5;	//up
		}
		gworld.untouchable.add(sign.getBlock().getRelative(BlockFace.DOWN));
		gworld.untouchable.add(sign.getBlock().getRelative(BlockFace.UP));
		gworld.untouchable.add(sign.getBlock().getRelative(BlockFace.WEST));
		gworld.untouchable.add(sign.getBlock().getRelative(BlockFace.EAST));
		gworld.untouchable.add(sign.getBlock().getRelative(BlockFace.NORTH));
		gworld.untouchable.add(sign.getBlock().getRelative(BlockFace.SOUTH));
		sign.getBlock().setTypeId(0);
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
				killTask();
				active = false;
				sign.getBlock().setTypeId(0);
			}
		}
	}

	@Override
	public void onDiscover(){
		if(initialized && active){
			P.p.getServer().getScheduler().scheduleSyncDelayedTask(p, new DiscoveryTask(), 1);
			P.p.getServer().getScheduler().scheduleSyncDelayedTask(p, new DiscoveryTask(), 5);
		}
	}

	@Override
	public void killTask(){
		if(initialized && active){
			if(task != null){
				task.cancel();
				task = null;
			}
		}
	}


	@Override
	public void onTrigger() {
		if(initialized){
			if(!active){
				sign.getBlock().setData(side);
				sign.getBlock().setTypeId(76);
				active = true;
				if(task == null){
					task = P.p.getServer().getScheduler().runTaskTimer(p, new DiscoveryTask(), 1, 60);
					P.p.getServer().getScheduler().scheduleSyncDelayedTask(p, new DiscoveryTask(), 5);
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

	public class DiscoveryTask implements Runnable  {

		public DiscoveryTask() {
	    }
	 
	 	@Override
	    public void run() {
	    	if(initialized && active){
				for(DPlayer dplayer:DPlayer.players){
					if(!dplayer.isEditing){
						dplayer.player.sendBlockChange(sign.getBlock().getLocation(),0,(byte)0);
					}
				}
			}
		}
	}
}

