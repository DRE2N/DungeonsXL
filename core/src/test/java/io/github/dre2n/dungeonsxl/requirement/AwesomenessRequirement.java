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
package io.github.dre2n.dungeonsxl.requirement;

import io.github.dre2n.commons.util.messageutil.MessageUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class AwesomenessRequirement extends Requirement {

    private RequirementType type = RequirementTypeCustom.AWESOMENESS;

    private int level;

    /* Getters and setters */
    /**
     * @return the awesomeness level
     */
    public int getAwesomenessLevel() {
        return level;
    }

    /**
     * @param level
     * the awesomeness level to set
     */
    public void setAwesomenessLevel(int level) {
        this.level = level;
    }

    @Override
    public RequirementType getType() {
        return type;
    }

    /* Actions */
    @Override
    public void setup(ConfigurationSection config) {
        this.level = config.getInt("awesomeness");
    }

    @Override
    public boolean check(Player player) {
        // Code that checks if the player has the requirement
        MessageUtil.sendTitleMessage(player, "&6Are you AWESOME?");
        return true;
    }

    @Override
    public void demand(Player player) {
        // Code that removes the requirement if it is a fee
    }

}
