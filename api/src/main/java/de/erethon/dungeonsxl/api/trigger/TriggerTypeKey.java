/*
 * Copyright (C) 2014-2023 Daniel Saukel
 *
 * This library is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNULesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.erethon.dungeonsxl.api.trigger;

/**
 * Type keys of default triggers.
 *
 * @author Daniel Saukel
 */
public class TriggerTypeKey {

    public static final char DISTANCE = 'D';
    public static final char FORTUNE = 'F';
    public static final char INTERACT = 'I';
    /**
     * The terms "generic" and "sign trigger" are used synonymously. Trigger strings without prefix default to generic triggers.
     */
    public static final char GENERIC = 'T';
    public static final char MOB = 'M';
    public static final char PRESENCE = 'P';
    @Deprecated
    public static final char PROGRESS = 'P';
    public static final char REDSTONE = 'R';
    public static final char USE_ITEM = 'U';
    @Deprecated
    public static final char WAVE = 'W';

}
