package com.dre.dungeonsxl.commands;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CMDList extends DCommand{

	public CMDList(){
		this.command="list";
		this.args=0;
		this.help=p.language.get("Help_Cmd_List");
	}


	@Override
	public void onExecute(String[] args, Player player) {

		File dungeonsfolder=new File(p.getDataFolder()+"/dungeons");
		if(dungeonsfolder.exists()){
			p.msg(player, ChatColor.DARK_GREEN+"-----[ "+ChatColor.GOLD+"Dungeons "+ChatColor.RED+dungeonsfolder.list().length+ChatColor.DARK_GREEN+"]-----");

			for(String dungeon:dungeonsfolder.list()){
				p.msg(player, dungeon);
			}
		}
	}

}
