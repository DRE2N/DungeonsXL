/*
 * Copyright (C) 2016 Daniel Saukel
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
package io.github.dre2n.dungeonsxl.listener;

import io.github.dre2n.commons.util.messageutil.MessageUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.event.dgroup.*;
import io.github.dre2n.dungeonsxl.reward.HighwayToHellReward;
import io.github.dre2n.dungeonsxl.reward.Reward;
import io.github.dre2n.dungeonsxl.reward.RewardTypeCustom;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author Daniel Saukel
 */
public class DGroupListener implements Listener {

    DungeonsXL plugin = DungeonsXL.getInstance();

    @EventHandler
    public void onCreate(DGroupCreateEvent event) {
        MessageUtil.log(plugin, "&b== " + event.getEventName() + "==");
        MessageUtil.log(plugin, "Cause: " + event.getCause());
        MessageUtil.log(plugin, "Creator: " + event.getCreator().getName());
    }

    @EventHandler
    public void onDisband(DGroupDisbandEvent event) {
        MessageUtil.log(plugin, "&b== " + event.getEventName() + "==");
        MessageUtil.log(plugin, "Cause: " + event.getCause());
        MessageUtil.log(plugin, "Disbander: " + event.getDisbander().getName());
    }

    @EventHandler
    public void onFinishDungeon(DGroupFinishDungeonEvent event) {
        MessageUtil.log(plugin, "&b== " + event.getEventName() + "==");

        MessageUtil.log("Giving one " + RewardTypeCustom.HIGHWAY_TO_HELL + " to all group members!");
        Reward reward = new HighwayToHellReward();
        for (Player Player : event.getDGroup().getPlayers()) {
            reward.giveTo(Player);
        }
    }

    @EventHandler
    public void onStartFloor(DGroupStartFloorEvent event) {
        MessageUtil.log(plugin, "&b== " + event.getEventName() + "==");
        MessageUtil.log(plugin, "GameWorld: " + event.getGameWorld().getMapName());
    }

    @EventHandler
    public void onFinishFloor(DGroupFinishFloorEvent event) {
        MessageUtil.log(plugin, "&b== " + event.getEventName() + "==");
        MessageUtil.log(plugin, "Finished: " + event.getFinished().getMapName());
        MessageUtil.log(plugin, "Next: " + event.getNext());
    }

    @EventHandler
    public void onReward(DGroupRewardEvent event) {
        MessageUtil.log(plugin, "&b== " + event.getEventName() + "==");
        MessageUtil.log(plugin, "Rewards: " + event.getRewards());
        MessageUtil.log(plugin, "Excluded players: " + event.getExcludedPlayers());
    }

}
