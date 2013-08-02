package com.dre.dungeonsxl.trigger;

import java.util.Set;
import java.util.HashSet;

import org.bukkit.entity.Player;

import com.dre.dungeonsxl.P;
import com.dre.dungeonsxl.signs.DSign;
import com.dre.dungeonsxl.game.GameWorld;

public abstract class Trigger {

	protected Set<DSign> dsigns = new HashSet<DSign>();
	public boolean triggered = false;
	public Player player = null; // Holds Player for Player specific Triggers

	public Trigger() {

	}

	public abstract void register(GameWorld gworld);

	public abstract void unregister(GameWorld gworld);

	public static Trigger getOrCreate(String[] splitted, DSign dsign) {
		Trigger trigger = null;

		if (splitted.length > 0) {
			if (splitted[0].equalsIgnoreCase("R")) {

				trigger = RedstoneTrigger.getOrCreate(dsign.getSign(), dsign.getGameWorld());

			} else if (splitted[0].equalsIgnoreCase("D")) {

				if (splitted.length > 1) {
					trigger = new DistanceTrigger(P.p.parseInt(splitted[1]), dsign.getSign().getLocation());
				} else {
					trigger = new DistanceTrigger(dsign.getSign().getLocation());
				}

			} else if (splitted[0].equalsIgnoreCase("T")) {

				if (splitted.length > 1) {
					trigger = SignTrigger.getOrCreate(P.p.parseInt(splitted[1]), dsign.getGameWorld());
				}

			} else if (splitted[0].equalsIgnoreCase("I")) {

				if (splitted.length > 1) {
					trigger = InteractTrigger.getOrCreate(P.p.parseInt(splitted[1]), dsign.getGameWorld());
				}

			}
		}
		return trigger;
	}

	public void addListener(DSign dsign) {
		if (dsigns.isEmpty()) {
			register(dsign.getGameWorld());
		}
		dsigns.add(dsign);
	}

	public void removeListener(DSign dsign) {
		dsigns.remove(dsign);
		if (dsigns.isEmpty()) {
			unregister(dsign.getGameWorld());
		}
	}

	public void updateDSigns() {
		for (DSign dsign : dsigns.toArray(new DSign[dsigns.size()])) {
			dsign.onUpdate();
		}
	}

}
