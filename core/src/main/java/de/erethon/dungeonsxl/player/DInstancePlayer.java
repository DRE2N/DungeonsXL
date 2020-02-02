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
package de.erethon.dungeonsxl.player;

import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.config.MainConfig;
import de.erethon.dungeonsxl.util.ParsingUtil;
import de.erethon.dungeonsxl.world.DGameWorld;
import de.erethon.dungeonsxl.world.DInstanceWorld;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

/**
 * Represents a player in an instance.
 *
 * @author Daniel Saukel
 */
public abstract class DInstancePlayer extends DGlobalPlayer {

    MainConfig config;

    private World world;

    DInstancePlayer(DungeonsXL plugin, Player player, World world) {
        super(plugin, player, false);

        config = plugin.getMainConfig();

        this.world = world;
        getData().savePlayerState(player);
    }

    /* Getters and setters */
    /**
     * @return the instance
     */
    public World getWorld() {
        return world;
    }

    /**
     * @param instance the instance to set
     */
    public void setWorld(World instance) {
        world = instance;
    }

    // Players in dungeons never get announcer messages
    @Override
    public boolean isAnnouncerEnabled() {
        return false;
    }

    /* Actions */
    /**
     * Clear the player's inventory, potion effects etc.
     * <p>
     * Does NOT handle flight.
     */
    public void clearPlayerData() {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setExp(0f);
        player.setLevel(0);
        player.setMaxHealth(20);
        player.setHealth(20);
        player.setFoodLevel(20);
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        if (is1_9) {
            player.setCollidable(true);
            player.setInvulnerable(false);
        }
    }

    /**
     * Delete this DInstancePlayer. Creates a DGlobalPlayer to replace it!
     */
    public void delete() {
        if (player.isOnline()) {
            // Create a new DGlobalPlayer (outside a dungeon)
            new DGlobalPlayer(this);

        } else {
            plugin.getDPlayerCache().removePlayer(this);
        }
    }

    /**
     * Makes the player send a message to the world.
     *
     * @param message the message to send
     */
    public void chat(String message) {
        DInstanceWorld instance = plugin.getDWorldCache().getInstanceByWorld(world);
        if (instance == null) {
            return;
        }
        String chatFormat = instance instanceof DGameWorld ? config.getChatFormatGame() : config.getChatFormatEdit();
        instance.sendMessage(ParsingUtil.replaceChatPlaceholders(chatFormat, this) + message);

        for (DGlobalPlayer player : plugin.getDPlayerCache().getDGlobalPlayers()) {
            if (player.isInChatSpyMode()) {
                if (!instance.getWorld().getPlayers().contains(player.getPlayer())) {
                    player.sendMessage(ParsingUtil.replaceChatPlaceholders(config.getChatFormatSpy(), this) + message);
                }
            }
        }
    }

    /* Abstracts */
    /**
     * The player leaves the dungeon and / or his group.
     */
    public abstract void leave();

    /**
     * Repeating checks for the player.
     *
     * @param updateSecond Not all checks have to be done as often as others; some are just done in "update seconds".
     */
    public abstract void update(boolean updateSecond);

}
