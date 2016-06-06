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

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.DMessages;
import io.github.dre2n.dungeonsxl.player.DGlobalPlayer;
import java.util.Arrays;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author Daniel Saukel
 */
public class ToggleAnnouncementsButton implements Button {

    DungeonsXL plugin = DungeonsXL.getInstance();

    private String permission = "";
    private ButtonType type = ButtonType.SWITCH;

    /* Getters and setters */
    @Override
    public String getRequiredPermission() {
        return permission;
    }

    @Override
    public ButtonType getType() {
        return type;
    }

    /* Actions */
    @Override
    public ItemStack toItemStack(Player player) {
        DGlobalPlayer dPlayer = plugin.getDPlayers().getByPlayer(player);

        ItemStack button = new ItemStack(Material.INK_SACK, 1, (short) (dPlayer.isAnnouncerEnabled() ? 10 : 8));
        ItemMeta meta = button.getItemMeta();
        meta.setDisplayName((dPlayer.isAnnouncerEnabled() ? ChatColor.GREEN : ChatColor.DARK_RED) + "Toggle Announcements");
        meta.setLore(Arrays.asList(DMessages.SETTINGS_ANNOUNCEMENTS_1.getMessage(), DMessages.SETTINGS_ANNOUNCEMENTS_2.getMessage()));
        button.setItemMeta(meta);

        return button;
    }

    @Override
    public void onClick(Player player) {
        DGlobalPlayer dPlayer = plugin.getDPlayers().getByPlayer(player);
        dPlayer.setAnnouncerEnabled(!dPlayer.isAnnouncerEnabled());
    }

}
