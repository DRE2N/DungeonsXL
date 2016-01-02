package io.github.dre2n.dungeonsxl.trigger;

import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;

public class RedstoneTrigger extends Trigger {
	
	private static Map<GameWorld, ArrayList<RedstoneTrigger>> triggers = new HashMap<GameWorld, ArrayList<RedstoneTrigger>>();
	
	private TriggerType type = TriggerTypeDefault.REDSTONE;
	
	private Block rtBlock;
	
	public RedstoneTrigger(Block block) {
		rtBlock = block;
	}
	
	public void onTrigger() {
		if (rtBlock.isBlockPowered()) {
			if ( !isTriggered()) {
				setTriggered(true);
				updateDSigns();
			}
			
		} else {
			if (isTriggered()) {
				setTriggered(false);
				updateDSigns();
			}
		}
	}
	
	@Override
	public void register(GameWorld gameWorld) {
		if ( !hasTriggers(gameWorld)) {
			ArrayList<RedstoneTrigger> list = new ArrayList<RedstoneTrigger>();
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
	
	@SuppressWarnings("deprecation")
	public static RedstoneTrigger getOrCreate(Sign sign, GameWorld gameWorld) {
		Block rtBlock = null;
		if (sign.getBlock().getType() == Material.WALL_SIGN) {
			switch (sign.getData().getData()) {
				case 5:
					rtBlock = sign.getBlock().getRelative(BlockFace.WEST);
					break;
				case 4:
					rtBlock = sign.getBlock().getRelative(BlockFace.EAST);
					break;
				case 3:
					rtBlock = sign.getBlock().getRelative(BlockFace.NORTH);
					break;
				case 2:
					rtBlock = sign.getBlock().getRelative(BlockFace.SOUTH);
					break;
			}
			
		} else {
			rtBlock = sign.getBlock().getRelative(BlockFace.DOWN);
		}
		
		if (rtBlock != null) {
			if (hasTriggers(gameWorld)) {
				for (RedstoneTrigger trigger : getTriggers(gameWorld)) {
					if (trigger.rtBlock.equals(rtBlock)) {
						return trigger;
					}
				}
			}
			return new RedstoneTrigger(rtBlock);
		}
		return null;
	}
	
	public static void updateAll(GameWorld gameWorld) {
		if (hasTriggers(gameWorld)) {
			for (RedstoneTrigger trigger : getTriggersArray(gameWorld)) {
				trigger.onTrigger();
			}
		}
	}
	
	public static boolean hasTriggers(GameWorld gameWorld) {
		return !triggers.isEmpty() && triggers.containsKey(gameWorld);
	}
	
	public static ArrayList<RedstoneTrigger> getTriggers(GameWorld gameWorld) {
		return triggers.get(gameWorld);
	}
	
	public static RedstoneTrigger[] getTriggersArray(GameWorld gameWorld) {
		return getTriggers(gameWorld).toArray(new RedstoneTrigger[getTriggers(gameWorld).size()]);
	}
	
}
