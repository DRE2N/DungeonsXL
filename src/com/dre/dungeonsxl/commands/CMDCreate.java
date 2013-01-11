package com.dre.dungeonsxl.commands;

import org.bukkit.entity.Player;

import com.dre.dungeonsxl.DPlayer;
import com.dre.dungeonsxl.EditWorld;

public class CMDCreate extends DCommand {
	
	public CMDCreate(){
		this.args=1;
		this.command="create";
		this.help=p.language.get("help_cmd_create");
		this.permissions="dxl.create";
	}
	
	@Override
	public void onExecute(String[] args, Player player) {
		String name=args[1];
		
		if(DPlayer.get(player)==null){
			if(name.length()<=15){
			
				//Msg create
				p.log(p.language.get("log_newdungeon"));//"New Dungeon: "+name);
				p.log(p.language.get("log_generatenewworld"));//"Generate new world...");
				
				//Create World
				EditWorld eworld=new EditWorld();
				eworld.generate();
				eworld.dungeonname=name;
				
				//MSG Done
				p.log(p.language.get("log_worldgenerationfinished"));//"World generation finished!"
				
				//Tp Player
				if(eworld.lobby==null){
					new DPlayer(player,eworld.world,eworld.world.getSpawnLocation(), true);
				}else{
					new DPlayer(player,eworld.world,eworld.lobby, true);
				}
			}else{
				p.msg(player, p.language.get("cmd_create_error1"));//ChatColor.RED+"Der Name darf nicht länger sein als 15 Zeichen!");
			}
		}else{
			p.msg(player, p.language.get("cmd_create_error2"));//ChatColor.RED+"Du musst zuerst aus dem aktuellen Dungeon raus!");
		}
		
	}


}
