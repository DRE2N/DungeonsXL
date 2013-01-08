package com.dre.dungeonsxl.commands;

import java.io.File;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.dre.dungeonsxl.ConfigReader;
import com.dre.dungeonsxl.EditWorld;

public class CMDMsg extends DCommand{
	
	public CMDMsg(){
		this.args=-1;
		this.command="msg";
		this.help="/dxl msg <id> '[msg]' - Display the msg or change the msg";
		this.permissions="dxl.msg";
	}
	
	@Override
	public void onExecute(String[] args, Player player) {
		EditWorld eworld=EditWorld.get(player.getWorld());
		
		if(eworld!=null){
			if(args.length>1){
				try{
					int id=Integer.parseInt(args[1]);
					
					ConfigReader confreader=new ConfigReader(new File(p.getDataFolder()+"/dungeons/"+eworld.dungeonname, "config.yml"));
					
					if(args.length==2){
						String msg=confreader.msgs.get(id);
						if(msg!=null){
							p.msg(player, ChatColor.GOLD+"Msg("+id+"):"+ChatColor.WHITE+msg);
						}else{
							p.msg(player, ChatColor.RED+"Nachricht mit der Id "+ChatColor.GOLD+id+ChatColor.RED+" existiert nicht!");
						}
						
					}else{
						String msg="";
						int i=0;
						for(String arg:args){
							i++;
							if(i>2){
								msg=msg+" "+arg;
							}
						}
						
						String[] splitMsg=msg.split("'");
						if(splitMsg.length>1){
							msg=splitMsg[1];
							String old=confreader.msgs.get(id);
							if(old==null){
								p.msg(player, ChatColor.GREEN+"Neue Nachricht ("+ChatColor.GOLD+id+ChatColor.GREEN+") hinzugefügt!");
							}else{
								p.msg(player, ChatColor.GREEN+"Nachricht ("+ChatColor.GOLD+id+ChatColor.GREEN+") aktualisiert!");
							}
							
							confreader.msgs.put(id, msg);
							confreader.save();
						}else{
							p.msg(player, ChatColor.RED+"Du musst die Nachricht zwischen ' einfügen!");
						}
					}
				}catch(NumberFormatException e){
					p.msg(player, ChatColor.RED+"Parameter <id> muss eine Zahl beinhalten!");
				}
				
			}else{
				this.displayhelp(player);
			}
		}else{
			p.msg(player, ChatColor.RED+"Du musst einen Dungeon bearbeiten um diesen Befehl zu benutzen!");
		}
		
	}
	

}
