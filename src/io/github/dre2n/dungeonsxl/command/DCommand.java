package io.github.dre2n.dungeonsxl.command;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.MessageConfig;
import io.github.dre2n.dungeonsxl.util.messageutil.MessageUtil;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class DCommand {
	
	protected static DungeonsXL plugin = DungeonsXL.getPlugin();
	protected static MessageConfig messageConfig = plugin.getMessageConfig();
	
	public boolean costsMoney;
	private String command;
	private int minArgs;
	private int maxArgs;
	private String help;
	private String permission;
	private boolean isPlayerCommand = false;
	private boolean isConsoleCommand = false;
	
	public DCommand() {
		costsMoney = false;
	}
	
	public void displayHelp(CommandSender sender) {
		MessageUtil.sendMessage(sender, ChatColor.RED + help);
	}
	
	public boolean playerHasPermissions(Player player) {
		if (player.hasPermission(permission) || permission == null) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * @return the command
	 */
	public String getCommand() {
		return command;
	}
	
	/**
	 * @param command
	 * the command to set
	 */
	public void setCommand(String command) {
		this.command = command;
	}
	
	/**
	 * @return the minimal amount of arguments
	 */
	public int getMinArgs() {
		return minArgs;
	}
	
	/**
	 * @param minArgs
	 * the minimal amount of arguments to set
	 */
	public void setMinArgs(int minArgs) {
		this.minArgs = minArgs;
	}
	
	/**
	 * @return the maximum amount of arguments
	 */
	public int getMaxArgs() {
		return maxArgs;
	}
	
	/**
	 * @param maxArgs
	 * the maximum amount of arguments to set
	 */
	public void setMaxArgs(int maxArgs) {
		this.maxArgs = maxArgs;
	}
	
	/**
	 * @return the help
	 */
	public String getHelp() {
		return help;
	}
	
	/**
	 * @param help
	 * the help to set
	 */
	public void setHelp(String help) {
		this.help = help;
	}
	
	/**
	 * @return the permission
	 */
	public String getPermission() {
		return permission;
	}
	
	/**
	 * @param permission
	 * the permission to set
	 */
	public void setPermission(String permission) {
		this.permission = permission;
	}
	
	/**
	 * @return the isPlayerCommand
	 */
	public boolean isPlayerCommand() {
		return isPlayerCommand;
	}
	
	/**
	 * @param isPlayerCommand
	 * the isPlayerCommand to set
	 */
	public void setPlayerCommand(boolean isPlayerCommand) {
		this.isPlayerCommand = isPlayerCommand;
	}
	
	/**
	 * @return the isConsoleCommand
	 */
	public boolean isConsoleCommand() {
		return isConsoleCommand;
	}
	
	/**
	 * @param isConsoleCommand
	 * the isConsoleCommand to set
	 */
	public void setConsoleCommand(boolean isConsoleCommand) {
		this.isConsoleCommand = isConsoleCommand;
	}
	
	// Abstracts
	public abstract void onExecute(String[] args, CommandSender sender);
	
}
