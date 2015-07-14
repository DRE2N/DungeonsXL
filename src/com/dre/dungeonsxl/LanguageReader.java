package com.dre.dungeonsxl;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class LanguageReader {
	private Map<String, String> entries = new TreeMap<String, String>();
	private Map<String, String> defaults = new TreeMap<String, String>();

	private File file;
	private boolean changed;

	public LanguageReader(File file) {
		this.setDefaults();

		/* Load */
		this.file = file;

		FileConfiguration configFile = YamlConfiguration.loadConfiguration(file);

		Set<String> keySet = configFile.getKeys(false);
		for (String key : keySet) {
			entries.put(key, configFile.getString(key));
		}

		/* Check */
		this.check();
	}

	private void setDefaults() {

		/* Log */
		defaults.put("Log_NewDungeon", "&6New Dungeon");
		defaults.put("Log_GenerateNewWorld", "&6Generate new world...");
		defaults.put("Log_WorldGenerationFinished", "&6World generation finished!");
		defaults.put("Log_Error_MobEnchantment", "&4Error at loading mob.yml: Enchantment &6&v1&4 doesn't exist!");
		defaults.put("Log_Error_MobType", "&4Error at loading mob.yml: Mob &6&v1&4 doesn't exist!");
		defaults.put("Log_Error_NoConsoleCommand", "&6/dxl &v1&4 can not be executed as Console!");

		/* Player */
		defaults.put("Player_CheckpointReached", "&6Checkpoint reached!");
		defaults.put("Player_LootAdded", "&4&v1&6 have been added to your reward inventory!");
		defaults.put("Player_Ready", "&6You are now ready for the Dungeon!");
		defaults.put("Player_FinishedDungeon", "&6You successfully finished the Dungeon!");
		defaults.put("Player_WaitForOtherPlayers", "&6Waiting for teammates...");
		defaults.put("Player_LeaveGroup", "&6You have successfully left your group!");
		defaults.put("Player_Offline", "&Player &4&v1&6 went offline. In &4&v2&6 seconds he will autmatically be kicked from the Dungeon!");
		defaults.put("Player_OfflineNever", "&Player &4&v1&6 went offline. He will &4not&6 be kicked from the Dungeon automatically!");
		defaults.put("Player_LeftGroup", "&Player &4&v1&6 has left the Group!");
		defaults.put("Player_JoinGroup", "&Player &4&v1&6 has joined the Group!");
		defaults.put("Player_PortalAbort", "&6Portal creation cancelled!");
		defaults.put("Player_PortalIntroduction", "&6Click the two edges of the Portal with the wooden sword!");
		defaults.put("Player_PortalDeleted", "&6Portal deleted!");
		defaults.put("Player_PortalProgress", "&6First Block, now the second one!");
		defaults.put("Player_PortalCreated", "&6Portal created!");
		defaults.put("Player_SignCreated", "&6Sign created!");
		defaults.put("Player_SignCopied", "&6Copied!");
		defaults.put("Player_BlockInfo", "&6Block-ID: &2&v1");
		defaults.put("Player_Death", "&6You died, lives left: &2v1");
		defaults.put("Player_DeathKick", "&2v1&6 died and lost his last life.");

		/* Cmds */
		defaults.put("Cmd_Chat_DungeonChat", "&6You have entered the Dungeon-chat");
		defaults.put("Cmd_Chat_NormalChat", "&6You are now in the public chat");
		defaults.put("Cmd_Chatspy_Stopped", "&6You stopped spying the DXL-chat!");
		defaults.put("Cmd_Chatspy_Start", "&You started spying the DXL-chat!");
		defaults.put("Cmd_Invite_Success", "&6Player &4&v1&6 was successfully invited to edit the Dungeon &4&v2&6!");
		defaults.put("Cmd_Leave_Success", "&6You have successfully left your group!");
		defaults.put("Cmd_Msg_Added", "&6New Message (&4&v1&6) added!");
		defaults.put("Cmd_Msg_Updated", "&6Message (&4&v1&6) updated!");
		defaults.put("Cmd_Reload_Start", "&6Reloading DungeonsXL...");
		defaults.put("Cmd_Reload_Done", "&6DungeonsXL was successfully reloaded!");
		defaults.put("Cmd_Save_Success", "&6Dungeon saved!");
		defaults.put("Cmd_Uninvite_Success", "&4&v1&6 was successfully uninvited to edit the Dungeon &4&v1&6!");
		defaults.put("Cmd_Lives", "&4v1&6 has &4v2 &6lives left.");

		/* Errors */
		defaults.put("Error_Enderchest", "&4You cannot use an enderchest while in a Dungeon!");
		defaults.put("Error_Dispenser", "&4You cannot access this dispenser!");
		defaults.put("Error_Ready", "&4Choose your class first!");
		defaults.put("Error_Cooldown", "&4You can only enter this Dungeon every &6&v1&4 hours!");
		defaults.put("Error_Requirements", "&4You don't fulfill the requirements for this Dungeon!");
		defaults.put("Error_Leftklick", "&4You have to use Left-Click on this sign!");
		defaults.put("Error_Drop", "&4You cannot drop safe items");
		defaults.put("Error_Cmd", "&4Commands are not allowed while in a dungeon!");
		defaults.put("Error_NotInGroup", "&4You have to join a group first!");
		defaults.put("Error_NoPermissions", "&4You have no permission to do this!");
		defaults.put("Error_CmdNotExist1", "&4Command &6&v1&4 does not exist!");
		defaults.put("Error_CmdNotExist2", "&4Pleaser enter &6/dxl help&4 for help!");
		defaults.put("Error_NotInDungeon", "&4You are not in a dungeon!");
		defaults.put("Error_DungeonNotExist", "&4Dungeon &6&v1&4 does not exist!");
		defaults.put("Error_LeaveDungeon", "&4You have to leave your current dungeon first!");
		defaults.put("Error_NameToLong", "&4The name may not be longer than 15 characters!");
		defaults.put("Error_LeaveGroup", "&4You have to leave your group first!");
		defaults.put("Error_NoLeaveInTutorial", "&4You cannot use this command in the tutorial!");
		defaults.put("Error_MsgIdNotExist", "&4Message with Id &6&v1&4 does not exist!");
		defaults.put("Error_MsgFormat", "&4The Message has to be between \"!");
		defaults.put("Error_MsgNoInt", "&4Argument <id> has to include a number!");
		defaults.put("Error_TutorialNotExist", "&4Tutorial dungeon does not exist!");
		defaults.put("Error_NoPortal", "&4You have to look at a portal!");
		defaults.put("Error_NoPlayerCommand", "&6/dxl &v1&4 can not be executed as player!");
		defaults.put("Error_SignWrongFormat", "&4The sign is not written correctly!");

		/* Help */
		defaults.put("Help_Cmd_Chat", "/dxl chat - Change the Chat-Mode");
		defaults.put("Help_Cmd_Chatspy", "/dxl chatspy - Dis/enables the spymode");
		defaults.put("Help_Cmd_Create", "/dxl create <name> - Creates a new dungeon");
		defaults.put("Help_Cmd_Edit", "/dxl edit <name> - Edit an existing dungeon");
		defaults.put("Help_Cmd_Help", "/dxl help - Shows the help page");
		defaults.put("Help_Cmd_Invite", "/dxl invite <player> <dungeon> - Invite a player to edit a dungeon");
		defaults.put("Help_Cmd_Leave", "/dxl leave - Leaves the current dungeon");
		defaults.put("Help_Cmd_Escape", "/dxl escape - Leaves the current dungeon, without saving!");
		defaults.put("Help_Cmd_List", "/dxl list - Lists all dungeons");
		defaults.put("Help_Cmd_Msg", "/dxl msg <id> '[msg]' - Show or edit a message");
		defaults.put("Help_Cmd_Portal", "/dxl portal - Creates a portal that leads into a dungeon");
		defaults.put("Help_Cmd_DeletePortal", "/dxl deleteportal - Deletes the portal you are looking at");
		defaults.put("Help_Cmd_Reload", "/dxl reload - Reloads the plugin");
		defaults.put("Help_Cmd_Save", "/dxl save - Saves the current dungeon");
		defaults.put("Help_Cmd_Test", "/dxl test [dungeon] - Tests a dungeon");
		defaults.put("Help_Cmd_Uninvite", "/dxl uninvite <player> <dungeon> - Uninvite a player to edit a dungeon");
		defaults.put("Help_Cmd_Lives", "/dxl lives <player> - show the lives a player has left");

	}

	private void check() {
		for (String defaultEntry : defaults.keySet()) {
			if (!entries.containsKey(defaultEntry)) {
				entries.put(defaultEntry, defaults.get(defaultEntry));
				changed = true;
			}
		}
	}

	public void save() {
		if (changed) {
			/* Copy old File */
			File source = new File(file.getPath());
			String filePath = file.getPath();
			File temp = new File(filePath.substring(0, filePath.length() - 4) + "_old.yml");

			if (temp.exists())
				temp.delete();

			source.renameTo(temp);

			/* Save */
			FileConfiguration configFile = new YamlConfiguration();

			for (String key : entries.keySet()) {
				configFile.set(key, entries.get(key));
			}

			try {
				configFile.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String get(String key, String... args) {
		String entry = entries.get(key);

		if (entry != null) {
			int i = 0;
			for (String arg : args) {
				i++;
				if(arg != null){
					entry = entry.replace("&v" + i, arg);
				} else {
					entry = entry.replace("&v" + i, "null");
				}
			}
		}

		return entry;
	}
}
