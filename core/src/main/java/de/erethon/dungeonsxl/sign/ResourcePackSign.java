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
package de.erethon.dungeonsxl.sign;

import de.erethon.caliburn.item.VanillaItem;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.trigger.InteractTrigger;
import de.erethon.dungeonsxl.world.DGameWorld;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class ResourcePackSign extends DSign {

    private String resourcePack;

    public ResourcePackSign(DungeonsXL plugin, Sign sign, String[] lines, DGameWorld gameWorld) {
        super(plugin, sign, lines, gameWorld);
    }

    /* Getters and setters */
    @Override
    public DSignType getType() {
        return DSignTypeDefault.RESOURCE_PACK;
    }

    /**
     * @return the external mob
     */
    public String getResourcePack() {
        return resourcePack;
    }

    /**
     * @param resourcePack the resource pack to set
     */
    public void setExternalMob(String resourcePack) {
        this.resourcePack = resourcePack;
    }

    /* Actions */
    @Override
    public boolean check() {
        return plugin.getMainConfig().getResourcePacks().get(lines[1]) != null || lines[1].equalsIgnoreCase("reset");
    }

    @Override
    public void onInit() {
        Object url = null;
        if (lines[1].equalsIgnoreCase("reset")) {
            // Placeholder to reset to default
            url = "http://google.com";
        } else {
            url = plugin.getMainConfig().getResourcePacks().get(lines[1]);
        }

        if (url instanceof String) {
            resourcePack = (String) url;

        } else {
            markAsErroneous("Unknown resourcepack format");
            return;
        }

        if (!getTriggers().isEmpty()) {
            getSign().getBlock().setType(VanillaItem.AIR.getMaterial());
            return;
        }

        InteractTrigger trigger = InteractTrigger.getOrCreate(0, getSign().getBlock(), getGameWorld());
        if (trigger != null) {
            trigger.addListener(this);
            addTrigger(trigger);
        }

        String name = lines[1];
        getSign().setLine(0, ChatColor.DARK_BLUE + "############");
        getSign().setLine(1, DMessage.SIGN_RESOURCE_PACK.getMessage());
        getSign().setLine(2, ChatColor.DARK_GREEN + name);
        getSign().setLine(3, ChatColor.DARK_BLUE + "############");
        getSign().update();
    }

    @Override
    public boolean onPlayerTrigger(Player player) {
        player.setResourcePack(resourcePack);
        return true;
    }

}
