/*
 * Copyright (C) 2012-2017 Frank Baumann
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
package io.github.dre2n.dungeonsxl.command;

import io.github.dre2n.commons.command.BRCommand;
import io.github.dre2n.commons.util.messageutil.MessageUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.DMessages;
import io.github.dre2n.dungeonsxl.player.DGlobalPlayer;
import io.github.dre2n.dungeonsxl.player.DPermissions;
import io.github.dre2n.dungeonsxl.reward.DLootInventory;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Daniel Saukel
 */
public class RewardsCommand extends BRCommand {

    DungeonsXL plugin = DungeonsXL.getInstance();

    public RewardsCommand() {
        setCommand("rewards");
        setMinArgs(0);
        setMaxArgs(0);
        setHelp(DMessages.HELP_CMD_REWARDS.getMessage());
        setPermission(DPermissions.REWARDS.getNode());
        setPlayerCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        DGlobalPlayer dGlobalPlayer = plugin.getDPlayers().getByPlayer(player);

        if (!dGlobalPlayer.hasRewardItemsLeft()) {
            MessageUtil.sendMessage(player, DMessages.ERROR_NO_REWARDS_LEFT.getMessage());
            return;
        }

        List<ItemStack> rewardItems = dGlobalPlayer.getRewardItems();
        List<ItemStack> rewards = rewardItems.subList(0, rewardItems.size() > 54 ? 53 : rewardItems.size());
        new DLootInventory(player, rewards.toArray(new ItemStack[54]));
        rewardItems.removeAll(rewards);
        if (rewardItems.isEmpty()) {
            dGlobalPlayer.setRewardItems(null);
        }
    }

}
