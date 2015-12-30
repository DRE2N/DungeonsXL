package io.github.dre2n.dungeonsxl.trigger;

import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
import io.github.dre2n.dungeonsxl.sign.DSign;
import io.github.dre2n.dungeonsxl.util.IntegerUtil;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;

public abstract class Trigger {
	
	private boolean triggered;
	private Player player; // Holds Player for Player specific Triggers
	
	private Set<DSign> dSigns = new HashSet<DSign>();
	
	/**
	 * @return the triggered
	 */
	public boolean isTriggered() {
		return triggered;
	}
	
	/**
	 * @param triggered
	 * the triggered to set
	 */
	public void setTriggered(boolean triggered) {
		this.triggered = triggered;
	}
	
	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}
	
	/**
	 * @param player
	 * the player to set
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	/**
	 * @return the dSigns
	 */
	public Set<DSign> getDSigns() {
		return dSigns;
	}
	
	/**
	 * @param dSign
	 * the dSign to add
	 */
	public void addDSign(DSign dSign) {
		dSigns.add(dSign);
	}
	
	/**
	 * @param dSign
	 * the dSign to remove
	 */
	public void removeDSign(DSign dSign) {
		dSigns.remove(dSign);
	}
	
	public void addListener(DSign dSign) {
		if (dSigns.isEmpty()) {
			register(dSign.getGameWorld());
		}
		dSigns.add(dSign);
	}
	
	public void removeListener(DSign dSign) {
		dSigns.remove(dSign);
		if (dSigns.isEmpty()) {
			unregister(dSign.getGameWorld());
		}
	}
	
	public void updateDSigns() {
		for (DSign dSign : dSigns.toArray(new DSign[dSigns.size()])) {
			dSign.onUpdate();
		}
	}
	
	//TODO: Dynamic checks
	public static Trigger getOrCreate(String type, String value, DSign dsign) {
		if (type.equalsIgnoreCase("R")) {
			
			return RedstoneTrigger.getOrCreate(dsign.getSign(), dsign.getGameWorld());
			
		} else if (type.equalsIgnoreCase("D")) {
			
			if (value != null) {
				return new DistanceTrigger(IntegerUtil.parseInt(value), dsign.getSign().getLocation());
			} else {
				return new DistanceTrigger(dsign.getSign().getLocation());
			}
			
		} else if (type.equalsIgnoreCase("T")) {
			
			if (value != null) {
				return SignTrigger.getOrCreate(IntegerUtil.parseInt(value), dsign.getGameWorld());
			}
			
		} else if (type.equalsIgnoreCase("I")) {
			
			if (value != null) {
				return InteractTrigger.getOrCreate(IntegerUtil.parseInt(value), dsign.getGameWorld());
			}
			
		} else if (type.equalsIgnoreCase("M")) {
			
			if (value != null) {
				return MobTrigger.getOrCreate(value, dsign.getGameWorld());
			}
			
		} else if (type.equalsIgnoreCase("U")) {
			
			if (value != null) {
				return UseItemTrigger.getOrCreate(value, dsign.getGameWorld());
			}
		}
		
		return null;
	}
	
	// Abstract methods
	
	public abstract void register(GameWorld gameWorld);
	
	public abstract void unregister(GameWorld gameWorld);
	
	public abstract TriggerType getType();
	
}
