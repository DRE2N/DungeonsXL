package io.github.dre2n.dungeonsxl.trigger;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
import io.github.dre2n.dungeonsxl.event.trigger.TriggerRegistrationEvent;
import io.github.dre2n.dungeonsxl.sign.DSign;
import io.github.dre2n.dungeonsxl.util.NumberUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;

public abstract class Trigger {
	
	static DungeonsXL plugin = DungeonsXL.getPlugin();
	
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
	
	public static Trigger getOrCreate(String identifier, String value, DSign dSign) {
		TriggerType type = plugin.getTriggers().getByIdentifier(identifier);
		
		if (type == TriggerTypeDefault.REDSTONE) {
			
			return RedstoneTrigger.getOrCreate(dSign.getSign(), dSign.getGameWorld());
			
		} else if (type == TriggerTypeDefault.DISTANCE) {
			
			if (value != null) {
				return new DistanceTrigger(NumberUtil.parseInt(value), dSign.getSign().getLocation());
				
			} else {
				return new DistanceTrigger(dSign.getSign().getLocation());
			}
			
		} else if (type == TriggerTypeDefault.SIGN) {
			
			if (value != null) {
				return SignTrigger.getOrCreate(NumberUtil.parseInt(value), dSign.getGameWorld());
			}
			
		} else if (type == TriggerTypeDefault.INTERACT) {
			
			if (value != null) {
				return InteractTrigger.getOrCreate(NumberUtil.parseInt(value), dSign.getGameWorld());
			}
			
		} else if (type == TriggerTypeDefault.MOB) {
			
			if (value != null) {
				return MobTrigger.getOrCreate(value, dSign.getGameWorld());
			}
			
		} else if (type == TriggerTypeDefault.USE_ITEM) {
			
			if (value != null) {
				return UseItemTrigger.getOrCreate(value, dSign.getGameWorld());
			}
			
		} else if (type != null) {
			Trigger trigger = null;
			
			Method method;
			try {
				method = type.getHandler().getDeclaredMethod("getOrCreate", String.class, GameWorld.class);
				trigger = (Trigger) method.invoke(value, dSign.getGameWorld());
				
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
				plugin.getLogger().info("An error occurred while accessing the handler class of the sign " + type.getIdentifier() + ": " + exception.getClass().getSimpleName());
				if ( !(type instanceof TriggerTypeDefault)) {
					plugin.getLogger().info("Please note that this trigger is an unsupported feature added by an addon!");
				}
			}
			
			TriggerRegistrationEvent event = new TriggerRegistrationEvent(trigger);
			
			if (event.isCancelled()) {
				return null;
			}
			
			return trigger;
		}
		
		return null;
	}
	
	// Abstract methods
	
	public abstract void register(GameWorld gameWorld);
	
	public abstract void unregister(GameWorld gameWorld);
	
	public abstract TriggerType getType();
	
}
