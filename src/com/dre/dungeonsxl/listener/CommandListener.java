package com.dre.dungeonsxl.listener;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.dre.dungeonsxl.P;
import com.dre.dungeonsxl.commands.DCommand;
import com.dre.dungeonsxl.commands.DCommandRoot;

public class CommandListener implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd_notused, String arg, String[] args) {
		if(args.length > 0){
            String cmd = args[0];

            for(DCommand command:DCommandRoot.root.commands){
            	if(cmd.equals(command.command)){
            		if(sender instanceof ConsoleCommandSender){
            			if(!command.isConsoleCommand){
            				P.p.msg(sender, P.p.language.get("Log_Error_NoConsoleCommand", command.command));
            				return false;
            			}
            		}
            		
            		if(sender instanceof Player){
            			Player player = (Player) sender;
            			if(!command.isPlayerCommand){
            				P.p.msg(player, P.p.language.get("Error_NoPlayerCommand", command.command));
            				return false;
            			} else {
            				if(!command.playerHasPermissions(player)){
                        		P.p.msg(player, P.p.language.get("Error_NoPermissions"));
            					return false;
            				} 
            			}
            		}
            		
            		if(command.args == args.length-1 || command.args == -1){
            			command.onExecute(args,sender);
            			return true;
            		} else {
            			command.displayHelp(sender);
            		}
            	}
            }

    		P.p.msg(sender, P.p.language.get("Error_CmdNotExist1",cmd));
    		P.p.msg(sender, P.p.language.get("Error_CmdNotExist2"));
		}else{
			DCommandRoot.root.cmdHelp.onExecute(args,sender);
		}
		
		return false;
	}



}
