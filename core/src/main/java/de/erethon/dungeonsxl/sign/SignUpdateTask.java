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

import org.bukkit.block.Sign;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Milan Albrecht, Daniel Saukel
 */
public class SignUpdateTask extends BukkitRunnable {

    private Sign sign;

    public SignUpdateTask(Sign sign) {
        this.sign = sign;
    }

    @Override
    public void run() {
        sign.update();
    }

}
