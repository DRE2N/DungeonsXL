package io.github.dre2n.dungeonsxl.sign;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
import io.github.dre2n.dungeonsxl.trigger.Trigger;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public abstract class DSign {
	
	static DungeonsXL plugin = DungeonsXL.getPlugin();
	
	private Sign sign;
	private GameWorld gWorld;
	
	// List of Triggers
	private Set<Trigger> triggers = new HashSet<Trigger>();
	
	public DSign(Sign sign, GameWorld gWorld) {
		this.setSign(sign);
		this.gWorld = gWorld;
		
		// Check Trigger
		if (gWorld != null) {
			String line3 = sign.getLine(3).replaceAll("\\s", "");
			String[] triggerTypes = line3.split(",");
			
			for (String triggerString : triggerTypes) {
				if ( !triggerString.equals("")) {
					
					String type = triggerString.substring(0, 1);
					String value = null;
					if (triggerString.length() > 1) {
						value = triggerString.substring(1);
					}
					
					Trigger trigger = Trigger.getOrCreate(type, value, this);
					if (trigger != null) {
						trigger.addListener(this);
						addTrigger(trigger);
					}
				}
			}
		}
	}
	
	/**
	 * @return the sign
	 */
	public Sign getSign() {
		return sign;
	}
	
	/**
	 * @param sign
	 * the sign to set
	 */
	public void setSign(Sign sign) {
		this.sign = sign;
	}
	
	/**
	 * @return the gWorld
	 */
	public GameWorld getGWorld() {
		return gWorld;
	}
	
	/**
	 * @return the triggers
	 */
	public Set<Trigger> getTriggers() {
		return triggers;
	}
	
	/**
	 * @param trigger
	 * the trigger to add
	 */
	public void addTrigger(Trigger trigger) {
		addTrigger(trigger);
	}
	
	/**
	 * @param trigger
	 * the trigger to remove
	 */
	public void removeTrigger(Trigger trigger) {
		triggers.remove(trigger);
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
			if ( !trigger.triggered) {
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
		gWorld.dSigns.remove(this);
	}
	
	public boolean hasTriggers() {
		return !triggers.isEmpty();
	}
	
	public static DSign create(Sign sign, GameWorld gWorld) {
		String[] lines = sign.getLines();
		DSign dSign = null;
		
		if (lines[0].equalsIgnoreCase("[" + CheckpointSign.name + "]")) {
			dSign = new CheckpointSign(sign, gWorld);
			
		} else if (lines[0].equalsIgnoreCase("[" + ChestSign.name + "]")) {
			dSign = new ChestSign(sign, gWorld);
			
		} else if (lines[0].equalsIgnoreCase("[" + ChunkUpdaterSign.name + "]")) {
			dSign = new ChunkUpdaterSign(sign, gWorld);
			
		} else if (lines[0].equalsIgnoreCase("[" + ClassesSign.name + "]")) {
			dSign = new ClassesSign(sign, gWorld);
			
		} else if (lines[0].equalsIgnoreCase("[" + CommandSign.name + "]")) {
			dSign = new CommandSign(sign, gWorld);
			
		} else if (lines[0].equalsIgnoreCase("[" + EndSign.name + "]")) {
			dSign = new EndSign(sign, gWorld);
			
		} else if (lines[0].equalsIgnoreCase("[" + LeaveSign.name + "]")) {
			dSign = new LeaveSign(sign, gWorld);
			
		} else if (lines[0].equalsIgnoreCase("[" + LobbySign.name + "]")) {
			dSign = new LobbySign(sign, gWorld);
			
		} else if (lines[0].equalsIgnoreCase("[" + MobSign.name + "]")) {
			dSign = new MobSign(sign, gWorld);
			
		} else if (lines[0].equalsIgnoreCase("[" + MsgSign.name + "]")) {
			dSign = new MsgSign(sign, gWorld);
			
		} else if (lines[0].equalsIgnoreCase("[" + MythicMobsSign.name + "]")) {
			dSign = new MythicMobsSign(sign, gWorld);
			
		} else if (lines[0].equalsIgnoreCase("[" + PlaceSign.name + "]")) {
			dSign = new PlaceSign(sign, gWorld);
			
		} else if (lines[0].equalsIgnoreCase("[" + ReadySign.name + "]")) {
			dSign = new ReadySign(sign, gWorld);
			
		} else if (lines[0].equalsIgnoreCase("[" + SoundMsgSign.name + "]")) {
			dSign = new SoundMsgSign(sign, gWorld);
			
		} else if (lines[0].equalsIgnoreCase("[" + StartSign.name + "]")) {
			dSign = new StartSign(sign, gWorld);
			
		} else if (lines[0].equalsIgnoreCase("[" + TriggerSign.name + "]")) {
			dSign = new TriggerSign(sign, gWorld);
			
		} else if (lines[0].equalsIgnoreCase("[" + InteractSign.name + "]")) {
			dSign = new InteractSign(sign, gWorld);
			
		} else if (lines[0].equalsIgnoreCase("[" + RedstoneSign.name + "]")) {
			dSign = new RedstoneSign(sign, gWorld);
			
		} else if (lines[0].equalsIgnoreCase("[" + BlockSign.name + "]")) {
			dSign = new BlockSign(sign, gWorld);
		}
		
		if (dSign != null && gWorld != null) {
			if (dSign.isOnDungeonInit()) {
				dSign.onInit();
			}
		}
		
		return dSign;
	}
	
	// Abstract methods
	
	public abstract boolean check();
	
	public abstract String getPermissions();
	
	public abstract boolean isOnDungeonInit();
	
}
