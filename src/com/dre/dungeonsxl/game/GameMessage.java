package com.dre.dungeonsxl.game;

import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.block.Block;

import com.dre.dungeonsxl.DPlayer;
import com.dre.dungeonsxl.DungeonsXL;

public class GameMessage {
	public static CopyOnWriteArrayList<GameMessage> gmessages=new CopyOnWriteArrayList<GameMessage>();
	
	//Variables
	public CopyOnWriteArrayList<DPlayer> playerDone=new CopyOnWriteArrayList<DPlayer>();
	
	public Block block;
	public String msg;
	public GameWorld gworld;
	public int radius;
	
	public GameMessage(Block block, int msgid,GameWorld gworld,int radius){
		this.block=block;
		this.msg=gworld.confReader.getMsg(msgid);
		this.gworld=gworld;
		this.radius=radius;
		
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
						DungeonsXL.p.msg(dplayer.player, gmessage.msg);
						gmessage.playerDone.add(dplayer);
					}
				}
			}
		}
	}
	
	
}
