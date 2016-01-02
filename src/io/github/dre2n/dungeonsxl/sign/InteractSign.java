package io.github.dre2n.dungeonsxl.sign;

import io.github.dre2n.dungeonsxl.dungeon.EditWorld;
import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
import io.github.dre2n.dungeonsxl.trigger.InteractTrigger;
import io.github.dre2n.dungeonsxl.util.NumberUtil;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class InteractSign extends DSign {
	
	private DSignType type = DSignTypeDefault.INTERACT;
	
	public InteractSign(Sign sign, GameWorld gameWorld) {
		super(sign, gameWorld);
	}
	
	@Override
	public boolean check() {
		Set<Integer> used = new HashSet<Integer>();
		for (Block block : EditWorld.getByWorld(getSign().getLocation().getWorld()).getSign()) {
			if (block == null) {
				continue;
			}
			
			if ( !block.getChunk().isLoaded()) {
				block.getChunk().load();
			}
			
			if (block.getState() instanceof Sign) {
				Sign rsign = (Sign) block.getState();
				if (rsign.getLine(0).equalsIgnoreCase("[" + type.getName() + "]")) {
					used.add(NumberUtil.parseInt(rsign.getLine(1)));
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
			id = NumberUtil.parseInt(getSign().getLine(1));
			if (id == 0 || used.contains(id)) {
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
		InteractTrigger trigger = InteractTrigger.getOrCreate(NumberUtil.parseInt(getSign().getLine(1)), getSign().getBlock(), getGameWorld());
		if (trigger != null) {
			trigger.addListener(this);
			addTrigger(trigger);
		}
		
		getSign().setLine(0, ChatColor.DARK_BLUE + "############");
		getSign().setLine(1, ChatColor.GREEN + getSign().getLine(2));
		getSign().setLine(2, ChatColor.GREEN + getSign().getLine(3));
		getSign().setLine(3, ChatColor.DARK_BLUE + "############");
		getSign().update();
	}
	
	@Override
	public boolean onPlayerTrigger(Player player) {
		return true;
	}
	
	@Override
	public DSignType getType() {
		return type;
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
