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
package de.erethon.dungeonsxl.player;

import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.api.player.GlobalPlayer;
import de.erethon.dungeonsxl.api.player.InstancePlayer;
import de.erethon.dungeonsxl.api.world.GameWorld;
import de.erethon.dungeonsxl.api.world.InstanceWorld;
import de.erethon.dungeonsxl.config.MainConfig;
import de.erethon.dungeonsxl.util.AttributeUtil;
import de.erethon.dungeonsxl.util.ParsingUtil;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

/**
 * @author Daniel Saukel
 */
public abstract class DInstancePlayer extends DGlobalPlayer implements InstancePlayer {

    protected MainConfig config;

    private InstanceWorld instanceWorld;

    DInstancePlayer(DungeonsXL plugin, Player player, InstanceWorld world) {
        super(plugin, player, false);

        config = plugin.getMainConfig();

        instanceWorld = world;
        getData().savePlayerState(player);
    }

    /* Getters and setters */
    @Override
    public InstanceWorld getInstanceWorld() {
        return instanceWorld;
    }

    @Override
    public World getWorld() {
        return instanceWorld.getWorld();
    }

    public void setInstanceWorld(InstanceWorld instanceWorld) {
        this.instanceWorld = instanceWorld;
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
        double maxHealth;
        if (is1_9) {
            AttributeUtil.resetPlayerAttributes(player);
            maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        } else {
            maxHealth = player.getMaxHealth();
        }
        player.setHealth(maxHealth);
        player.setFoodLevel(20);
        player.setExp(0f);
        player.setLevel(0);
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
            plugin.getPlayerCache().remove(this);
        }
    }

    /**
     * Makes the player send a message to the world.
     *
     * @param message the message to send
     */
    public void chat(String message) {
        String chatFormat = instanceWorld instanceof GameWorld ? config.getChatFormatGame() : config.getChatFormatEdit();
        instanceWorld.sendMessage(ParsingUtil.replaceChatPlaceholders(chatFormat, this) + message);

        for (GlobalPlayer player : plugin.getPlayerCache()) {
            if (player.isInChatSpyMode()) {
                if (!getWorld().getPlayers().contains(player.getPlayer())) {
                    player.sendMessage(ParsingUtil.replaceChatPlaceholders(config.getChatFormatSpy(), this) + message);
                }
            }
        }
    }

    /* Abstracts */
    /**
     * Repeating checks for the player.
     */
    public abstract void update();

}
