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
import io.github.dre2n.dungeonsxl.task.DropItemTask;
import io.github.dre2n.dungeonsxl.world.GameWorld;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

/**
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class DropSign extends DSign {

    private DSignType type = DSignTypeDefault.DROP;

    private ItemStack item;
    private double interval = -1;

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

        String[] attributes = lines[2].split(",");
        if (attributes.length >= 1) {
            this.item = item.toItemStack(NumberUtil.parseInt(attributes[0], 1));
        }
        if (attributes.length == 2) {
            interval = NumberUtil.parseDouble(attributes[1]);
        }

        getSign().getBlock().setType(Material.AIR);
    }

    @Override
    public void onTrigger() {
        Location spawnLocation = getSign().getLocation().add(0.5, 0, 0.5);
        if (interval < 0) {
            getSign().getWorld().dropItem(spawnLocation, item);

        } else {
            new DropItemTask(item, spawnLocation).runTaskTimer(plugin, 0, (long) interval * 20);
        }
    }

    @Override
    public DSignType getType() {
        return type;
    }

}
