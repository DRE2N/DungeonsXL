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
package io.github.dre2n.dungeonsxl.settings;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Daniel Saukel
 */
public interface Button {

    /* Getters and setters */
    /**
     * @return the permissions that are required to see the button
     */
    public String getRequiredPermission();

    /**
     * @return the input type
     */
    public ButtonType getType();

    /* Actions */
    /**
     * @return the button as an ItemStack
     */
    public ItemStack toItemStack(Player player);

    /**
     * The on click action
     */
    public void onClick(Player player);

}
