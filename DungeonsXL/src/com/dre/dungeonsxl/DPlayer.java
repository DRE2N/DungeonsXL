package com.dre.dungeonsxl;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.dre.dungeonsxl.game.GameCheckpoint;
import com.dre.dungeonsxl.game.GameWorld;

public class DPlayer {
	public DungeonsXL p=DungeonsXL.p;
	
	public static CopyOnWriteArrayList<DPlayer> players=new CopyOnWriteArrayList<DPlayer>();
	
	//Variables
	public Player player;
	public World world;
	
	public DPlayer oldDPlayer=null;
	public int isinTestMode=0;
	
	public Location oldLocation;
	public ItemStack[] oldInventory;
	public ItemStack[] oldArmor;
	public int oldLvl;
	public int oldExp;
	public int oldHealth;
	public int oldFoodLevel;
	public GameMode oldGamemode;
	
	public boolean isEditing;
	public boolean isInWorldChat=false;
	public boolean isReady=false;
	public boolean isFinished=false;
	
	public DClass dclass;
	public GameCheckpoint checkpoint;
	public CopyOnWriteArrayList<Integer> classItems=new CopyOnWriteArrayList<Integer>();
	public Wolf wolf;
	public int wolfRespawnTime=30;
	public int offlineTime;
	public int invItemInHand;
	public CopyOnWriteArrayList<ItemStack> respawnInventory=new CopyOnWriteArrayList<ItemStack>();
	
	public Inventory treasureInv = DungeonsXL.p.getServer().createInventory(player,  45, "Belohnungen");
	
	public DPlayer(Player player, World world, Location teleport, boolean isEditing){
		players.add(this);
		this.player=player;
		this.world=world;
		
		this.oldLocation=player.getLocation();
		this.oldInventory=player.getInventory().getContents();
		this.oldArmor=player.getInventory().getArmorContents();
		this.oldExp=player.getTotalExperience();
		this.oldHealth=player.getHealth();
		this.oldFoodLevel=player.getFoodLevel();
		this.oldGamemode=player.getGameMode();
		this.oldLvl=player.getLevel();
		
		this.player.teleport(teleport);
		this.player.getInventory().clear();
		this.player.getInventory().setArmorContents(null);
		this.player.setTotalExperience(0);
		this.player.setLevel(0);
		this.player.setHealth(20);
		this.player.setFoodLevel(20);
		this.isEditing=isEditing;
		if(isEditing) this.player.setGameMode(GameMode.CREATIVE); else this.player.setGameMode(GameMode.SURVIVAL);
	
		if(!isEditing){
			if(GameWorld.get(world).confReader.isLobbyDisabled){
				this.ready();
			}
		}
	}
	
	public void goOffline(){
		offlineTime=1;
	}
	
	public void leave(){
		remove(this);
		
		if(this.oldDPlayer!=null){
			this.oldDPlayer.isinTestMode=0;
		}
		
		this.player.teleport(this.oldLocation);
		this.player.getInventory().setContents(this.oldInventory);
		this.player.getInventory().setArmorContents(this.oldArmor);
		this.player.setTotalExperience(this.oldExp);
		this.player.setLevel(this.oldLvl);
		this.player.setHealth(oldHealth);
		this.player.setFoodLevel(oldFoodLevel);
		this.player.setGameMode(oldGamemode);
		
		
		if(this.isEditing){
			EditWorld eworld=EditWorld.get(this.world);
			if(eworld!=null){
				eworld.save();
			}
		}else{
			GameWorld gworld=GameWorld.get(this.world);
			DGroup dgroup=DGroup.get(this.player);
			if(dgroup!=null){
				dgroup.removePlayer(this.player);
				if(dgroup.isEmpty()){
					dgroup.remove();
				}
			}
			
			//Belohnung
			if(this.oldDPlayer==null&&this.isinTestMode!=2){//Nur wenn man nicht am Testen ist
				if(isFinished){ 
					this.addTreasure();
					
					//Set Time
					File file=new File(p.getDataFolder()+"/dungeons/"+gworld.dungeonname, "players.yml");
					
					if(!file.exists()){
						try {
							file.createNewFile();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(file);
					
					playerConfig.set(player.getName(), System.currentTimeMillis());
					
					try {
						playerConfig.save(file);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					//Tutorial Permissions
					if(gworld.isTutorial){
						p.permission.playerAddGroup(this.player, p.tutorialEndGroup);
						p.permission.playerRemoveGroup(this.player, p.tutorialStartGroup);
					}
				}
			}
			
			//Give Secure Objects other Players
			if(dgroup!=null){
				if(!dgroup.isEmpty()){
					int i=0;
					Player groupplayer;
					do{
						groupplayer=dgroup.players.get(i);
						if(groupplayer!=null){
							for(ItemStack istack:this.player.getInventory()){
								if(istack!=null){
									if(gworld.secureobjects.contains(istack.getType())){
										groupplayer.getInventory().addItem(istack);
									}
								}
							}
							DungeonsXL.p.updateInventory(groupplayer);
						}
						i++;
					}while(groupplayer==null);
						
				}
			}
		}
		
		
		
		
		
		
		DungeonsXL.p.updateInventory(this.player);
	}
	
	public void ready(){
		this.isReady=true;
		
		DGroup dgroup=DGroup.get(this.player);
		if(!dgroup.isPlaying){
			if(dgroup!=null){
				for(Player player:dgroup.players){
					DPlayer dplayer=get(player);
					if(!dplayer.isReady){
						return;
					}
				}
			}
			
		
			dgroup.startGame();
		}else{
			this.respawn();
		}
	}
	
	public void respawn(){
		DGroup dgroup=DGroup.get(this.player);
		if(this.checkpoint==null){
			this.player.teleport(dgroup.gworld.locStart);
		}else{
			this.player.teleport(this.checkpoint.location);
		}
		if(this.wolf!=null){
			this.wolf.teleport(this.player);
		}
		
		//Respawn Items
		for(ItemStack istack:this.respawnInventory){
			if(istack!=null){
				this.player.getInventory().addItem(istack);
			}
		}
		this.respawnInventory.clear();
		DungeonsXL.p.updateInventory(this.player);
	}
	
	public void finish(){
		DungeonsXL.p.msg(this.player, ChatColor.YELLOW+"Du hast den Dungeon erfolgreich beendet!");
		this.isFinished=true;
		
		DGroup dgroup=DGroup.get(this.player);
		if(dgroup!=null){
			if(dgroup.isPlaying){
				for(Player player:dgroup.players){
					DPlayer dplayer=get(player);
					if(!dplayer.isFinished){
						DungeonsXL.p.msg(this.player, ChatColor.YELLOW+"Noch auf Mitspieler warten...");
						return;
					}
				}
				
				for(Player player:dgroup.players){
					DPlayer dplayer=get(player);
					dplayer.leave();
				}
			}
		}
	}
	
	public void msg(String msg){
		if(this.isEditing){
			EditWorld eworld=EditWorld.get(this.world);
			eworld.msg(msg);
			for(Player player:p.chatSpyer){
				if(!eworld.world.getPlayers().contains(player)){
					p.msg(player, ChatColor.GREEN+"[Chatspy] "+ChatColor.WHITE+msg);
				}
			}
			
		}else{
			GameWorld gworld=GameWorld.get(this.world);
			gworld.msg(msg);
			for(Player player:p.chatSpyer){
				if(!gworld.world.getPlayers().contains(player)){
					p.msg(player, ChatColor.GREEN+"[Chatspy] "+ChatColor.WHITE+msg);
				}
			}
		}
	}
	
	public void setClass(String classname) {
		GameWorld gworld=GameWorld.get(this.player.getWorld());
		if(gworld==null) return;
		
		DClass dclass=gworld.confReader.getClass(classname);
		if(dclass!=null){
			if(this.dclass!=dclass){
				this.dclass=dclass;
				
				//Set Dog
				if(this.wolf!=null){
					this.wolf.remove();
					this.wolf=null;
				}
				
				if(dclass.hasDog){
					this.wolf=(Wolf) this.world.spawnEntity(this.player.getLocation(), EntityType.WOLF);
					this.wolf.setTamed(true);
					this.wolf.setOwner(this.player);
					this.wolf.setHealth(this.wolf.getMaxHealth());
				}
				
				//Delete Inventory
				this.classItems.clear();
				this.player.getInventory().clear();
				this.player.getInventory().setArmorContents(null);
				player.getInventory().setItemInHand(new ItemStack(0));
				DungeonsXL.p.updateInventory(this.player);
				
				//Set Inventory
				for(ItemStack istack:dclass.items){
					
					//Leggings
					if(istack.getTypeId()==300||
							istack.getTypeId()==304||
							istack.getTypeId()==308||
							istack.getTypeId()==312||
							istack.getTypeId()==316)
					{
						this.player.getInventory().setLeggings(istack);
					}
					//Helmet
					else if(istack.getTypeId()==298||
							istack.getTypeId()==302||
							istack.getTypeId()==306||
							istack.getTypeId()==310||
							istack.getTypeId()==314)
					{
						this.player.getInventory().setHelmet(istack);
					}
					//Chestplate
					else if(istack.getTypeId()==299||
							istack.getTypeId()==303||
							istack.getTypeId()==307||
							istack.getTypeId()==311||
							istack.getTypeId()==315)
					{
						this.player.getInventory().setChestplate(istack);
					}
					//Boots
					else if(istack.getTypeId()==301||
							istack.getTypeId()==305||
							istack.getTypeId()==309||
							istack.getTypeId()==313||
							istack.getTypeId()==317)
					{
						this.player.getInventory().setBoots(istack);
					}
					
					else{
						this.player.getInventory().addItem(istack);
					}
					
					
					
					DungeonsXL.p.updateInventory(this.player);
				}
				
				for(int i=0;i<36;i++){
					ItemStack istack=this.player.getInventory().getItem(i);
					if(istack!=null){
						this.classItems.add(i);
					}
				}
				

				DungeonsXL.p.updateInventory(this.player);
				
			}
		}
	}
	
	public void setCheckpoint(GameCheckpoint checkpoint){
		this.checkpoint=checkpoint;
	}
	
	public void addTreasure(){
		new DLootInventory(this.player,this.treasureInv.getContents());
	}
	
	//Static
	public static void remove(DPlayer player){
		players.remove(player);
	}
	
	public static DPlayer get(Player player){
		EditWorld eworld=EditWorld.get(player.getWorld());
		boolean isEditing=false;
		if(eworld!=null){
			isEditing=true;
		}
		for(DPlayer dplayer:players){
			if(dplayer.player.equals(player)){
				if(dplayer.isEditing==isEditing){
					return dplayer;
				}
			}
		}
		return null;
	}
	
	public static CopyOnWriteArrayList<DPlayer> get(World world){
		CopyOnWriteArrayList<DPlayer> dplayers=new CopyOnWriteArrayList<DPlayer>();
		
		for(DPlayer dplayer:players){
			if(dplayer.world==world){
				dplayers.add(dplayer);
			}
		}
		
		return dplayers;
	}

	public static void update(boolean updateSecond){
		for(DPlayer dplayer:players){
			if(!updateSecond){
				//Check in World
				if(dplayer.isinTestMode!=1){
					if(!dplayer.player.getWorld().equals(dplayer.world)){
						if(dplayer.isEditing){
							EditWorld eworld=EditWorld.get(dplayer.world);
							if(eworld!=null){
								if(eworld.lobby==null){
									dplayer.player.teleport(eworld.world.getSpawnLocation());
								}else{
									dplayer.player.teleport(eworld.lobby);
								}
							}
						}else{
							GameWorld gworld=GameWorld.get(dplayer.world);
							if(gworld!=null){
								if(gworld!=null){
									
									DGroup dgroup=DGroup.get(dplayer.player);
									if(dplayer.checkpoint==null){
										dplayer.player.teleport(dgroup.gworld.locStart);
										if(dplayer.wolf!=null){
											dplayer.wolf.teleport(dgroup.gworld.locStart);
										}
									}else{
										dplayer.player.teleport(dplayer.checkpoint.location);
										if(dplayer.wolf!=null){
											dplayer.wolf.teleport(dplayer.checkpoint.location);
										}
									}
									
									
									//Respawn Items
									for(ItemStack istack:dplayer.respawnInventory){
										if(istack!=null){
											dplayer.player.getInventory().addItem(istack);
										}
									}
									dplayer.respawnInventory.clear();
									DungeonsXL.p.updateInventory(dplayer.player);
								}
							}
						}
					}
				}
			}else{
				//Update Wolf
				if(dplayer.wolf!=null){
					if(dplayer.wolf.isDead()){
						if(dplayer.wolfRespawnTime<=0){
							dplayer.wolf=(Wolf) dplayer.world.spawnEntity(dplayer.player.getLocation(), EntityType.WOLF);
							dplayer.wolf.setTamed(true);
							dplayer.wolf.setOwner(dplayer.player);
							dplayer.wolfRespawnTime=30;
						}
						dplayer.wolfRespawnTime--;
					}
				}
				//Update Offline Players
				if(dplayer.offlineTime>0){
					dplayer.offlineTime++;
					if(dplayer.offlineTime>300){
						DOfflinePlayer offplayer=new DOfflinePlayer();
						offplayer.name=dplayer.player.getName();
						offplayer.oldLocation=dplayer.oldLocation;
						offplayer.oldInventory=dplayer.oldInventory;
						offplayer.oldArmor=dplayer.oldArmor;
						offplayer.oldExp=dplayer.oldExp;
						offplayer.oldHealth=dplayer.oldHealth;
						offplayer.oldFoodLevel=dplayer.oldFoodLevel;
						offplayer.oldGamemode=dplayer.oldGamemode;
						offplayer.oldLvl=dplayer.oldLvl;
						
						remove(dplayer);
						
						if(dplayer.isEditing){
							EditWorld eworld=EditWorld.get(dplayer.world);
							if(eworld!=null){
								eworld.save();
							}
						}else{
							GameWorld gworld=GameWorld.get(dplayer.world);
							DGroup dgroup=DGroup.get(dplayer.player);
							if(dgroup!=null){
								dgroup.removePlayer(dplayer.player);
								if(dgroup.isEmpty()){
									dgroup.remove();
									gworld.delete();
								}
							}
						}

					}
					
				}
			}
			
			//Update ClassItems
			for(Integer istackplace:dplayer.classItems){
				ItemStack istack=dplayer.player.getInventory().getItem(istackplace);
				
				if(istack!=null){
					if(istack.getTypeId()!=0){
						if(istack.getTypeId()>255 && istack.getTypeId()<318){
							istack.setDurability((short) 0);
						}
					}else{
						dplayer.classItems.remove(istackplace);
					}
				}else{
					dplayer.classItems.remove(istackplace);
				}
			}
		}
	}

}
