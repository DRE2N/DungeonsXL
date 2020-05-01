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
package de.erethon.dungeonsxl.world;

import de.erethon.commons.chat.MessageUtil;
import de.erethon.commons.player.PlayerUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.dungeon.GameRule;
import de.erethon.dungeonsxl.api.dungeon.GameRuleContainer;
import de.erethon.dungeonsxl.api.player.InstancePlayer;
import de.erethon.dungeonsxl.api.player.PlayerCache;
import de.erethon.dungeonsxl.api.sign.DungeonSign;
import de.erethon.dungeonsxl.api.world.InstanceWorld;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

/**
 * @author Daniel Saukel
 */
public abstract class DInstanceWorld implements InstanceWorld {

    protected DungeonsXL plugin;
    protected PlayerCache dPlayers;

    static int counter;

    protected Map<Block, DungeonSign> signs = new HashMap<>();
    private DResourceWorld resourceWorld;
    private File folder;
    String world;
    private int id;
    private Location lobby;

    DInstanceWorld(DungeonsXL plugin, DResourceWorld resourceWorld, File folder) {
        this.plugin = plugin;
        dPlayers = plugin.getPlayerCache();

        this.resourceWorld = resourceWorld;
        this.folder = folder;
        id = counter++;

        plugin.getInstanceCache().add(id, this);
    }

    /* Getters and setters */
    @Override
    public String getName() {
        return resourceWorld.getName();
    }

    @Override
    public DResourceWorld getResource() {
        return resourceWorld;
    }

    @Override
    public File getFolder() {
        return folder;
    }

    @Override
    public World getWorld() {
        if (world == null) {
            return null;
        }
        return Bukkit.getWorld(world);
    }

    /**
     * Returns false if this instance does not have a world, yet
     *
     * @return false if this instance does not have a world, yet
     */
    public boolean exists() {
        return world != null;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Collection<DungeonSign> getDungeonSigns() {
        return signs.values();
    }

    @Override
    public DungeonSign createDungeonSign(Sign sign, String[] lines) {
        String type = lines[0].substring(1, lines[0].length() - 1);
        try {
            Class<? extends DungeonSign> clss = plugin.getSignRegistry().get(type.toUpperCase());
            if (clss == null) {
                return null;
            }
            Constructor constructor = clss.getConstructor(DungeonsAPI.class, Sign.class, String[].class, InstanceWorld.class);
            DungeonSign dSign = (DungeonSign) constructor.newInstance(plugin, sign, lines, this);
            signs.put(sign.getBlock(), dSign);
            return dSign;

        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException exception) {
            MessageUtil.log(plugin, "&4Could not create a dungeon sign of the type \"" + type
                    + "\". A dungeon sign implementation needs a constructor with the types (DungeonsAPI, org.bukkit.block.Sign, String[], InstanceWorld).");
            return null;
        }
    }

    @Override
    public void removeDungeonSign(DungeonSign sign) {
        signs.remove(sign.getSign().getBlock());
    }

    @Override
    public void removeDungeonSign(Block sign) {
        signs.remove(sign);
    }

    @Override
    public DungeonSign getDungeonSign(Block sign) {
        return signs.get(sign);
    }

    @Override
    public Location getLobbyLocation() {
        return lobby;
    }

    @Override
    public void setLobbyLocation(Location lobby) {
        this.lobby = lobby;
    }

    @Override
    public Collection<InstancePlayer> getPlayers() {
        return plugin.getPlayerCache().getAllInstancePlayersIf(p -> p.getInstanceWorld() == this);
    }

    /* Actions */
    @Override
    public void sendMessage(String message) {
        getPlayers().forEach(p -> MessageUtil.sendMessage(p.getPlayer(), message));
    }

    @Override
    public void kickAllPlayers() {
        getPlayers().forEach(p -> p.leave());
        // Players who shouldn't be in the dungeon but still are for some reason
        getWorld().getPlayers().forEach(p -> PlayerUtil.secureTeleport(p, Bukkit.getWorlds().get(0).getSpawnLocation()));
    }

    /**
     * @param rules sets up the time and weather to match the rules
     */
    public void setWeather(GameRuleContainer rules) {
        if (world == null || getWorld() == null) {
            return;
        }

        if (rules.getState(GameRule.THUNDER) != null) {
            if (rules.getState(GameRule.THUNDER)) {
                getWorld().setThundering(true);
                getWorld().setStorm(true);
                getWorld().setThunderDuration(Integer.MAX_VALUE);
            } else {
                getWorld().setThundering(false);
                getWorld().setStorm(false);
            }
        }

        if (rules.getState(GameRule.TIME) != null) {
            getWorld().setTime(rules.getState(GameRule.TIME));
        }
    }

    /**
     * @return a name for the instance
     * @param game whether the instance is a GameWorld
     * @param id   the id to use
     */
    public static String generateName(boolean game, int id) {
        return "DXL_" + (game ? "Game" : "Edit") + "_" + id;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{name=" + getName() + "}";
    }

}
