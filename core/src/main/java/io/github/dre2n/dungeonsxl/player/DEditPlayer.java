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
package io.github.dre2n.dungeonsxl.player;

import io.github.dre2n.commons.util.messageutil.MessageUtil;
import io.github.dre2n.commons.util.playerutil.PlayerUtil;
import io.github.dre2n.dungeonsxl.config.DMessages;
import io.github.dre2n.dungeonsxl.event.dplayer.instance.DInstancePlayerUpdateEvent;
import io.github.dre2n.dungeonsxl.task.CreateDInstancePlayerTask;
import io.github.dre2n.dungeonsxl.world.DEditWorld;
import java.util.concurrent.CopyOnWriteArrayList;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

/**
 * Represents a player in a DEditWorld.
 *
 * @author Daniel Saukel
 */
public class DEditPlayer extends DInstancePlayer {

    private String[] linesCopy;

    public DEditPlayer(Player player, DEditWorld world) {
        super(player, world.getWorld());

        player.setGameMode(GameMode.CREATIVE);
        clearPlayerData();

        Location teleport = world.getLobbyLocation();
        if (teleport == null) {
            PlayerUtil.secureTeleport(player, world.getWorld().getSpawnLocation());
        } else {
            PlayerUtil.secureTeleport(player, teleport);
        }

        // Permission bridge
        if (plugin.getPermissionProvider() != null) {
            for (String permission : plugin.getMainConfig().getEditPermissions()) {
                plugin.getPermissionProvider().playerAddTransient(world.getName(), player, permission);
            }
        }
    }

    /**
     * @param player
     * the represented Player
     * @param editWorld
     * the player's EditWorld
     */
    public static void create(Player player, DEditWorld editWorld) {
        new CreateDInstancePlayerTask(player, editWorld).runTaskTimer(plugin, 0L, 5L);
    }

    /* Getters and setters */
    /**
     * @return the linesCopy
     */
    public String[] getLinesCopy() {
        return linesCopy;
    }

    /**
     * @param linesCopy
     * the linesCopy to set
     */
    public void setLinesCopy(String[] linesCopy) {
        this.linesCopy = linesCopy;
    }

    /* Actions */
    @Override
    public void delete() {
        // Permission bridge
        if (plugin.getPermissionProvider() != null) {
            for (String permission : plugin.getMainConfig().getEditPermissions()) {
                plugin.getPermissionProvider().playerRemoveTransient(getWorld().getName(), player, permission);
            }
        }

        super.delete();
    }

    /**
     * Escape the DEditWorld without saving.
     */
    public void escape() {
        delete();
        reset(false);
    }

    public void poke(Block block) {
        if (block.getState() instanceof Sign) {
            Sign sign = (Sign) block.getState();
            String[] lines = sign.getLines();
            if (lines[0].isEmpty() && lines[1].isEmpty() && lines[2].isEmpty() && lines[3].isEmpty()) {
                if (linesCopy != null) {
                    SignChangeEvent event = new SignChangeEvent(block, getPlayer(), linesCopy);
                    plugin.getServer().getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        sign.setLine(0, event.getLine(0));
                        sign.setLine(1, event.getLine(1));
                        sign.setLine(2, event.getLine(2));
                        sign.setLine(3, event.getLine(3));
                        sign.update();
                    }
                }
            } else {
                linesCopy = lines;
                MessageUtil.sendMessage(getPlayer(), DMessages.PLAYER_SIGN_COPIED.getMessage());
            }
        } else {
            String info = "" + block.getType();
            if (block.getData() != 0) {
                info = info + "," + block.getData();
            }
            MessageUtil.sendMessage(getPlayer(), DMessages.PLAYER_BLOCK_INFO.getMessage(info));
        }
    }

    @Override
    public void leave() {
        delete();

        reset(false);

        DEditWorld editWorld = DEditWorld.getByWorld(getWorld());
        if (editWorld != null) {
            editWorld.save();
        }
    }

    @Override
    public void sendMessage(String message) {
        DEditWorld editWorld = DEditWorld.getByWorld(getWorld());
        editWorld.sendMessage(message);

        for (DGlobalPlayer player : plugin.getDPlayers().getDGlobalPlayers()) {
            if (player.isInChatSpyMode()) {
                if (!editWorld.getWorld().getPlayers().contains(player.getPlayer())) {
                    MessageUtil.sendMessage(player.getPlayer(), ChatColor.GREEN + "[Chatspy] " + ChatColor.WHITE + message);
                }
            }
        }
    }

    @Override
    public void update(boolean updateSecond) {
        boolean locationValid = true;
        Location teleportLocation = player.getLocation();

        DEditWorld editWorld = DEditWorld.getByWorld(getWorld());

        if (!getPlayer().getWorld().equals(getWorld())) {
            locationValid = false;
        }

        if (editWorld != null) {
            if (editWorld.getLobbyLocation() == null) {
                teleportLocation = editWorld.getWorld().getSpawnLocation();
            } else {
                teleportLocation = editWorld.getLobbyLocation();
            }
        }

        DInstancePlayerUpdateEvent event = new DInstancePlayerUpdateEvent(this, locationValid, false, false, false, false, false);
        plugin.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        if (!locationValid) {
            PlayerUtil.secureTeleport(getPlayer(), teleportLocation);
        }
    }

    /* Statics */
    public static DEditPlayer getByPlayer(Player player) {
        for (DEditPlayer dPlayer : plugin.getDPlayers().getDEditPlayers()) {
            if (dPlayer.getPlayer().equals(player)) {
                return dPlayer;
            }
        }
        return null;
    }

    public static DEditPlayer getByName(String name) {
        for (DEditPlayer dPlayer : plugin.getDPlayers().getDEditPlayers()) {
            if (dPlayer.getName().equalsIgnoreCase(name)) {
                return dPlayer;
            }
        }
        return null;
    }

    public static CopyOnWriteArrayList<DEditPlayer> getByWorld(World world) {
        CopyOnWriteArrayList<DEditPlayer> dPlayers = new CopyOnWriteArrayList<>();

        for (DEditPlayer dPlayer : plugin.getDPlayers().getDEditPlayers()) {
            if (dPlayer.getWorld() == world) {
                dPlayers.add(dPlayer);
            }
        }

        return dPlayers;
    }

}
