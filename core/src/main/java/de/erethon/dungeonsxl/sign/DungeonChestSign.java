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
import de.erethon.dungeonsxl.world.DGameWorld;
import java.util.Arrays;
import java.util.List;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

/**
 * @author Daniel Saukel
 */
public class DungeonChestSign extends ChestSign {

    public DungeonChestSign(DungeonsXL plugin, Sign sign, String[] lines, DGameWorld gameWorld) {
        super(plugin, sign, lines, gameWorld);
    }

    /* Getters and setters */
    @Override
    public DSignType getType() {
        return DSignTypeDefault.DUNGEON_CHEST;
    }

    /* Actions */
    @Override
    public boolean check() {
        return true;
    }

    @Override
    public void onInit() {
        // For consistency with reward chests but also for intuitiveness, both lines should be possible
        if (!lines[1].isEmpty()) {
            lootTable = plugin.getCaliburn().getLootTable(lines[1]);
        }
        if (!lines[2].isEmpty()) {
            lootTable = plugin.getCaliburn().getLootTable(lines[2]);
        }

        checkChest();
        if (chest != null) {
            getSign().getBlock().setType(VanillaItem.AIR.getMaterial());
        } else {
            getSign().getBlock().setType(VanillaItem.CHEST.getMaterial());
            chest = getSign().getBlock();
        }

        List<ItemStack> list = null;
        if (lootTable != null) {
            list = lootTable.generateLootList();
        }
        if (chestContent != null) {
            if (list != null) {
                list.addAll(Arrays.asList(chestContent));
            } else {
                list = Arrays.asList(chestContent);
            }
        }
        if (list == null) {
            return;
        }

        chestContent = Arrays.copyOfRange(list.toArray(new ItemStack[list.size()]), 0, 26);
        ((Container) chest.getState()).getInventory().setContents(chestContent);
    }

}
