/*
 * Copyright (C) 2012-2016 Frank Baumann
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

import io.github.dre2n.dungeonsxl.player.DPermissions;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class PermissionRequirement extends Requirement {

    private RequirementType type = RequirementTypeDefault.PERMISSION;

    private List<String> permissions = new ArrayList<>();

    /* Getters and setters */
    /**
     * @return the permission the player must have to play the dungeon
     */
    public List<String> getPermissions() {
        return permissions;
    }

    /**
     * @param permissions
     * the permissions to set
     */
    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    @Override
    public RequirementType getType() {
        return type;
    }

    /* Actions */
    @Override
    public boolean check(Player player) {
        for (String permission : permissions) {
            if (!DPermissions.hasPermission(player, permission)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void demand(Player player) {
    }

}
