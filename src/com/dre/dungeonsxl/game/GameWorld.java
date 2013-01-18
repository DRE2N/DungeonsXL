package com.dre.dungeonsxl.game;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Spider;

import com.dre.dungeonsxl.DClass;
import com.dre.dungeonsxl.DConfig;
import com.dre.dungeonsxl.DGSign;
import com.dre.dungeonsxl.DPlayer;
import com.dre.dungeonsxl.P;

public class GameWorld {
	private static P p=P.p;
	public static CopyOnWriteArrayList<GameWorld> gworlds=new CopyOnWriteArrayList<GameWorld>();

	//Variables placeable
	public boolean isTutorial;

	public CopyOnWriteArrayList<GamePlaceableBlock> placeableBlocks=new CopyOnWriteArrayList<GamePlaceableBlock>();
	public World world;
	public String dungeonname;
	public Location locLobby;
	public Location locStart;
	public CopyOnWriteArrayList<Block> blocksEnd=new CopyOnWriteArrayList<Block>();
	public CopyOnWriteArrayList<Block> blocksReady=new CopyOnWriteArrayList<Block>();
	public CopyOnWriteArrayList<Block> blocksLeave=new CopyOnWriteArrayList<Block>();
	public boolean isPlaying=false;
	public int id;
	public CopyOnWriteArrayList<Material> secureObjects = new CopyOnWriteArrayList<Material>();
	public CopyOnWriteArrayList<Chunk> loadedChunks = new CopyOnWriteArrayList<Chunk>();
	public CopyOnWriteArrayList<GameMessage> messages = new CopyOnWriteArrayList<GameMessage>();
	
	public CopyOnWriteArrayList<Sign> signClass=new CopyOnWriteArrayList<Sign>();
	public CopyOnWriteArrayList<DMob> dmobs = new CopyOnWriteArrayList<DMob>();
	public CopyOnWriteArrayList<GameChest> gchests = new CopyOnWriteArrayList<GameChest>();
	public DConfig config;

	public GameWorld(){
		gworlds.add(this);

		//ID
		this.id=-1;
		int i=-1;
		while(this.id==-1){
			i++;
			boolean exist=false;
			for(GameWorld gworld:gworlds){
				if(gworld.id==i){
					exist=true;
					break;
				}
			}
			if(!exist) this.id=i;
		}
	}

	public void checkSign(Block block){
		if((block.getState() instanceof Sign)){
			Sign sign = (Sign) block.getState();
			String[] lines=sign.getLines();

			if(!isPlaying){
				if (lines[1].equalsIgnoreCase("lobby")) {
					this.locLobby=block.getLocation();
					block.setTypeId(0);
				} else if (lines[1].equalsIgnoreCase("ready")) {
					this.blocksReady.add(block);
					sign.setLine(0, ChatColor.BLUE+"############");
					sign.setLine(1, ChatColor.DARK_GREEN+"Ready");
					sign.setLine(2, "");
					sign.setLine(3, ChatColor.BLUE+"############");
					sign.update();
				} else if (lines[1].equalsIgnoreCase("leave")){
					this.blocksLeave.add(block);
					sign.setLine(0, ChatColor.BLUE+"############");
					sign.setLine(1, ChatColor.DARK_GREEN+"Leave");
					sign.setLine(2, "");
					sign.setLine(3, ChatColor.BLUE+"############");
					sign.update();
				} else if (lines[1].equalsIgnoreCase("start")){
					this.locStart=block.getLocation();
					block.setTypeId(0);
				} else if (lines[1].equalsIgnoreCase("end")){
					this.blocksEnd.add(block);
					sign.setLine(0, ChatColor.DARK_BLUE+"############");
					sign.setLine(1, ChatColor.DARK_GREEN+"End");
					sign.setLine(2, "");
					sign.setLine(3, ChatColor.DARK_BLUE+"############");
					sign.update();
				} else if (lines[1].equalsIgnoreCase("classes")){
					if(!config.isLobbyDisabled){
						int[] direction=DGSign.getDirection(block.getData());
						int directionX=direction[0];
						int directionZ=direction[1];

						int xx=0,zz=0;
						for(DClass dclass:this.config.getClasses()){

							//Check existing signs
							boolean isContinued=true;
							for(Sign isusedsign:this.signClass){
								if(dclass.name.equalsIgnoreCase(ChatColor.stripColor(isusedsign.getLine(1)))){
									isContinued=false;
								}
							}

							if(isContinued){
								Block classBlock=block.getRelative(xx,0,zz);

								if(classBlock.getData()==sign.getData().getData()&&classBlock.getTypeId()==68&&(classBlock.getState() instanceof Sign)){
									Sign classSign = (Sign) classBlock.getState();

									classSign.setLine(0, ChatColor.DARK_BLUE+"############");
									classSign.setLine(1, ChatColor.DARK_GREEN+dclass.name);
									classSign.setLine(2, "");
									classSign.setLine(3, ChatColor.DARK_BLUE+"############");
									classSign.update();
									this.signClass.add(classSign);
								}else{
									break;
								}
								xx=xx+directionX;
								zz=zz+directionZ;
							}
						}
					}
					else{
						block.setTypeId(0);
					}

				} else if (lines[1].equalsIgnoreCase("chunkupdater")){
					Chunk chunk = this.world.getChunkAt(block);
					if(!lines[2].equals("")){
						Integer radius = p.parseInt(lines[2]);
						for(int x = -radius; x<radius; x++){
							for(int z = -radius; z<radius; z++){
								Chunk chunk1 = this.world.getChunkAt(chunk.getX()-x,chunk.getZ()-z);
								chunk1.load();
								this.loadedChunks.add(chunk1);
							}
						}
					} else {
						chunk.load();
						this.loadedChunks.add(chunk);
					}
					block.setTypeId(0);
				}

			}else{
				if(lines[1].equalsIgnoreCase("mob")){
					if(lines[2]!=""&&lines[3]!=""){
						String mob=lines[2];
						if(mob!=null){
							String[] atributes=lines[3].split(",");
							if(atributes.length==3){
								new MobSpawner(block, mob, p.parseInt(atributes[0]), p.parseInt(atributes[1]), p.parseInt(atributes[2]),0);
							}
							if(atributes.length==4){
								new MobSpawner(block, mob, p.parseInt(atributes[0]), p.parseInt(atributes[1]), p.parseInt(atributes[2]),p.parseInt(atributes[3]));
							}
						}
					}
					block.setTypeId(0);
				}
				if(lines[1].equalsIgnoreCase("place")){
					placeableBlocks.add(new GamePlaceableBlock(block, lines[2], lines[3]) );
					block.setTypeId(0);
				}
				if(lines[1].equalsIgnoreCase("msg")){
					if(lines[2]!=""&&lines[3]!=""){
						String msg = config.getMsg(p.parseInt(lines[2]));
						if(msg!=null){
							messages.add(new GameMessage(block.getLocation(),msg,p.parseInt(lines[3]),false));
							block.setTypeId(0);
						}
					}
				}
				if(lines[1].equalsIgnoreCase("soundmsg")){
					if(lines[2]!=""&&lines[3]!=""){
						String msg = config.getMsg(p.parseInt(lines[2]));
						if(msg!=null){
							messages.add(new GameMessage(block.getLocation(),msg,p.parseInt(lines[3]),true));
							block.setTypeId(0);
						}
					}
				}
				if(lines[1].equalsIgnoreCase("checkpoint")){
					int radius=0;


					if(lines[2]!=null ){
						if(lines[2].length()>0){
							radius=p.parseInt(lines[2]);
						}
					}

					new GameCheckpoint(this,block.getLocation(),radius);
					block.setTypeId(0);
				}
				if(lines[1].equalsIgnoreCase("chest")){
					if(sign.getTypeId()==63){
						for(int x=-1;x<=1;x++){
							if(sign.getBlock().getRelative(x, 0, 0).getTypeId()==54){
								new GameChest(sign.getBlock().getRelative(x, 0, 0),this);
							}
						}
						for(int z=-1;z<=1;z++){
							if(sign.getBlock().getRelative(0, 0, z).getTypeId()==54){
								if(sign.getBlock().getRelative(0, 0, z)!=null){
									new GameChest(sign.getBlock().getRelative(0, 0, z),this);
								}
							}
						}
					}
					block.setTypeId(0);
				}
			}
		}
	}

	public void startGame() {
		this.isPlaying=true;
		ObjectInputStream os;
		try {
			os = new ObjectInputStream(new FileInputStream(new File("DXL_Game_"+this.id+"/DXLData.data")));

			int length=os.readInt();
			for(int i=0; i<length; i++){
				int x=os.readInt();
				int y=os.readInt();
				int z=os.readInt();
				Block block=this.world.getBlockAt(x, y, z);
				this.checkSign(block);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void msg(String msg) {
		for(DPlayer dplayer:DPlayer.get(this.world)){
			p.msg(dplayer.player, msg);
		}
	}

	//Static
	public static GameWorld get(World world){
		for(GameWorld gworld:gworlds){
			if(gworld.world.equals(world)){
				return gworld;
			}
		}

		return null;
	}

	public static void deleteAll(){
		for(GameWorld gworld:gworlds){
			gworlds.remove(gworld);

			p.getServer().unloadWorld(gworld.world,true);
			File dir = new File("DXL_Game_"+gworld.id);
			p.removeDirectory(dir);
		}
	}

	public static boolean canPlayDungeon(String dungeon, Player player){

		if(p.permission.has(player, "dungeonsxl.ignoretimelimit")||player.isOp()){
			return true;
		}

		File dungeonFolder=new File(p.getDataFolder()+"/dungeons/"+dungeon);
		if(dungeonFolder.isDirectory()){
			DConfig config=new DConfig(new File(p.getDataFolder()+"/dungeons/"+dungeon, "config.yml"));

			if(config.timeToNextPlay!=0){
				//read PlayerConfig
				File file=new File(p.getDataFolder()+"/dungeons/"+dungeon, "players.yml");

				if(!file.exists()){
					try {
						file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(file);

				if(playerConfig.contains(player.getName())){
					Long time=playerConfig.getLong(player.getName());
					if(time+(config.timeToNextPlay*1000*60*60)>System.currentTimeMillis()){
						return false;
					}
				}
			}
		}else{
			return false;
		}

		return true;
	}

	public void delete(){
		//for(GameWorld gworld:gworlds){
			gworlds.remove(this);

			p.getServer().unloadWorld(this.world,true);
			File dir = new File("DXL_Game_"+this.id);
			p.removeDirectory(dir);
		//}
	}

	public static GameWorld load(String name){

		File file=new File(p.getDataFolder(),"/dungeons/"+name);

		if(file.exists()){
			GameWorld gworld = new GameWorld();
			gworld.dungeonname=name;


			//Config einlesen
			gworld.config = new DConfig(new File(p.getDataFolder()+"/dungeons/"+gworld.dungeonname, "config.yml"));

			//Secure Objects
			gworld.secureObjects=gworld.config.secureObjects;

			//World
			p.copyDirectory(file,new File("DXL_Game_"+gworld.id));

			gworld.world=p.getServer().createWorld(WorldCreator.name("DXL_Game_"+gworld.id));

			ObjectInputStream os;
			try {
				os = new ObjectInputStream(new FileInputStream(new File("DXL_Game_"+gworld.id+"/DXLData.data")));

				int length=os.readInt();
				for(int i=0; i<length; i++){
					int x=os.readInt();
					int y=os.readInt();
					int z=os.readInt();
					Block block=gworld.world.getBlockAt(x, y, z);
					gworld.checkSign(block);

				}

				os.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return gworld;
		}

		return null;
	}

	public static void update(){
		for(GameWorld gworld:gworlds){
			//Update Messages
			GameMessage.update(gworld);
			
			//Update Spiders
			for(LivingEntity mob:gworld.world.getLivingEntities()){
				if(mob.getType()==EntityType.SPIDER){
					Spider spider=(Spider) mob;
					if(spider.getTarget()!=null){
						if(spider.getTarget().getType()==EntityType.PLAYER){
							continue;
						}
					}
					for(Entity player:spider.getNearbyEntities(10,10,10)){
						if(player.getType()==EntityType.PLAYER){
							spider.setTarget((LivingEntity) player);
						}
					}
				}
			}
		}
	}


}
