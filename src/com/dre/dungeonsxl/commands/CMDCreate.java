package com.dre.dungeonsxl.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.dre.dungeonsxl.DPlayer;
import com.dre.dungeonsxl.EditWorld;

public class CMDCreate extends DCommand {

	public CMDCreate(){
		this.args=1;
		this.command="create";
		this.help=p.language.get("Help_Cmd_Create");
		this.permissions="dxl.create";
		this.isPlayerCommand = true;
		this.isConsoleCommand = true;
	}

	@Override
	public void onExecute(String[] args, CommandSender sender) {
		String name=args[1];

		if(sender instanceof ConsoleCommandSender){
			if(name.length()<=15){
				//Msg create
				p.log(p.language.get("Log_NewDungeon"));
				p.log(p.language.get("Log_GenerateNewWorld"));

				//Create World
				EditWorld eworld=new EditWorld();
				eworld.generate();
				eworld.dungeonname=name;
				eworld.save();
				eworld.delete();

				//MSG Done
				p.log(p.language.get("Log_WorldGenerationFinished"));
			}else{
				p.msg(sender, p.language.get("Error_NameToLong"));
			}
		} else if(sender instanceof Player){
			Player player = (Player) sender;
			
			if(DPlayer.get(player)==null){
				if(name.length()<=15){
					//Msg create
					p.log(p.language.get("Log_NewDungeon"));
					p.log(p.language.get("Log_GenerateNewWorld"));

					//Create World
					EditWorld eworld=new EditWorld();
					eworld.generate();
					eworld.dungeonname=name;

					//MSG Done
					p.log(p.language.get("Log_WorldGenerationFinished"));

					//Tp Player
					if(eworld.lobby==null){
						new DPlayer(player,eworld.world,eworld.world.getSpawnLocation(), true);
					}else{
						new DPlayer(player,eworld.world,eworld.lobby, true);
					}
				}else{
					p.msg(player, p.language.get("Error_NameToLong"));
				}
			}else{
				p.msg(player, p.language.get("Error_LeaveDungeon"));
			}
		}
	}


}
