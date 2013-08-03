package com.dre.dungeonsxl.trigger;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import com.dre.dungeonsxl.game.GameWorld;

public class InteractTrigger extends Trigger {

	private static Map<GameWorld, ArrayList<InteractTrigger>> triggers = new HashMap<GameWorld, ArrayList<InteractTrigger>>();

	private int interactId;
	private Block interactBlock;


	public InteractTrigger(int id, Block block) {
		this.interactId = id;
		this.interactBlock = block;
	}

	public void onTrigger(Player player) {
		triggered = true;
		this.player = player;
		updateDSigns();
	}

	public void register(GameWorld gworld) {
		if (!hasTriggers(gworld)) {
			ArrayList<InteractTrigger> list = new ArrayList<InteractTrigger>();
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

	public static InteractTrigger getOrCreate(int id, GameWorld gworld) {
		if (id == 0) {
			return null;
		}
		InteractTrigger trigger = get(id, gworld);
		if (trigger != null) {
			return trigger;
		}
		return new InteractTrigger(id, null);
	}

	public static InteractTrigger getOrCreate(int id, Block block, GameWorld gworld) {
		InteractTrigger trigger = get(id, gworld);
		if (trigger != null) {
			trigger.interactBlock = block;
			return trigger;
		}
		return new InteractTrigger(id, block);
	}

	public static InteractTrigger get(Block block, GameWorld gworld) {
		if (hasTriggers(gworld)) {
			for (InteractTrigger trigger : triggers.get(gworld)) {
				if (trigger.interactBlock != null) {
					if (trigger.interactBlock.equals(block)) {
						return trigger;
					}
				}
			}
		}
		return null;
	}

	public static InteractTrigger get(int id, GameWorld gworld) {
		if (hasTriggers(gworld)) {
			for (InteractTrigger trigger : triggers.get(gworld)) {
				if (trigger.interactId == id) {
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
