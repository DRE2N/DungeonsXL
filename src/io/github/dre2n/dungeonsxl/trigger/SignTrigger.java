package io.github.dre2n.dungeonsxl.trigger;

import io.github.dre2n.dungeonsxl.event.trigger.TriggerActionEvent;
import io.github.dre2n.dungeonsxl.game.GameWorld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignTrigger extends Trigger {
	
	private static Map<GameWorld, ArrayList<SignTrigger>> triggers = new HashMap<GameWorld, ArrayList<SignTrigger>>();
	
	private TriggerType type = TriggerTypeDefault.SIGN;
	
	private int stId;
	
	public SignTrigger(int stId) {
		this.stId = stId;
	}
	
	public void onTrigger(boolean enable) {
		TriggerActionEvent event = new TriggerActionEvent(this);
		
		if (event.isCancelled()) {
			return;
		}
		
		if (enable != isTriggered()) {
			setTriggered(enable);
			updateDSigns();
		}
	}
	
	@Override
	public void register(GameWorld gameWorld) {
		if ( !hasTriggers(gameWorld)) {
			ArrayList<SignTrigger> list = new ArrayList<SignTrigger>();
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
	
	public static SignTrigger getOrCreate(int id, GameWorld gameWorld) {
		SignTrigger trigger = get(id, gameWorld);
		if (trigger != null) {
			return trigger;
		}
		return new SignTrigger(id);
	}
	
	public static SignTrigger get(int id, GameWorld gameWorld) {
		if (hasTriggers(gameWorld)) {
			for (SignTrigger trigger : triggers.get(gameWorld)) {
				if (trigger.stId == id) {
					return trigger;
				}
			}
		}
		return null;
	}
	
	public static boolean hasTriggers(GameWorld gameWorld) {
		return !triggers.isEmpty() && triggers.containsKey(gameWorld);
	}
	
}
