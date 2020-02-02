/*
 * Copyright (C) 2012-2020 Frank Baumann
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
package de.erethon.dungeonsxl.sign;

import de.erethon.dungeonsxl.player.DPermission;
import de.erethon.dungeonsxl.sign.lobby.ClassesSign;
import de.erethon.dungeonsxl.sign.lobby.LobbySign;
import de.erethon.dungeonsxl.sign.lobby.ReadySign;
import de.erethon.dungeonsxl.sign.lobby.StartSign;
import de.erethon.dungeonsxl.sign.message.ActionBarSign;
import de.erethon.dungeonsxl.sign.message.HologramSign;
import de.erethon.dungeonsxl.sign.message.MessageSign;
import de.erethon.dungeonsxl.sign.message.SoundMessageSign;
import de.erethon.dungeonsxl.sign.message.TitleSign;

/**
 * Default implementation of DSignType.
 *
 * @author Daniel Saukel
 */
public enum DSignTypeDefault implements DSignType {

    ACTION_BAR("ActionBar", "actionbar", true, false, ActionBarSign.class),
    BED("Bed", "bed", false, false, BedSign.class),
    BLOCK("Block", "block", false, true, BlockSign.class),
    BOSS_SHOP("BossShop", "bossshop", false, true, BossShopSign.class),
    CHECKPOINT("Checkpoint", "checkpoint", false, false, CheckpointSign.class),
    @Deprecated
    CHEST("Chest", "chest", false, false, RewardChestSign.class),
    CLASSES("Classes", "classes", true, true, ClassesSign.class),
    COMMAND("CMD", "cmd", false, false, CommandSign.class),
    DROP("Drop", "drop", false, false, DropSign.class),
    DUNGEON_CHEST("DungeonChest", "dungeonchest", true, false, DungeonChestSign.class),
    END("End", "end", false, true, EndSign.class),
    @Deprecated
    EXTERNAL_MOB("ExternalMob", "mob", false, false, MobSign.class),
    FLAG("Flag", "flag", false, false, FlagSign.class),
    @Deprecated
    FLOOR("Floor", "end", false, true, EndSign.class),
    HOLOGRAM("Hologram", "hologram", true, false, HologramSign.class),
    INTERACT("Interact", "interact", true, true, InteractSign.class),
    LEAVE("Leave", "leave", true, true, LeaveSign.class),
    LIVES_MODIFIER("Lives", "lives", false, false, LivesModifierSign.class),
    LOBBY("Lobby", "lobby", true, false, LobbySign.class),
    MOB("Mob", "mob", false, false, MobSign.class),
    MESSAGE("MSG", "msg", false, false, MessageSign.class),
    NOTE("Note", "note", false, false, NoteSign.class),
    OPEN_DOOR("Door", "door", false, false, OpenDoorSign.class),
    PLACE("Place", "place", false, false, PlaceSign.class),
    PROTECTION("Protection", "protection", false, false, ProtectionSign.class),
    READY("Ready", "ready", true, true, ReadySign.class),
    REDSTONE("Redstone", "redstone", false, false, RedstoneSign.class),
    RESOURCE_PACK("ResourcePack", "resourcepack", true, true, ResourcePackSign.class),
    REWARD_CHEST("RewardChest", "rewardchest", false, false, RewardChestSign.class),
    SCRIPT("Script", "script", false, false, ScriptSign.class),
    SOUND_MESSAGE("SoundMSG", "soundmsg", false, false, SoundMessageSign.class),
    START("Start", "start", true, false, StartSign.class),
    TELEPORT("Teleport", "teleport", false, false, TeleportSign.class),
    TITLE("Title", "title", true, false, TitleSign.class),
    TRIGGER("Trigger", "trigger", true, false, TriggerSign.class),
    WAVE("Wave", "wave", false, false, WaveSign.class);

    private String name;
    private String buildPermission;
    private boolean onDungeonInit;
    private boolean isProtected;
    private Class<? extends DSign> handler;

    DSignTypeDefault(String name, String buildPermission, boolean onDungeonInit, boolean isProtected, Class<? extends DSign> handler) {
        this.name = name;
        this.buildPermission = buildPermission;
        this.onDungeonInit = onDungeonInit;
        this.isProtected = isProtected;
        this.handler = handler;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getBuildPermission() {
        return DPermission.SIGN.getNode() + "." + buildPermission;
    }

    @Override
    public boolean isOnDungeonInit() {
        return onDungeonInit;
    }

    @Override
    public boolean isProtected() {
        return isProtected;
    }

    public boolean isLegacy() {
        switch (this) {
            case CHEST:
            case EXTERNAL_MOB:
            case FLOOR:
                return true;
            default:
                return false;
        }
    }

    @Override
    public Class<? extends DSign> getHandler() {
        return handler;
    }

}
