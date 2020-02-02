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
package de.erethon.dungeonsxl.global;

import de.erethon.caliburn.category.Category;
import de.erethon.commons.chat.MessageUtil;
import de.erethon.commons.misc.NumberUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.game.Game;
import de.erethon.dungeonsxl.player.DGroup;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

/**
 * Basically a GroupSign, but to form a game of multiple groups.
 *
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class GameSign extends JoinSign {

    public static final String GAME_SIGN_TAG = "Game";

    private Game game;

    public GameSign(DungeonsXL plugin, int id, Block startSign, String identifier, int maxGroupsPerGame, int startIfElementsAtLeast) {
        super(plugin, id, startSign, identifier, maxGroupsPerGame, startIfElementsAtLeast);
    }

    public GameSign(DungeonsXL plugin, World world, int id, ConfigurationSection config) {
        super(plugin, world, id, config);
    }

    /**
     * @return the attached game
     */
    public Game getGame() {
        return game;
    }

    /**
     * @param game the game to set
     */
    public void setGame(Game game) {
        this.game = game;
    }

    /**
     * Update this game sign to show the game(s) correctly.
     */
    @Override
    public void update() {
        if (!(startSign.getState() instanceof Sign)) {
            return;
        }

        super.update();
        Sign sign = (Sign) startSign.getState();

        if (game == null || game.getDGroups().isEmpty()) {
            loadedWorld = false;
            sign.setLine(0, DMessage.SIGN_GLOBAL_NEW_GAME.getMessage());
            sign.update();
            return;
        }

        if (game.getDGroups().size() >= startIfElementsAtLeast && startIfElementsAtLeast != -1) {
            loadedWorld = true;
            game.getDGroups().forEach(g -> g.teleport());
        }

        if (game.getDGroups().get(0).isPlaying()) {
            sign.setLine(0, DMessage.SIGN_GLOBAL_IS_PLAYING.getMessage());

        } else if (game.getDGroups().size() >= maxElements) {
            sign.setLine(0, DMessage.SIGN_GLOBAL_FULL.getMessage());

        } else {
            sign.setLine(0, DMessage.SIGN_GLOBAL_JOIN_GAME.getMessage());
        }

        int j = 1;
        Sign rowSign = sign;

        for (DGroup dGroup : game.getDGroups()) {
            if (j > 3) {
                j = 0;
                rowSign = (Sign) sign.getBlock().getRelative(0, -1, 0).getState();
            }

            if (rowSign != null) {
                rowSign.setLine(j, dGroup.getName());
            }

            j++;
            rowSign.update();
        }

        sign.update();
    }

    @Override
    public String getDataPath() {
        return "protections.gameSigns";
    }

    public void onPlayerInteract(Block block, Player player) {
        DGroup dGroup = DGroup.getByPlayer(player);
        if (dGroup == null) {
            MessageUtil.sendMessage(player, DMessage.ERROR_JOIN_GROUP.getMessage());
            return;
        }
        if (!dGroup.getCaptain().equals(player)) {
            MessageUtil.sendMessage(player, DMessage.ERROR_NOT_LEADER.getMessage());
            return;
        }

        if (Game.getByDGroup(dGroup) != null) {
            MessageUtil.sendMessage(player, DMessage.ERROR_LEAVE_GAME.getMessage());
            return;
        }

        Block topBlock = block.getRelative(0, startSign.getY() - block.getY(), 0);
        if (!(topBlock.getState() instanceof Sign)) {
            return;
        }

        Sign topSign = (Sign) topBlock.getState();

        if (topSign.getLine(0).equals(DMessage.SIGN_GLOBAL_NEW_GAME.getMessage())) {
            if (dungeon == null) {
                MessageUtil.sendMessage(player, DMessage.ERROR_SIGN_WRONG_FORMAT.getMessage());
                return;
            }

            game = new Game(plugin, dGroup);
            dGroup.setDungeon(dungeon);
            update();

        } else if (topSign.getLine(0).equals(DMessage.SIGN_GLOBAL_JOIN_GAME.getMessage())) {
            game.addDGroup(dGroup);
            update();
        }
    }

    /* Statics */
    /**
     * @param plugin the plugin instance
     * @param block  a block which is protected by the returned GameSign
     * @return the game sign the block belongs to, null if it belongs to none
     */
    public static GameSign getByBlock(DungeonsXL plugin, Block block) {
        if (!Category.SIGNS.containsBlock(block)) {
            return null;
        }

        for (GlobalProtection protection : plugin.getGlobalProtectionCache().getProtections(GameSign.class)) {
            GameSign gameSign = (GameSign) protection;
            Block start = gameSign.startSign;
            if (start == block || (start.getX() == block.getX() && start.getZ() == block.getZ() && (start.getY() >= block.getY() && start.getY() - gameSign.verticalSigns <= block.getY()))) {
                return gameSign;
            }
        }

        return null;
    }

    /**
     * @param plugin the plugin instance
     * @param game   the game to check
     * @return the game that this sign creates
     */
    public static GameSign getByGame(DungeonsXL plugin, Game game) {
        for (GlobalProtection protection : plugin.getGlobalProtectionCache().getProtections(GameSign.class)) {
            GameSign gameSign = (GameSign) protection;
            if (gameSign.game == game) {
                return gameSign;
            }
        }
        return null;
    }

    public static GameSign tryToCreate(DungeonsXL plugin, SignChangeEvent event) {
        if (!event.getLine(0).equalsIgnoreCase(SIGN_TAG)) {
            return null;
        }
        if (!event.getLine(1).equalsIgnoreCase(GAME_SIGN_TAG)) {
            return null;
        }

        String identifier = event.getLine(2);
        String[] data = event.getLine(3).split(",");
        int maxGroupsPerGame = NumberUtil.parseInt(data[0], 1);
        int startIfElementsAtLeast = -1;
        if (data.length > 1) {
            startIfElementsAtLeast = NumberUtil.parseInt(data[1], -1);
        }

        return tryToCreate(plugin, event.getBlock(), identifier, maxGroupsPerGame, startIfElementsAtLeast);
    }

    public static GameSign tryToCreate(DungeonsXL plugin, Block startSign, String identifier, int maxElements, int startIfElementsAtLeast) {
        onCreation(plugin, startSign, identifier, maxElements, startIfElementsAtLeast);
        GameSign sign = new GameSign(plugin, plugin.getGlobalProtectionCache().generateId(GameSign.class, startSign.getWorld()), startSign, identifier,
                maxElements, startIfElementsAtLeast);
        return sign;
    }

}
