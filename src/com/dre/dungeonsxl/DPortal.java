package com.dre.dungeonsxl;

import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.Spout;

import com.dre.dungeonsxl.game.GameWorld;

public class DPortal {
	public static P p=P.p;

	public static CopyOnWriteArrayList<DPortal> portals = new CopyOnWriteArrayList<DPortal>();

	//Variables
	public World world;
	public Block block1,block2;
	public Player player;
	public boolean isActive;
	public String type;


	public DPortal(boolean active){
		portals.add(this);
		this.isActive=active;
	}

	public boolean fillwithportal(){
		if(this.block1!=null && this.block2!=null){
			int x1=block1.getX(),y1=block1.getY(),z1=block1.getZ();
			int x2=block2.getX(),y2=block2.getY(),z2=block2.getZ();
			int xcount=0,ycount=0,zcount=0;

			if(x1>x2){xcount=-1;}else if(x1<x2){xcount=1;}
			if(y1>y2){ycount=-1;}else if(y1<y2){ycount=1;}
			if(z1>z2){zcount=-1;}else if(z1<z2){zcount=1;}

			int xx=x1;
			do{
				int yy=y1;

				do{
					int zz=z1;

					do{
						int typeid=this.world.getBlockAt(xx, yy, zz).getType().getId();

						if(
								typeid==0||
								typeid==8||
								typeid==9||
								typeid==10||
								typeid==11||
								typeid==6||
								typeid==30||
								typeid==31||
								typeid==32||
								typeid==34||
								typeid==37||
								typeid==38||
								typeid==39||
								typeid==40||
								typeid==50||
								typeid==51||
								typeid==59||
								typeid==55||
								typeid==75||
								typeid==78||
								typeid==76
								)
						{
							this.world.getBlockAt(xx, yy, zz).setTypeId(90);
						}

						zz=zz+zcount;
					}while(zz!=z2+zcount);

					yy=yy+ycount;
				}while(yy!=y2+ycount);

				xx=xx+xcount;
			}while(xx!=x2+xcount);

		}

		return false;
	}

	public void teleport(Player player){
		if(this.type.equals("toworld")){
			DPlayer dplayer=DPlayer.get(player);
			if(dplayer!=null){
				dplayer.leave();
			}
		}

		else if(this.type.equals("todungeon")){
			DGroup dgroup=DGroup.get(player);
			if(dgroup!=null){
				if(dgroup.getGworld()==null){
					dgroup.setGworld(GameWorld.load(DGroup.get(player).getDungeonname()));
				}
				if(dgroup.getGworld()!=null){
					
					/* Check Spout */
					boolean spoutCheck = true;
					if(P.p.isSpoutEnabled){
						if(dgroup.getGworld().config.spoutCraftOnly){
							if(!Spout.getServer().getPlayer(player.getName()).isSpoutCraftEnabled()){
								spoutCheck = false;
							}
						}
					}
					
					/* Teleport Player */
					if(spoutCheck){	
						if(dgroup.getGworld().locLobby==null){
							new DPlayer(player,dgroup.getGworld().world,dgroup.getGworld().world.getSpawnLocation(), false);
						}else{
							new DPlayer(player,dgroup.getGworld().world,dgroup.getGworld().locLobby, false);
						}
					}else{
						p.msg(player,p.language.get("Error_SpoutCraftOnly"));
					}
				}else{
					p.msg(player,p.language.get("Error_DungeonNotExist",DGroup.get(player).getDungeonname()));
				}
			}else{
				p.msg(player,p.language.get("Error_NotInGroup"));
			}
		}
	}

	//Statics

	public static DPortal get(Location location) {
		return  get(location.getBlock());
	}

	public static DPortal get(Block block) {
		for(DPortal portal:portals){
			int x1=portal.block1.getX(),y1=portal.block1.getY(),z1=portal.block1.getZ();
			int x2=portal.block2.getX(),y2=portal.block2.getY(),z2=portal.block2.getZ();
			int x3=block.getX(),y3=block.getY(),z3=block.getZ();

			if(x1>x2){
				if(x3<x2 || x3>x1) continue;
			}else{
				if(x3>x2 || x3<x1) continue;
			}

			if(y1>y2){
				if(y3<y2 || y3>y1) continue;
			}else{
				if(y3>y2 || y3<y1) continue;
			}

			if(z1>z2){
				if(z3<z2 || z3>z1) continue;
			}else{
				if(z3>z2 || z3<z1) continue;
			}

			return portal;
		}

		return null;
	}

	//Save and Load

	public static void save(FileConfiguration configFile){
		int id = 0;
		for(DPortal dportal:portals){
			id++;
			if(dportal.isActive){
				String preString="portal."+dportal.world.getName()+"."+id;
				//Location1
				configFile.set(preString+".loc1.x",dportal.block1.getX());
				configFile.set(preString+".loc1.y",dportal.block1.getY());
				configFile.set(preString+".loc1.z",dportal.block1.getZ());
				//Location1
				configFile.set(preString+".loc2.x",dportal.block2.getX());
				configFile.set(preString+".loc2.y",dportal.block2.getY());
				configFile.set(preString+".loc2.z",dportal.block2.getZ());
			}
		}
	}

	public static void load(FileConfiguration configFile) {
		for(World world:p.getServer().getWorlds()){
			if(configFile.contains("portal."+world.getName())){
				int id=0;
				String preString;
				do{
					id++;
					preString="portal."+world.getName()+"."+id+".";
					if(configFile.contains(preString)){
						DPortal dportal=new DPortal(true);
						dportal.world=world;
						dportal.block1=world.getBlockAt(configFile.getInt(preString+"loc1.x"),configFile.getInt(preString+"loc1.y"),configFile.getInt(preString+"loc1.z"));
						dportal.block2=world.getBlockAt(configFile.getInt(preString+"loc2.x"),configFile.getInt(preString+"loc2.y"),configFile.getInt(preString+"loc2.z"));
						dportal.type="todungeon";
						dportal.fillwithportal();
					}
				}while(configFile.contains(preString));
			}
		}

	}

}
