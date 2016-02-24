package io.github.dre2n.dungeonsxl.sign;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.event.dsign.DSignRegistrationEvent;
import io.github.dre2n.dungeonsxl.game.GameWorld;
import io.github.dre2n.dungeonsxl.trigger.Trigger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public abstract class DSign {
	
	protected static DungeonsXL plugin = DungeonsXL.getPlugin();
	
	private Sign sign;
	private GameWorld gameWorld;
	
	// List of Triggers
	private Set<Trigger> triggers = new HashSet<Trigger>();
	
	public DSign(Sign sign, GameWorld gameWorld) {
		this.setSign(sign);
		this.gameWorld = gameWorld;
		
		// Check Trigger
		if (gameWorld == null) {
			return;
		}
		
		String line3 = sign.getLine(3).replaceAll("\\s", "");
		String[] triggerTypes = line3.split(",");
		
		for (String triggerString : triggerTypes) {
			if (triggerString.equals("")) {
				continue;
			}
			
			String type = triggerString.substring(0, 1);
			String value = null;
			if (triggerString.length() > 1) {
				value = triggerString.substring(1);
			}
			
			Trigger trigger = Trigger.getOrCreate(type, value, this);
			if (trigger != null) {
				trigger.addListener(this);
				addTrigger(trigger);
			}
		}
	}
	
	/**
	 * @return the sign
	 */
	public Sign getSign() {
		return sign;
	}
	
	/**
	 * @param sign
	 * the sign to set
	 */
	public void setSign(Sign sign) {
		this.sign = sign;
	}
	
	/**
	 * @return the gameWorld
	 */
	public GameWorld getGameWorld() {
		return gameWorld;
	}
	
	/**
	 * @return the triggers
	 */
	public Set<Trigger> getTriggers() {
		return triggers;
	}
	
	/**
	 * @param trigger
	 * the trigger to add
	 */
	public void addTrigger(Trigger trigger) {
		triggers.add(trigger);
	}
	
	/**
	 * @param trigger
	 * the trigger to remove
	 */
	public void removeTrigger(Trigger trigger) {
		triggers.remove(trigger);
	}
	
	public void onInit() {
	}
	
	public void onTrigger() {
	}
	
	public boolean onPlayerTrigger(Player player) {
		return false;
	}
	
	public void onDisable() {
	}
	
	public void onUpdate() {
		for (Trigger trigger : triggers) {
			if ( !trigger.isTriggered()) {
				onDisable();
				return;
			}
			
			if (triggers.size() != 1) {
				continue;
			}
			
			if (trigger.getPlayer() == null) {
				continue;
			}
			
			if (onPlayerTrigger(trigger.getPlayer())) {
				return;
			}
		}
		
		onTrigger();
	}
	
	public void remove() {
		for (Trigger trigger : triggers) {
			trigger.removeListener(this);
		}
		gameWorld.getDSigns().remove(this);
	}
	
	public boolean hasTriggers() {
		return !triggers.isEmpty();
	}
	
	public static DSign create(Sign sign, GameWorld gameWorld) {
		String[] lines = sign.getLines();
		DSign dSign = null;
		
		for (DSignType type : plugin.getDSigns().getDSigns()) {
			if ( !lines[0].equalsIgnoreCase("[" + type.getName() + "]")) {
				continue;
			}
			
			try {
				Constructor<? extends DSign> constructor = type.getHandler().getConstructor(Sign.class, GameWorld.class);
				dSign = constructor.newInstance(sign, gameWorld);
				
			} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
				plugin.getLogger().info("An error occurred while accessing the handler class of the sign " + type.getName() + ": " + exception.getClass().getSimpleName());
				if ( !(type instanceof DSignTypeDefault)) {
					plugin.getLogger().info("Please note that this sign is an unsupported feature added by an addon!");
				}
			}
		}
		
		DSignRegistrationEvent event = new DSignRegistrationEvent(sign, gameWorld, dSign);
		
		if (event.isCancelled()) {
			return null;
		}
		
		if ( !(dSign != null && gameWorld != null)) {
			return dSign;
		}
		
		if (dSign.getType().isOnDungeonInit()) {
			dSign.onInit();
		}
		
		return dSign;
	}
	
	// Abstract methods
	
	public abstract boolean check();
	
	public abstract DSignType getType();
	
}
