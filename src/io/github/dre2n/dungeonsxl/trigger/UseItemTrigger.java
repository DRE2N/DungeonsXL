package io.github.dre2n.dungeonsxl.trigger;

import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public class UseItemTrigger extends Trigger {
	
	private static Map<GameWorld, ArrayList<UseItemTrigger>> triggers = new HashMap<GameWorld, ArrayList<UseItemTrigger>>();
	
	private TriggerType type = TriggerTypeDefault.USE_ITEM;
	
	private String name;
	private String matchedName;
	
	public UseItemTrigger(String name) {
		this.name = name;
		Material mat = Material.matchMaterial(name);
		if (mat != null) {
			matchedName = mat.toString();
		}
	}
	
	public void onTrigger(Player player) {
		setTriggered(true);
		this.setPlayer(player);
		updateDSigns();
	}
	
	@Override
	public void register(GameWorld gameWorld) {
		if ( !hasTriggers(gameWorld)) {
			ArrayList<UseItemTrigger> list = new ArrayList<UseItemTrigger>();
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
	
	public static UseItemTrigger getOrCreate(String name, GameWorld gameWorld) {
		UseItemTrigger trigger = get(name, gameWorld);
		if (trigger != null) {
			return trigger;
		}
		return new UseItemTrigger(name);
	}
	
	public static UseItemTrigger get(String name, GameWorld gameWorld) {
		if (hasTriggers(gameWorld)) {
			for (UseItemTrigger trigger : triggers.get(gameWorld)) {
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
	
	public static boolean hasTriggers(GameWorld gameWorld) {
		return !triggers.isEmpty() && triggers.containsKey(gameWorld);
	}
	
}
