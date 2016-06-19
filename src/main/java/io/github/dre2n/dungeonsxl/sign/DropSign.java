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

import io.github.dre2n.caliburn.item.UniversalItem;
import io.github.dre2n.commons.util.NumberUtil;
import io.github.dre2n.dungeonsxl.world.GameWorld;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

/**
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class DropSign extends DSign {

    private DSignType type = DSignTypeDefault.DROP;

    private ItemStack item;

    public DropSign(Sign sign, String[] lines, GameWorld gameWorld) {
        super(sign, lines, gameWorld);
    }

    /* Getters and setters */
    /**
     * @return the item
     */
    public ItemStack getItem() {
        return item;
    }

    /**
     * @param item
     * the item to set
     */
    public void setItem(ItemStack item) {
        this.item = item;
    }

    /* Actions */
    @Override
    public boolean check() {
        return plugin.getCaliburnAPI().getItems().getById(lines[1]) != null;
    }

    @Override
    public void onInit() {
        UniversalItem item = plugin.getCaliburnAPI().getItems().getById(lines[1]);
        this.item = item.toItemStack(NumberUtil.parseInt(lines[2], 1));
        getSign().getBlock().setType(Material.AIR);
    }

    @Override
    public void onTrigger() {
        getSign().getWorld().dropItem(getSign().getLocation(), item);
    }

    @Override
    public DSignType getType() {
        return type;
    }

}
