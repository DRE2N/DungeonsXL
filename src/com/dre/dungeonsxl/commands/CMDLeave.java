package com.dre.dungeonsxl.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.dre.dungeonsxl.DGroup;
import com.dre.dungeonsxl.DPlayer;
import com.dre.dungeonsxl.game.GameWorld;

public class CMDLeave extends DCommand {
	
	public CMDLeave(){
		this.command="leave";
		this.args=0;
		this.help="/dxl leave - leaves the current dungeon.";
	}
	
	@Override
	public void onExecute(String[] args, Player player) {
		DPlayer dplayer=DPlayer.get(player);
		
		if(GameWorld.get(player.getWorld())!=null){
			if(GameWorld.get(player.getWorld()).isTutorial){
				p.msg(player,ChatColor.RED+"Du kannst diesen Befehl nicht in einem Tutorial benutzen!");
				return;
			}
		}
		
		if(dplayer!=null){
			dplayer.leave();
			return;
		}else{
			DGroup dgroup=DGroup.get(player);
			if(dgroup!=null){
				dgroup.removePlayer(player);
				p.msg(player,ChatColor.YELLOW+"Du hast deine Gruppe erfolgreich verlassen!");
				return;
			}
			p.msg(player,ChatColor.RED+"You aren't in a dungeon!");
		}
	}
}
