/*
 * Copyright (C) 2014-2020 Daniel Saukel
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

import de.erethon.caliburn.item.ExItem;
import de.erethon.caliburn.item.VanillaItem;
import de.erethon.commons.chat.MessageUtil;
import de.erethon.commons.compatibility.Version;
import de.erethon.commons.player.PlayerCollection;
import de.erethon.dungeonsxl.api.Reward;
import de.erethon.dungeonsxl.api.dungeon.Dungeon;
import de.erethon.dungeonsxl.api.world.GameWorld;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

/**
 * Represents a group of players provided by DungeonsXL.
 *
 * @author Daniel Saukel
 */
// Implementation-specific methods: setDungeon, setPlaying, [color, unplayed floor, floor count methods], isEmpty, isCustom, teleport,
// finish, finishFloor, startGame, winGame, requirements methods
public interface PlayerGroup {

    /**
     * Links different color types together.
     */
    public enum Color {

        BLACK(ChatColor.BLACK, DyeColor.BLACK, VanillaItem.BLACK_WOOL),
        DARK_GRAY(ChatColor.DARK_GRAY, DyeColor.GRAY, VanillaItem.GRAY_WOOL),
        LIGHT_GRAY(ChatColor.GRAY, DyeColor.valueOf(Version.isAtLeast(Version.MC1_13) ? "LIGHT_GRAY" : "SILVER"), VanillaItem.LIGHT_GRAY_WOOL),
        WHITE(ChatColor.WHITE, DyeColor.WHITE, VanillaItem.WHITE_WOOL),
        DARK_GREEN(ChatColor.DARK_GREEN, DyeColor.GREEN, VanillaItem.GREEN_WOOL),
        LIGHT_GREEN(ChatColor.GREEN, DyeColor.LIME, VanillaItem.LIME_WOOL),
        CYAN(ChatColor.DARK_AQUA, DyeColor.CYAN, VanillaItem.CYAN_WOOL),
        DARK_BLUE(ChatColor.DARK_BLUE, DyeColor.BLUE, VanillaItem.BLUE_WOOL),
        LIGHT_BLUE(ChatColor.AQUA, DyeColor.LIGHT_BLUE, VanillaItem.LIGHT_BLUE_WOOL),
        PURPLE(ChatColor.DARK_PURPLE, DyeColor.PURPLE, VanillaItem.PURPLE_WOOL),
        MAGENTA(ChatColor.LIGHT_PURPLE, DyeColor.MAGENTA, VanillaItem.MAGENTA_WOOL),
        DARK_RED(ChatColor.DARK_RED, DyeColor.BROWN, VanillaItem.BROWN_WOOL),
        LIGHT_RED(ChatColor.RED, DyeColor.RED, VanillaItem.RED_WOOL),
        ORANGE(ChatColor.GOLD, DyeColor.ORANGE, VanillaItem.ORANGE_WOOL),
        YELLOW(ChatColor.YELLOW, DyeColor.YELLOW, VanillaItem.YELLOW_WOOL),
        PINK(ChatColor.BLUE, DyeColor.PINK, VanillaItem.PINK_WOOL);

        private ChatColor chat;
        private DyeColor dye;
        private VanillaItem woolMaterial;

        Color(ChatColor chat, DyeColor dye, VanillaItem woolMaterial) {
            this.chat = chat;
            this.dye = dye;
            this.woolMaterial = woolMaterial;
        }

        /**
         * Returns the ChatColor.
         *
         * @return the ChatColor
         */
        public ChatColor getChatColor() {
            return chat;
        }

        /**
         * Returns the DyeColor.
         *
         * @return the DyeColor
         */
        public DyeColor getDyeColor() {
            return dye;
        }

        /**
         * Returns the RGB value.
         *
         * @return the RGB value
         */
        public int getRGBColor() {
            return dye.getColor().asRGB();
        }

        /**
         * Returns the wool material.
         *
         * @return the wool material
         */
        public VanillaItem getWoolMaterial() {
            return woolMaterial;
        }

        /**
         * Returns the GroupColor matching the ChatColor or null if none exists.
         *
         * @param color the ChatColor to check
         * @return the GroupColor matching the ChatColor or null if none exists
         */
        public static Color getByChatColor(ChatColor color) {
            for (Color groupColor : values()) {
                if (groupColor.chat == color) {
                    return groupColor;
                }
            }
            return null;
        }

        /**
         * Returns the GroupColor matching the DyeColor or null if none exists.
         *
         * @param color the DyeColor to check
         * @return the GroupColor matching the DyeColor or null if none exists.
         */
        public static Color getByDyeColor(DyeColor color) {
            for (Color groupColor : values()) {
                if (groupColor.dye == color) {
                    return groupColor;
                }
            }
            return null;
        }

        /**
         * Returns the GroupColor matching the wool material or null if none exists.
         *
         * @param wool the wool material to check
         * @return the GroupColor matching the wool material or null if none exists
         */
        public static Color getByWoolType(ExItem wool) {
            for (Color groupColor : values()) {
                if (groupColor.woolMaterial == wool) {
                    return groupColor;
                }
            }
            return null;
        }

    }

    /**
     * Returns the ID.
     *
     * @return the ID
     */
    int getId();

    /**
     * Returns the formatted name.
     * <p>
     * This is the name used e.g. in messages.
     *
     * @return the formatted name
     */
    String getName();

    /**
     * Returns the raw, unformatted name.
     * <p>
     * This is the name used e.g. in command arguments.
     *
     * @return the raw, unformatted name
     */
    String getRawName();

    /**
     * Sets the name.
     *
     * @param name the name
     */
    void setName(String name);

    /**
     * Sets the name to a default value taken from the color.
     * <p>
     * In the default implementation, this is nameOfTheColor#{@link #getId()}
     *
     * @param color the color
     */
    default void setName(Color color) {
        setName(color.toString() + "#" + getId());
    }

    /**
     * The player who has permission to manage the group.
     *
     * @return the player who has permission to manage the group
     */
    Player getLeader();

    /**
     * Sets the leader to another group member.
     *
     * @param player the new leader
     */
    void setLeader(Player player);

    /**
     * Returns a PlayerCollection of the group members
     *
     * @return a PlayerCollection of the group members
     */
    PlayerCollection getMembers();

    /**
     * Adds a player to the group.
     * <p>
     * The default implemenation calls {@link #addPlayer(Player, boolean)} with messages set to true.
     *
     * @param player the player to add
     */
    default void addPlayer(Player player) {
        addPlayer(player, true);
    }

    /**
     * Adds a player to the group.
     *
     * @param player  the player to add
     * @param message if messages shall be sent
     */
    void addPlayer(Player player, boolean message);

    /**
     * Removes a player from the group.
     * <p>
     * The default implemenation calls {@link #removePlayer(Player, boolean)} with messages set to true.
     *
     * @param player the player to add
     */
    default void removePlayer(Player player) {
        addPlayer(player, true);
    }

    /**
     * Removes a player from the group.
     *
     * @param player  the player to add
     * @param message if messages shall be sent
     */
    void removePlayer(Player player, boolean message);

    /**
     * Returns a PlayerCollection of the players who are invited to join the group but did not yet do so.
     *
     * @return a PlayerCollection of the players who are invited to join the group but did not yet do so
     */
    PlayerCollection getInvitedPlayers();

    /**
     * Invites a player to join the group.
     *
     * @param player  the player to invite
     * @param message if messages shall be sent
     */
    void addInvitedPlayer(Player player, boolean message);

    /**
     * Removes an invitation priviously made for a player to join the group.
     *
     * @param player  the player to uninvite
     * @param message if messages shall be sent
     */
    void removeInvitedPlayer(Player player, boolean message);

    /**
     * Removes all invitations for players who are not online.
     */
    void clearOfflineInvitedPlayers();

    /**
     * Returns the game world the group is in.
     *
     * @return the game world the group is in
     */
    GameWorld getGameWorld();

    /**
     * Sets the game world the group is in.
     *
     * @param gameWorld the game world to set
     */
    void setGameWorld(GameWorld gameWorld);

    /**
     * Returns the dungeon the group is playing or has remembered to play next.
     * <p>
     * The latter is for example used when a group is created by a group sign sothat a portal or the auto-join function knows where to send the group.
     *
     * @return the dungeon the group is playing or has remembered to play next
     */
    Dungeon getDungeon();

    /**
     * Returns if the group is already playing its remembered {@link #getDungeon() dungeon}.
     *
     * @return if the group is already playing its remembered {@link #getDungeon() dungeon}
     */
    boolean isPlaying();

    /**
     * Returns the rewards that are memorized for the group. These are given when the game is finished.
     *
     * @return the rewards
     */
    List<Reward> getRewards();

    /**
     * Memorizes the given reward for the group. These are given when the game is finished.
     *
     * @param reward the reward
     */
    void addReward(Reward reward);

    /**
     * Removes the given reward.
     *
     * @param reward the reward
     */
    void removeReward(Reward reward);

    /**
     * Returns the score number, which is used for capture the flag and similar game types.
     *
     * @return the score number
     */
    int getScore();

    /**
     * Sets the score of this group to a new value.
     *
     * @param score the value
     */
    void setScore(int score);

    /**
     * Returns the initial amount of lives or -1 if group lives are not used.
     *
     * @return the initial amount of lives or -1 if group lives are not used
     */
    int getInitialLives();

    /**
     * Sets the initial amount of lives.
     * <p>
     * The value must be &gt;=0 or -1, which means unlimited lives.
     *
     * @param lives the new amount of lives known as the initial amount
     */
    void setInitialLives(int lives);

    /**
     * Returns the amount of lives the group currently has left or -1 if group lives are not used.
     *
     * @return the amount of lives the group currently has left or -1 if group lives are not used
     */
    int getLives();

    /**
     * Sets the amount of lives the group currently has left.
     * <p>
     * The value must be &gt;=0 or -1, which means unlimited lives.
     *
     * @param lives the amount of lives the group currently has left
     */
    void setLives(int lives);

    /**
     * Returns true if all players of the group have finished the game; false if not.
     *
     * @return true if all players of the group have finished the game; false if not
     */
    boolean isFinished();

    /**
     * Disbands the group.
     */
    void delete();

    /**
     * Sends a message to all players in the group.
     * <p>
     * Supports color codes.
     *
     * @param message the message to sent
     * @param except  Players who shall not receive the message
     */
    default void sendMessage(String message, Player... except) {
        members:
        for (Player player : getMembers().getOnlinePlayers()) {
            if (!player.isOnline()) {
                continue;
            }
            for (Player nope : except) {
                if (player == nope) {
                    continue members;
                }
            }
            MessageUtil.sendMessage(player, message);
        }
    }

}
