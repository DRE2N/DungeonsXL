/*
 * Copyright (C) 2012-2018 Frank Baumann
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
package io.github.dre2n.dungeonsxl.requirement;

import io.github.dre2n.commons.chat.MessageUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.DMessage;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class FeeMoneyRequirement extends Requirement {

    DungeonsXL plugin = DungeonsXL.getInstance();

    private RequirementType type = RequirementTypeDefault.FEE_MONEY;

    private double fee;

    /* Getters and setters */
    /**
     * @return the fee
     */
    public double getFee() {
        return fee;
    }

    /**
     * @param fee
     * the fee to set
     */
    public void setFee(double fee) {
        this.fee = fee;
    }

    @Override
    public RequirementType getType() {
        return type;
    }

    /* Actions */
    @Override
    public void setup(ConfigurationSection config) {
        fee = config.getDouble("feeMoney");
    }

    @Override
    public boolean check(Player player) {
        if (plugin.getEconomyProvider() == null) {
            return true;
        }

        return plugin.getEconomyProvider().getBalance(player) >= fee;
    }

    @Override
    public void demand(Player player) {
        if (plugin.getEconomyProvider() == null) {
            return;
        }

        plugin.getEconomyProvider().withdrawPlayer(player, fee);
        MessageUtil.sendMessage(player, DMessage.REQUIREMENT_FEE.getMessage(plugin.getEconomyProvider().format(fee)));
    }

}
