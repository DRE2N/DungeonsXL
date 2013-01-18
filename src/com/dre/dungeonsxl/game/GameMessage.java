package com.dre.dungeonsxl.game;

import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Location;
import org.getspout.spoutapi.Spout;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.dre.dungeonsxl.DPlayer;
import com.dre.dungeonsxl.P;

public class GameMessage {
	
	//Variables
	public CopyOnWriteArrayList<DPlayer> playerDone=new CopyOnWriteArrayList<DPlayer>();
	
	public Location location;
	public String msg;
	public int radius;
	public boolean isSpoutSoundMsg;
	
	public GameMessage(Location location, String msg,int radius,boolean isSpoutSoundMsg){
		this.location=location;
		this.msg = msg;
		this.radius=radius;
		this.isSpoutSoundMsg=isSpoutSoundMsg;
	}
	
	
	//Static
	
	public static void update(GameWorld gworld){
		for(GameMessage gmessage:gworld.messages){
			for(DPlayer dplayer:DPlayer.get(gworld.world)){
				if(!gmessage.playerDone.contains(dplayer)){
					if(dplayer.player.getLocation().distance(gmessage.location)<gmessage.radius+1){
						gmessage.playerDone.add(dplayer);
						
						if(gmessage.isSpoutSoundMsg){
							if(P.p.isSpoutEnabled){
								SpoutPlayer sPlayer = Spout.getServer().getPlayer(dplayer.player.getName());
								if(sPlayer.isSpoutCraftEnabled()){
									SpoutManager.getSoundManager().playCustomMusic(P.p, sPlayer, gmessage.msg, false, gmessage.location);
								}
							}
						} else {
							P.p.msg(dplayer.player, gmessage.msg);
						}
					}
				}
			}
		}
	}
	
	
}
