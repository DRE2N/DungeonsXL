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
import de.erethon.dungeonsxl.api.player.PlayerGroup;
import de.erethon.dungeonsxl.config.DMessage;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class GroupSizeRequirement implements Requirement {

    private DungeonsAPI api;

    private int minimum;
    private int maximum;

    public GroupSizeRequirement(DungeonsAPI api) {
        this.api = api;
    }

    /**
     * @return the group minimum
     */
    public int getMinimum() {
        return minimum;
    }

    /**
     * @param minimum the minimal group size to set
     */
    public void setMinimum(int minimum) {
        this.minimum = minimum;
    }

    /**
     * @return the group size maximum
     */
    public int getMaximum() {
        return maximum;
    }

    /**
     * @param maximum the maximal group size to set
     */
    public void setMaximum(int maximum) {
        this.maximum = maximum;
    }

    /* Actions */
    @Override
    public void setup(ConfigurationSection config) {
        minimum = config.getInt("groupSize.minimum");
        maximum = config.getInt("groupSize.maximum");
    }

    @Override
    public boolean check(Player player) {
        PlayerGroup group = api.getPlayerGroup(player);
        int size = group.getMembers().size();
        return size >= minimum && size <= maximum;
    }

    @Override
    public BaseComponent[] getCheckMessage(Player player) {
        int size = api.getPlayerGroup(player).getMembers().size();
        ChatColor color = size >= minimum && size <= maximum ? ChatColor.GREEN : ChatColor.DARK_RED;
        return new ComponentBuilder(DMessage.REQUIREMENT_GROUP_SIZE.getMessage() + ": ").color(ChatColor.GOLD)
                .append(String.valueOf(size)).color(color)
                .append("/" + minimum + "-" + maximum).color(ChatColor.WHITE)
                .create();
    }

    @Override
    public void demand(Player player) {
    }

    @Override
    public String toString() {
        return "GroupSizeRequirement{minimum=" + minimum + "; maximum=" + maximum + "}";
    }

}
