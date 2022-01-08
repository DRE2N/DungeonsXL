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
package de.erethon.dungeonsxl;

import de.erethon.dungeonsxl.api.DungeonModule;
import de.erethon.dungeonsxl.api.Requirement;
import de.erethon.dungeonsxl.api.Reward;
import de.erethon.dungeonsxl.api.dungeon.GameRule;
import de.erethon.dungeonsxl.api.sign.DungeonSign;
import de.erethon.dungeonsxl.requirement.*;
import de.erethon.dungeonsxl.reward.*;
import de.erethon.dungeonsxl.sign.button.*;
import de.erethon.dungeonsxl.sign.passive.*;
import de.erethon.dungeonsxl.sign.rocker.*;
import de.erethon.dungeonsxl.sign.windup.*;
import de.erethon.dungeonsxl.util.commons.misc.Registry;

/**
 * @author Daniel Saukel
 */
public class DXLModule implements DungeonModule {

    @Override
    public void initRequirements(Registry<String, Class<? extends Requirement>> requirementRegistry) {
        requirementRegistry.add("feeLevel", FeeLevelRequirement.class);
        requirementRegistry.add("feeMoney", FeeMoneyRequirement.class);
        requirementRegistry.add("finishedDungeons", FinishedDungeonsRequirement.class);
        requirementRegistry.add("forbiddenItems", ForbiddenItemsRequirement.class);
        requirementRegistry.add("groupSize", GroupSizeRequirement.class);
        requirementRegistry.add("keyItems", KeyItemsRequirement.class);
        requirementRegistry.add("permission", PermissionRequirement.class);
        requirementRegistry.add("timeSinceFinish", TimeSinceFinishRequirement.class);
        requirementRegistry.add("timeSinceStart", TimeSinceStartRequirement.class);
        requirementRegistry.add("timeframe", TimeframeRequirement.class);
    }

    @Override
    public void initRewards(Registry<String, Class<? extends Reward>> rewardRegistry) {
        rewardRegistry.add("item", ItemReward.class);
        rewardRegistry.add("money", MoneyReward.class);
        rewardRegistry.add("level", LevelReward.class);
    }

    @Override
    public void initSigns(Registry<String, Class<? extends DungeonSign>> signRegistry) {
        signRegistry.add("ACTIONBAR", ActionBarSign.class);
        signRegistry.add("BED", BedSign.class);
        signRegistry.add("BLOCK", BlockSign.class);
        signRegistry.add("BOSSSHOP", BossShopSign.class);
        signRegistry.add("CHECKPOINT", CheckpointSign.class);
        signRegistry.add("CLASSES", ClassesSign.class);
        signRegistry.add("CMD", CommandSign.class);
        signRegistry.add("DROP", DropSign.class);
        signRegistry.add("DUNGEONCHEST", DungeonChestSign.class);
        signRegistry.add("END", EndSign.class);
        signRegistry.add("FLAG", FlagSign.class);
        signRegistry.add("HOLOGRAM", HologramSign.class);
        signRegistry.add("INTERACT", InteractSign.class);
        signRegistry.add("LEAVE", LeaveSign.class);
        signRegistry.add("LIVES", LivesModifierSign.class);
        signRegistry.add("LOBBY", LobbySign.class);
        signRegistry.add("MOB", MobSign.class);
        signRegistry.add("MSG", ChatMessageSign.class);
        signRegistry.add("NOTE", NoteSign.class);
        signRegistry.add("DOOR", OpenDoorSign.class);
        signRegistry.add("PLACE", PlaceSign.class);
        signRegistry.add("PROTECTION", ProtectionSign.class);
        signRegistry.add("READY", ReadySign.class);
        signRegistry.add("REDSTONE", RedstoneSign.class);
        signRegistry.add("RESOURCEPACK", ResourcePackSign.class);
        signRegistry.add("REWARDCHEST", RewardChestSign.class);
        signRegistry.add("SCRIPT", ScriptSign.class);
        signRegistry.add("SOUNDMSG", SoundMessageSign.class);
        signRegistry.add("START", StartSign.class);
        signRegistry.add("TELEPORT", TeleportSign.class);
        signRegistry.add("TITLE", TitleSign.class);
        signRegistry.add("TRIGGER", TriggerSign.class);
        signRegistry.add("WAVE", WaveSign.class);
    }

    @Override
    public void initGameRules(Registry<String, GameRule> gameRuleRegistry) {
        for (GameRule rule : GameRule.VALUES) {
            gameRuleRegistry.add(rule.getKey(), rule);
        }
    }

}
