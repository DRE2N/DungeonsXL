package com.dre.dungeonsxl.game;

import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Location;

import com.dre.dungeonsxl.DPlayer;
import com.dre.dungeonsxl.P;


public class GameCheckpoint {
	public static CopyOnWriteArrayList<GameCheckpoint> gcheckpoints=new CopyOnWriteArrayList<GameCheckpoint>();
	
	//Variables
	public GameWorld gworld;
	public Location location;
	public int radius;
	public CopyOnWriteArrayList<DPlayer> dplayerHasUsed=new CopyOnWriteArrayList<DPlayer>();
	
	public GameCheckpoint(GameWorld gworld, Location location, int radius){
		gcheckpoints.add(this);
		
		this.location=location;
		this.radius=radius;
		if(this.radius==0){
			this.radius=5;
		}
		this.gworld=gworld;
	}
	
	
	//Statics
	
	public static void update(){
		for(GameCheckpoint gpoint:gcheckpoints){
			for(DPlayer dplayer:DPlayer.get(gpoint.gworld.world)){
				if(!gpoint.dplayerHasUsed.contains(dplayer)){
					if(dplayer.player.getLocation().distance(gpoint.location)<=gpoint.radius){
						dplayer.setCheckpoint(gpoint);
						P.p.msg(dplayer.player, P.p.language.get("player_checkpoint_reached"));//ChatColor.GOLD+"Checkpoint erreicht!");
						gpoint.dplayerHasUsed.add(dplayer);
					}
				}
			}
		}
	}
	
	
}
