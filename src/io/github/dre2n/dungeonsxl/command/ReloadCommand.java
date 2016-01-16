package io.github.dre2n.dungeonsxl.command;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.MessageConfig.Messages;
import io.github.dre2n.dungeonsxl.util.VersionUtil.Internals;
import io.github.dre2n.dungeonsxl.util.messageutil.MessageUtil;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;

public class ReloadCommand extends DCommand {
	
	public ReloadCommand() {
		setCommand("reload");
		setMinArgs(0);
		setMaxArgs(0);
		setHelp(messageConfig.getMessage(Messages.HELP_CMD_RELOAD));
		setPermission("dxl.reload");
		setPlayerCommand(true);
		setConsoleCommand(true);
	}
	
	@Override
	public void onExecute(String[] args, CommandSender sender) {
		PluginManager plugins = Bukkit.getServer().getPluginManager();
		
		int maps = new File(plugin.getDataFolder() + "/maps").listFiles().length;
		int dungeons = new File(plugin.getDataFolder() + "/dungeons").listFiles().length;
		int loaded = plugin.getEditWorlds().size() + plugin.getGameWorlds().size();
		int players = plugin.getDPlayers().size();
		Internals internals = DungeonsXL.getPlugin().getVersion().getInternals();
		String vault = "";
		if (plugins.getPlugin("Vault") != null) {
			vault = plugins.getPlugin("Vault").getDescription().getVersion();
		}
		String mythicMobs = "";
		if (plugins.getPlugin("MythicMobs") != null) {
			mythicMobs = plugins.getPlugin("MythicMobs").getDescription().getVersion();
		}
		
		// Save
		plugin.saveData();
		plugin.getMessageConfig().save();
		
		// Load Config
		plugin.loadMainConfig(new File(plugin.getDataFolder(), "config.yml"));
		plugin.loadMessageConfig(new File(plugin.getDataFolder(), "languages/" + plugin.getMainConfig().getLanguage() + ".yml"));
		plugin.loadVersionUtil();
		plugin.loadDCommands();
		plugin.loadDungeons();
		
		MessageUtil.sendPluginTag(sender, plugin);
		MessageUtil.sendCenteredMessage(sender, messageConfig.getMessage(Messages.CMD_RELOAD_DONE));
		MessageUtil.sendCenteredMessage(sender, messageConfig.getMessage(Messages.CMD_MAIN_LOADED, String.valueOf(maps), String.valueOf(dungeons), String.valueOf(loaded), String.valueOf(players)));
		MessageUtil.sendCenteredMessage(sender, messageConfig.getMessage(Messages.CMD_MAIN_COMPATIBILITY, String.valueOf(internals), vault, mythicMobs));
	}
	
}
