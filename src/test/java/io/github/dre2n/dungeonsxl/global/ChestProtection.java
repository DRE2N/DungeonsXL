/*
 * Copyright (C) 2016 Daniel Saukel
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
package io.github.dre2n.dungeonsxl.global;

import java.util.Arrays;
import java.util.Collection;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * @author Daniel Saukel
 */
public class ChestProtection extends GlobalProtection {

    private Block chest;

    public ChestProtection(Block chest) {
        super(chest.getWorld(), plugin.getGlobalProtections().generateId(ChestProtection.class, chest.getWorld()));
        this.chest = chest;
    }

    @Override
    public void save(FileConfiguration config) {
        String preString = "protections.chests." + getWorld().getName() + "." + getId();

        config.set(preString + ".x", chest.getX());
        config.set(preString + ".y", chest.getY());
        config.set(preString + ".z", chest.getZ());
    }

    @Override
    public Collection<Block> getBlocks() {
        return Arrays.asList(chest);
    }

}
