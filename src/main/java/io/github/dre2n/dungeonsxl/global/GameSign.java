/*
 * Copyright (C) 2012-2016 Frank Baumann
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
package io.github.dre2n.dungeonsxl.global;

import io.github.dre2n.commons.util.BlockUtil;
import io.github.dre2n.commons.util.messageutil.MessageUtil;
import io.github.dre2n.dungeonsxl.config.DMessages;
import io.github.dre2n.dungeonsxl.dungeon.Dungeon;
import io.github.dre2n.dungeonsxl.game.Game;
import io.github.dre2n.dungeonsxl.player.DGroup;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class GameSign extends GlobalProtection {

    // Sign Labels
    public static final String IS_PLAYING = ChatColor.DARK_RED + "Is Playing";
    public static final String FULL = ChatColor.DARK_RED + "Full";
    public static final String JOIN_GAME = ChatColor.DARK_GREEN + "Join Game";
    public static final String NEW_GAME = ChatColor.DARK_GREEN + "New Game";

    // Variables
    private Game[] games;
    private boolean multiFloor;
    private String dungeonName;
    private String mapName;
    private int maxGroupsPerGame;
    private Block startSign;
    private int directionX = 0, directionZ = 0;
    private int verticalSigns;
    private Set<Block> blocks;

    public GameSign(int id, Block startSign, String identifier, int maxGames, int maxGroupsPerGame, boolean multiFloor) {
        super(startSign.getWorld(), id);

        this.startSign = startSign;
        games = new Game[maxGames];
        this.setMultiFloor(multiFloor);
        if (multiFloor) {
            dungeonName = identifier;
            Dungeon dungeon = plugin.getDungeons().getByName(identifier);
            if (dungeon != null) {
                mapName = dungeon.getConfig().getStartFloor();
            } else {
                mapName = "invalid";
            }
        } else {
            mapName = identifier;
        }
        this.maxGroupsPerGame = maxGroupsPerGame;
        verticalSigns = (int) Math.ceil((float) (1 + maxGroupsPerGame) / 4);

        int[] direction = getDirection(this.startSign.getData());
        directionX = direction[0];
        directionZ = direction[1];

        update();
    }

    /**
     * @return the games
     */
    public Game[] getGames() {
        return games;
    }

    /**
     * @param games
     * the games to set
     */
    public void setGames(Game[] games) {
        this.games = games;
    }

    /**
     * @return the multiFloor
     */
    public boolean isMultiFloor() {
        return multiFloor;
    }

    /**
     * @param multiFloor
     * the multiFloor to set
     */
    public void setMultiFloor(boolean multiFloor) {
        this.multiFloor = multiFloor;
    }

    /**
     * @return the dungeonName
     */
    public String getDungeonName() {
        return dungeonName;
    }

    /**
     * @param dungeonName
     * the dungeonName to set
     */
    public void setDungeonName(String dungeonName) {
        this.dungeonName = dungeonName;
    }

    /**
     * @return the mapName
     */
    public String getMapName() {
        return mapName;
    }

    /**
     * @param mapName
     * the mapName to set
     */
    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    /**
     * @return the maximum player count per group
     */
    public int getMaxGroupsPerGame() {
        return maxGroupsPerGame;
    }

    /**
     * @param maxGroupsPerGame
     * the maximum player count per group to set
     */
    public void setMaxGroupsPerGame(int maxGroupsPerGame) {
        this.maxGroupsPerGame = maxGroupsPerGame;
    }

    /**
     * Update this game sign to show the game(s) correctly.
     */
    public void update() {
        int i = 0;
        for (Game game : games) {
            if (!(startSign.getRelative(i * directionX, 0, i * directionZ).getState() instanceof Sign)) {
                i++;
                continue;
            }

            Sign sign = (Sign) startSign.getRelative(i * directionX, 0, i * directionZ).getState();

            // Reset Signs
            sign.setLine(0, "");
            sign.setLine(1, "");
            sign.setLine(2, "");
            sign.setLine(3, "");

            int yy = -1;
            while (sign.getBlock().getRelative(0, yy, 0).getState() instanceof Sign) {
                Sign subsign = (Sign) sign.getBlock().getRelative(0, yy, 0).getState();
                subsign.setLine(0, "");
                subsign.setLine(1, "");
                subsign.setLine(2, "");
                subsign.setLine(3, "");
                subsign.update();
                yy--;
            }

            // Set Signs
            if (game != null && game.getDGroups().size() > 0) {
                if (game.getDGroups().get(0).isPlaying()) {
                    sign.setLine(0, IS_PLAYING);

                } else if (game.getDGroups().size() >= maxGroupsPerGame) {
                    sign.setLine(0, FULL);

                } else {
                    sign.setLine(0, JOIN_GAME);
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

            } else {
                sign.setLine(0, NEW_GAME);
            }

            sign.update();

            i++;
        }
    }

    @Override
    public Set<Block> getBlocks() {
        if (blocks == null) {
            blocks = new HashSet<>();

            int i = games.length;
            do {
                i--;

                blocks.add(startSign.getRelative(i * directionX, 0, i * directionZ));

            } while (i >= 0);

            HashSet<Block> toAdd = new HashSet<>();
            for (Block block : blocks) {
                i = verticalSigns;
                do {
                    i--;

                    Block beneath = block.getLocation().add(0, -1 * i, 0).getBlock();
                    toAdd.add(beneath);
                    toAdd.add(BlockUtil.getAttachedBlock(beneath));

                } while (i >= 0);
            }
            blocks.addAll(toAdd);
        }

        return blocks;
    }

    @Override
    public void save(FileConfiguration config) {
        String preString = "protections.gameSigns." + getWorld().getName() + "." + getId();

        config.set(preString + ".x", startSign.getX());
        config.set(preString + ".y", startSign.getY());
        config.set(preString + ".z", startSign.getZ());

        if (isMultiFloor()) {
            config.set(preString + ".dungeon", dungeonName);

        } else {
            config.set(preString + ".dungeon", mapName);
        }

        config.set(preString + ".maxGames", games.length);
        config.set(preString + ".maxGroupsPerGame", maxGroupsPerGame);
        config.set(preString + ".multiFloor", isMultiFloor());
    }

    /* Statics */
    /**
     * @param block
     * a block which is protected by the returned GameSign
     */
    public static GameSign getByBlock(Block block) {
        if (!(block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST)) {
            return null;
        }

        int x = block.getX(), y = block.getY(), z = block.getZ();
        for (GlobalProtection protection : protections.getProtections(GameSign.class)) {
            GameSign gameSign = (GameSign) protection;

            int sx1 = gameSign.startSign.getX(), sy1 = gameSign.startSign.getY(), sz1 = gameSign.startSign.getZ();
            int sx2 = sx1 + (gameSign.games.length - 1) * gameSign.directionX;
            int sy2 = sy1 - gameSign.verticalSigns + 1;
            int sz2 = sz1 + (gameSign.games.length - 1) * gameSign.directionZ;

            if (sx1 > sx2) {
                if (x < sx2 || x > sx1) {
                    continue;
                }

            } else if (sx1 < sx2) {
                if (x > sx2 || x < sx1) {
                    continue;
                }

            } else if (x != sx1) {
                continue;
            }

            if (sy1 > sy2) {
                if (y < sy2 || y > sy1) {
                    continue;
                }

            } else if (y != sy1) {
                continue;
            }

            if (sz1 > sz2) {
                if (z < sz2 || z > sz1) {
                    continue;
                }

            } else if (sz1 < sz2) {
                if (z > sz2 || z < sz1) {
                    continue;
                }

            } else if (z != sz1) {
                continue;
            }

            return gameSign;
        }

        return null;
    }

    /**
     * @param game
     * the game to check
     */
    public static GameSign getByGame(Game game) {
        for (GlobalProtection protection : plugin.getGlobalProtections().getProtections(GameSign.class)) {
            GameSign gameSign = (GameSign) protection;

            for (Game signGame : gameSign.games) {
                if (signGame == game) {
                    return gameSign;
                }
            }
        }

        return null;
    }

    /* SUBJECT TO CHANGE*/
    @Deprecated
    public static GameSign tryToCreate(Block startSign, String mapName, int maxGames, int maxGroupsPerGame, boolean multiFloor) {
        World world = startSign.getWorld();
        int direction = startSign.getData();
        int x = startSign.getX(), y = startSign.getY(), z = startSign.getZ();

        int verticalSigns = (int) Math.ceil((float) (1 + maxGroupsPerGame) / 4);

        CopyOnWriteArrayList<Block> changeBlocks = new CopyOnWriteArrayList<>();

        int xx, yy, zz;
        switch (direction) {
            case 2:
                zz = z;

                for (yy = y; yy > y - verticalSigns; yy--) {
                    for (xx = x; xx > x - maxGames; xx--) {
                        Block block = world.getBlockAt(xx, yy, zz);

                        if (block.getType() != Material.AIR && block.getType() != Material.WALL_SIGN) {
                            return null;
                        }

                        if (block.getRelative(0, 0, 1).getType() == Material.AIR) {
                            return null;
                        }

                        changeBlocks.add(block);
                    }
                }

                break;

            case 3:
                zz = z;
                for (yy = y; yy > y - verticalSigns; yy--) {
                    for (xx = x; xx < x + maxGames; xx++) {

                        Block block = world.getBlockAt(xx, yy, zz);
                        if (block.getType() != Material.AIR && block.getType() != Material.WALL_SIGN) {
                            return null;
                        }

                        if (block.getRelative(0, 0, -1).getType() == Material.AIR) {
                            return null;
                        }

                        changeBlocks.add(block);
                    }
                }

                break;

            case 4:
                xx = x;
                for (yy = y; yy > y - verticalSigns; yy--) {
                    for (zz = z; zz < z + maxGames; zz++) {

                        Block block = world.getBlockAt(xx, yy, zz);
                        if (block.getType() != Material.AIR && block.getType() != Material.WALL_SIGN) {
                            return null;
                        }

                        if (block.getRelative(1, 0, 0).getType() == Material.AIR) {
                            return null;
                        }

                        changeBlocks.add(block);
                    }
                }
                break;

            case 5:
                xx = x;
                for (yy = y; yy > y - verticalSigns; yy--) {
                    for (zz = z; zz > z - maxGames; zz--) {

                        Block block = world.getBlockAt(xx, yy, zz);
                        if (block.getType() != Material.AIR && block.getType() != Material.WALL_SIGN) {
                            return null;
                        }

                        if (block.getRelative(-1, 0, 0).getType() == Material.AIR) {
                            return null;
                        }

                        changeBlocks.add(block);
                    }
                }

                break;
        }

        for (Block block : changeBlocks) {
            block.setTypeIdAndData(68, startSign.getData(), true);
        }

        GameSign sign = new GameSign(protections.generateId(GameSign.class, world), startSign, mapName, maxGames, maxGroupsPerGame, multiFloor);

        return sign;
    }

    @Deprecated
    public static boolean playerInteract(Block block, Player player) {
        int x = block.getX(), y = block.getY(), z = block.getZ();
        GameSign gameSign = getByBlock(block);

        if (gameSign == null) {
            return false;
        }

        if (plugin.getGameWorlds().size() >= plugin.getMainConfig().getMaxInstances()) {
            MessageUtil.sendMessage(player, DMessages.ERROR_TOO_MANY_INSTANCES.getMessage());
            return true;
        }

        DGroup dGroup = DGroup.getByPlayer(player);

        if (dGroup == null) {
            MessageUtil.sendMessage(player, plugin.getMessageConfig().getMessage(DMessages.ERROR_JOIN_GROUP));
            return true;
        }

        if (!dGroup.getCaptain().equals(player)) {
            MessageUtil.sendMessage(player, plugin.getMessageConfig().getMessage(DMessages.ERROR_NOT_CAPTAIN));
            return true;
        }

        if (Game.getByDGroup(dGroup) != null) {
            MessageUtil.sendMessage(player, plugin.getMessageConfig().getMessage(DMessages.ERROR_LEAVE_GAME));
            return true;
        }

        int sx1 = gameSign.startSign.getX(), sy1 = gameSign.startSign.getY(), sz1 = gameSign.startSign.getZ();

        Block topBlock = block.getRelative(0, sy1 - y, 0);

        int column;
        if (gameSign.directionX != 0) {
            column = Math.abs(x - sx1);

        } else {
            column = Math.abs(z - sz1);
        }

        if (!(topBlock.getState() instanceof Sign)) {
            return true;
        }

        Sign topSign = (Sign) topBlock.getState();

        if (topSign.getLine(0).equals(NEW_GAME)) {
            Game game = new Game(dGroup);
            dGroup.setDungeonName(gameSign.dungeonName);
            dGroup.setMapName(gameSign.mapName);
            gameSign.games[column] = game;
            gameSign.update();

        } else if (topSign.getLine(0).equals(JOIN_GAME)) {
            gameSign.games[column].addDGroup(dGroup);
            gameSign.update();
        }

        return true;
    }

    @Deprecated
    public static int[] getDirection(byte data) {
        int[] direction = new int[2];

        switch (data) {
            case 2:
                direction[0] = -1;
                break;

            case 3:
                direction[0] = 1;
                break;

            case 4:
                direction[1] = 1;
                break;

            case 5:
                direction[1] = -1;
                break;
        }
        return direction;
    }

}
