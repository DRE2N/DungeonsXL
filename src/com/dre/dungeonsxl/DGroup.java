package com.dre.dungeonsxl;

import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.entity.Player;
import com.dre.dungeonsxl.game.GameWorld;

public class DGroup {
	public static CopyOnWriteArrayList<DGroup> dgroups=new CopyOnWriteArrayList<DGroup>();
	
	//Variables
	public CopyOnWriteArrayList<Player> players=new CopyOnWriteArrayList<Player>();
	public String dungeonname;
	public GameWorld gworld;
	public boolean isPlaying;
	
	
	public DGroup(Player player, String dungeonname){
		dgroups.add(this);
		
		this.players.add(player);
		this.isPlaying=false;
		this.dungeonname=dungeonname;
	}
	
	public void removePlayer(Player player) {
		this.players.remove(player);
		DGSign.updatePerGroup(this);
	}

	public boolean isEmpty() {
		return this.players.isEmpty();
	}

	public void remove() {
		dgroups.remove(this);
		DGSign.updatePerGroup(this);
	}
	
	public void startGame(){
		this.isPlaying=true;
		gworld.startGame();
		for(Player player:players){
			DPlayer dplayer=DPlayer.get(player);
			dplayer.respawn();
		}
		DGSign.updatePerGroup(this);
		
	}
	
	//Statics
	public static DGroup get(Player player){
		for(DGroup dgroup:dgroups){
			if(dgroup.players.contains(player)){
				return dgroup;
			}
		}
		return null;
	}
	
	public static DGroup get(GameWorld gworld){
		for(DGroup dgroup:dgroups){
			if(dgroup.gworld==gworld){
				return dgroup;
			}
		}
		return null;
	}
	
	public static void leaveGroup(Player player){
		for(DGroup dgroup:dgroups){
			if(dgroup.players.contains(player)){
				dgroup.players.remove(player);
			}
		}
	}

}
