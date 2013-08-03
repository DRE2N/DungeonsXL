package com.dre.dungeonsxl.trigger;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import com.dre.dungeonsxl.game.GameWorld;


public class UseItemTrigger extends Trigger {

	private static Map<GameWorld, ArrayList<UseItemTrigger>> triggers = new HashMap<GameWorld, ArrayList<UseItemTrigger>>();

	private String name;
	private String matchedName;

	public UseItemTrigger(String name) {
		this.name = name;
		Material mat = Material.matchMaterial(name);
		if (mat != null) {
			this.matchedName = mat.toString();
		}
	}

	public void onTrigger(Player player) {
		triggered = true;
		this.player = player;
		updateDSigns();
	}

	public void register(GameWorld gworld) {
		if (!hasTriggers(gworld)) {
			ArrayList<UseItemTrigger> list = new ArrayList<UseItemTrigger>();
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

	public static UseItemTrigger getOrCreate(String name, GameWorld gworld) {
		UseItemTrigger trigger = get(name, gworld);
		if (trigger != null) {
			return trigger;
		}
		return new UseItemTrigger(name);
	}

	public static UseItemTrigger get(String name, GameWorld gworld) {
		if (hasTriggers(gworld)) {
			for (UseItemTrigger trigger : triggers.get(gworld)) {
				if (trigger.name.equalsIgnoreCase(name)) {
					return trigger;
				} else {
					if (trigger.matchedName != null) {
						if (trigger.matchedName.equalsIgnoreCase(name)) {
							return trigger;
						}
					}
				}
			}
		}
		return null;
	}

	public static boolean hasTriggers(GameWorld gworld) {
		return !triggers.isEmpty() && triggers.containsKey(gworld);
	}

}
