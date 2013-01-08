package com.dre.dungeonsxl.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.dre.dungeonsxl.DGroup;
import com.dre.dungeonsxl.DPlayer;
import com.dre.dungeonsxl.EditWorld;
import com.dre.dungeonsxl.game.GameWorld;

public class CMDTest extends DCommand {
	
	public CMDTest(){
		this.command="test";
		this.args=-1;
		this.help="/dxl test [dungeon] - Test a Dungeon";
		this.permissions="dxl.test";
	}
	
	@Override
	public void onExecute(String[] args, Player player) {
		DPlayer dplayer=DPlayer.get(player);
		String dungeonname;
		
		if(dplayer==null){
			if(args.length>1){
				dungeonname=args[1];
				
				if(EditWorld.exist(dungeonname)){
					DGroup dgroup=new DGroup(player, dungeonname);
					if(dgroup!=null){
						if(dgroup.gworld==null){
							dgroup.gworld=GameWorld.load(DGroup.get(player).dungeonname);
						}
						
						DPlayer newDPlayer;
						
						if(dgroup.gworld.locLobby==null){
							newDPlayer=new DPlayer(player,dgroup.gworld.world,dgroup.gworld.world.getSpawnLocation(), false);
						}else{
							newDPlayer=new DPlayer(player,dgroup.gworld.world,dgroup.gworld.locLobby, false);
						}
						newDPlayer.isinTestMode=2;
					}
				}else{
					p.msg(player, ChatColor.RED+"Dungeon "+ChatColor.GOLD+dungeonname+ChatColor.RED+" existiert nicht!");
				}
			}else{
				this.displayhelp(player);
			}
		}else if(dplayer.isEditing){
			
			if(args.length>1){
				dungeonname=args[1];
			}else{
				dungeonname=EditWorld.get(dplayer.world).dungeonname;
			}
			
			DGroup dgroup=new DGroup(player, dungeonname);
			if(dgroup!=null){
				if(dgroup.gworld==null){
					dgroup.gworld=GameWorld.load(DGroup.get(player).dungeonname);
				}
				
				DPlayer newDPlayer;
				
				if(dgroup.gworld.locLobby==null){
					newDPlayer=new DPlayer(player,dgroup.gworld.world,dgroup.gworld.world.getSpawnLocation(), false);
				}else{
					newDPlayer=new DPlayer(player,dgroup.gworld.world,dgroup.gworld.locLobby, false);
				}
				
				newDPlayer.oldDPlayer=dplayer;
				dplayer.isinTestMode=1;
			}
			
		}else{
			p.msg(player, ChatColor.RED+"Du must zuerst den aktuellen Dungeon verlassen!");
		}
		
		
		
	}
	
}
