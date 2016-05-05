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
package io.github.dre2n.dungeonsxl.reward;

import io.github.dre2n.commons.util.messageutil.MessageUtil;
import io.github.dre2n.dungeonsxl.config.DMessages;
import java.util.Arrays;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author Daniel Saukel
 */
public class HighwayToHellReward extends Reward {

    private RewardType type = RewardTypeCustom.HIGHWAY_TO_HELL;

    public static final ItemStack RECORD;

    static {
        RECORD = new ItemStack(Material.GOLD_RECORD);
        ItemMeta meta = RECORD.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_RED + "Highway To Hell");
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "1. Highway To Hell 3:28",
                ChatColor.GOLD + "2. Girls Got Rhythm 3:24",
                ChatColor.GOLD + "3. Walk All Over You 5:09",
                ChatColor.GOLD + "4. Touch Too Much 4:26",
                ChatColor.GOLD + "5. Beating Around The Bush 3:56",
                ChatColor.GOLD + "6. Shot Down In Flames 3:23",
                ChatColor.GOLD + "7. Get It Hot 2:34",
                ChatColor.GOLD + "8. If You Want Blood (You've Got It) 4:37",
                ChatColor.GOLD + "9. Love Hungry Man 4:17",
                ChatColor.GOLD + "10. Night Prowler 6:16",
                ChatColor.DARK_RED + "All titles A. Young - M. Young - B. Scott"
        ));
        RECORD.setItemMeta(meta);
    }

    @Override
    public void giveTo(Player player) {
        // This is called when all rewards are given to the players. Each group member gets one item.
        player.getInventory().addItem(RECORD);
        MessageUtil.sendMessage(player, plugin.getMessageConfig().getMessage(DMessages.REWARD_GENERAL, "1 Highway To Hell album"));
    }

    @Override
    public RewardType getType() {
        return type;
    }

}
