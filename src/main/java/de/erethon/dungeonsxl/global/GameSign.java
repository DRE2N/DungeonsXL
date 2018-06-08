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
package de.erethon.dungeonsxl.global;

import com.griefcraft.lwc.LWC;
import com.griefcraft.model.Protection;
import de.erethon.caliburn.category.Category;
import de.erethon.caliburn.item.VanillaItem;
import de.erethon.commons.chat.MessageUtil;
import de.erethon.commons.misc.NumberUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.game.Game;
import de.erethon.dungeonsxl.player.DGroup;
import de.erethon.dungeonsxl.util.LWCUtil;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.material.Attachable;

/**
 * Basically a GroupSign, but to form a game of multiple groups.
 *
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class GameSign extends JoinSign {

    public static final String GAME_SIGN_TAG = "Game";

    private Game game;

    public GameSign(int id, Block startSign, String identifier, int maxGroupsPerGame) {
        super(id, startSign, identifier, maxGroupsPerGame);
    }

    /**
     * @return
     * the attached game
     */
    public Game getGame() {
        return game;
    }

    /**
     * @param game
     * the game to set
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
            sign.setLine(0, DMessage.SIGN_GLOBAL_NEW_GAME.getMessage());
            sign.update();
            return;
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
    public void save(FileConfiguration config) {
        String preString = "protections.gameSigns." + getWorld().getName() + "." + getId();

        config.set(preString + ".x", startSign.getX());
        config.set(preString + ".y", startSign.getY());
        config.set(preString + ".z", startSign.getZ());
        config.set(preString + ".dungeon", dungeon.getName());
        config.set(preString + ".maxGroupsPerGame", maxElements);
    }

    public boolean onPlayerInteract(Block block, Player player) {
        if (DungeonsXL.getInstance().getDWorlds().getGameWorlds().size() >= DungeonsXL.getInstance().getMainConfig().getMaxInstances()) {
            MessageUtil.sendMessage(player, DMessage.ERROR_TOO_MANY_INSTANCES.getMessage());
            return true;
        }

        DGroup dGroup = DGroup.getByPlayer(player);
        if (dGroup == null) {
            MessageUtil.sendMessage(player, DMessage.ERROR_JOIN_GROUP.getMessage());
            return true;
        }
        if (!dGroup.getCaptain().equals(player)) {
            MessageUtil.sendMessage(player, DMessage.ERROR_NOT_CAPTAIN.getMessage());
            return true;
        }

        if (Game.getByDGroup(dGroup) != null) {
            MessageUtil.sendMessage(player, DMessage.ERROR_LEAVE_GAME.getMessage());
            return true;
        }

        Block topBlock = block.getRelative(0, startSign.getY() - block.getY(), 0);
        if (!(topBlock.getState() instanceof Sign)) {
            return true;
        }

        Sign topSign = (Sign) topBlock.getState();

        if (topSign.getLine(0).equals(DMessage.SIGN_GLOBAL_NEW_GAME.getMessage())) {
            game = new Game(dGroup);
            dGroup.setDungeon(dungeon);
            update();

        } else if (topSign.getLine(0).equals(DMessage.SIGN_GLOBAL_JOIN_GAME.getMessage())) {
            game.addDGroup(dGroup);
            update();
        }

        return true;
    }

    /* Statics */
    /**
     * @param block
     * a block which is protected by the returned GameSign
     */
    public static GameSign getByBlock(Block block) {
        if (!Category.SIGNS.containsBlock(block)) {
            return null;
        }

        for (GlobalProtection protection : DungeonsXL.getInstance().getGlobalProtections().getProtections(GameSign.class)) {
            GameSign gameSign = (GameSign) protection;
            Block start = gameSign.startSign;
            if (start == block || (start.getX() == block.getX() && start.getZ() == block.getZ() && (start.getY() >= block.getY() && start.getY() - gameSign.verticalSigns <= block.getY()))) {
                return gameSign;
            }
        }

        return null;
    }

    /**
     * @param game
     * the game to check
     */
    public static GameSign getByGame(Game game) {
        for (GlobalProtection protection : DungeonsXL.getInstance().getGlobalProtections().getProtections(GameSign.class)) {
            GameSign gameSign = (GameSign) protection;
            if (gameSign.game == game) {
                return gameSign;
            }
        }
        return null;
    }

    public static GameSign tryToCreate(SignChangeEvent event) {
        if (!event.getLine(0).equalsIgnoreCase(SIGN_TAG)) {
            return null;
        }
        if (!event.getLine(1).equalsIgnoreCase(GAME_SIGN_TAG)) {
            return null;
        }

        String identifier = event.getLine(2);
        int maxGroupsPerGame = NumberUtil.parseInt(event.getLine(3), 1);

        return tryToCreate(event.getBlock(), identifier, maxGroupsPerGame);
    }

    public static GameSign tryToCreate(Block startSign, String identifier, int maxGroupsPerGame) {
        World world = startSign.getWorld();
        BlockFace facing = ((Attachable) startSign.getState().getData()).getAttachedFace().getOppositeFace();
        int x = startSign.getX(), y = startSign.getY(), z = startSign.getZ();

        int verticalSigns = (int) Math.ceil((float) (1 + maxGroupsPerGame) / 4);
        while (verticalSigns > 1) {
            Block block = world.getBlockAt(x, y - verticalSigns + 1, z);
            block.setType(VanillaItem.WALL_SIGN.getMaterial(), false);
            org.bukkit.material.Sign signData = new org.bukkit.material.Sign(VanillaItem.WALL_SIGN.getMaterial());
            signData.setFacingDirection(facing);
            org.bukkit.block.Sign sign = (org.bukkit.block.Sign) block.getState();
            sign.setData(signData);
            sign.update(true, false);

            verticalSigns--;
        }
        GameSign sign = new GameSign(DungeonsXL.getInstance().getGlobalProtections().generateId(GameSign.class, world), startSign, identifier, maxGroupsPerGame);

        LWCUtil.removeProtection(startSign);

        return sign;
    }

}
