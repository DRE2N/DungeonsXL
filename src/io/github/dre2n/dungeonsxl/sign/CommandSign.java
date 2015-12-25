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
	
	private DSignType type = DSignTypeDefault.COMMAND;
	
	// Variables
	private CCommand cCommand;
	private String command;
	private String executor;
	private boolean initialized;
	
	public CommandSign(Sign sign, GameWorld gameWorld) {
		super(sign, gameWorld);
	}
	
	@Override
	public boolean check() {
		return true;
	}
	
	@Override
	public void onInit() {
		command = getSign().getLine(1);
		executor = getSign().getLine(2);
		cCommand = CommandsXL.getCCommands().getCCommand(command);
		
		if (getTriggers().isEmpty()) {
			getSign().getBlock().setType(Material.AIR);
			return;
		}
		
		InteractTrigger trigger = InteractTrigger.getOrCreate(0, getSign().getBlock(), getGameWorld());
		
		if (trigger != null) {
			trigger.addListener(this);
			getTriggers().add(trigger);
		}
		
		getSign().setLine(0, ChatColor.DARK_BLUE + "############");
		getSign().setLine(1, ChatColor.DARK_GREEN + command);
		getSign().setLine(2, "");
		getSign().setLine(3, ChatColor.DARK_BLUE + "############");
		getSign().update();
		
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
	public DSignType getType() {
		return type;
	}
	
}
