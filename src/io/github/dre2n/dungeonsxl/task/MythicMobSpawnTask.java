package io.github.dre2n.dungeonsxl.task;

import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
import io.github.dre2n.dungeonsxl.mob.DMob;
import io.github.dre2n.dungeonsxl.sign.MythicMobsSign;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class MythicMobSpawnTask extends BukkitRunnable {
	
	private MythicMobsSign sign;
	
	public MythicMobSpawnTask(MythicMobsSign sign) {
		this.sign = sign;
	}
	
	@Override
	public void run() {
		if (sign.getInterval() <= 0) {
			World world = sign.getSign().getWorld();
			GameWorld gameWorld = GameWorld.getByWorld(world);
			
			if (gameWorld != null) {
				sign.setSpawnLocation(sign.getSign().getLocation().add(0.5, 0, 0.5));
				double x = sign.getSpawnLocation().getX();
				double y = sign.getSpawnLocation().getY();
				double z = sign.getSpawnLocation().getZ();
				
				String command = "mm mobs spawn " + sign.getMob() + " " + sign.getAmount() + " DXL_Game_" + gameWorld.getId() + "," + x + "," + y + "," + z;
				Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
				
				sign.setMythicMobs();
				if (sign.getMythicMob() != null) {
					new DMob(sign.getMythicMob(), sign.getGameWorld(), null, sign.getMob());
				}
				
				// Set the amount
				if (sign.getAmount() != -1) {
					if (sign.getAmount() > 1) {
						sign.setAmount(sign.getAmount() - 1);
					} else {
						sign.killTask();
						sign.remove();
					}
				}
				
				sign.setInterval(sign.getMaxinterval());
			} else {
				sign.killTask();
			}
		}
		sign.setInterval(sign.getInterval() - 1);
	}
	
}
