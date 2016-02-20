package io.github.dre2n.dungeonsxl.trigger;

import io.github.dre2n.dungeonsxl.event.trigger.TriggerActionEvent;
import io.github.dre2n.dungeonsxl.game.GameWorld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WaveTrigger extends Trigger {
	
	private static Map<GameWorld, ArrayList<WaveTrigger>> triggers = new HashMap<GameWorld, ArrayList<WaveTrigger>>();
	
	private TriggerType type = TriggerTypeDefault.WAVE;
	
	private int mustKillAmount;
	
	public WaveTrigger(int mustKillAmount) {
		this.mustKillAmount = mustKillAmount;
	}
	
	public void onTrigger() {
		TriggerActionEvent event = new TriggerActionEvent(this);
		
		if (event.isCancelled()) {
			return;
		}
		
		setTriggered(true);
		updateDSigns();
	}
	
	@Override
	public void register(GameWorld gameWorld) {
		if ( !hasTriggers(gameWorld)) {
			ArrayList<WaveTrigger> list = new ArrayList<WaveTrigger>();
			list.add(this);
			triggers.put(gameWorld, list);
			
		} else {
			triggers.get(gameWorld).add(this);
		}
	}
	
	@Override
	public void unregister(GameWorld gameWorld) {
		if (hasTriggers(gameWorld)) {
			triggers.get(gameWorld).remove(this);
		}
	}
	
	@Override
	public TriggerType getType() {
		return type;
	}
	
	public static WaveTrigger getOrCreate(int mustKillAmount, GameWorld gameWorld) {
		WaveTrigger trigger = get(gameWorld);
		if (trigger != null) {
			return trigger;
		}
		return new WaveTrigger(mustKillAmount);
	}
	
	public static WaveTrigger get(GameWorld gameWorld) {
		if (hasTriggers(gameWorld)) {
			for (WaveTrigger trigger : triggers.get(gameWorld)) {
				return trigger;
			}
		}
		return null;
	}
	
	public static boolean hasTriggers(GameWorld gameWorld) {
		return !triggers.isEmpty() && triggers.containsKey(gameWorld);
	}
	
	/**
	 * @return the mustKillAmount
	 */
	public int getMustKillAmount() {
		return mustKillAmount;
	}
	
	/**
	 * @param mustKillAmount
	 * the mustKillAmount to set
	 */
	public void setMustKillAmount(int mustKillAmount) {
		this.mustKillAmount = mustKillAmount;
	}
	
}
