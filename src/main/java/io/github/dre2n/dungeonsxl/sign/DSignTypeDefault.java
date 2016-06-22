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
package io.github.dre2n.dungeonsxl.sign;

import io.github.dre2n.dungeonsxl.player.DPermissions;

/**
 * @author Daniel Saukel
 */
public enum DSignTypeDefault implements DSignType {

    BLOCK("Block", "block", false, BlockSign.class),
    CHECKPOINT("Checkpoint", "checkpoint", false, CheckpointSign.class),
    CHEST("Chest", "chest", false, ChestSign.class),
    CHUNK_UPDATER("ChunkUpdater", "chunkupdater", true, ChunkUpdaterSign.class),
    CLASSES("Classes", "classes", true, ClassesSign.class),
    COMMAND("CMD", "cmd", false, CommandSign.class),
    DROP("Drop", "drop", false, DropSign.class),
    END("End", "end", false, EndSign.class),
    EXTERNAL_MOB("ExternalMob", "mob", false, ExternalMobSign.class),
    FLOOR("Floor", "floor", false, FloorSign.class),
    HOLOGRAM("Hologram", "hologram", true, HologramSign.class),
    INTERACT("Interact", "interact", true, InteractSign.class),
    LEAVE("Leave", "leave", true, LeaveSign.class),
    LIVES_MODIFIER("Lives", "lives", false, LivesModifierSign.class),
    LOBBY("Lobby", "lobby", true, LobbySign.class),
    MOB("Mob", "mob", false, DMobSign.class),
    MESSAGE("MSG", "msg", false, MessageSign.class),
    @Deprecated
    MYTHIC_MOBS("MythicMobs", "mob", false, ExternalMobSign.class),
    OPEN_DOOR("Door", "door", false, OpenDoorSign.class),
    PLACE("Place", "place", false, PlaceSign.class),
    READY("Ready", "ready", true, ReadySign.class),
    REDSTONE("Redstone", "redstone", false, RedstoneSign.class),
    SCRIPT("Script", "script", false, ScriptSign.class),
    SOUND_MESSAGE("SoundMSG", "soundmsg", false, SoundMessageSign.class),
    START("Start", "start", true, StartSign.class),
    TELEPORT("Teleport", "teleport", false, TeleportSign.class),
    TRIGGER("Trigger", "trigger", true, TriggerSign.class),
    WAVE("Wave", "wave", false, WaveSign.class);

    private String name;
    private String buildPermission;
    private boolean onDungeonInit;
    private Class<? extends DSign> handler;

    DSignTypeDefault(String name, String buildPermission, boolean onDungeonInit, Class<? extends DSign> handler) {
        this.name = name;
        this.buildPermission = buildPermission;
        this.onDungeonInit = onDungeonInit;
        this.handler = handler;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getBuildPermission() {
        return DPermissions.SIGN.getNode() + "." + buildPermission;
    }

    @Override
    public boolean isOnDungeonInit() {
        return onDungeonInit;
    }

    @Override
    public Class<? extends DSign> getHandler() {
        return handler;
    }

}
