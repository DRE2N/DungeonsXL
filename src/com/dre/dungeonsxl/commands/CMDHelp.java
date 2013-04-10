package com.dre.dungeonsxl.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class CMDHelp extends DCommand{

	public CMDHelp(){
		this.command="help";
		this.args=-1;
		this.help=p.language.get("Help_Cmd_Help");
		this.isPlayerCommand = true;
		this.isConsoleCommand = true;
	}

	@Override
	public void onExecute(String[] args, CommandSender sender) {
		boolean isConsole = false, isPlayer = false;
		
		if(sender instanceof ConsoleCommandSender){
			isConsole = true;
		}else if(sender instanceof Player){
			isPlayer = true;
		}
		
		int page=1;
		int pages=(int)Math.ceil(DCommandRoot.root.commands.size()/6.0);

		if(args.length>1){
			try{
				page=p.parseInt(args[1]);
			}catch(NumberFormatException e){
				page=1;
			}
			if(page<1) page=1;
			if(page>pages) page=pages;
		}

		p.msg(sender, ChatColor.GREEN+"============[ "+ChatColor.GOLD+"Help DungeonsXL - "+page+"/"+pages+ChatColor.GREEN+" ]============",false);

		int i=-1;
		int ipage=1;
		for(DCommand command:DCommandRoot.root.commands){
			if((command.isConsoleCommand && isConsole) || (command.isPlayerCommand && isPlayer)){
				i++;
				if(i>5){
					i=0;
					ipage++;
				}
				if(ipage==page){
					p.msg(sender, ChatColor.YELLOW+command.help,false);
				}
			}
		}

		p.msg(sender, ChatColor.GREEN+"==============[ "+ChatColor.GOLD+"By Frank Baumann"+ChatColor.GREEN+" ]==============",false);
	}

}
