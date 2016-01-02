package io.github.dre2n.dungeonsxl.trigger;

import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MobTrigger extends Trigger {
	
	private static Map<GameWorld, ArrayList<MobTrigger>> triggers = new HashMap<GameWorld, ArrayList<MobTrigger>>();
	
	private TriggerType type = TriggerTypeDefault.MOB;
	
	private String name;
	
	public MobTrigger(String name) {
		this.name = name;
	}
	
	public void onTrigger() {
		setTriggered(true);
		updateDSigns();
	}
	
	@Override
	public void register(GameWorld gameWorld) {
		if ( !hasTriggers(gameWorld)) {
			ArrayList<MobTrigger> list = new ArrayList<MobTrigger>();
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
	
	public static MobTrigger getOrCreate(String name, GameWorld gameWorld) {
		MobTrigger trigger = get(name, gameWorld);
		if (trigger != null) {
			return trigger;
		}
		return new MobTrigger(name);
	}
	
	public static MobTrigger get(String name, GameWorld gameWorld) {
		if (hasTriggers(gameWorld)) {
			for (MobTrigger trigger : triggers.get(gameWorld)) {
				if (trigger.name.equalsIgnoreCase(name)) {
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
