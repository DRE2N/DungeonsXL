package com.dre.dungeonsxl.trigger;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import com.dre.dungeonsxl.game.GameWorld;

public class DistanceTrigger extends Trigger {

	private static Map<GameWorld, ArrayList<DistanceTrigger>> triggers = new HashMap<GameWorld, ArrayList<DistanceTrigger>>();

	private int distance = 5;
	private Location loc;


	public DistanceTrigger(int distance, Location loc) {
		if (distance >= 0) {
			this.distance = distance;
		}
		this.loc = loc;
	}

	public DistanceTrigger(Location loc) {
		this.loc = loc;
	}

	public void onTrigger(Player player) {
		triggered = true;
		this.player = player;
		updateDSigns();

	}

	public void register(GameWorld gworld) {
		if (!hasTriggers(gworld)) {
			ArrayList<DistanceTrigger> list = new ArrayList<DistanceTrigger>();
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

	public static void triggerAllInDistance(Player player, GameWorld gworld) {
		if (hasTriggers(gworld)) {
			for (DistanceTrigger trigger : getTriggersArray(gworld)) {
				if (player.getLocation().distance(trigger.loc) < trigger.distance) {
					trigger.onTrigger(player);
				}
			}
		}
	}

	public static boolean hasTriggers(GameWorld gworld) {
		return !triggers.isEmpty() && triggers.containsKey(gworld);
	}

	public static ArrayList<DistanceTrigger> getTriggers(GameWorld gworld) {
		return triggers.get(gworld);
	}

	public static DistanceTrigger[] getTriggersArray(GameWorld gworld) {
		return getTriggers(gworld).toArray(new DistanceTrigger[getTriggers(gworld).size()]);
	}

}
