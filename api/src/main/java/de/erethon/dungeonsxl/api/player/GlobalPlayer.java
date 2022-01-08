/*
 * Copyright (C) 2014-2022 Daniel Saukel
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
package de.erethon.dungeonsxl.api.player;

import de.erethon.commons.chat.MessageUtil;
import de.erethon.commons.player.PlayerWrapper;
import de.erethon.dungeonsxl.api.dungeon.Dungeon;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a player anywhere on the server.
 * <p>
 * All players on the server, including the ones in dungeons, have one wrapper object that is an instance of GlobalPlayer.
 * <p>
 * Do not cache this for the whole runtime (or use {@link de.erethon.commons.player.PlayerCollection}). The object may be deleted and replaced with an object of
 * the appropriate type when the player enters or leaves an instance.
 *
 * @author Daniel Saukel
 */
// Implementation-specific methods: getters and setters: data, portal, cached item, announcer; startTutorial
public interface GlobalPlayer extends PlayerWrapper {

    /**
     * Returns the player's group.
     *
     * @return the player's group.
     */
    PlayerGroup getGroup();

    /**
     * Returns if the player uses the built-in group chat.
     *
     * @return if the player uses the built-in group chat
     */
    boolean isInGroupChat();

    /**
     * Sets if the player uses the built-in group chat.
     *
     * @param groupChat if the player shall use the built-in group chat
     */
    void setInGroupChat(boolean groupChat);

    /**
     * Returns if the player may read messages from the built-in group chat.
     *
     * @return if the player may read messages from the built-in group chat
     */
    boolean isInChatSpyMode();

    /**
     * Sets if the player may read messages from the built-in group chat.
     *
     * @param chatSpyMode if the player may read messages from the built-in group chat
     */
    void setInChatSpyMode(boolean chatSpyMode);

    /**
     * Checks if the player has the given permission.
     *
     * @param permission the permission
     * @return if the player has the given permission
     */
    boolean hasPermission(String permission);

    /**
     * Returns the reward items the player gets after leaving the dungeon.
     *
     * @return the reward items the player gets after leaving the dungeon
     */
    List<ItemStack> getRewardItems();

    /**
     * Sets the reward items the player gets after leaving the dungeon.
     *
     * @param rewardItems the reward items the player gets after leaving the dungeon
     */
    void setRewardItems(List<ItemStack> rewardItems);

    /**
     * Returns if the player has any reward items left.
     *
     * @return if the player has any reward items left
     */
    boolean hasRewardItemsLeft();

    /**
     * Returns if the player is currently breaking a global protection (=using /dxl break).
     *
     * @return if the player is currently breaking a global protection (=using /dxl break)
     */
    boolean isInBreakMode();

    /**
     * Sets the player into or out of break mode; see {@link #isInBreakMode()}.
     *
     * @param breakMode if the player may break global protections
     */
    void setInBreakMode(boolean breakMode);

    /**
     * Sends a message to the player.
     * <p>
     * Supports color codes.
     *
     * @param message the message to send
     */
    default void sendMessage(String message) {
        MessageUtil.sendMessage(getPlayer(), message);
    }

    /**
     * Respawns the player at the location defined by the game rules or his old position before he was in a dungeon.
     *
     * @param gameFinished if the game was finished
     */
    void reset(boolean gameFinished);

    /**
     * Respawns the player at the given location.
     *
     * @param tpLoc         the location where the player shall respawn
     * @param keepInventory if the saved status shall be reset
     */
    void reset(Location tpLoc, boolean keepInventory);

    /**
     * Performs a requirement check for the given dungeon.
     * <p>
     * This method might send messages to the player to inform him that he does not fulfill them.
     *
     * @param dungeon the dungeon to check
     * @return if the player fulfills the requirements or may bypass them
     */
    boolean checkRequirements(Dungeon dungeon);

}
