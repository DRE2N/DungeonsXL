package com.dre.dungeonsxl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class EditWorld {
	private static DungeonsXL p=DungeonsXL.p;
	public static CopyOnWriteArrayList<EditWorld> eworlds=new CopyOnWriteArrayList<EditWorld>();

	//Variables
	public World world;
	public String owner;
	public String name;
	public String dungeonname;
	public int id;
	public Location lobby;
	public CopyOnWriteArrayList<String> invitedPlayers=new CopyOnWriteArrayList<String>();
	public CopyOnWriteArrayList<Block> sign=new CopyOnWriteArrayList<Block>();

	public EditWorld(){
		eworlds.add(this);

		//ID
		this.id=-1;
		int i=-1;
		while(this.id==-1){
			i++;
			boolean exist=false;
			for(EditWorld eworld:eworlds){
				if(eworld.id==i){
					exist=true;
					break;
				}
			}
			if(!exist) this.id=i;
		}

		name="DXL_Edit_"+this.id;
	}

	public void generate(){
		WorldCreator creator=WorldCreator.name(name);
		creator.type(WorldType.FLAT);
		creator.generateStructures(false);

		this.world=p.getServer().createWorld(creator);
	}

	public void save(){
		this.world.save();
		p.copyDirectory(new File("DXL_Edit_"+this.id),new File("plugins/DungeonsXL/dungeons/"+this.dungeonname));
		p.deletenotusingfiles(new File("plugins/DungeonsXL/dungeons/"+this.dungeonname));
		try {
			ObjectOutputStream out=new ObjectOutputStream(new FileOutputStream(new File("plugins/DungeonsXL/dungeons/"+this.dungeonname+"/DXLData.data")));
			out.writeInt(this.sign.size());
			for(Block sign:this.sign){
				out.writeInt(sign.getX());
				out.writeInt(sign.getY());
				out.writeInt(sign.getZ());
			}
			out.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//Check Configuration
		/*File file=new File("plugins/DungeonsXL/dungeons/"+this.dungeonname+"/config.yml");
		if(!file.exists()){
			File copyfile=new File("plugins/DungeonsXL/config.yml");
			if(copyfile.exists()){
				try {
					DungeonsXL.p.copyFile(copyfile,file);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else{
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}*/

	}

	public void checkSign(Block block){
		if((block.getState() instanceof Sign)){
			Sign sign = (Sign) block.getState();
			String[] lines=sign.getLines();

			if(lines[1].equalsIgnoreCase("lobby")){
				this.lobby=block.getLocation();
			}
		}
	}

	public void delete(){
			eworlds.remove(this);
			for(Player player:this.world.getPlayers()){
				DPlayer dplayer=DPlayer.get(player);
				dplayer.leave();
			}

			p.getServer().unloadWorld(this.world,true);
			File dir = new File("DXL_Edit_"+this.id);
			p.removeDirectory(dir);
	}

	//Static
	public static EditWorld get(World world){
		for(EditWorld eworld:eworlds){
			if(eworld.world.equals(world)){
				return eworld;
			}
		}

		return null;
	}

	public static EditWorld get(String name){
		for(EditWorld eworld:eworlds){
			if(eworld.name.equalsIgnoreCase(name)){
				return eworld;
			}
		}

		return null;
	}

	public static void deleteAll(){
		for(EditWorld eworld:eworlds){
			eworlds.remove(eworld);
			for(Player player:eworld.world.getPlayers()){
				DPlayer dplayer=DPlayer.get(player);
				dplayer.leave();
			}

			p.getServer().unloadWorld(eworld.world,true);
			File dir = new File("DXL_Edit_"+eworld.id);
			p.removeDirectory(dir);
		}
	}

	public static EditWorld load(String name){
		for(EditWorld eworld:eworlds){

			if(eworld.dungeonname.equalsIgnoreCase(name)){
				return eworld;
			}
		}

		File file=new File("plugins/DungeonsXL/dungeons/"+name);

		if(file.exists()){
			EditWorld eworld = new EditWorld();
			eworld.dungeonname=name;
			//World
			p.copyDirectory(file,new File("DXL_Edit_"+eworld.id));

			eworld.world=p.getServer().createWorld(WorldCreator.name("DXL_Edit_"+eworld.id));

			try {
				ObjectInputStream os=new ObjectInputStream(new FileInputStream(new File("plugins/DungeonsXL/dungeons/"+eworld.dungeonname+"/DXLData.data")));
				int length=os.readInt();
				for(int i=0; i<length; i++){
					int x=os.readInt();
					int y=os.readInt();
					int z=os.readInt();
					Block block=eworld.world.getBlockAt(x, y, z);
					eworld.checkSign(block);
					eworld.sign.add(block);
				}
				os.close();

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return eworld;
		}

		return null;
	}

	public static boolean exist(String name){
		//Cheack Loaded EditWorlds
		for(EditWorld eworld:eworlds){
			if(eworld.dungeonname.equalsIgnoreCase(name)){
				return true;
			}
		}

		//Cheack Unloaded Worlds
		File file=new File("plugins/DungeonsXL/dungeons/"+name);

		if(file.exists()){
			return true;
		}

		return false;
	}

	public void msg(String msg) {
		for(DPlayer dplayer:DPlayer.get(this.world)){
			p.msg(dplayer.player, msg);
		}
	}

	//Invite
	public static boolean addInvitedPlayer(String eworldname,String player){

		EditWorld eworld=EditWorld.get(eworldname);



		if(eworld!=null){
			eworld.invitedPlayers.add(player.toLowerCase());
		}else{
			if(exist(eworldname)){
				ConfigReader confreader=new ConfigReader(new File(p.getDataFolder()+"/dungeons/"+eworldname, "config.yml"));
				confreader.invitedPlayer.add(player.toLowerCase());
				confreader.save();
				return true;
			}
		}

		return false;

	}

	public static boolean removeInvitedPlayer(String eworldname,String player){

		EditWorld eworld=EditWorld.get(eworldname);

		if(eworld!=null){
			eworld.invitedPlayers.remove(player.toLowerCase());
		}else{
			if(exist(eworldname)){
				ConfigReader confreader=new ConfigReader(new File(p.getDataFolder()+"/dungeons/"+eworldname, "config.yml"));
				confreader.invitedPlayer.remove(player.toLowerCase());
				confreader.save();
				return true;
			}
		}

		return false;

	}

	public static boolean isInvitedPlayer(String eworldname,String player){

		EditWorld eworld=EditWorld.get(eworldname);

		if(eworld!=null){
			return eworld.invitedPlayers.contains(player.toLowerCase());
		}else{
			if(exist(eworldname)){
				ConfigReader confreader=new ConfigReader(new File(p.getDataFolder()+"/dungeons/"+eworldname, "config.yml"));
				return confreader.invitedPlayer.contains(player.toLowerCase());
			}
		}

		return false;

	}
}
