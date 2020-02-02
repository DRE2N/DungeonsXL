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
package de.erethon.dungeonsxl.game;

import de.erethon.commons.chat.MessageUtil;
import de.erethon.dungeonsxl.sign.DSign;
import java.util.ArrayList;
import java.util.List;

/**
 * GameType instance manager.
 *
 * @author Daniel Saukel
 */
public class GameTypeCache {

    private List<GameType> types = new ArrayList<>();

    public GameTypeCache() {
        for (GameType type : GameTypeDefault.values()) {
            if (type == GameTypeDefault.PVP_FACTIONS_BATTLEFIELD) {
                try {
                    Class.forName("com.massivecraft.factions.Patch");

                } catch (ClassNotFoundException exception) {
                    MessageUtil.log("Could not find compatible Factions plugin. The game type PVP_FACTIONS_BATTLEFIELD will not get enabled...");
                    continue;
                }
            }

            types.add(type);
        }
    }

    /**
     * @param name the name to check
     * @return the game type which has the enum value name
     */
    public GameType getByName(String name) {
        for (GameType type : types) {
            if (type.toString().equals(name)) {
                return type;
            }
        }

        return null;
    }

    /**
     * @param sign the sign to check
     * @return the game type which has the enum value sign text in the second line of the sign
     */
    public GameType getBySign(DSign sign) {
        String[] lines = sign.getLines();

        for (GameType type : types) {
            if (type.getSignName().equals(lines[1])) {
                return type;
            }
        }

        return null;
    }

    /**
     * @return the game types
     */
    public List<GameType> getGameTypes() {
        return types;
    }

    /**
     * @param type the game type to add
     */
    public void addGameType(GameType type) {
        types.add(type);
    }

    /**
     * @param type the game type to remove
     */
    public void removeGameType(GameType type) {
        types.remove(type);
    }

}
