package com.dre.dungeonsxl.trigger;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import com.dre.dungeonsxl.game.GameWorld;


public class MobTrigger extends Trigger {

	private static Map<GameWorld, ArrayList<MobTrigger>> triggers = new HashMap<GameWorld, ArrayList<MobTrigger>>();

	private String name;

	public MobTrigger(String name) {
		this.name = name;
	}

	public void onTrigger() {
		triggered = true;
		updateDSigns();
	}

	public void register(GameWorld gworld) {
		if (!hasTriggers(gworld)) {
			ArrayList<MobTrigger> list = new ArrayList<MobTrigger>();
			list.add(this);
			triggers.put(gworld, list);
		} else {
			triggers.get(gworld).add(this);
		}
	}

	public void unregister(GameWorld gworld) {
		if (hasTriggers(gworld)) {
			triggers.get(gworld).remove(this);
		}
	}

	public static MobTrigger getOrCreate(String name, GameWorld gworld) {
		MobTrigger trigger = get(name, gworld);
		if (trigger != null) {
			return trigger;
		}
		return new MobTrigger(name);
	}

	public static MobTrigger get(String name, GameWorld gworld) {
		if (hasTriggers(gworld)) {
			for (MobTrigger trigger : triggers.get(gworld)) {
				if (trigger.name.equalsIgnoreCase(name)) {
					return trigger;
				}
			}
		}
		return null;
	}

	public static boolean hasTriggers(GameWorld gworld) {
		return !triggers.isEmpty() && triggers.containsKey(gworld);
	}

}
