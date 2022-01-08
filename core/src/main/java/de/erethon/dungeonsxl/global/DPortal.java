/*
 * Copyright (C) 2012-2022 Frank Baumann
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

import de.erethon.caliburn.item.ExItem;
import de.erethon.caliburn.item.VanillaItem;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.api.dungeon.Dungeon;
import de.erethon.dungeonsxl.api.dungeon.Game;
import de.erethon.dungeonsxl.api.player.PlayerGroup;
import de.erethon.dungeonsxl.api.world.GameWorld;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.dungeon.DGame;
import de.erethon.dungeonsxl.player.DGamePlayer;
import de.erethon.dungeonsxl.player.DGlobalPlayer;
import de.erethon.dungeonsxl.util.commons.chat.MessageUtil;
import de.erethon.dungeonsxl.util.commons.misc.BlockUtil;
import java.util.HashSet;
import java.util.Set;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * A portal that leads into a dungeon.
 *
 * @author Frank Baumann, Daniel Saukel
 */
public class DPortal extends GlobalProtection {

    private Block block1;
    private Block block2;
    private ExItem material = VanillaItem.NETHER_PORTAL;
    private boolean zAxis;
    private boolean active;
    private Set<Block> blocks;

    public DPortal(DungeonsXL plugin, int id, World world, ExItem material, boolean active) {
        super(plugin, world, id);

        this.material = material;
        this.active = active;
    }

    public DPortal(DungeonsXL plugin, World world, int id, ConfigurationSection config) {
        super(plugin, world, id);

        block1 = world.getBlockAt(config.getInt("loc1.x"), config.getInt("loc1.y"), config.getInt("loc1.z"));
        block2 = world.getBlockAt(config.getInt("loc2.x"), config.getInt("loc2.y"), config.getInt("loc2.z"));
        material = plugin.getCaliburn().getExItem(config.getString("material"));
        if (material == null) {
            material = VanillaItem.NETHER_PORTAL;
        }
        String axis = config.getString("axis");
        zAxis = axis != null && axis.equalsIgnoreCase("z");
        active = true;
        create(null);
    }

    /**
     * @return the block1
     */
    public Block getBlock1() {
        return block1;
    }

    /**
     * @param block1 the block1 to set
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
     * @param block2 the block2 to set
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
     * @param active set the DPortal active
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Create a new DPortal
     *
     * @param player the creator
     */
    public void create(DGlobalPlayer player) {
        if (block1 == null || block2 == null) {
            delete();
            return;
        }

        if (player != null && material == VanillaItem.NETHER_PORTAL) {
            float yaw = player.getPlayer().getLocation().getYaw();
            if (yaw >= 45 & yaw < 135 || yaw >= 225 & yaw < 315) {
                zAxis = true;
            } else if (yaw >= 315 | yaw < 45 || yaw >= 135 & yaw < 225) {
                zAxis = false;
            }
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
                    if (!type.isSolid()) {
                        Block block = getWorld().getBlockAt(xx, yy, zz);
                        block.setType(material.getMaterial(), false);
                        if (material == VanillaItem.NETHER_PORTAL) {
                            DungeonsXL.BLOCK_ADAPTER.setAxis(block, zAxis);
                        }
                    }

                    zz = zz + zcount;
                } while (zz != z2 + zcount);

                yy = yy + ycount;
            } while (yy != y2 + ycount);

            xx = xx + xcount;
        } while (xx != x2 + xcount);

        if (player == null) {
            return;
        }
        player.getPlayer().getInventory().setItemInHand(player.getCachedItem());
        player.setCachedItem(null);

        if (material != VanillaItem.NETHER_PORTAL) {
            player.sendMessage(DMessage.PLAYER_PORTAL_CREATED.getMessage());
            player.setCreatingPortal(null);

        } else {
            ClickEvent onClickYes = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dungeonsxl portal " + VanillaItem.NETHER_PORTAL.getId() + " -rotate");
            TextComponent yes = new TextComponent(DMessage.BUTTON_ACCEPT.getMessage());
            yes.setClickEvent(onClickYes);

            ClickEvent onClickNo = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dungeonsxl portal " + VanillaItem.NETHER_PORTAL.getId() + " -norotate");
            TextComponent no = new TextComponent(DMessage.BUTTON_DENY.getMessage());
            no.setClickEvent(onClickNo);

            player.sendMessage(DMessage.PLAYER_PORTAL_ROTATE.getMessage());
            MessageUtil.sendMessage(player.getPlayer(), yes, new TextComponent(" "), no);
        }
    }

    public void rotate() {
        zAxis = !zAxis;
        for (Block block : getBlocks()) {
            if (block.getType() == VanillaItem.NETHER_PORTAL.getMaterial()) {
                DungeonsXL.BLOCK_ADAPTER.setAxis(block, zAxis);
            }
        }
    }

    /**
     * @param player the player to teleport into his dungeon
     */
    public void teleport(Player player) {
        if (plugin.isLoadingWorld()) {
            return;
        }

        PlayerGroup group = plugin.getPlayerGroup(player);
        if (group == null) {
            MessageUtil.sendActionBarMessage(player, DMessage.ERROR_JOIN_GROUP.getMessage());
            return;
        }

        Dungeon dungeon = group.getDungeon();
        if (dungeon == null) {
            MessageUtil.sendActionBarMessage(player, DMessage.ERROR_NO_SUCH_DUNGEON.getMessage());
            return;
        }

        if (!plugin.getPlayerCache().get(player).checkRequirements(dungeon)) {
            return;
        }

        Game game = group.getGame();
        if (game == null) {
            game = new DGame(plugin, dungeon, group);
        }
        GameWorld target = game.ensureWorldIsLoaded(false);
        if (target == null) {
            MessageUtil.sendActionBarMessage(player, DMessage.ERROR_TOO_MANY_INSTANCES.getMessage());
            return;
        }

        new DGamePlayer(plugin, player, target);
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
    public String getDataPath() {
        return "protections.portals";
    }

    @Override
    public void save(ConfigurationSection config) {
        if (!active) {
            return;
        }

        config.set("loc1.x", block1.getX());
        config.set("loc1.y", block1.getY());
        config.set("loc1.z", block1.getZ());

        config.set("loc2.x", block2.getX());
        config.set("loc2.y", block2.getY());
        config.set("loc2.z", block2.getZ());

        config.set("material", material.getId());
        if (material == VanillaItem.NETHER_PORTAL) {
            config.set("axis", zAxis ? "z" : "x");
        }
    }

    @Override
    public void delete() {
        plugin.getGlobalProtectionCache().removeProtection(this);

        if (block1 == null || block2 == null) {
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

                    if (material.getMaterial() == type) {
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
     * @param plugin   the plugin instance
     * @param location a location covered by the returned portal
     * @return the portal at the location, null if there is none
     */
    public static DPortal getByLocation(DungeonsXL plugin, Location location) {
        return getByBlock(plugin, location.getBlock());
    }

    /**
     * @param plugin the plugin instance
     * @param block  a block covered by the returned portal
     * @return the portal that the block belongs to, null if it belongs to none
     */
    public static DPortal getByBlock(DungeonsXL plugin, Block block) {
        if (plugin.isInstance(block.getWorld())) {
            return null;
        }
        for (GlobalProtection protection : plugin.getGlobalProtectionCache().getProtections(DPortal.class)) {
            DPortal portal = (DPortal) protection;
            if (!portal.getWorld().equals(block.getWorld()) || portal.getBlock1() == null || portal.getBlock2() == null) {
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
