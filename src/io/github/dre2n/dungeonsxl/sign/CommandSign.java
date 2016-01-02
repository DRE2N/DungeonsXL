package io.github.dre2n.dungeonsxl.sign;

import io.github.dre2n.commandsxl.command.CCommand;
import io.github.dre2n.commandsxl.command.CCommandExecutorTask;
import io.github.dre2n.commandsxl.CommandsXL;
import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
import io.github.dre2n.dungeonsxl.trigger.InteractTrigger;
import io.github.dre2n.dungeonsxl.util.NumberUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CommandSign extends DSign {
	
	private DSignType type = DSignTypeDefault.COMMAND;
	
	// Variables
	private CCommand cCommand;
	private long delay;
	
	private String command;
	private String executor;
	private boolean initialized;
	
	public CommandSign(Sign sign, GameWorld gameWorld) {
		super(sign, gameWorld);
	}
	
	@Override
	public boolean check() {
		String lines[] = getSign().getLines();
		if (lines[1].equals("") || lines[2].equals("")) {
			return false;
		}
		
		if (lines[1] == null) {
			return false;
		}
		
		String[] attributes = lines[2].split(",");
		if (attributes.length == 2) {
			return true;
			
		} else {
			return false;
		}
	}
	
	@Override
	public void onInit() {
		String[] lines = getSign().getLines();
		String[] attributes = lines[2].split(",");
		
		command = lines[1];
		delay = NumberUtil.parseInt(attributes[0]);
		executor = attributes[1];
		
		cCommand = CommandsXL.getPlugin().getCCommands().getCCommand(command);
		
		if ( !getTriggers().isEmpty()) {
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
	public boolean onPlayerTrigger(final Player player) {
		if (executor.equalsIgnoreCase("Console")) {
			new CCommandExecutorTask(player, cCommand, Bukkit.getConsoleSender(), true).runTaskLater(plugin, delay * 20);
			
		} else if (executor.equalsIgnoreCase("OP")) {
			boolean isOp = player.isOp();
			
			player.setOp(true);
			
			new CCommandExecutorTask(player, cCommand, player, true).runTaskLater(plugin, delay * 20);
			
			if ( !isOp) {
				new BukkitRunnable() {
					@Override
					public void run() {
						player.setOp(false);
					}
				}.runTaskLater(plugin, delay * 20 + 1);
			}
			
		} else {
			new CCommandExecutorTask(player, cCommand, player, false).runTaskLater(plugin, delay * 20);
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
