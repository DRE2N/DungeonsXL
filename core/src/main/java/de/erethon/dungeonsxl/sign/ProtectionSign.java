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
import de.erethon.commons.misc.BlockUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.world.DGameWorld;
import de.erethon.dungeonsxl.world.block.ProtectedBlock;
import org.bukkit.block.Sign;

/**
 * @author Daniel Saukel
 */
public class ProtectionSign extends DSign {

    public ProtectionSign(DungeonsXL plugin, Sign sign, String[] lines, DGameWorld gameWorld) {
        super(plugin, sign, lines, gameWorld);
    }

    /* Getters and setters */
    @Override
    public DSignType getType() {
        return DSignTypeDefault.PROTECTION;
    }

    /* Actions */
    @Override
    public boolean check() {
        return true;
    }

    @Override
    public void onInit() {
        getGameWorld().addGameBlock(new ProtectedBlock(plugin, BlockUtil.getAttachedBlock(getSign().getBlock())));
        getSign().getBlock().setType(VanillaItem.AIR.getMaterial());
    }

}
