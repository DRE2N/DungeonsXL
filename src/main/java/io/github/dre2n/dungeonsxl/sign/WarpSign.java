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
package io.github.dre2n.dungeonsxl.sign;

import io.github.dre2n.dungeonsxl.trigger.InteractTrigger;
import io.github.dre2n.dungeonsxl.world.GameWorld;
import io.github.dre2n.itemsxl.util.commons.util.playerutil.PlayerUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class WarpSign extends DSign {

    private DSignType type = DSignTypeDefault.WARP;

    private String destination;

    public WarpSign(Sign sign, String[] lines, GameWorld gameWorld) {
        super(sign, lines, gameWorld);
    }

    /**
     * @return the destination sign
     */
    public DestinationSign getDestinationSign() {
        for (DSign dSign : getGameWorld().getDSigns()) {
            if (dSign.getType() == DSignTypeDefault.DESTINATION) {
                if (((DestinationSign) dSign).getId().equals(destination)) {
                    return (DestinationSign) dSign;
                }
            }
        }

        return null;
    }

    /**
     * @return the destination
     */
    public Location getDestination() {
        if (getDestinationSign() != null) {
            return getDestinationSign().getSign().getLocation();

        } else {
            return null;
        }
    }

    /**
     * @param id
     * the ID of the destination sign
     */
    public void setDestination(String id) {
        destination = id;
    }

    @Override
    public boolean check() {
        return true;
    }

    @Override
    public void onInit() {
        destination = lines[1];

        if (!getTriggers().isEmpty()) {
            getSign().getBlock().setType(Material.AIR);
            return;
        }

        InteractTrigger trigger = InteractTrigger.getOrCreate(0, getSign().getBlock(), getGameWorld());
        if (trigger != null) {
            trigger.addListener(this);
            addTrigger(trigger);
        }

        getSign().setLine(0, ChatColor.DARK_BLUE + "############");
        getSign().setLine(1, ChatColor.DARK_GREEN + "Warp");
        getSign().setLine(2, "");
        getSign().setLine(3, ChatColor.DARK_BLUE + "############");
        getSign().update();
    }

    @Override
    public boolean onPlayerTrigger(Player player) {
        PlayerUtil.secureTeleport(player, getDestination());
        return true;
    }

    @Override
    public DSignType getType() {
        return type;
    }

}
