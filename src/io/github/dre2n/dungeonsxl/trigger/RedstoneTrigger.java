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
	
	private Block rtBlock;
	
	public RedstoneTrigger(Block block) {
		rtBlock = block;
	}
	
	public void onTrigger() {
		if (rtBlock.isBlockPowered()) {
			if ( !triggered) {
				triggered = true;
				updateDSigns();
			}
			
		} else {
			if (triggered) {
				triggered = false;
				updateDSigns();
			}
		}
	}
	
	@Override
	public void register(GameWorld gworld) {
		if ( !hasTriggers(gworld)) {
			ArrayList<RedstoneTrigger> list = new ArrayList<RedstoneTrigger>();
			list.add(this);
			triggers.put(gworld, list);
			
		} else {
			triggers.get(gworld).add(this);
		}
	}
	
	@Override
	public void unregister(GameWorld gworld) {
		if (hasTriggers(gworld)) {
			triggers.get(gworld).remove(this);
		}
	}
	
	@SuppressWarnings("deprecation")
    public static RedstoneTrigger getOrCreate(Sign sign, GameWorld gworld) {
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
			if (hasTriggers(gworld)) {
				for (RedstoneTrigger trigger : getTriggers(gworld)) {
					if (trigger.rtBlock.equals(rtBlock)) {
						return trigger;
					}
				}
			}
			return new RedstoneTrigger(rtBlock);
		}
		return null;
	}
	
	public static void updateAll(GameWorld gworld) {
		if (hasTriggers(gworld)) {
			for (RedstoneTrigger trigger : getTriggersArray(gworld)) {
				trigger.onTrigger();
			}
		}
	}
	
	public static boolean hasTriggers(GameWorld gworld) {
		return !triggers.isEmpty() && triggers.containsKey(gworld);
	}
	
	public static ArrayList<RedstoneTrigger> getTriggers(GameWorld gworld) {
		return triggers.get(gworld);
	}
	
	public static RedstoneTrigger[] getTriggersArray(GameWorld gworld) {
		return getTriggers(gworld).toArray(new RedstoneTrigger[getTriggers(gworld).size()]);
	}
	
}
