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
package de.erethon.dungeonsxl.sign.lobby;

import de.erethon.caliburn.item.VanillaItem;
import de.erethon.commons.misc.NumberUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.sign.DSignType;
import de.erethon.dungeonsxl.sign.DSignTypeDefault;
import de.erethon.dungeonsxl.sign.LocationSign;
import de.erethon.dungeonsxl.world.DGameWorld;
import org.bukkit.block.Sign;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class StartSign extends LocationSign {

    private int id;

    public StartSign(DungeonsXL plugin, Sign sign, String[] lines, DGameWorld gameWorld) {
        super(plugin, sign, lines, gameWorld);
    }

    /* Getters and setters */
    /**
     * @return the ID
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the ID to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /* Actions */
    @Override
    public boolean check() {
        return true;
    }

    @Override
    public void onInit() {
        super.onInit();
        id = NumberUtil.parseInt(lines[1]);
        getSign().getBlock().setType(VanillaItem.AIR.getMaterial());
    }

    @Override
    public DSignType getType() {
        return DSignTypeDefault.START;
    }

}
