package com.dre.dungeonsxl.signs;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import com.dre.dungeonsxl.P;
import com.dre.dungeonsxl.game.GameWorld;
import com.dre.dungeonsxl.trigger.Trigger;

public abstract class DSign {
	protected static P p = P.p;

	protected Sign sign;
	protected GameWorld gworld;

	// List of Triggers
	protected Set<Trigger> triggers = new HashSet<Trigger>();

	public abstract boolean check();

	public abstract String getPermissions();

	public abstract boolean isOnDungeonInit();

	public DSign(Sign sign, GameWorld gworld) {
		this.sign = sign;
		this.gworld = gworld;

		// Check Trigger
		if (gworld != null) {
			String line3 = sign.getLine(3).replaceAll("\\s", "");
			String[] triggerTypes = line3.split(",");

			for (String triggerString : triggerTypes) {
				if (!triggerString.equals("")) {

					String type = triggerString.substring(0, 1);
					String value = null;
					if (triggerString.length() > 1) {
						value = triggerString.substring(1);
					}
					
					Trigger trigger = Trigger.getOrCreate(type, value, this);
					if (trigger != null) {
						trigger.addListener(this);
						this.triggers.add(trigger);
					}
				}
			}
		}
	}

	public void onInit() {

	}

	public void onTrigger() {

	}

	public boolean onPlayerTrigger(Player player) {
		return false;
	}

	public void onDisable() {

	}

	public void onUpdate() {
		for (Trigger trigger : triggers) {
			if (!trigger.triggered) {
				onDisable();
				return;
			}
			if (triggers.size() == 1) {
				if (trigger.player != null) {
					if (onPlayerTrigger(trigger.player)) {
						return;
					}
				}
			}
		}

		onTrigger();
	}

	public void remove() {
		for (Trigger trigger : triggers) {
			trigger.removeListener(this);
		}
		gworld.dSigns.remove(this);
	}

	public boolean hasTriggers() {
		return !triggers.isEmpty();
	}

	public static DSign create(Sign sign, GameWorld gworld) {
		String[] lines = sign.getLines();
		DSign dSign = null;

		if (lines[0].equalsIgnoreCase("[" + SIGNCheckpoint.name + "]")) {
			dSign = new SIGNCheckpoint(sign, gworld);
		} else if (lines[0].equalsIgnoreCase("[" + SIGNChest.name + "]")) {
			dSign = new SIGNChest(sign, gworld);
		} else if (lines[0].equalsIgnoreCase("[" + SIGNChunkUpdater.name + "]")) {
			dSign = new SIGNChunkUpdater(sign, gworld);
		} else if (lines[0].equalsIgnoreCase("[" + SIGNClasses.name + "]")) {
			dSign = new SIGNClasses(sign, gworld);
		} else if (lines[0].equalsIgnoreCase("[" + SIGNEnd.name + "]")) {
			dSign = new SIGNEnd(sign, gworld);
		} else if (lines[0].equalsIgnoreCase("[" + SIGNLeave.name + "]")) {
			dSign = new SIGNLeave(sign, gworld);
		} else if (lines[0].equalsIgnoreCase("[" + SIGNLobby.name + "]")) {
			dSign = new SIGNLobby(sign, gworld);
		} else if (lines[0].equalsIgnoreCase("[" + SIGNMob.name + "]")) {
			dSign = new SIGNMob(sign, gworld);
		} else if (lines[0].equalsIgnoreCase("[" + SIGNMsg.name + "]")) {
			dSign = new SIGNMsg(sign, gworld);
		} else if (lines[0].equalsIgnoreCase("[" + SIGNPlace.name + "]")) {
			dSign = new SIGNPlace(sign, gworld);
		} else if (lines[0].equalsIgnoreCase("[" + SIGNReady.name + "]")) {
			dSign = new SIGNReady(sign, gworld);
		} else if (lines[0].equalsIgnoreCase("[" + SIGNSoundMsg.name + "]")) {
			dSign = new SIGNSoundMsg(sign, gworld);
		} else if (lines[0].equalsIgnoreCase("[" + SIGNStart.name + "]")) {
			dSign = new SIGNStart(sign, gworld);
		} else if (lines[0].equalsIgnoreCase("[" + SIGNTrigger.name + "]")) {
			dSign = new SIGNTrigger(sign, gworld);
		} else if (lines[0].equalsIgnoreCase("[" + SIGNInteract.name + "]")) {
			dSign = new SIGNInteract(sign, gworld);
		} else if (lines[0].equalsIgnoreCase("[" + SIGNRedstone.name + "]")) {
			dSign = new SIGNRedstone(sign, gworld);
		} else if (lines[0].equalsIgnoreCase("[" + SIGNBlock.name + "]")) {
			dSign = new SIGNBlock(sign, gworld);
		}

		if (dSign != null && gworld != null) {
			if (dSign.isOnDungeonInit()) {
				dSign.onInit();
			}
		}

		return dSign;
	}

	// Getter and Setter
	public GameWorld getGameWorld() {
		return gworld;
	}

	public Sign getSign() {
		return sign;
	}

}
