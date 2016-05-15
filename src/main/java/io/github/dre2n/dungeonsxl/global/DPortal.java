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
import io.github.dre2n.dungeonsxl.game.Game;
import io.github.dre2n.dungeonsxl.player.DGamePlayer;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.world.GameWorld;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class DPortal extends GlobalProtection {

    private Block block1;
    private Block block2;
    private boolean active;
    private Set<Block> blocks;

    public DPortal(int id, World world, boolean active) {
        super(world, id);
        this.active = active;
    }

    public DPortal(int id, Block block1, Block block2, boolean active) {
        super(block1.getWorld(), id);

        this.block1 = block1;
        this.block2 = block2;
        this.active = active;
    }

    /**
     * @return the block1
     */
    public Block getBlock1() {
        return block1;
    }

    /**
     * @param block1
     * the block1 to set
     */
    public void setBlock1(Block block1) {
        this.block1 = block1;
    }

    /**
     * @return the block2
     */
    public Block getBlock2() {
        return block2;
    }

    /**
     * @param block2
     * the block2 to set
     */
    public void setBlock2(Block block2) {
        this.block2 = block2;
    }

    /**
     * @return if the portal is active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active
     * set the DPortal active
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Create a new DPortal
     */
    public void create() {
        if (block1 == null || block2 == null) {
            delete();
            return;
        }

        int x1 = block1.getX(), y1 = block1.getY(), z1 = block1.getZ();
        int x2 = block2.getX(), y2 = block2.getY(), z2 = block2.getZ();
        int xcount = 0, ycount = 0, zcount = 0;

        if (x1 > x2) {
            xcount = -1;
        } else if (x1 < x2) {
            xcount = 1;
        }
        if (y1 > y2) {
            ycount = -1;
        } else if (y1 < y2) {
            ycount = 1;
        }
        if (z1 > z2) {
            zcount = -1;
        } else if (z1 < z2) {
            zcount = 1;
        }

        int xx = x1;
        do {
            int yy = y1;

            do {
                int zz = z1;

                do {
                    Material type = getWorld().getBlockAt(xx, yy, zz).getType();
                    if (type == Material.AIR || type == Material.WATER || type == Material.STATIONARY_WATER || type == Material.LAVA || type == Material.STATIONARY_LAVA || type == Material.SAPLING
                            || type == Material.WEB || type == Material.LONG_GRASS || type == Material.DEAD_BUSH || type == Material.PISTON_EXTENSION || type == Material.YELLOW_FLOWER
                            || type == Material.RED_ROSE || type == Material.BROWN_MUSHROOM || type == Material.RED_MUSHROOM || type == Material.TORCH || type == Material.FIRE
                            || type == Material.CROPS || type == Material.REDSTONE_WIRE || type == Material.REDSTONE_TORCH_OFF || type == Material.SNOW || type == Material.REDSTONE_TORCH_ON) {
                        getWorld().getBlockAt(xx, yy, zz).setType(Material.PORTAL);
                    }

                    zz = zz + zcount;
                } while (zz != z2 + zcount);

                yy = yy + ycount;
            } while (yy != y2 + ycount);

            xx = xx + xcount;
        } while (xx != x2 + xcount);
    }

    /**
     * @param player
     * the player to teleport into his dungeon
     */
    public void teleport(Player player) {
        DGroup dGroup = DGroup.getByPlayer(player);

        if (dGroup == null) {
            MessageUtil.sendMessage(player, plugin.getMessageConfig().getMessage(DMessages.ERROR_JOIN_GROUP));
            return;
        }

        GameWorld target = dGroup.getGameWorld();
        Game game = Game.getByDGroup(dGroup);

        if (target == null && game != null) {
            target = game.getWorld();
        }

        if (target == null) {
            if (game != null) {
                for (DGroup otherTeam : game.getDGroups()) {
                    if (otherTeam.getGameWorld() != null) {
                        target = otherTeam.getGameWorld();
                        break;
                    }
                }
            }
        }

        if (target == null) {
            target = new GameWorld(dGroup.getMapName());
            dGroup.setGameWorld(target);
        }

        if (target == null) {
            MessageUtil.sendMessage(player, DMessages.ERROR_DUNGEON_NOT_EXIST.getMessage(DGroup.getByPlayer(player).getMapName()));
            return;
        }

        if (game == null) {
            game = new Game(dGroup);
        }

        game.setWorld(target);
        dGroup.setGameWorld(target);

        new DGamePlayer(player, target);
    }

    @Override
    public Set<Block> getBlocks() {
        if (blocks == null) {
            if (block1 != null && block2 != null) {
                blocks = BlockUtil.getBlocksBetween(block1, block2);
            } else {
                blocks = new HashSet<>();
            }
        }

        return blocks;
    }

    @Override
    public void save(FileConfiguration configFile) {
        if (!active) {
            return;
        }

        String preString = "protections.portals." + getWorld().getName() + "." + getId();
        // Location1
        configFile.set(preString + ".loc1.x", block1.getX());
        configFile.set(preString + ".loc1.y", block1.getY());
        configFile.set(preString + ".loc1.z", block1.getZ());
        // Location1
        configFile.set(preString + ".loc2.x", block2.getX());
        configFile.set(preString + ".loc2.y", block2.getY());
        configFile.set(preString + ".loc2.z", block2.getZ());
    }

    @Override
    public void delete() {
        protections.removeProtection(this);

        int x1 = block1.getX(), y1 = block1.getY(), z1 = block1.getZ();
        int x2 = block2.getX(), y2 = block2.getY(), z2 = block2.getZ();
        int xcount = 0, ycount = 0, zcount = 0;

        if (x1 > x2) {
            xcount = -1;
        } else if (x1 < x2) {
            xcount = 1;
        }

        if (y1 > y2) {
            ycount = -1;
        } else if (y1 < y2) {
            ycount = 1;
        }

        if (z1 > z2) {
            zcount = -1;
        } else if (z1 < z2) {
            zcount = 1;
        }

        int xx = x1;
        do {
            int yy = y1;
            do {
                int zz = z1;
                do {
                    Material type = getWorld().getBlockAt(xx, yy, zz).getType();

                    if (type == Material.PORTAL) {
                        getWorld().getBlockAt(xx, yy, zz).setType(Material.AIR);
                    }

                    zz = zz + zcount;
                } while (zz != z2 + zcount);

                yy = yy + ycount;
            } while (yy != y2 + ycount);

            xx = xx + xcount;
        } while (xx != x2 + xcount);
    }

    /* Statics */
    /**
     * @param location
     * a location covered by the returned portal
     */
    public static DPortal getByLocation(Location location) {
        return getByBlock(location.getBlock());
    }

    /**
     * @param block
     * a block covered by the returned portal
     */
    public static DPortal getByBlock(Block block) {
        for (GlobalProtection protection : protections.getProtections(DPortal.class)) {
            DPortal portal = (DPortal) protection;
            if (portal.getBlock1() == null || portal.getBlock2() == null) {
                continue;
            }

            int x1 = portal.block1.getX(), y1 = portal.block1.getY(), z1 = portal.block1.getZ();
            int x2 = portal.block2.getX(), y2 = portal.block2.getY(), z2 = portal.block2.getZ();
            int x3 = block.getX(), y3 = block.getY(), z3 = block.getZ();

            if (x1 > x2) {
                if (x3 < x2 || x3 > x1) {
                    continue;
                }

            } else if (x3 > x2 || x3 < x1) {
                continue;
            }

            if (y1 > y2) {
                if (y3 < y2 || y3 > y1) {
                    continue;
                }

            } else if (y3 > y2 || y3 < y1) {
                continue;
            }

            if (z1 > z2) {
                if (z3 < z2 || z3 > z1) {
                    continue;
                }
            } else if (z3 > z2 || z3 < z1) {
                continue;
            }

            return portal;
        }

        return null;
    }

}
