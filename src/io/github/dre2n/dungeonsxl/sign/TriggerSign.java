package io.github.dre2n.dungeonsxl.sign;

import io.github.dre2n.dungeonsxl.dungeon.EditWorld;
import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
import io.github.dre2n.dungeonsxl.trigger.SignTrigger;
import io.github.dre2n.dungeonsxl.util.IntegerUtil;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class TriggerSign extends DSign {
	public static String name = "Trigger";
	public String buildPermissions = "dxl.sign.trigger";
	public boolean onDungeonInit = true;
	
	// Variables
	private int triggerId;
	private boolean initialized;
	
	public TriggerSign(Sign sign, GameWorld gWorld) {
		super(sign, gWorld);
	}
	
	@Override
	public boolean check() {
		Set<Integer> used = new HashSet<Integer>();
		for (Block block : EditWorld.get(getSign().getLocation().getWorld()).sign) {
			if (block != null) {
				if ( !block.getChunk().isLoaded()) {
					block.getChunk().load();
				}
				if (block.getState() instanceof Sign) {
					Sign rsign = (Sign) block.getState();
					if (rsign.getLine(0).equalsIgnoreCase("[" + name + "]")) {
						used.add(IntegerUtil.parseInt(rsign.getLine(1)));
					}
				}
			}
		}
		
		int id = 1;
		if (getSign().getLine(1).equalsIgnoreCase("")) {
			if (used.size() != 0) {
				while (used.contains(id)) {
					id++;
				}
			}
		} else {
			id = IntegerUtil.parseInt(getSign().getLine(1));
			if (used.contains(id)) {
				return false;
			} else {
				return true;
			}
		}
		
		getSign().setLine(1, id + "");
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new UpdateTask(), 2);
		return true;
	}
	
	@Override
	public void onInit() {
		triggerId = IntegerUtil.parseInt(getSign().getLine(1));
		getSign().getBlock().setType(Material.AIR);
		
		initialized = true;
	}
	
	@Override
	public void onTrigger() {
		if (initialized) {
			SignTrigger trigger = SignTrigger.get(triggerId, getGWorld());
			if (trigger != null) {
				trigger.onTrigger(true);
			}
		}
	}
	
	@Override
	public void onDisable() {
		if (initialized) {
			SignTrigger trigger = SignTrigger.get(triggerId, getGWorld());
			if (trigger != null) {
				trigger.onTrigger(false);
			}
		}
	}
	
	@Override
	public String getPermissions() {
		return buildPermissions;
	}
	
	@Override
	public boolean isOnDungeonInit() {
		return onDungeonInit;
	}
	
	public class UpdateTask implements Runnable {
		
		public UpdateTask() {
		}
		
		@Override
		public void run() {
			getSign().update();
		}
	}
	
}
