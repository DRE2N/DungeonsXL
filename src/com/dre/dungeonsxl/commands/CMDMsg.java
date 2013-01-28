package com.dre.dungeonsxl.commands;

import java.io.File;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dre.dungeonsxl.DConfig;
import com.dre.dungeonsxl.EditWorld;

public class CMDMsg extends DCommand{

	public CMDMsg(){
		this.args=-1;
		this.command="msg";
		this.help=p.language.get("Help_Cmd_Msg");
		this.permissions="dxl.msg";
		this.isPlayerCommand = true;
	}

	@Override
	public void onExecute(String[] args, CommandSender sender) {
		Player player = (Player) sender;
		EditWorld eworld=EditWorld.get(player.getWorld());

		if(eworld!=null){
			if(args.length>1){
				try{
					int id=p.parseInt(args[1]);

					DConfig confreader = new DConfig(new File(p.getDataFolder()+"/dungeons/"+eworld.dungeonname, "config.yml"));

					if(args.length==2){
						String msg=confreader.getMsg(id, true);
						if(msg!=null){
							p.msg(player, ChatColor.WHITE+msg);
						}else{
							p.msg(player, p.language.get("Error_MsgIdNotExist",""+id));
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

						String[] splitMsg=msg.split("\"");
						if(splitMsg.length>1){
							msg=splitMsg[1];
							String old=confreader.getMsg(id, false);
							if(old==null){
								p.msg(player, p.language.get("Cmd_Msg_Added",""+id));
							}else{
								p.msg(player, p.language.get("Cmd_Msg_Updated",""+id));
							}

							confreader.setMsg(msg, id);
							confreader.save();
						}else{
							p.msg(player, p.language.get("Error_MsgFormat"));
						}
					}
				}catch(NumberFormatException e){
					p.msg(player, p.language.get("Error_MsgNoInt"));
				}

			}else{
				this.displayHelp(player);
			}
		}else{
			p.msg(player, p.language.get("Error_NotInDungeon"));
		}

	}


}
