/*
 * Copyright (C) 2012-2023 Frank Baumann
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

import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.Requirement;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.bedrock.chat.MessageUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class FeeMoneyRequirement implements Requirement {

    private Economy econ;

    private double fee;

    public FeeMoneyRequirement(DungeonsAPI api) {
        econ = ((DungeonsXL) api).getEconomyProvider();
    }

    /* Getters and setters */
    /**
     * @return the fee
     */
    public double getFee() {
        return fee;
    }

    /**
     * @param fee the fee to set
     */
    public void setFee(double fee) {
        this.fee = fee;
    }

    /* Actions */
    @Override
    public void setup(ConfigurationSection config) {
        fee = config.getDouble("feeMoney");
    }

    @Override
    public boolean check(Player player) {
        if (econ == null) {
            return true;
        }

        return econ.getBalance(player) >= fee;
    }

    @Override
    public BaseComponent[] getCheckMessage(Player player) {
        double money = econ.getBalance(player);
        ChatColor color = money >= fee ? ChatColor.GREEN : ChatColor.DARK_RED;
        return new ComponentBuilder(DMessage.REQUIREMENT_FEE_MONEY.getMessage() + ": ").color(ChatColor.GOLD)
                .append(String.valueOf(money)).color(color)
                .append("/" + fee).color(ChatColor.WHITE)
                .create();
    }

    @Override
    public void demand(Player player) {
        if (econ == null) {
            return;
        }

        econ.withdrawPlayer(player, fee);
        MessageUtil.sendMessage(player, DMessage.REQUIREMENT_FEE.getMessage(econ.format(fee)));
    }

    @Override
    public String toString() {
        return "FeeMoneyRequirement{fee=" + fee + "}";
    }

}
