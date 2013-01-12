package com.dre.dungeonsxl.commands;

import org.bukkit.entity.Player;

import com.dre.dungeonsxl.DGroup;
import com.dre.dungeonsxl.DPlayer;
import com.dre.dungeonsxl.game.GameWorld;

public class CMDLeave extends DCommand {

	public CMDLeave(){
		this.command="leave";
		this.args=0;
		this.help=p.language.get("Help_Cmd_Leave");
	}

	@Override
	public void onExecute(String[] args, Player player) {
		DPlayer dplayer=DPlayer.get(player);

		if(GameWorld.get(player.getWorld())!=null){
			if(GameWorld.get(player.getWorld()).isTutorial){
				p.msg(player,p.language.get("Error_NoLeaveInTutorial"));
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
				p.msg(player,p.language.get("Cmd_Leave_Success"));
				return;
			}
			p.msg(player,p.language.get("Error_NotInDungeon"));
		}
	}
}
