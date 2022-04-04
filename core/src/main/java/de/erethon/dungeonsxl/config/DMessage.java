/*
 * Copyright (C) 2012-2022 Frank Baumann
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
package de.erethon.dungeonsxl.config;

import de.erethon.bedrock.chat.MessageUtil;
import de.erethon.bedrock.config.Message;
import de.erethon.bedrock.config.MessageHandler;
import de.erethon.dungeonsxl.DungeonsXL;

/**
 * An enumeration of all messages. The values are fetched from the language file.
 *
 * @author Daniel Saukel
 */
public enum DMessage implements Message {

    ANNOUNCER_CLICK("announcer.click"),
    BUTTON_ACCEPT("button.accept"),
    BUTTON_DENY("button.deny"),
    BUTTON_OKAY("button.okay"),
    CMD_ANNOUNCE_HELP("cmd.announce.help"),
    CMD_BREAK_BREAK_MODE("cmd.break.breakMode"),
    CMD_BREAK_HELP("cmd.break.help"),
    CMD_BREAK_PROTECTED_MODE("cmd.break.protectedMode"),
    CMD_CHAT_HELP("cmd.chat.help"),
    CMD_CHAT_DUNGEON_CHAT("cmd.chat.dungeonChat"),
    CMD_CHAT_NORMAL_CHAT("cmd.chat.normalChat"),
    CMD_CHATSPY_HELP("cmd.chatspy.help"),
    CMD_CHATSPY_STOPPED("cmd.chatspy.stopped"),
    CMD_CHATSPY_STARTED("cmd.chatspy.started"),
    CMD_CREATE_HELP("cmd.create.help"),
    CMD_DELETE_BACKUPS("cmd.delete.backups"),
    CMD_DELETE_HELP("cmd.delete.help"),
    CMD_DELETE_SUCCESS("cmd.delete.success"),
    CMD_DUNGEON_ITEM_HELP("cmd.dungeonItem.help"),
    CMD_DUNGEON_ITEM_DUNGEON_ITEM_HELP("cmd.dungeonItem.dungeonItemHelp"),
    CMD_DUNGEON_ITEM_GLOBAL_ITEM_HELP("cmd.dungeonItem.globalItemHelp"),
    CMD_DUNGEON_ITEM_INFO_DUNGEON("cmd.dungeonItem.info.dungeon"),
    CMD_DUNGEON_ITEM_INFO_GLOBAL("cmd.dungeonItem.info.global"),
    CMD_DUNGEON_ITEM_SET_DUNGEON("cmd.dungeonItem.set.dungeon"),
    CMD_DUNGEON_ITEM_SET_GLOBAL("cmd.dungeonItem.set.global"),
    CMD_EDIT_HELP("cmd.edit.help"),
    CMD_ENTER_HELP("cmd.enter.help"),
    CMD_ENTER_SUCCESS("cmd.enter.success"),
    CMD_ESCAPE_HELP("cmd.escape.help"),
    CMD_GAME_HELP("cmd.game.help"),
    CMD_GROUP_HELP_MAIN("cmd.group.help.main"),
    CMD_GROUP_HELP_CREATE("cmd.group.help.create"),
    CMD_GROUP_HELP_DISBAND("cmd.group.help.disband"),
    CMD_GROUP_HELP_INVITE("cmd.group.help.invite"),
    CMD_GROUP_HELP_JOIN("cmd.group.help.join"),
    CMD_GROUP_HELP_KICK("cmd.group.help.kick"),
    CMD_GROUP_HELP_SHOW("cmd.group.help.show"),
    CMD_GROUP_HELP_UNINVITE("cmd.group.help.uninvite"),
    CMD_HELP_HELP("cmd.help.help"),
    CMD_IMPORT_HELP("cmd.import.help"),
    CMD_IMPORT_SUCCESS("cmd.import.success"),
    CMD_INVITE_HELP("cmd.invite.help"),
    CMD_INVITE_SUCCESS("cmd.invite.success"),
    CMD_JOIN_HELP("cmd.join.help"),
    CMD_KICK_HELP("cmd.kick.help"),
    CMD_KICK_SUCCESS("cmd.kick.success"),
    CMD_LEAVE_HELP("cmd.leave.help"),
    CMD_LEAVE_SUCCESS("cmd.leave.success"),
    CMD_LIST_HELP("cmd.list.help"),
    CMD_LIVES_GROUP("cmd.lives.group"),
    CMD_LIVES_HELP("cmd.lives.help"),
    CMD_LIVES_PLAYER("cmd.lives.player"),
    CMD_MAIN_WELCOME("cmd.main.welcome"),
    CMD_MAIN_LOADED("cmd.main.loaded"),
    CMD_MAIN_COMPATIBILITY("cmd.main.compatibility"),
    CMD_MAIN_HELP("cmd.main.help"),
    CMD_MAIN_HELP_INFO("cmd.main.helpInfo"),
    CMD_MSG_ADDED("cmd.msg.added"),
    CMD_MSG_HELP("cmd.msg.help"),
    CMD_MSG_UPDATED("cmd.msg.updated"),
    CMD_PORTAL_HELP("cmd.portal.help"),
    CMD_PLAY_HELP("cmd.play.help"),
    CMD_RELOAD_BUTTON_CALIBURN("cmd.reload.buttonCaliburn"),
    CMD_RELOAD_CALIBURN("cmd.reload.caliburn"),
    CMD_RELOAD_FAIL("cmd.reload.fail"),
    CMD_RELOAD_HELP("cmd.reload.help"),
    CMD_RELOAD_SUCCESS("cmd.reload.success"),
    CMD_RELOAD_PLAYERS("cmd.reload.players"),
    CMD_RENAME_HELP("cmd.rename.help"),
    CMD_RENAME_SUCCESS("cmd.rename.success"),
    CMD_RESOURCE_PACK_HELP("cmd.resourcePack.help"),
    CMD_SAVE_HELP("cmd.save.help"),
    CMD_SAVE_SUCCESS("cmd.save.success"),
    CMD_STATUS_HELP("cmd.status.help"),
    CMD_TEST_HELP("cmd.test.help"),
    CMD_UNINVITE_HELP("cmd.uninvite.help"),
    CMD_UNINVITE_SUCCESS("cmd.uninvite.success"),
    DAY_OF_WEEK_0("dayOfWeek.0"),
    DAY_OF_WEEK_1("dayOfWeek.1"),
    DAY_OF_WEEK_2("dayOfWeek.2"),
    DAY_OF_WEEK_3("dayOfWeek.3"),
    DAY_OF_WEEK_4("dayOfWeek.4"),
    DAY_OF_WEEK_5("dayOfWeek.5"),
    DAY_OF_WEEK_6("dayOfWeek.6"),
    ERROR_BED("error.bed"),
    ERROR_CHEST_IS_OPENED("error.chestIsOpened"),
    ERROR_CMD("error.cmd"),
    ERROR_DISPENSER("error.dispenser"),
    ERROR_DROP("error.drop"),
    ERROR_ENDERCHEST("error.enderchest"),
    ERROR_GROUP_IS_PLAYING("error.groupIsPlaying"),
    ERROR_IN_GROUP("error.inGroup"),
    ERROR_JOIN_GROUP("error.joinGroup"),
    ERROR_LEAVE_DUNGEON("error.leaveDungeon"),
    ERROR_LEAVE_GAME("error.leaveGame"),
    ERROR_LEAVE_GROUP("error.leaveGroup"),
    ERROR_MSG_ID_NOT_EXIST("error.msgIdDoesNotExist"),
    ERROR_MSG_FORMAT("error.msgFormat"),
    ERROR_MSG_NO_INT("error.msgNoInt"),
    ERROR_NAME_IN_USE("error.nameInUse"),
    ERROR_NAME_TOO_LONG("error.nameTooLong"),
    ERROR_NO_GAME("error.noGame"),
    ERROR_NO_ITEM_IN_MAIN_HAND("error.noItemInMainHand"),
    ERROR_NO_LEAVE_IN_TUTORIAL("error.noLeaveInTutorial"),
    ERROR_NO_PERMISSIONS("error.noPermissions"),
    ERROR_NO_PROTECTED_BLOCK("error.noProtectedBlock"),
    ERROR_NO_READY_SIGN("error.noReadySign"),
    ERROR_NO_REWARDS_TIME("error.noRewardsTime"),
    ERROR_NO_SUCH_ANNOUNCER("error.noSuchAnnouncer"),
    ERROR_NO_SUCH_DUNGEON("error.noSuchDungeon"),
    ERROR_NO_SUCH_GROUP("error.noSuchGroup"),
    ERROR_NO_SUCH_MAP("error.noSuchMap"),
    ERROR_NO_SUCH_PLAYER("error.noSuchPlayer"),
    ERROR_NO_SUCH_RESOURCE_PACK("error.noSuchResourcePack"),
    ERROR_NO_SUCH_SHOP("error.noSuchShop"),
    ERROR_NOT_IN_DUNGEON("error.notInDungeon"),
    ERROR_NOT_IN_GAME("error.notInGame"),
    ERROR_NOT_IN_GROUP("error.notInGroup"),
    ERROR_NOT_INVITED("error.notInvited"),
    ERROR_NOT_LEADER("error.notLeader"),
    ERROR_NOT_SAVED("error.notSaved"),
    ERROR_BLOCK_OWN_TEAM("error.blockOwnTeam"),
    ERROR_READY("error.ready"),
    ERROR_REQUIREMENTS("error.requirements"),
    ERROR_SELF_NOT_IN_GROUP("error.selfNotInGroup"),
    ERROR_SIGN_WRONG_FORMAT("error.signWrongFormat"),
    ERROR_TOO_MANY_INSTANCES("error.tooManyInstances"),
    ERROR_TOO_MANY_TUTORIALS("error.tooManyTutorials"),
    ERROR_TUTORIAL_DOES_NOT_EXIST("error.tutorialDoesNotExist"),
    GROUP_BED_DESTROYED("group.bedDestroyed"),
    GROUP_CONGRATS("group.congrats"),
    GROUP_CONGRATS_SUB("group.congratsSub"),
    GROUP_CREATED("group.created"),
    GROUP_DEATH("group.death"),
    GROUP_DEATH_KICK("group.deathKick"),
    GROUP_DEFEATED("group.defeated"),
    GROUP_DISBANDED("group.disbanded"),
    GROUP_FLAG_CAPTURED("group.flagCaptured"),
    GROUP_FLAG_LOST("group.flagLost"),
    GROUP_FLAG_STEALING("group.flagStealing"),
    GROUP_INVITED_PLAYER("group.invitedPlayer"),
    GROUP_JOINED_GAME("group.joinedGame"),
    GROUP_KILLED("group.killed"),
    GROUP_KILLED_KICK("group.killedKick"),
    GROUP_LIVES_ADDED("group.livesAdded"),
    GROUP_LIVES_REMOVED("group.livesRemoved"),
    GROUP_KICKED_PLAYER("group.kickedPlayer"),
    GROUP_PLAYER_JOINED("group.playerJoined"),
    GROUP_REWARD_CHEST("group.rewardChest"),
    GROUP_UNINVITED_PLAYER("group.uninvitedPlayer"),
    GROUP_WAVE_FINISHED("group.waveFinished"),
    PLAYER_BLOCK_INFO("player.blockInfo"),
    PLAYER_CHECKPOINT_REACHED("player.checkpointReached"),
    PLAYER_DEATH("player.death"),
    PLAYER_DEATH_KICK("player.deathKick"),
    PLAYER_FINISHED_DUNGEON("player.finishedDungeon"),
    PLAYER_FINISHED_FLOOR("player.finished_Floor"),
    PLAYER_INVITED("player.invited"),
    PLAYER_UNINVITED("player.uninvited"),
    PLAYER_JOIN_GROUP("player.joinGroup"),
    PLAYER_KICKED("player.kicked"),
    PLAYER_KILLED("player.killed"),
    PLAYER_KILLED_KICK("player.killedKick"),
    PLAYER_LEAVE_GROUP("player.leaveGroup"),
    PLAYER_LEFT_GROUP("player.leftGroup"),
    PLAYER_LIVES_ADDED("player.livesAdded"),
    PLAYER_LIVES_REMOVED("player.livesRemoved"),
    PLAYER_LOOT_ADDED("player.lootAdded"),
    PLAYER_NEW_LEADER("player.newLeader"),
    PLAYER_OFFLINE("player.offline"),
    PLAYER_OFFLINE_NEVER("player.offlineNever"),
    PLAYER_PORTAL_ABORT("player.portal.abort"),
    PLAYER_PORTAL_INTRODUCTION("player.portal.introduction"),
    PLAYER_PORTAL_CREATED("player.portal.created"),
    PLAYER_PORTAL_PROGRESS("player.portal.progress"),
    PLAYER_PORTAL_ROTATE("player.portal.rotate"),
    PLAYER_PROTECTED_BLOCK_DELETED("player.protectedBlockDeleted"),
    PLAYER_READY("player.ready"),
    PLAYER_SIGN_CREATED("player.signCreated"),
    PLAYER_SIGN_COPIED("player.signCopied"),
    PLAYER_TIME_LEFT("player.timeLeft"),
    PLAYER_TIME_KICK("player.timeKick"),
    PLAYER_TREASURES("player.treasures"),
    PLAYER_UNLIMITED_LIVES("player.unlimitedLives"),
    PLAYER_WAIT_FOR_OTHER_PLAYERS("player.waitForOtherPlayers"),
    REQUIREMENT_FEE("requirement.fee"),
    REQUIREMENT_FEE_ITEMS("requirement.feeItems"),
    REQUIREMENT_FEE_LEVEL("requirement.feeLevel"),
    REQUIREMENT_FEE_MONEY("requirement.feeMoney"),
    REQUIREMENT_FINISHED_DUNGEONS_AND("requirement.finishedDungeons.and"),
    REQUIREMENT_FINISHED_DUNGEONS_NAME("requirement.finishedDungeons.name"),
    REQUIREMENT_FINISHED_DUNGEONS_OR("requirement.finishedDungeons.or"),
    REQUIREMENT_FINISHED_DUNGEONS_WITHIN_TIME("requirement.finishedDungeons.withinTime"),
    REQUIREMENT_FORBIDDEN_ITEMS("requirement.forbiddenItems"),
    REQUIREMENT_GROUP_SIZE("requirement.groupSize"),
    REQUIREMENT_KEY_ITEMS("requirement.keyItems"),
    REQUIREMENT_PERMISSION("requirement.permission"),
    REQUIREMENT_TIME_SINCE_NEVER("requirement.timeSince.never"),
    REQUIREMENT_TIME_SINCE_FINISH("requirement.timeSince.finish"),
    REQUIREMENT_TIME_SINCE_START("requirement.timeSince.start"),
    REQUIREMENT_TIMEFRAME("requirement.timeframe"),
    REWARD_GENERAL("reward.general"),
    SIGN_END("sign.end"),
    SIGN_FLOOR_1("sign.floor.1"),
    SIGN_FLOOR_2("sign.floor.2"),
    SIGN_GLOBAL_FULL("sign.global.full"),
    SIGN_GLOBAL_IS_PLAYING("sign.global.isPlaying"),
    SIGN_GLOBAL_JOIN_GAME("sign.global.joinGame"),
    SIGN_GLOBAL_JOIN_GROUP("sign.global.joinGroup"),
    SIGN_GLOBAL_NEW_GAME("sign.global.newGame"),
    SIGN_GLOBAL_NEW_GROUP("sign.global.newGroup"),
    SIGN_LEAVE("sign.leave"),
    SIGN_READY("sign.ready"),
    SIGN_RESOURCE_PACK("sign.resourcePack"),
    SIGN_WAVE_1("sign.wave.1"),
    SIGN_WAVE_2("sign.wave.2");

    private String path;

    DMessage(String path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public MessageHandler getMessageHandler() {
        return DungeonsXL.getInstance().getMessageHandler();
    }

    @Override
    public void debug() {
        MessageUtil.log(DungeonsXL.getInstance(), getMessage());
    }

}
