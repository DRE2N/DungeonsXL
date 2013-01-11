package com.dre.dungeonsxl.commands;

import org.bukkit.entity.Player;

import com.dre.dungeonsxl.DGroup;
import com.dre.dungeonsxl.DPlayer;
import com.dre.dungeonsxl.EditWorld;

public class CMDEdit extends DCommand{
	
	public CMDEdit(){
		this.command="edit";
		this.args=1;
		this.help=p.language.get("help_cmd_edit");//"/dxl edit <name> - Edit a existing dungeon";
	}
	
	@Override
	public void onExecute(String[] args, Player player) {
		String dungeonname=args[1];
		
		EditWorld eworld=EditWorld.load(dungeonname);
		
		
		DGroup dgroup=DGroup.get(player);
		DPlayer dplayer=DPlayer.get(player);
		
		if(EditWorld.isInvitedPlayer(dungeonname,player.getName())||p.permission.has(player, "dxl.edit")||player.isOp()){
			if(dplayer==null){
				if(dgroup==null){
					if(eworld!=null){
						if(eworld.lobby==null){
							new DPlayer(player,eworld.world,eworld.world.getSpawnLocation(), true);
						}else{
							new DPlayer(player,eworld.world,eworld.lobby, true);
						}
					}else{
						p.msg(player,p.language.get("cmd_edit_error1",dungeonname));
					}
				}else{
					p.msg(player,p.language.get("cmd_edit_error2"));
				}
			}else{
				p.msg(player,p.language.get("cmd_edit_error3"));
			}
		}
		
	}

}
