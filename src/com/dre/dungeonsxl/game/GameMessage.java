package com.dre.dungeonsxl.game;

import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.block.Block;
import org.getspout.spoutapi.Spout;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.dre.dungeonsxl.DPlayer;
import com.dre.dungeonsxl.P;

public class GameMessage {
	public static CopyOnWriteArrayList<GameMessage> gmessages=new CopyOnWriteArrayList<GameMessage>();
	
	//Variables
	public CopyOnWriteArrayList<DPlayer> playerDone=new CopyOnWriteArrayList<DPlayer>();
	
	public Block block;
	public String msg;
	public GameWorld gworld;
	public int radius;
	public boolean isSpoutSoundMsg;
	
	public GameMessage(Block block, int msgid,GameWorld gworld,int radius,boolean isSpoutSoundMsg){
		this.block=block;
		this.msg=gworld.config.getMsg(msgid);
		this.gworld=gworld;
		this.radius=radius;
		this.isSpoutSoundMsg=isSpoutSoundMsg;
		
		if(this.msg!=null){
			gmessages.add(this);
		}
	}
	
	
	//Static
	
	public static void updateAll(){
		for(GameMessage gmessage:gmessages){
			for(DPlayer dplayer:DPlayer.get(gmessage.gworld.world)){
				if(!gmessage.playerDone.contains(dplayer)){
					if(dplayer.player.getLocation().distance(gmessage.block.getLocation())<gmessage.radius+1){
						gmessage.playerDone.add(dplayer);
						
						if(gmessage.isSpoutSoundMsg){
							if(P.p.isSpoutEnabled){
								SpoutPlayer sPlayer = Spout.getServer().getPlayer(dplayer.player.getName());
								if(sPlayer.isSpoutCraftEnabled()){
									SpoutManager.getSoundManager().playCustomMusic(P.p, sPlayer, gmessage.msg, false, gmessage.block.getLocation());
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
