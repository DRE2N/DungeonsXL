package com.dre.dungeonsxl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.milkbowl.vault.permission.Permission;
import net.minecraft.server.v1_4_6.EntityPlayer;
import net.minecraft.server.v1_4_6.MinecraftServer;
import net.minecraft.server.v1_4_6.PlayerInteractManager;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_4_6.CraftServer;
import org.bukkit.craftbukkit.v1_4_6.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.dre.dungeonsxl.commands.DCommandRoot;
import com.dre.dungeonsxl.game.GameCheckpoint;
import com.dre.dungeonsxl.game.GameWorld;
import com.dre.dungeonsxl.game.MobSpawner;
import com.dre.dungeonsxl.listener.BlockListener;
import com.dre.dungeonsxl.listener.CommandListener;
import com.dre.dungeonsxl.listener.EntityListener;
import com.dre.dungeonsxl.listener.PlayerListener;
import com.dre.dungeonsxl.listener.WorldListener;

public class P extends JavaPlugin{
	public static P p;

	//Listener
	private static Listener entitylistener;
	private static Listener playerlistener;
	private static Listener blocklistener;
	private static Listener worldlistener;

	//Main Config Reader
	public MainConfig mainConfig;

	//Language Reader
	public LanguageReader language;

	//Chatspyer
	public CopyOnWriteArrayList<Player> chatSpyer=new CopyOnWriteArrayList<Player>();
	
	//Spout
	public boolean isSpoutEnabled = false;
	
	@Override
	public void onEnable(){
		p = this;

		//Commands
		getCommand("dungeonsxl").setExecutor(new CommandListener());

		//Load Config
		mainConfig=new MainConfig(new File(p.getDataFolder(), "config.yml"));
		
		//Load Language
		language = new LanguageReader(new File(p.getDataFolder(), "languages/"+mainConfig.language+".yml"));

		//Init Classes
		new DCommandRoot();

		//InitFolders
		this.initFolders();

		//Setup Permissions
		this.setupPermissions();

		//Listener
		entitylistener = new EntityListener();
		playerlistener = new PlayerListener();
		blocklistener = new BlockListener();
		worldlistener = new WorldListener();

		Bukkit.getServer().getPluginManager().registerEvents(entitylistener,this);
		Bukkit.getServer().getPluginManager().registerEvents(playerlistener,this);
		Bukkit.getServer().getPluginManager().registerEvents(blocklistener,this);
		Bukkit.getServer().getPluginManager().registerEvents(worldlistener,this);

		//Load All
		this.loadAll();
		
		//Spout
		if(mainConfig.enableSpout){
			if(P.p.getServer().getPluginManager().getPlugin("Spout")!=null){
				isSpoutEnabled = true;
			} else {
				isSpoutEnabled = false;
				mainConfig.enableSpout = false;
				P.p.log(P.p.language.get("Log_Error_Spout"));
			}
		}
		
		
		//Scheduler
		this.initSchedulers();
		

		//MSG
		this.log(this.getDescription().getName()+" enabled!");
	}


	@Override
	public void onDisable(){
		//Save
		this.saveData();
		language.save();

		//MSG
		this.log(this.getDescription().getName()+" disabled!");

		//DPlayer leaves World
		for(DPlayer dplayer:DPlayer.players){
			dplayer.leave();
		}

		//Delete all Data
		DGroup.dgroups.clear();
		DGSign.dgsigns.clear();
		DLootInventory.LootInventorys.clear();
		DMobType.clear();
		DPlayer.players.clear();
		DPortal.portals.clear();
		LeaveSign.lsigns.clear();
		DCommandRoot.root.commands.clear();
		GameCheckpoint.gcheckpoints.clear();
		MobSpawner.mobspawners.clear();

		//Delete Worlds
		GameWorld.deleteAll();
		EditWorld.deleteAll();

		//Disable listeners
		HandlerList.unregisterAll(p);

		//Stop shedulers
		p.getServer().getScheduler().cancelTasks(this);
	}


	//Init.
	public void initFolders(){
		//Check Folder
		File folder = new File(this.getDataFolder()+"");
		if(!folder.exists()){
			folder.mkdir();
		}

		folder = new File(this.getDataFolder()+File.separator+"dungeons");
		if(!folder.exists()){
			folder.mkdir();
		}
	}
	
	public void initSchedulers(){
		p.getServer().getScheduler().scheduleSyncRepeatingTask(p, new Runnable() {
			public void run() {
				for(GameWorld gworld:GameWorld.gworlds){
					if(gworld.world.getPlayers().isEmpty()){
						if(DPlayer.get(gworld.world).isEmpty()){
							gworld.delete();
						}
					}
				}
				for(EditWorld eworld:EditWorld.eworlds){
					if(eworld.world.getPlayers().isEmpty()){
						eworld.delete();
					}
				}
		    }
		}, 0L, 1200L);

		p.getServer().getScheduler().scheduleSyncRepeatingTask(p, new Runnable() {
		    public void run() {
		        MobSpawner.updateAll();
		        GameWorld.update();
		        GameCheckpoint.update();
		        DPlayer.update(true);
		        
		        //Tutorial Mode
		        if(p.mainConfig.tutorialActivated){
			        for(Player player:p.getServer().getOnlinePlayers()){
			        	if(DPlayer.get(player)==null){
				    		if(p.mainConfig.tutorialDungeon!=null && p.mainConfig.tutorialStartGroup!=null && p.mainConfig.tutorialEndGroup!=null){
				    			for(String group:p.permission.getPlayerGroups(player)){
				    				if(p.mainConfig.tutorialStartGroup.equalsIgnoreCase(group)){
				    					DGroup dgroup=new DGroup(player, p.mainConfig.tutorialDungeon);
				    					if(dgroup.gworld==null){
				    						dgroup.gworld=GameWorld.load(DGroup.get(player).dungeonname);
				    						dgroup.gworld.isTutorial=true;
				    					}
				    					if(dgroup.gworld!=null){
				    						if(dgroup.gworld.locLobby==null){
				    							new DPlayer(player,dgroup.gworld.world,dgroup.gworld.world.getSpawnLocation(), false);
				    						}else{
				    							new DPlayer(player,dgroup.gworld.world,dgroup.gworld.locLobby, false);
				    						}
				    					}else{
				    						p.msg(player,p.language.get("Error_TutorialNotExist"));
				    					}
				    				}
				    			}
				    		}
			        	}
			        }
		        }
		    }
		}, 0L, 20L);

		p.getServer().getScheduler().scheduleSyncRepeatingTask(p, new Runnable() {
		    public void run() {
		        DPlayer.update(false);
		    }
		}, 0L, 2L);
	}

	//Permissions
	public Permission permission = null;

    private Boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }

    public Boolean GroupEnabled(String group){

    	for(String agroup:permission.getGroups()){
    		if(agroup.equalsIgnoreCase(group)){
    			return true;
    		}
    	}

    	return false;
    }


    //Save and Load
	public void saveData(){
		File file = new File(this.getDataFolder(), "data.yml");
		FileConfiguration configFile = new YamlConfiguration();

		DPortal.save(configFile);
		DGSign.save(configFile);
		LeaveSign.save(configFile);

		try {
			configFile.save(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public void loadAll(){
		
		//Load world data
		File file = new File(this.getDataFolder(), "data.yml");
		FileConfiguration configFile = YamlConfiguration.loadConfiguration(file);
		
		DPortal.load(configFile);
		DGSign.load(configFile);
		LeaveSign.load(configFile);
		
		//Load mob types
		DMobType.load(new File(p.getDataFolder(), "mobs.yml"));
		
		//Load saved players
		DSavePlayer.load();
	}


	//File Control
	public boolean removeDirectory(File directory) {
		if (directory.isDirectory()) {
			for (File f : directory.listFiles()) {
				if (!removeDirectory(f)) return false;
			}
		}
		return directory.delete();
	}

	public void copyFile(File in, File out) throws IOException {
        FileChannel inChannel=null;
        FileChannel outChannel=null;
        try {
        	inChannel = new FileInputStream(in).getChannel();
            outChannel = new FileOutputStream(out).getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (IOException e) {
            throw e;
        } finally {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }
    }

	public String[] excludedFiles={"config.yml"};

	public void copyDirectory(File sourceLocation, File targetLocation) {

        if (sourceLocation.isDirectory()) {

            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }

            String[] children = sourceLocation.list();
            for (int i = 0; i < children.length; i++) {
            	boolean isOk=true;
            	for (String excluded:excludedFiles){
            		if(children[i].contains(excluded)){
            			isOk=false;
            			break;
            		}
            	}
            	if(isOk){
            		copyDirectory(new File(sourceLocation, children[i]), new File(
                            targetLocation, children[i]));
            	}

            }
        } else {

            try {

                if (!targetLocation.getParentFile().exists()) {

                    createDirectory(targetLocation.getParentFile().getAbsolutePath());
                    targetLocation.createNewFile();

                } else if (!targetLocation.exists()) {

                    targetLocation.createNewFile();
                }

                InputStream in = new FileInputStream(sourceLocation);
                OutputStream out = new FileOutputStream(targetLocation);

                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();

            } catch (Exception e) {

                if (e.getMessage().contains("Zugriff")
                        || e.getMessage().contains("Access"))
                    P.p.log("Error: " + e.getMessage() + " // Access denied");
                else
                	P.p.log("Error: " + e.getMessage());
            }
        }
    }

    public void createDirectory(String s) {

        if (!new File(s).getParentFile().exists()) {

            createDirectory(new File(s).getParent());
        }

        new File(s).mkdir();
    }

	public void deletenotusingfiles(File directory){
		File[] files=directory.listFiles();
		for(File file:files){
			if(file.getName().equalsIgnoreCase("uid.dat")){
				file.delete();
			}
		}
	}

	//Msg
	public void msg(Player player,String msg){
		msg = replaceColors(msg);
		player.sendMessage(ChatColor.DARK_GREEN+"[DXL] "+ChatColor.WHITE+msg);
	}

	public void msg(Player player,String msg, boolean zusatz){
		msg = replaceColors(msg);
		if(zusatz){
			player.sendMessage(ChatColor.DARK_GREEN+"[DXL]"+ChatColor.WHITE+msg);
		}else{
			player.sendMessage(ChatColor.WHITE+msg);
		}
	}

	private String replaceColors(String msg){
		if (msg!=null) {
			msg = msg.replace("&0", ChatColor.getByChar("0").toString());
			msg = msg.replace("&1", ChatColor.getByChar("1").toString());
			msg = msg.replace("&2", ChatColor.getByChar("2").toString());
			msg = msg.replace("&3", ChatColor.getByChar("3").toString());
			msg = msg.replace("&4", ChatColor.getByChar("4").toString());
			msg = msg.replace("&5", ChatColor.getByChar("5").toString());
			msg = msg.replace("&6", ChatColor.getByChar("6").toString());
			msg = msg.replace("&7", ChatColor.getByChar("7").toString());
			msg = msg.replace("&8", ChatColor.getByChar("8").toString());
			msg = msg.replace("&9", ChatColor.getByChar("9").toString());
			msg = msg.replace("&a", ChatColor.getByChar("a").toString());
			msg = msg.replace("&b", ChatColor.getByChar("b").toString());
			msg = msg.replace("&c", ChatColor.getByChar("c").toString());
			msg = msg.replace("&d", ChatColor.getByChar("d").toString());
			msg = msg.replace("&e", ChatColor.getByChar("e").toString());
			msg = msg.replace("&f", ChatColor.getByChar("f").toString());
			msg = msg.replace("&k", ChatColor.getByChar("k").toString());
			msg = msg.replace("&l", ChatColor.getByChar("l").toString());
			msg = msg.replace("&m", ChatColor.getByChar("m").toString());
			msg = msg.replace("&n", ChatColor.getByChar("n").toString());
			msg = msg.replace("&o", ChatColor.getByChar("o").toString());
			msg = msg.replace("&r", ChatColor.getByChar("r").toString());
		}

		return msg;
	}

	//Misc.
	public EntityType getEntitiyType(String name){
		for(EntityType type:EntityType.values()){
			if(name.equalsIgnoreCase(type.getName())){
				return type;
			}
		}

		return null;
	}
	
	public int parseInt(String string){
		return NumberUtils.toInt(string, 0);
	}
	
	public Player getOfflinePlayer(String player) {
		Player pplayer = null;
		try {
			//See if the player has data files

			// Find the player folder
			File playerfolder = new File(Bukkit.getWorlds().get(0).getWorldFolder(), "players");

			// Find player name
			for (File playerfile : playerfolder.listFiles()) {
				String filename = playerfile.getName();
				String playername = filename.substring(0, filename.length() - 4);

				if(playername.trim().equalsIgnoreCase(player)) {
					//This player plays on the server!
					MinecraftServer server = ((CraftServer)Bukkit.getServer()).getServer();
					EntityPlayer entity = new EntityPlayer(server, server.getWorldServer(0), playername, new PlayerInteractManager(server.getWorldServer(0)));
					Player target = (entity == null) ? null : (Player) entity.getBukkitEntity();
					if(target != null) {
						target.loadData();
						return target;
					}
				}
			}
		}
		catch(Exception e) {
			return null;
		}
		return pplayer;
	}
	
	public Player getOfflinePlayer(String player, Location location){
		Player pplayer = null;
		try {
			//See if the player has data files
			
			// Find the player folder
			File playerfolder = new File(Bukkit.getWorlds().get(0).getWorldFolder(), "players");
			
			// Find player name
			for (File playerfile : playerfolder.listFiles()) {
				String filename = playerfile.getName();
				String playername = filename.substring(0, filename.length() - 4);

				if(playername.trim().equalsIgnoreCase(player)) {
					//This player plays on the server!
					MinecraftServer server = ((CraftServer)Bukkit.getServer()).getServer();
					EntityPlayer entity = new EntityPlayer(server, server.getWorldServer(0), playername, new PlayerInteractManager(server.getWorldServer(0)));
					entity.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
					entity.world = ((CraftWorld) location.getWorld()).getHandle();
					Player target = (entity == null) ? null : (Player) entity.getBukkitEntity();
					if(target != null) {
						//target.loadData();
						return target;
					}
				}
			}
		}
		catch(Exception e) {
			return null;
		}
		return pplayer;
	}
	
    // -------------------------------------------- //
 	// LOGGING
 	// -------------------------------------------- //
 	public void log(Object msg)
 	{
 		log(Level.INFO, msg);
 	}

 	public void log(Level level, Object msg)
 	{
 		Logger.getLogger("Minecraft").log(level, "["+this.getDescription().getFullName()+"] "+msg);
 	}

}
