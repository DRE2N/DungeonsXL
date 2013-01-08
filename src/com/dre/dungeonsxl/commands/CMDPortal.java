package com.dre.dungeonsxl.commands;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.dre.dungeonsxl.DPlayer;
import com.dre.dungeonsxl.DPortal;

public class CMDPortal extends DCommand{
	
	public CMDPortal(){
		this.command="portal";
		this.args=0;
		this.help="/dxl portal - Create a portal that goes in a dungeon or out a dungeon";
		this.permissions="dxl.portal";
	}
	
	@Override
	public void onExecute(String[] args, Player player) {
		
		
		
		DPortal dportal=new DPortal(false);
		dportal.player=player;
		dportal.world=player.getWorld();
		player.getInventory().setItemInHand(new ItemStack(268));
		
		//Check Destination
		DPlayer dplayer=DPlayer.get(player);
		if(dplayer!=null){ //Is player in EditWorld
			dportal.type="toworld";
		}else{ //Is player in normal World
			dportal.type="todungeon";
		}
		
		
	}
 
}
