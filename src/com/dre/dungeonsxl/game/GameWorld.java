package com.dre.dungeonsxl.game;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.CopyOnWriteArrayList;

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

import com.dre.dungeonsxl.DConfig;
import com.dre.dungeonsxl.DPlayer;
import com.dre.dungeonsxl.P;
import com.dre.dungeonsxl.signs.DSign;

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
	
	public CopyOnWriteArrayList<Sign> signClass=new CopyOnWriteArrayList<Sign>();
	public CopyOnWriteArrayList<DMob> dmobs = new CopyOnWriteArrayList<DMob>();
	public CopyOnWriteArrayList<GameChest> gchests = new CopyOnWriteArrayList<GameChest>();
	public CopyOnWriteArrayList<DSign> dSigns = new CopyOnWriteArrayList<DSign>();
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
			dSigns.add(DSign.create(sign, this));
		}
	}

	public void startGame() {
		this.isPlaying=true;

		for(DSign dSign : this.dSigns){
			if(dSign != null){
				if(!dSign.isOnDungeonInit()){
					dSign.onInit();
				}
			}
		}
		for(DSign dSign : this.dSigns){
			if(dSign != null){
				if(dSign.isRedstoneTrigger()){
					if(dSign.getRtBlock().isBlockPowered()){
						dSign.onUpdate(0,true);
					}else{
						dSign.onUpdate(0,false);
					}
				}
			}
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

			if(config.getTimeToNextPlay()!=0){
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
					if(time+(config.getTimeToNextPlay()*1000*60*60)>System.currentTimeMillis()){
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
		gworlds.remove(this);

		p.getServer().unloadWorld(this.world,true);
		File dir = new File("DXL_Game_"+this.id);
		p.removeDirectory(dir);
	}

	public static GameWorld load(String name){

		File file=new File(p.getDataFolder(),"/dungeons/"+name);

		if(file.exists()){
			GameWorld gworld = new GameWorld();
			gworld.dungeonname=name;


			//Config einlesen
			gworld.config = new DConfig(new File(p.getDataFolder()+"/dungeons/"+gworld.dungeonname, "config.yml"));

			//Secure Objects
			gworld.secureObjects=gworld.config.getSecureObjects();

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
