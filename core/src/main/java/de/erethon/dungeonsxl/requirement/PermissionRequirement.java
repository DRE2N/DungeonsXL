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
package de.erethon.dungeonsxl.requirement;

import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.Requirement;
import de.erethon.dungeonsxl.config.DMessage;
import java.util.ArrayList;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class PermissionRequirement implements Requirement {

    private List<String> permissions = new ArrayList<>();

    public PermissionRequirement(DungeonsAPI api) {
    }

    /* Getters and setters */
    /**
     * @return the permission the player must have to play the dungeon
     */
    public List<String> getPermissions() {
        return permissions;
    }

    /**
     * @param permissions the permissions to set
     */
    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    /* Actions */
    @Override
    public void setup(ConfigurationSection config) {
        permissions = config.getStringList("permission");
    }

    @Override
    public boolean check(Player player) {
        for (String permission : permissions) {
            if (!player.hasPermission(permission)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public BaseComponent[] getCheckMessage(Player player) {
        ComponentBuilder builder = new ComponentBuilder(DMessage.REQUIREMENT_PERMISSION.getMessage() + ": ").color(ChatColor.GOLD);
        boolean first = true;
        for (String node : permissions) {
            if (!first) {
                builder.append(", ").color(ChatColor.WHITE);
            } else {
                first = false;
            }
            builder.append(node).color(player.hasPermission(node) ? ChatColor.GREEN : ChatColor.DARK_RED);
        }
        return builder.create();
    }

    @Override
    public void demand(Player player) {
    }

    @Override
    public String toString() {
        return "PermissionRequirement{permissions=" + permissions + "}";
    }

}
