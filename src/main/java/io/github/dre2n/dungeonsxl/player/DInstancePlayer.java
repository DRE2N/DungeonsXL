/*
 * Copyright (C) 2016 Daniel Saukel
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

import io.github.dre2n.commons.compatibility.CompatibilityHandler;
import io.github.dre2n.commons.compatibility.Version;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

/**
 * @author Daniel Saukel
 */
public abstract class DInstancePlayer extends DGlobalPlayer {

    private DSavePlayer savePlayer;
    private World world;
    private boolean inDungeonChat = false;

    public DInstancePlayer(Player player, World world) {
        super(player);

        double health = player.getHealth();
        if (!Version.andHigher(Version.MC1_9).contains(CompatibilityHandler.getInstance().getVersion())) {
            savePlayer = new DSavePlayer(player.getName(), player.getUniqueId(), player.getLocation(), player.getInventory().getContents(), player.getInventory().getArmorContents(), null, player.getLevel(),
                    player.getTotalExperience(), (int) health, player.getFoodLevel(), player.getFireTicks(), player.getGameMode(), player.getActivePotionEffects());
        } else {
            savePlayer = new DSavePlayer(player.getName(), player.getUniqueId(), player.getLocation(), player.getInventory().getContents(), player.getInventory().getArmorContents(), player.getInventory().getItemInOffHand(), player.getLevel(),
                    player.getTotalExperience(), (int) health, player.getFoodLevel(), player.getFireTicks(), player.getGameMode(), player.getActivePotionEffects());
        }

        this.world = world;
    }

    /* Getters and setters */
    /**
     * @return the savePlayer
     */
    public DSavePlayer getSavePlayer() {
        return savePlayer;
    }

    /**
     * @param savePlayer
     * the savePlayer to set
     */
    public void setSavePlayer(DSavePlayer savePlayer) {
        this.savePlayer = savePlayer;
    }

    /**
     * @return
     * the instance
     */
    public World getWorld() {
        return world;
    }

    /**
     * @param world
     * the instance to set
     */
    public void setWorld(World instance) {
        world = instance;
    }

    /**
     * @return the inDungeonChat
     */
    public boolean isInDungeonChat() {
        return inDungeonChat;
    }

    /**
     * @param inDungeonChat
     * the inDungeonChat to set
     */
    public void setInDungeonChat(boolean inDungeonChat) {
        this.inDungeonChat = inDungeonChat;
    }

    /* Actions */
    /**
     * Clear the player's inventory, potion effects etc.
     */
    public void clearPlayerData() {
        getPlayer().getInventory().clear();
        getPlayer().getInventory().setArmorContents(null);
        getPlayer().setTotalExperience(0);
        getPlayer().setLevel(0);
        getPlayer().setHealth(20);
        getPlayer().setFoodLevel(20);
        for (PotionEffect effect : getPlayer().getActivePotionEffects()) {
            getPlayer().removePotionEffect(effect.getType());
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
            plugin.getDPlayers().removePlayer(this);
        }
    }

    /* Abstracts */
    /**
     * The player leaves the dungeon and / or his group.
     */
    public abstract void leave();

    /**
     * Sends a message to the player and the world.
     */
    public abstract void sendMessage(String message);

    /**
     * Repeating checks for the player.
     *
     * @param updateSecond
     * Not all checks have to be done as often as others;
     * some are just done in "update seconds".
     */
    public abstract void update(boolean updateSecond);

}
