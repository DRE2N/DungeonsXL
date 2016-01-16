package io.github.dre2n.dungeonsxl.sign;

import io.github.dre2n.dungeonsxl.dungeon.EditWorld;
import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
import io.github.dre2n.dungeonsxl.task.SignUpdateTask;
import io.github.dre2n.dungeonsxl.trigger.SignTrigger;
import io.github.dre2n.dungeonsxl.util.NumberUtil;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class TriggerSign extends DSign {
	
	private DSignType type = DSignTypeDefault.TRIGGER;
	
	// Variables
	private int triggerId;
	private boolean initialized;
	
	public TriggerSign(Sign sign, GameWorld gameWorld) {
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
			if (used.contains(id)) {
				return false;
			} else {
				return true;
			}
		}
		
		getSign().setLine(1, id + "");
		
		new SignUpdateTask(getSign()).runTaskLater(plugin, 2L);
		
		return true;
	}
	
	@Override
	public void onInit() {
		triggerId = NumberUtil.parseInt(getSign().getLine(1));
		getSign().getBlock().setType(Material.AIR);
		
		initialized = true;
	}
	
	@Override
	public void onTrigger() {
		if ( !initialized) {
			return;
		}
		
		SignTrigger trigger = SignTrigger.get(triggerId, getGameWorld());
		if (trigger != null) {
			trigger.onTrigger(true);
		}
	}
	
	@Override
	public void onDisable() {
		if ( !initialized) {
			return;
		}
		
		SignTrigger trigger = SignTrigger.get(triggerId, getGameWorld());
		if (trigger != null) {
			trigger.onTrigger(false);
		}
	}
	
	@Override
	public DSignType getType() {
		return type;
	}
	
}
