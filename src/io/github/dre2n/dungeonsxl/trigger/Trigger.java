package io.github.dre2n.dungeonsxl.trigger;

import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
import io.github.dre2n.dungeonsxl.sign.DSign;
import io.github.dre2n.dungeonsxl.util.IntegerUtil;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;

public abstract class Trigger {
	
	protected Set<DSign> dsigns = new HashSet<DSign>();
	public boolean triggered = false;
	public Player player = null; // Holds Player for Player specific Triggers
	
	public Trigger() {
		
	}
	
	public abstract void register(GameWorld gworld);
	
	public abstract void unregister(GameWorld gworld);
	
	public static Trigger getOrCreate(String type, String value, DSign dsign) {
		Trigger trigger = null;
		
		if (type.equalsIgnoreCase("R")) {
			
			trigger = RedstoneTrigger.getOrCreate(dsign.getSign(), dsign.getGameWorld());
			
		} else if (type.equalsIgnoreCase("D")) {
			
			if (value != null) {
				trigger = new DistanceTrigger(IntegerUtil.parseInt(value), dsign.getSign().getLocation());
			} else {
				trigger = new DistanceTrigger(dsign.getSign().getLocation());
			}
			
		} else if (type.equalsIgnoreCase("T")) {
			
			if (value != null) {
				trigger = SignTrigger.getOrCreate(IntegerUtil.parseInt(value), dsign.getGameWorld());
			}
			
		} else if (type.equalsIgnoreCase("I")) {
			
			if (value != null) {
				trigger = InteractTrigger.getOrCreate(IntegerUtil.parseInt(value), dsign.getGameWorld());
			}
			
		} else if (type.equalsIgnoreCase("M")) {
			
			if (value != null) {
				trigger = MobTrigger.getOrCreate(value, dsign.getGameWorld());
			}
			
		} else if (type.equalsIgnoreCase("U")) {
			
			if (value != null) {
				trigger = UseItemTrigger.getOrCreate(value, dsign.getGameWorld());
			}
			
		}
		return trigger;
	}
	
	public void addListener(DSign dsign) {
		if (dsigns.isEmpty()) {
			register(dsign.getGameWorld());
		}
		dsigns.add(dsign);
	}
	
	public void removeListener(DSign dsign) {
		dsigns.remove(dsign);
		if (dsigns.isEmpty()) {
			unregister(dsign.getGameWorld());
		}
	}
	
	public void updateDSigns() {
		for (DSign dsign : dsigns.toArray(new DSign[dsigns.size()])) {
			dsign.onUpdate();
		}
	}
	
}
