/*
 * Copyright (C) 2012-2016 Frank Baumann
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.dre2n.dungeonsxl.config;

import io.github.dre2n.commons.config.Messages;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * @author Daniel Saukel
 */
public enum DMessages implements Messages {

    ANNOUNCER_CMD("Announcer_Cmd", "&4&l=> &6USE &4/DXL JOIN &v1 &6TO JOIN &4&l<="),
    ANNOUNCER_CLICK("Announcer_Click", "&4&l=> &6CLICK HERE TO JOIN &4&l<="),
    CMD_BREAK_PROTECTED_MODE("Cmd_Break_ProtectedMode", "&6You may not break blocks protected by DungeonsXL anymore."),
    CMD_BREAK_BREAK_MODE("Cmd_Break_BreakMode", "&6You may break a block protected by DungeonsXL."),
    CMD_CHAT_DUNGEON_CHAT("Cmd_Chat_DungeonChat", "&6You have entered the Dungeon-chat"),
    CMD_CHAT_NORMAL_CHAT("Cmd_Chat_NormalChat", "&6You are now in the public chat"),
    CMD_CHATSPY_STOPPED("Cmd_Chatspy_Stopped", "&6You stopped spying the DXL-chat!"),
    CMD_CHATSPY_START("Cmd_Chatspy_Start", "&You started spying the DXL-chat!"),
    CMD_ENTER_SUCCESS("Cmd_Enter", "&6The group &4&v1 &6successfully entered the game of the group &4&v2&6."),
    CMD_INVITE_SUCCESS("Cmd_Invite_Success", "&6Player &4&v1&6 was successfully invited to edit the Dungeon &4&v2&6!"),
    CMD_LEAVE_SUCCESS("Cmd_Leave_Success", "&6You have successfully left your group!"),
    CMD_LIVES("Cmd_Lives", "&4&v1&6 has &4&v2 &6lives left."),
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
    ERROR_CMD_NOT_EXIST_2("Error_CmdNotExist2", "&4Please enter &6/dxl help&4 for help!"),
    ERROR_COOLDOWN("Error_Cooldown", "&4You can only enter this Dungeon every &6&v1&4 hours!"),
    ERROR_DISPENSER("Error_Dispenser", "&4You cannot access this dispenser!"),
    ERROR_DROP("Error_Drop", "&4You cannot drop safe items"),
    ERROR_DUNGEON_NOT_EXIST("Error_DungeonNotExist", "&4Dungeon &6&v1&4 does not exist!"),
    ERROR_ENDERCHEST("Error_Enderchest", "&4You cannot use an enderchest while in a Dungeon!"),
    ERROR_IN_GROUP("Error_InGroup", "&4The player &6&v1&4 is already member of a group."),
    ERROR_JOIN_GROUP("Error_JoinGroup", "&4You have to join a group first!"),
    ERROR_LEAVE_DUNGEON("Error_LeaveDungeon", "&4You have to leave your current dungeon first!"),
    ERROR_LEAVE_GAME("Error_LeaveGame", "&4You have to leave your current game first!"),
    ERROR_LEAVE_GROUP("Error_LeaveGroup", "&4You have to leave your group first!"),
    ERROR_LEFT_CLICK("Error_Leftklick", "&4You have to use Left-Click on this sign!"),
    ERROR_MSG_ID_NOT_EXIST("Error_MsgIdNotExist", "&4Messages with Id &6&v1&4 does not exist!"),
    ERROR_MSG_FORMAT("Error_MsgFormat", "&4The Messages has to be between \"!"),
    ERROR_MSG_NO_INT("Error_MsgNoInt", "&4Argument <id> has to include a number!"),
    ERROR_NAME_IN_USE("Error_NameInUse", "&4The name &6&v1 &4is already in use."),
    ERROR_NAME_TO_LONG("Error_NameToLong", "&4The name may not be longer than 15 characters!"),
    ERROR_NO_CONSOLE_COMMAND("Error_NoConsoleCommand", "&6/dxl &v1&4 cannot be executed as console!"),
    ERROR_NO_GAME("Error_NoGame", "&4You currently do not take part in a game."),
    ERROR_NO_LEAVE_IN_TUTORIAL("Error_NoLeaveInTutorial", "&4You cannot use this command in the tutorial!"),
    ERROR_NO_PERMISSIONS("Error_NoPermissions", "&4You have no permission to do this!"),
    ERROR_NO_PLAYER_COMMAND("Error_NoPlayerCommand", "&6/dxl &v1&4 cannot be executed as player!"),
    ERROR_NO_PROTECTED_BLOCK("Error_NoDXLBlock", "&4This is not a block protected by DungeonsXL!"),
    ERROR_NO_SUCH_GROUP("Error_NoSuchGroup", "&4The group &6&v1&4 does not exist!"),
    ERROR_NO_SUCH_PLAYER("Error_NoSuchPlayer", "&4The player &6&v1&4 does not exist!"),
    ERROR_NOT_CAPTAIN("Error_NotCaptain", "&4You are not the captain of your group!"),
    ERROR_NOT_IN_DUNGEON("Error_NotInDungeon", "&4You are not in a dungeon!"),
    ERROR_NOT_IN_GAME("Error_NotInGame", "&4The group &6&v1&4 is not member of a game."),
    ERROR_NOT_IN_GROUP("Error_NotInGroup", "&4The player &6&v1&4 is not member of the group &6&v2&v4."),
    ERROR_NOT_INVITED("Error_NotInvited", "&4You are not invited to the group &6&v1&4."),
    ERROR_NOT_SAVED("Error_NotSaved", "&4The map &6&v1&4 has not been saved to the &6DungeonsXL/maps/ &4folder yet!"),
    ERROR_TUTORIAL_NOT_EXIST("Error_TutorialNotExist", "&4Tutorial dungeon does not exist!"),
    ERROR_READY("Error_Ready", "&4Choose your class first!"),
    ERROR_REQUIREMENTS("Error_Requirements", "&4You don't fulfill the requirements for this dungeon!"),
    ERROR_SIGN_WRONG_FORMAT("Error_SignWrongFormat", "&4The sign is not written correctly!"),
    HELP_CMD_BREAK("Help_Cmd_Break", "/dxl break - Break a block protected by DungeonsXL"),
    HELP_CMD_CHAT("Help_Cmd_Chat", "/dxl chat - Change the chat mode"),
    HELP_CMD_CHATSPY("Help_Cmd_Chatspy", "/dxl chatspy - Dis/enables the spymode"),
    HELP_CMD_CREATE("Help_Cmd_Create", "/dxl create <name> - Creates a new dungeon"),
    HELP_CMD_EDIT("Help_Cmd_Edit", "/dxl edit <name> - Edit an existing dungeon"),
    HELP_CMD_ESCAPE("Help_Cmd_Escape", "/dxl escape - Leaves the current dungeon, without saving!"),
    HELP_CMD_GAME("Help_Cmd_Game", "/dxl game - Shows information about the current game session"),
    HELP_CMD_GROUP("Help_Cmd_Group", "/dxl group - Shows group command help"),
    HELP_CMD_GROUP_CREATE("Help_Cmd_GroupCreate", "/dxl group create [group] - Creates a new group"),
    HELP_CMD_GROUP_DISBAND("Help_Cmd_GroupDisband", "/dxl group disband ([group]) - Disbands a group"),
    HELP_CMD_GROUP_INVITE("Help_Cmd_GroupInvite", "/dxl group invite [player]- Invites someone to your group"),
    HELP_CMD_GROUP_UNINVITE("Help_Cmd_GroupUninvite", "/dxl group uninvite [player] - Takes back an invitation to your group"),
    HELP_CMD_GROUP_JOIN("Help_Cmd_GroupJoin", "/dxl group join [group]- Join a group"),
    HELP_CMD_GROUP_KICK("Help_Cmd_GroupKick", "/dxl group kick [player] - Kicks a player"),
    HELP_CMD_GROUP_SHOW("Help_Cmd_GroupShow", "/dxl group show [group] - Shows a group"),
    HELP_CMD_HELP("Help_Cmd_Help", "/dxl help <page> - Shows the help page"),
    HELP_CMD_INVITE("Help_Cmd_Invite", "/dxl invite <player> <dungeon> - Invite a player to edit a dungeon"),
    HELP_CMD_JOIN("Help_Cmd_Join", "/dxl join [announcement] - Opens the GUI to join a group in an upcoming game"),
    HELP_CMD_ENTER("Help_Cmd_Enter", "/dxl enter ([joining group]) [target group] - Let the joining group enter the game of the target group"),
    HELP_CMD_LEAVE("Help_Cmd_Leave", "/dxl leave - Leaves the current dungeon"),
    HELP_CMD_LIST("Help_Cmd_List", "/dxl list ([dungeon|map|loaded]) ([dungeon]) - Lists all dungeons"),
    HELP_CMD_LIVES("Help_Cmd_Lives", "/dxl lives <player> - Shows the lives a player has left"),
    HELP_CMD_MAIN("Help_Cmd_Main", "/dxl - General status information"),
    HELP_CMD_MSG("Help_Cmd_Msg", "/dxl msg <id> '[msg]' - Show or edit a message"),
    HELP_CMD_PLAY("Help_Cmd_Play", "/dxl play ([dungeon|map]) [name] - Allows the player to play a dungeon without a portal"),
    HELP_CMD_PORTAL("Help_Cmd_Portal", "/dxl portal - Creates a portal that leads into a dungeon"),
    HELP_CMD_RELOAD("Help_Cmd_Reload", "/dxl reload - Reloads the plugin"),
    HELP_CMD_SAVE("Help_Cmd_Save", "/dxl save - Saves the current dungeon"),
    HELP_CMD_TEST("Help_Cmd_Test", "/dxl test - Starts the game in test mode"),
    HELP_CMD_UNINVITE("Help_Cmd_Uninvite", "/dxl uninvite <player> <dungeon> - Uninvite a player to edit a dungeon"),
    GROUP_CREATED("Group_Created", "&4&v1&6 created the group &4&v2&6!"),
    GROUP_DISBANDED("Group_Disbanded", "&4&v1&6 disbanded the group &4&v2&6."),
    GROUP_INVITED_PLAYER("Group_InvitedPlayer", "&4&v1&6 invited the player &4&v2&6 to the group &4&v3&6."),
    GROUP_JOINED_GAME("Group_JoinedGame", "&6Your group successfully joined the game."),
    GROUP_UNINVITED_PLAYER("Group_UninvitedPlayer", "&4&v1&6 took back the invitation for &4&v2&6 to the group &4&v3&6."),
    GROUP_KICKED_PLAYER("Group_KickedPlayer", "&4&v1&6 kicked the player &4&v2&6 from the group &4&v3&6."),
    GROUP_PLAYER_JOINED("Group_PlayerJoined", "&6Player &4&v1&6 has joined the group!"),
    GROUP_WAVE_FINISHED("Group_WaveFinished", "&6Your group finished wave no. &4&v1&6. The next one is going to start in &4&v2&6 seconds."),
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
    PLAYER_INVITED("Player_Invited", "&4&v1&6 invited you to the group &4&v2&6."),
    PLAYER_UNINVITED("Player_Uninvited", "&4&v1&6 took back your invitation to the group &4&v2&6."),
    PLAYER_JOIN_GROUP("Player_JoinGroup", "&6You successfully joined the group!"),
    PLAYER_KICKED("Player_Kicked", "&4You have been kicked out of the group &6&v1&4."),
    PLAYER_LEAVE_GROUP("Player_LeaveGroup", "&6You have successfully left your group!"),
    PLAYER_LEFT_GROUP("Player_LeftGroup", "&6Player &4&v1&6 has left the Group!"),
    PLAYER_LOOT_ADDED("Player_LootAdded", "&4&v1&6 have been added to your reward inventory!"),
    PLAYER_NEW_CAPTAIN("Player_NewCaptain", "&6You are now the new captain of your group."),
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
    PLAYER_TIME_LEFT("Player_TimeLeft", "&v1You have &6&v2 &v1seconds left to finish the dungeon!"),
    PLAYER_TIME_KICK("Player_TimeKick", "&2&v1&6's time expired."),
    PLAYER_TREASURES("Player_Treasures", "&1Treasures"),
    PLAYER_WAIT_FOR_OTHER_PLAYERS("Player_WaitForOtherPlayers", "&6Waiting for teammates..."),
    REQUIREMENT_FEE("Requirement_Fee", "&6You have been charged &4&v1 &6for entering the dungeon."),
    REWARD_GENERAL("Reward_General", "&6You received &4&v1 &6for finishing the dungeon.");

    private String identifier;
    private String message;

    DMessages(String identifier, String message) {
        this.identifier = identifier;
        this.message = message;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getMessage(String... args) {
        return DungeonsXL.getInstance().getMessageConfig().getMessage(this, args);
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    /* Statics */
    /**
     * @param identifer
     * the identifer to set
     */
    public static Messages getByIdentifier(String identifier) {
        for (Messages message : values()) {
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
        for (Messages message : values()) {
            config.set(message.getIdentifier(), message.getMessage());
        }

        return config;
    }

}
