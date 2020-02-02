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

/**
 * Implement this to create custom sign types.
 *
 * @author Daniel Saukel
 */
public interface DSignType {

    /**
     * @return the name
     */
    String getName();

    /**
     * @return the buildPermission
     */
    String getBuildPermission();

    /**
     * @return if the sign gets initialized when the dungeon is loaded instead of when the game starts
     */
    boolean isOnDungeonInit();

    /**
     * @return if the sign block should be destroyable after the initialization
     */
    boolean isProtected();

    /**
     * @return the handler
     */
    Class<? extends DSign> getHandler();

}
