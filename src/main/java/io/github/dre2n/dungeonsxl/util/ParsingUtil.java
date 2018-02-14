/*
 * Copyright (C) 2012-2018 Frank Baumann
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
package io.github.dre2n.dungeonsxl.util;

import io.github.dre2n.dungeonsxl.player.DGlobalPlayer;
import io.github.dre2n.dungeonsxl.player.DGroup;

/**
 * @author Daniel Saukel
 */
public enum ParsingUtil {

    GROUP_COLOR("%group_color%"),
    GROUP_NAME("%group_name%"),
    PLAYER_NAME("%player_name%");

    private String placeholder;

    ParsingUtil(String placeholder) {
        this.placeholder = placeholder;
    }

    /* Getters and setters */
    /**
     * @return the placeholder
     */
    public String getPlaceholder() {
        return placeholder;
    }

    @Override
    public String toString() {
        return placeholder;
    }

    /* Statics */
    /**
     * Replace the placeholders that are relevant for the chat in a String automatically.
     *
     * @param string
     * the String that contains the placeholders
     * @param sender
     * the DGlobalPlayer who sent the message
     */
    public static String replaceChatPlaceholders(String string, DGlobalPlayer sender) {
        DGroup group = DGroup.getByPlayer(sender.getPlayer());

        string = string.replaceAll(PLAYER_NAME.getPlaceholder(), sender.getName());
        string = string.replaceAll(GROUP_COLOR.getPlaceholder(), group.getDColor().getChatColor().toString());
        string = string.replaceAll(GROUP_NAME.getPlaceholder(), group.getName());
        return string;
    }

}
