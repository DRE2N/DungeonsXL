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
package de.erethon.dungeonsxl.event.dplayer;

import de.erethon.dungeonsxl.player.DGlobalPlayer;
import org.bukkit.event.Event;

/**
 * @author Daniel Saukel
 */
public abstract class DPlayerEvent extends Event {

    protected DGlobalPlayer dPlayer;

    public DPlayerEvent(DGlobalPlayer dPlayer) {
        this.dPlayer = dPlayer;
    }

    /**
     * @return the dPlayer
     */
    public DGlobalPlayer getDPlayer() {
        return dPlayer;
    }

    /**
     * @param dPlayer the dPlayer to set
     */
    public void setDPlayer(DGlobalPlayer dPlayer) {
        this.dPlayer = dPlayer;
    }

}
