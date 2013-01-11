package com.dre.dungeonsxl.listener;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dre.dungeonsxl.P;
import com.dre.dungeonsxl.commands.DCommand;
import com.dre.dungeonsxl.commands.DCommandRoot;

public class CommandListener implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd_notused, String arg, String[] args) {
		
		//Only Playercommands
		if(sender instanceof Player){
			Player player = (Player) sender;
			if(args.length > 0){
                String cmd = args[0];
                
                for(DCommand command:DCommandRoot.root.commands){
                	if(cmd.equals(command.command)){
                		if(command.playerHasPermissions(player)){
	                		if(command.args==args.length-1||command.args==-1){
	                			command.onExecute(args,player);
	                		}else{
	                			command.displayhelp(player);
	                		}
                		}
                		else{
                			P.p.msg(player, ChatColor.RED+"Du hast keine Permissions dazu!");
                		}
                		return true;
                	}
                }
                
        		P.p.msg(player, ChatColor.RED+"Befehl "+ChatColor.GOLD+cmd+ChatColor.RED+" existiert nicht!");
        		P.p.msg(player, ChatColor.RED+"Bitte gib "+ChatColor.GOLD+"/dxl help"+ChatColor.RED+" für Hilfe ein!");
			}else{
				DCommandRoot.root.cmdHelp.onExecute(args,player);
			}
		}
		return false;
	}
	
	

}
