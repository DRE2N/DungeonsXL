package io.github.dre2n.dungeonsxl.trigger;

import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class DistanceTrigger extends Trigger {
	
	private static Map<GameWorld, ArrayList<DistanceTrigger>> triggers = new HashMap<GameWorld, ArrayList<DistanceTrigger>>();
	
	private TriggerType type = TriggerTypeDefault.DISTANCE;
	
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
		setTriggered(true);
		this.setPlayer(player);
		updateDSigns();
	}
	
	@Override
	public void register(GameWorld gameWorld) {
		if ( !hasTriggers(gameWorld)) {
			ArrayList<DistanceTrigger> list = new ArrayList<DistanceTrigger>();
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
	
	public static void triggerAllInDistance(Player player, GameWorld gameWorld) {
		if ( !hasTriggers(gameWorld)) {
			return;
		}
		
		if ( !player.getLocation().getWorld().equals(gameWorld.getWorld())) {
			return;
		}
		
		for (DistanceTrigger trigger : getTriggersArray(gameWorld)) {
			if (player.getLocation().distance(trigger.loc) < trigger.distance) {
				trigger.onTrigger(player);
			}
		}
	}
	
	public static boolean hasTriggers(GameWorld gameWorld) {
		return !triggers.isEmpty() && triggers.containsKey(gameWorld);
	}
	
	public static ArrayList<DistanceTrigger> getTriggers(GameWorld gameWorld) {
		return triggers.get(gameWorld);
	}
	
	public static DistanceTrigger[] getTriggersArray(GameWorld gameWorld) {
		return getTriggers(gameWorld).toArray(new DistanceTrigger[getTriggers(gameWorld).size()]);
	}
	
}
