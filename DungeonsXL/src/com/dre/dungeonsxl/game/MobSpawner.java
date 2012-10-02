package com.dre.dungeonsxl.game;

import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class MobSpawner {
	public static CopyOnWriteArrayList<MobSpawner> mobspawners=new CopyOnWriteArrayList<MobSpawner>();
	
	//Variables
	public EntityType mob;
	public Block block;
	public int maxinterval;
	public int interval=0;
	public int ammount;
	public int radius;
	private int live;
	
	
	public MobSpawner(Block block, EntityType mob, int interval, int ammount, int radius, int live){
		mobspawners.add(this);
		
		this.block=block;
		this.mob=mob;
		this.maxinterval=interval;
		this.ammount=ammount;
		this.radius=radius;
		this.live=live;
	}
	
	public void update(){
		World world=this.block.getWorld();
		
		for(Player player:world.getPlayers()){
			if(player.getWorld()==world){
				if(player.getLocation().distance(this.block.getLocation())<this.radius){
					if(this.interval<=0){
						LivingEntity mob=world.spawnCreature(this.block.getLocation(), this.mob);
						if(this.live>0){
							new DMob(mob,live,GameWorld.get(world));
						}
						
						if(ammount!=-1){
							if(ammount>1){
								ammount--;
							}else{
								mobspawners.remove(this);
							}
						}
						this.interval=this.maxinterval;
					}
					this.interval--;
					return;
				}
			}
		}
		
	}
	
	//Static
	public static void updateAll(){
		for(MobSpawner spawner:mobspawners){
			spawner.update();
		}
	}
	
}
