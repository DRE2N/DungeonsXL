package com.dre.dungeonsxl.signs;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.block.Sign;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

import com.dre.dungeonsxl.game.GameWorld;
import com.dre.dungeonsxl.EditWorld;
import com.dre.dungeonsxl.trigger.InteractTrigger;

public class SIGNInteract extends DSign {
	public static String name = "Interact";
	public String buildPermissions = "dxl.sign.trigger";
	public boolean onDungeonInit = true;

	public SIGNInteract(Sign sign, GameWorld gworld) {
		super(sign, gworld);
	}

	@Override
	public boolean check() {
		Set<Integer> used = new HashSet<Integer>();
		for (Block block : EditWorld.get(sign.getLocation().getWorld()).sign) {
			if (block != null) {
				if (!block.getChunk().isLoaded()) {
					block.getChunk().load();
				}
				if (block.getState() instanceof Sign) {
					Sign rsign = (Sign) block.getState();
					if (rsign.getLine(0).equalsIgnoreCase("[" + name + "]")) {
						used.add(p.parseInt(rsign.getLine(1)));
					}
				}
			}
		}

		int id = 1;
		if (sign.getLine(1).equalsIgnoreCase("")) {
			if (used.size() != 0) {
				while (used.contains(id)) {
					id++;
				}
			}
		} else {
			id = p.parseInt(sign.getLine(1));
			if (id == 0 || used.contains(id)) {
				return false;
			} else {
				return true;
			}
		}

		sign.setLine(1, id + "");
		p.getServer().getScheduler().scheduleSyncDelayedTask(p, new UpdateTask(), 2);
		return true;
	}

	@Override
	public void onInit() {
		InteractTrigger trigger = InteractTrigger.getOrCreate(p.parseInt(sign.getLine(1)), sign.getBlock(), gworld);
		if (trigger != null) {
			trigger.addListener(this);
			this.triggers.add(trigger);
		}

		sign.setLine(0, ChatColor.DARK_BLUE + "############");
		sign.setLine(1, ChatColor.GREEN + sign.getLine(2));
		sign.setLine(2, ChatColor.GREEN + sign.getLine(3));
		sign.setLine(3, ChatColor.DARK_BLUE + "############");
		sign.update();
	}

	@Override
	public boolean onPlayerTrigger(Player player) {
		return true;
	}

	@Override
	public String getPermissions() {
		return buildPermissions;
	}

	@Override
	public boolean isOnDungeonInit() {
		return onDungeonInit;
	}

	public class UpdateTask implements Runnable {

		public UpdateTask() {
		}

		@Override
		public void run() {
			sign.update();
		}
	}
}
