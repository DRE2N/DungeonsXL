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
package io.github.dre2n.dungeonsxl.sign;

import io.github.dre2n.dungeonsxl.world.GameWorld;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class CustomSign extends DSign {

    private DSignType type = DSignTypeCustom.CUSTOM;

    public CustomSign(Sign sign, String[] lines, GameWorld gameWorld) {
        super(sign, lines, gameWorld);
    }

    @Override
    public boolean check() {
        // Check if the sign has the correct format
        if (getSign().getLine(1).isEmpty()) {
            return false;

        } else {
            return true;
        }
    }

    @Override
    public void onInit() {
        // Stuff that happens when the sign is transformed
    }

    @Override
    public boolean onPlayerTrigger(Player player) {
        // Stuff that happens when one player triggers the sign
        return true;
    }

    @Override
    public void onTrigger() {
        // Stuff that happens when the sign is triggered
        remove();
    }

    @Override
    public DSignType getType() {
        return type;
    }

}
