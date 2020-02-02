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
package de.erethon.dungeonsxl.mob;

/**
 * Officially supported external mob plugins.
 *
 * @author Daniel Saukel
 */
public enum ExternalMobPlugin implements ExternalMobProvider {

    CUSTOM_MOBS("CM", "ccmob spawn %mob% %world% %block_x% %block_y% %block_z%"),
    INSANE_MOBS("IM", "insanemobs %mob% %x% %y% %z% %world%"),
    MYTHIC_MOBS("MM", "mythicmobs mobs spawn %mob% 1 %world%,%x%,%y%,%z%");

    private String identifier;
    private String command;

    ExternalMobPlugin(String identifier, String command) {
        this.identifier = identifier;
        this.command = command;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getRawCommand() {
        return command;
    }

}
