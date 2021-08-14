/*
 * Copyright (C) 2012-2021 Frank Baumann
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
package de.erethon.dungeonsxl.requirement;

import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.Requirement;
import de.erethon.dungeonsxl.api.dungeon.Game;
import de.erethon.dungeonsxl.api.dungeon.GameRule;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.player.DGamePlayer;
import de.erethon.dungeonsxl.player.DGlobalPlayer;
import de.erethon.dungeonsxl.player.DPlayerData;
import de.erethon.dungeonsxl.util.commons.chat.MessageUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class FeeLevelRequirement implements Requirement {

    private DungeonsAPI api;

    private int fee;

    public FeeLevelRequirement(DungeonsAPI api) {
        this.api = api;
    }

    /* Getters and setters */
    /**
     * @return the fee
     */
    public int getFee() {
        return fee;
    }

    /**
     * @param fee the fee to set
     */
    public void setFee(int fee) {
        this.fee = fee;
    }

    /* Actions */
    @Override
    public void setup(ConfigurationSection config) {
        fee = config.getInt("feeLevel");
    }

    @Override
    public boolean check(Player player) {
        return getRelevantLevel(player) >= fee;
    }

    @Override
    public BaseComponent[] getCheckMessage(Player player) {
        int level = getRelevantLevel(player);
        ChatColor color = level >= fee ? ChatColor.GREEN : ChatColor.DARK_RED;
        return new ComponentBuilder(DMessage.REQUIREMENT_FEE_LEVEL.getMessage() + ": ").color(ChatColor.GOLD)
                .append(String.valueOf(level)).color(color)
                .append("/" + fee).color(ChatColor.WHITE)
                .create();
    }

    private int getRelevantLevel(Player player) {
        if (isKeepInventory(player)) {
            return player.getLevel();
        }

        DGlobalPlayer dPlayer = (DGlobalPlayer) api.getPlayerCache().get(player);
        return dPlayer.getData().getOldLevel();
    }

    @Override
    public void demand(Player player) {
        DGamePlayer dPlayer = (DGamePlayer) api.getPlayerCache().getGamePlayer(player);
        if (dPlayer == null) {
            return;
        }

        DPlayerData data = dPlayer.getData();
        data.setOldLevel(data.getOldLevel() - fee);
        data.getConfig().set(DPlayerData.PREFIX_STATE_PERSISTENCE + "oldLvl", data.getOldLevel());
        data.save();
        if (isKeepInventory(player)) {
            player.setLevel(player.getLevel() - fee);
        }

        MessageUtil.sendMessage(player, DMessage.REQUIREMENT_FEE.getMessage(fee + " levels"));
    }

    private boolean isKeepInventory(Player player) {
        Game game = api.getGame(player);
        if (game != null) {
            return game.getRules().getState(GameRule.KEEP_INVENTORY_ON_ENTER);
        }
        return true;
    }

    @Override
    public String toString() {
        return "FeeLevelRequirement{fee=" + fee + "}";
    }

}
