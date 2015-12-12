package io.github.dre2n.dungeonsxl.sign;

import io.github.dre2n.commandsxl.CCommand;
import io.github.dre2n.commandsxl.CommandsXL;
import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
import io.github.dre2n.dungeonsxl.trigger.InteractTrigger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class CommandSign extends DSign {
	
	public static String name = "Cmd";
	public String buildPermissions = "dxl.sign.cmd";
	public boolean onDungeonInit = false;
	
	// Variables
	private CCommand cCommand;
	private String command;
	private String executor;
	private boolean initialized;
	
	public CommandSign(Sign sign, GameWorld gWorld) {
		super(sign, gWorld);
	}
	
	@Override
	public boolean check() {
		// TODO Auto-generated method stub
		
		return true;
	}
	
	@Override
	public void onInit() {
		command = getSign().getLine(1);
		executor = getSign().getLine(2);
		cCommand = CommandsXL.getCCommands().getCCommand(command);
		
		if (getTriggers().isEmpty()) {
			InteractTrigger trigger = InteractTrigger.getOrCreate(0, getSign().getBlock(), getGWorld());
			if (trigger != null) {
				trigger.addListener(this);
				getTriggers().add(trigger);
			}
			getSign().setLine(0, ChatColor.DARK_BLUE + "############");
			getSign().setLine(1, ChatColor.DARK_GREEN + command);
			getSign().setLine(2, "");
			getSign().setLine(3, ChatColor.DARK_BLUE + "############");
			getSign().update();
		} else {
			getSign().getBlock().setType(Material.AIR);
		}
		
		initialized = true;
	}
	
	@Override
	public boolean onPlayerTrigger(Player player) {
		if (executor.equalsIgnoreCase("Console")) {
			cCommand.execute(player, Bukkit.getConsoleSender(), true);
			
		} else if (executor.equalsIgnoreCase("OP")) {
			cCommand.execute(player, player, true);
			
		} else {
			cCommand.execute(player, player, false);
		}
		return true;
	}
	
	@Override
	public void onTrigger() {
		if (initialized) {
			remove();
		}
	}
	
	@Override
	public String getPermissions() {
		return buildPermissions;
	}
	
	@Override
	public boolean isOnDungeonInit() {
		return onDungeonInit;
	}
}
