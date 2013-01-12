package com.dre.dungeonsxl.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CMDHelp extends DCommand{

	public CMDHelp(){
		this.command="help";
		this.args=-1;
		this.help=p.language.get("Help_Cmd_Help");
	}

	@Override
	public void onExecute(String[] args, Player player) {
		int page=1;
		int pages=(int)(DCommandRoot.root.commands.size()/6);

		if(args.length>1){
			try{
				page=Integer.parseInt(args[1]);
			}catch(NumberFormatException e){
				page=1;
			}
			if(page<1) page=1;
			if(page>pages) page=pages;
		}

		p.msg(player, ChatColor.GREEN+"============[ "+ChatColor.GOLD+"Help DungeonsXL - "+page+"/"+pages+ChatColor.GREEN+" ]============",false);

		int i=0;
		int ipage=1;
		for(DCommand command:DCommandRoot.root.commands){
			i++;
			if(i>6){
				i=0;
				ipage++;
			}
			if(ipage==page){
				p.msg(player, ChatColor.YELLOW+command.help,false);
			}
		}

		p.msg(player, ChatColor.GREEN+"==============[ "+ChatColor.GOLD+"By Frank Baumann"+ChatColor.GREEN+" ]==============",false);
	}

}
