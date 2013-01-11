package com.dre.dungeonsxl.game;

import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.inventory.ItemStack;

import com.dre.dungeonsxl.DMobType;

public class MobSpawner {
	public static CopyOnWriteArrayList<MobSpawner> mobspawners=new CopyOnWriteArrayList<MobSpawner>();
	
	//Variables
	private String mob;
	private Block block;
	private int maxinterval;
	private int interval=0;
	private int amount;
	private int radius;
	private int live;
	
	public MobSpawner(Block block, String mob, int interval, int amount, int radius, int live){
		mobspawners.add(this);
		
		this.block=block;
		this.mob=mob;
		this.maxinterval=interval;
		this.amount=amount;
		this.radius=radius;
		this.live=live;
	}
	
	public void update(){
		World world=this.block.getWorld();
		
		for(Player player:world.getPlayers()){
			if(player.getWorld()==world){
				if(player.getLocation().distance(this.block.getLocation())<this.radius){
					if(this.interval<=0){
						
						//Check normal mobs
						if(EntityType.fromName(this.mob)!=null){
							if(EntityType.fromName(this.mob).isAlive()){
								LivingEntity entity=(LivingEntity)world.spawnEntity(this.block.getLocation(), EntityType.fromName(this.mob));
								
								//Add Bow to normal Skeletons
								if(entity.getType() == EntityType.SKELETON){
									Skeleton skeleton = (Skeleton) entity;
									if(skeleton.getSkeletonType()==SkeletonType.NORMAL){
										skeleton.getEquipment().setItemInHand(new ItemStack(Material.BOW));
									}
								}
								
								new DMob(entity,this.live,GameWorld.get(world),null);
							}
						}
						
						//Check custom mobs
						DMobType mobType = DMobType.get(this.mob);
						
						if(mobType!=null){
							mobType.spawn(GameWorld.get(world), this.block.getLocation());
						}
						
						//Set the amount
						if(amount!=-1){
							if(amount>1){
								amount--;
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
