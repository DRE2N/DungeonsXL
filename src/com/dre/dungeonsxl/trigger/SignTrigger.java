package com.dre.dungeonsxl.trigger;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import com.dre.dungeonsxl.game.GameWorld;


public class SignTrigger extends Trigger {

	private static Map<GameWorld, ArrayList<SignTrigger>> triggers = new HashMap<GameWorld, ArrayList<SignTrigger>>();

	private int stId;

	public SignTrigger(int stId) {
		this.stId = stId;
	}

	public void onTrigger(boolean enable) {
		if (enable != triggered) {
			triggered = enable;
			updateDSigns();
		}
	}

	public void register(GameWorld gworld) {
		if (!hasTriggers(gworld)) {
			ArrayList<SignTrigger> list = new ArrayList<SignTrigger>();
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

	public static SignTrigger getOrCreate(int id, GameWorld gworld) {
		SignTrigger trigger = get(id, gworld);
		if (trigger != null) {
			return trigger;
		}
		return new SignTrigger(id);
	}

	public static SignTrigger get(int id, GameWorld gworld) {
		if (hasTriggers(gworld)) {
			for (SignTrigger trigger : triggers.get(gworld)) {
				if (trigger.stId == id) {
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
