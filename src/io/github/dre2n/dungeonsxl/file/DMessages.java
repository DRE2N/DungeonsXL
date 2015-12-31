package io.github.dre2n.dungeonsxl.file;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class DMessages {
	
	public enum Messages {
		
		LOG_ERROR_MOB_ENCHANTMENT("Log_Error_MobEnchantment", "&4Error at loading mob.yml: Enchantment &6&v1&4 doesn't exist!"),
		LOG_ERROR_MOBTYPE("Log_Error_MobType", "&4Error at loading mob.yml: Mob &6&v1&4 doesn't exist!"),
		LOG_ERROR_NO_CONSOLE_COMMAND("Log_Error_NoConsoleCommand", "&6/dxl &v1&4 can not be executed as Console!"),
		LOG_GENERATE_NEW_WORLD("Log_GenerateNewWorld", "&6Generate new world..."),
		LOG_NEW_DUNGEON("Log_NewDungeon", "&6New Dungeon"),
		LOG_WORLD_GENERATION_FINISHED("Log_WorldGenerationFinished", "&6World generation finished!"),
		
		PLAYER_BLOCK_INFO("Player_BlockInfo", "&6Block-ID: &2&v1"),
		PLAYER_CHECKPOINT_REACHED("Player_CheckpointReached", "&6Checkpoint reached!"),
		PLAYER_DEATH("Player_Death", "&6You died, lives left: &2&v1"),
		PLAYER_DEATH_KICK("Player_DeathKick", "&2&v1&6 died and lost his last life."),
		PLAYER_FINISHED_DUNGEON("Player_FinishedDungeon", "&6You successfully finished the Dungeon!"),
		PLAYER_JOIN_GROUP("Player_JoinGroup", "&6Player &4&v1&6 has joined the Group!"),
		PLAYER_LEAVE_GROUP("Player_LeaveGroup", "&6You have successfully left your group!"),
		PLAYER_LEFT_GROUP("Player_LeftGroup", "&6Player &4&v1&6 has left the Group!"),
		PLAYER_LOOT_ADDED("Player_LootAdded", "&4&v1&6 have been added to your reward inventory!"),
		PLAYER_OFFLINE("Player_Offline", "&Player &4&v1&6 went offline. In &4&v2&6 seconds he will autmatically be kicked from the Dungeon!"),
		PLAYER_OFFLINE_NEVER("Player_OfflineNever", "&Player &4&v1&6 went offline. He will &4not&6 be kicked from the Dungeon automatically!"),
		PLAYER_PORTAL_ABORT("Player_PortalAbort", "&6Portal creation cancelled!"),
		PLAYER_PORTAL_INTRODUCTION("Player_PortalIntroduction", "&6Click the two edges of the Portal with the wooden sword!"),
		PLAYER_PORTAL_CREATED("Player_PortalCreated", "&6Portal created!"),
		PLAYER_PORTAL_PROGRESS("Player_PortalProgress", "&6First Block, now the second one!"),
		PLAYER_PROTECTED_BLOCK_DELETED("Player_ProtectedBlockDeleted", "&6Protected block deleted!"),
		PLAYER_READY("Player_Ready", "&6You are now ready for the Dungeon!"),
		PLAYER_SIGN_CREATED("Player_SignCreated", "&6Sign created!"),
		PLAYER_SIGN_COPIED("Player_SignCopied", "&6Copied!"),
		PLAYER_TREASURES("Player_Treasures", "&1Treasures"),
		PLAYER_WAIT_FOR_OTHER_PLAYERS("Player_WaitForOtherPlayers", "&6Waiting for teammates..."),
		
		CMD_BREAK_PROTECTED_MODE("Cmd_Break_ProtectedMode", "&6You may not break blocks protected by DungeonsXL anymore."),
		CMD_BREAK_BREAK_MODE("Cmd_Break_BreakMode", "&6You may break a block protected by DungeonsXL."),
		CMD_CHAT_DUNGEON_CHAT("Cmd_Chat_DungeonChat", "&6You have entered the Dungeon-chat"),
		CMD_CHAT_NORMAL_CHAT("Cmd_Chat_NormalChat", "&6You are now in the public chat"),
		CMD_CHATSPY_STOPPED("Cmd_Chatspy_Stopped", "&6You stopped spying the DXL-chat!"),
		CMD_CHATSPY_START("Cmd_Chatspy_Start", "&You started spying the DXL-chat!"),
		CMD_INVITE_SUCCESS("Cmd_Invite_Success", "&6Player &4&v1&6 was successfully invited to edit the Dungeon &4&v2&6!"),
		CMD_LEAVE_SUCCESS("Cmd_Leave_Success", "&6You have successfully left your group!"),
		CMD_LIVES("Cmd_Lives", "&4v1&6 has &4v2 &6lives left."),
		CMD_MAIN_WELCOME("Cmd_Main_Welcome", "&7Welcome to &4Dungeons&fXL"),
		CMD_MAIN_LOADED("Cmd_Main_Loaded", "&eMaps: &o[&v1] &eDungeons: &o[&v2] &eLoaded: &o[&v3] &ePlayers: &o[&v4]"),
		CMD_MAIN_COMPATIBILITY("Cmd_Main_Compatibility", "&eInternals: &o[&v1] &eVault: &o[&v2] &eMythicMobs: &o[&v3]"),
		CMD_MAIN_HELP("Cmd_Main_Help", "&7Type in &o/dxl help&r&7 for further information."),
		CMD_MSG_ADDED("Cmd_Msg_Added", "&6New Messages (&4&v1&6) added!"),
		CMD_MSG_UPDATED("Cmd_Msg_Updated", "&6Messages (&4&v1&6) updated!"),
		CMD_RELOAD_DONE("Cmd_Reload_Done", "&7Successfully reloaded DungeonsXL."),
		CMD_SAVE_SUCCESS("Cmd_Save_Success", "&6Dungeon saved!"),
		CMD_UNINVITE_SUCCESS("Cmd_Uninvite_Success", "&4&v1&6 was successfully uninvited to edit the Dungeon &4&v1&6!"),
		
		ERROR_BED("Error_Bed", "&4You cannot use a bed while in a Dungeon!"),
		ERROR_CHEST_IS_OPENED("Error_ChestIsOpened", "&4This chest has already been opened."),
		ERROR_CMD("Error_Cmd", "&4Commands are not allowed while in a dungeon!"),
		ERROR_CMD_NOT_EXIST_1("Error_CmdNotExist1", "&4Command &6&v1&4 does not exist!"),
		ERROR_CMD_NOT_EXIST_2("Error_CmdNotExist2", "&4Pleaser enter &6/dxl help&4 for help!"),
		ERROR_COOLDOWN("Error_Cooldown", "&4You can only enter this Dungeon every &6&v1&4 hours!"),
		ERROR_DISPENSER("Error_Dispenser", "&4You cannot access this dispenser!"),
		ERROR_DROP("Error_Drop", "&4You cannot drop safe items"),
		ERROR_DUNGEON_NOT_EXIST("Error_DungeonNotExist", "&4Dungeon &6&v1&4 does not exist!"),
		ERROR_ENDERCHEST("Error_Enderchest", "&4You cannot use an enderchest while in a Dungeon!"),
		ERROR_LEAVE_DUNGEON("Error_LeaveDungeon", "&4You have to leave your current dungeon first!"),
		ERROR_LEAVE_GROUP("Error_LeaveGroup", "&4You have to leave your group first!"),
		ERROR_LEFT_CLICK("Error_Leftklick", "&4You have to use Left-Click on this sign!"),
		ERROR_MSG_ID_NOT_EXIST("Error_MsgIdNotExist", "&4Messages with Id &6&v1&4 does not exist!"),
		ERROR_MSG_FORMAT("Error_MsgFormat", "&4The Messages has to be between \"!"),
		ERROR_MSG_NO_INT("Error_MsgNoInt", "&4Argument <id> has to include a number!"),
		ERROR_NAME_TO_LONG("Error_NameToLong", "&4The name may not be longer than 15 characters!"),
		ERROR_NO_CONSOLE_COMMAND("Error_NoConsoleCommand", "&6/dxl &v1&4 cannot be executed as console!"),
		ERROR_NO_LEAVE_IN_TUTORIAL("Error_NoLeaveInTutorial", "&4You cannot use this command in the tutorial!"),
		ERROR_NO_PERMISSIONS("Error_NoPermissions", "&4You have no permission to do this!"),
		ERROR_NO_PLAYER_COMMAND("Error_NoPlayerCommand", "&6/dxl &v1&4 cannot be executed as player!"),
		ERROR_NO_PROTECTED_BLOCK("Error_NoDXLBlock", "&4This is not a block protected by DungeonsXL!"),
		ERROR_NOT_IN_DUNGEON("Error_NotInDungeon", "&4You are not in a dungeon!"),
		ERROR_NOT_IN_GROUP("Error_NotInGroup", "&4You have to join a group first!"),
		ERROR_TUTORIAL_NOT_EXIST("Error_TutorialNotExist", "&4Tutorial dungeon does not exist!"),
		ERROR_READY("Error_Ready", "&4Choose your class first!"),
		ERROR_REQUIREMENTS("Error_Requirements", "&4You don't fulfill the requirements for this Dungeon!"),
		ERROR_SIGN_WRONG_FORMAT("Error_SignWrongFormat", "&4The sign is not written correctly!"),
		
		HELP_CMD_BREAK("Help_Cmd_Break", "/dxl break - Break a block protected by DungeonsXL"),
		HELP_CMD_CHAT("Help_Cmd_Chat", "/dxl chat - Change the chat mode"),
		HELP_CMD_CHATSPY("Help_Cmd_Chatspy", "/dxl chatspy - Dis/enables the spymode"),
		HELP_CMD_CREATE("Help_Cmd_Create", "/dxl create <name> - Creates a new dungeon"),
		HELP_CMD_EDIT("Help_Cmd_Edit", "/dxl edit <name> - Edit an existing dungeon"),
		HELP_CMD_ESCAPE("Help_Cmd_Escape", "/dxl escape - Leaves the current dungeon, without saving!"),
		HELP_CMD_HELP("Help_Cmd_Help", "/dxl help <page> - Shows the help page"),
		HELP_CMD_INVITE("Help_Cmd_Invite", "/dxl invite <player> <dungeon> - Invite a player to edit a dungeon"),
		HELP_CMD_LEAVE("Help_Cmd_Leave", "/dxl leave - Leaves the current dungeon"),
		HELP_CMD_LIST("Help_Cmd_List", "/dxl list ([dungeon|map|loaded]) ([dungeon]) - Lists all dungeons"),
		HELP_CMD_LIVES("Help_Cmd_Lives", "/dxl lives <player> - Shows the lives a player has left"),
		HELP_CMD_MAIN("Help_Cmd_Main", "/dxl - General status information"),
		HELP_CMD_MSG("Help_Cmd_Msg", "/dxl msg <id> '[msg]' - Show or edit a message"),
		HELP_CMD_PLAY("Help_Cmd_Play", "/dxl play ([dungeon|map]) [name] - Allows the player to join a game without a portal"),
		HELP_CMD_PORTAL("Help_Cmd_Portal", "/dxl portal - Creates a portal that leads into a dungeon"),
		HELP_CMD_RELOAD("Help_Cmd_Reload", "/dxl reload - Reloads the plugin"),
		HELP_CMD_SAVE("Help_Cmd_Save", "/dxl save - Saves the current dungeon"),
		HELP_CMD_TEST("Help_Cmd_Test", "/dxl test ([dungeon|map]) [name] - Tests a dungeon"),
		HELP_CMD_UNINVITE("Help_Cmd_Uninvite", "/dxl uninvite <player> <dungeon> - Uninvite a player to edit a dungeon");
		
		private String identifier;
		private String message;
		
		Messages(String identifier, String message) {
			this.identifier = identifier;
			this.message = message;
		}
		
		/**
		 * @return the identifier
		 */
		public String getIdentifier() {
			return identifier;
		}
		
		/**
		 * @return the message
		 */
		public String getMessage() {
			return message;
		}
		
		/**
		 * @param message
		 * the message to set
		 */
		public void setMessage(String message) {
			this.message = message;
		}
		
		// Static
		
		/**
		 * @param identifer
		 * the identifer to set
		 */
		public static Messages getByIdentifier(String identifier) {
			for (Messages message : Messages.values()) {
				if (message.getIdentifier().equals(identifier)) {
					return message;
				}
			}
			
			return null;
		}
		
		/**
		 * @return a FileConfiguration containing all messages
		 */
		public static FileConfiguration toConfig() {
			FileConfiguration config = new YamlConfiguration();
			for (Messages message : Messages.values()) {
				config.set(message.getIdentifier(), message.getMessage());
			}
			
			return config;
		}
	}
	
	private File file;
	private FileConfiguration config;
	
	public DMessages(File file) {
		this.file = file;
		
		if ( !file.exists()) {
			try {
				file.createNewFile();
				config = Messages.toConfig();
				config.save(file);
				
			} catch (IOException exception) {
				exception.printStackTrace();
			}
			
		} else {
			config = YamlConfiguration.loadConfiguration(file);
			load();
		}
		
	}
	
	public void load() {
		if (config != null) {
			Set<String> keySet = config.getKeys(false);
			for (String key : keySet) {
				Messages message = Messages.getByIdentifier(key);
				if (message != null) {
					message.setMessage(config.getString(key));
				}
			}
		}
	}
	
	public boolean changed() {
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		if ( !Messages.toConfig().getValues(false).equals(config.getValues(false))) {
			return true;
			
		} else {
			return false;
		}
	}
	
	public void save() {
		if ( !changed()) {
			return;
		}
		
		String filePath = file.getPath();
		File oldMessages = new File(filePath.substring(0, filePath.length() - 4) + "_old.yml");
		
		try {
			Messages.toConfig().save(oldMessages);
			
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}
	
	public String getMessage(Messages message, String... args) {
		String output = message.getMessage();
		
		int i = 0;
		for (String arg : args) {
			i++;
			
			if (arg != null) {
				output = output.replace("&v" + i, arg);
				
			} else {
				output = output.replace("&v" + i, "null");
			}
		}
		
		return output;
	}
	
}
