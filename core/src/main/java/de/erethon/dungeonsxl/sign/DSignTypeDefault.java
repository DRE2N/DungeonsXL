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

import de.erethon.dungeonsxl.api.sign.DungeonSign;
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
 * @author Daniel Saukel
 */
public enum DSignTypeDefault {

    ACTION_BAR("ActionBar", ActionBarSign.class),
    BED("Bed", BedSign.class),
    BLOCK("Block", BlockSign.class),
    BOSS_SHOP("BossShop", BossShopSign.class),
    CHECKPOINT("Checkpoint", CheckpointSign.class),
    @Deprecated
    CHEST("Chest", RewardChestSign.class),
    CLASSES("Classes", ClassesSign.class),
    COMMAND("CMD", CommandSign.class),
    DROP("Drop", DropSign.class),
    DUNGEON_CHEST("DungeonChest", DungeonChestSign.class),
    END("End", EndSign.class),
    @Deprecated
    EXTERNAL_MOB("ExternalMob", MobSign.class),
    FLAG("Flag", FlagSign.class),
    @Deprecated
    FLOOR("Floor", EndSign.class),
    HOLOGRAM("Hologram", HologramSign.class),
    INTERACT("Interact", InteractSign.class),
    LEAVE("Leave", LeaveSign.class),
    LIVES_MODIFIER("Lives", LivesModifierSign.class),
    LOBBY("Lobby", LobbySign.class),
    MOB("Mob", MobSign.class),
    MESSAGE("MSG", MessageSign.class),
    NOTE("Note", NoteSign.class),
    OPEN_DOOR("Door", OpenDoorSign.class),
    PLACE("Place", PlaceSign.class),
    PROTECTION("Protection", ProtectionSign.class),
    READY("Ready", ReadySign.class),
    REDSTONE("Redstone", RedstoneSign.class),
    RESOURCE_PACK("ResourcePack", ResourcePackSign.class),
    REWARD_CHEST("RewardChest", RewardChestSign.class),
    SCRIPT("Script", ScriptSign.class),
    SOUND_MESSAGE("SoundMSG", SoundMessageSign.class),
    START("Start", StartSign.class),
    TELEPORT("Teleport", TeleportSign.class),
    TITLE("Title", TitleSign.class),
    TRIGGER("Trigger", TriggerSign.class),
    WAVE("Wave", WaveSign.class);

    private String name;
    private Class<? extends DungeonSign> handler;

    DSignTypeDefault(String name, Class<? extends DungeonSign> handler) {
        this.name = name;
        this.handler = handler;
    }

    public String getName() {
        return name;
    }

    public Class<? extends DungeonSign> getHandler() {
        return handler;
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

}
