package io.github.dre2n.dungeonsxl.command;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.util.MessageUtil;
import io.github.dre2n.dungeonsxl.util.VersionUtil.Internals;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;

public class ReloadCommand extends DCommand {
	
	public ReloadCommand() {
		setCommand("reload");
		setMinArgs(0);
		setMaxArgs(0);
		setHelp(plugin.getDMessages().get("Help_Cmd_Reload"));
		setPermission("dxl.reload");
		setPlayerCommand(true);
		setConsoleCommand(true);
	}
	
	@Override
	public void onExecute(String[] args, CommandSender sender) {
		PluginManager plugins = Bukkit.getServer().getPluginManager();
		
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
		plugin.getDMessages().save();
		
		// Load Config
		plugin.loadMainConfig(new File(plugin.getDataFolder(), "config.yml"));
		plugin.loadDMessages(new File(plugin.getDataFolder(), "languages/" + plugin.getMainConfig().getLanguage() + ".yml"));
		plugin.loadVersionUtil();
		plugin.loadDCommands();
		plugin.loadDungeons();
		
		MessageUtil.sendPluginTag(sender, plugin);
		MessageUtil.sendCenteredMessage(sender, plugin.getDMessages().get("Cmd_Reload_Done"));
		MessageUtil.sendCenteredMessage(sender, plugin.getDMessages().get("Cmd_Main_Loaded", String.valueOf(dungeons), String.valueOf(loaded), String.valueOf(players)));
		MessageUtil.sendCenteredMessage(sender, plugin.getDMessages().get("Cmd_Main_Compatibility", String.valueOf(internals), vault, mythicMobs));
	}
	
}
