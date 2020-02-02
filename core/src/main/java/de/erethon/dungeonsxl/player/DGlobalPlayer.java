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

import de.erethon.commons.chat.MessageUtil;
import de.erethon.commons.compatibility.Internals;
import de.erethon.commons.player.PlayerUtil;
import de.erethon.commons.player.PlayerWrapper;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.dungeon.Dungeon;
import de.erethon.dungeonsxl.event.dgroup.DGroupCreateEvent;
import de.erethon.dungeonsxl.game.Game;
import de.erethon.dungeonsxl.global.DPortal;
import de.erethon.dungeonsxl.util.NBTUtil;
import de.erethon.dungeonsxl.world.DGameWorld;
import java.io.File;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Represents a player in the non-DXL worlds of the server.
 *
 * @author Daniel Saukel
 */
public class DGlobalPlayer implements PlayerWrapper {

    protected DungeonsXL plugin;

    protected boolean is1_9 = Internals.isAtLeast(Internals.v1_9_R1);

    protected Player player;

    private DPlayerData data;

    private boolean breakMode = false;
    private boolean groupChat = false;
    private boolean chatSpyMode = false;
    private DPortal creatingPortal;
    private ItemStack cachedItem;
    private boolean announcerEnabled = true;

    private List<ItemStack> rewardItems;

    public DGlobalPlayer(DungeonsXL plugin, Player player) {
        this(plugin, player, false);
    }

    public DGlobalPlayer(DungeonsXL plugin, Player player, boolean reset) {
        this.plugin = plugin;

        this.player = player;

        loadPlayerData(new File(DungeonsXL.PLAYERS, player.getUniqueId().toString() + ".yml"));
        if (reset && data.wasInGame()) {
            reset(false);
        }

        plugin.getDPlayerCache().addPlayer(this);
    }

    public DGlobalPlayer(DGlobalPlayer dPlayer) {
        plugin = dPlayer.plugin;
        player = dPlayer.getPlayer();
        data = dPlayer.getData();
        breakMode = dPlayer.isInBreakMode();
        chatSpyMode = dPlayer.isInChatSpyMode();
        creatingPortal = dPlayer.getPortal();
        announcerEnabled = dPlayer.isAnnouncerEnabled();

        plugin.getDPlayerCache().addPlayer(this);
    }

    /* Getters and setters */
    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public UUID getUniqueId() {
        return player.getUniqueId();
    }

    /**
     * @return the saved data
     */
    public DPlayerData getData() {
        return data;
    }

    /**
     * Load / reload a new instance of DPlayerData
     *
     * @param file the file to load from
     */
    public void loadPlayerData(File file) {
        data = new DPlayerData(file);
    }

    /**
     * @return if the player is in break mode
     */
    public boolean isInBreakMode() {
        return breakMode;
    }

    /**
     * @param breakMode sets if the player is in break mode
     */
    public void setInBreakMode(boolean breakMode) {
        this.breakMode = breakMode;
    }

    /**
     * @return the DGroup of this player
     */
    public DGroup getDGroup() {
        return DGroup.getByPlayer(player);
    }

    /**
     * @return if the player is in group chat
     */
    public boolean isInGroupChat() {
        if (!plugin.getMainConfig().isChatEnabled()) {
            return false;
        }
        return groupChat;
    }

    /**
     * @param groupChat set if the player is in group chat
     */
    public void setInGroupChat(boolean groupChat) {
        this.groupChat = groupChat;
    }

    /**
     * @return if the player spies the DXL chat channels
     */
    public boolean isInChatSpyMode() {
        if (!plugin.getMainConfig().isChatEnabled()) {
            return false;
        }
        return chatSpyMode;
    }

    /**
     * @param chatSpyMode sets if the player is in chat spy mode
     */
    public void setInChatSpyMode(boolean chatSpyMode) {
        this.chatSpyMode = chatSpyMode;
    }

    /**
     * @return if the player is creating a DPortal
     */
    public boolean isCreatingPortal() {
        return creatingPortal != null;
    }

    /**
     * @return the portal the player is creating
     */
    public DPortal getPortal() {
        return creatingPortal;
    }

    /**
     * @param dPortal the portal to create
     */
    public void setCreatingPortal(DPortal dPortal) {
        creatingPortal = dPortal;
    }

    /**
     * @return the item the player had in his hand before he started to create a portal
     */
    public ItemStack getCachedItem() {
        return cachedItem;
    }

    /**
     * @param item the cached item to set
     */
    public void setCachedItem(ItemStack item) {
        cachedItem = item;
    }

    /**
     * @return if the players receives announcer messages
     */
    public boolean isAnnouncerEnabled() {
        return announcerEnabled;
    }

    /**
     * @param enabled set if the players receives announcer messages
     */
    public void setAnnouncerEnabled(boolean enabled) {
        announcerEnabled = enabled;
    }

    /**
     * @param permission the permission to check
     * @return if the player has the permission
     */
    public boolean hasPermission(DPermission permission) {
        return DPermission.hasPermission(player, permission);
    }

    /**
     * @return the reward items
     */
    public List<ItemStack> getRewardItems() {
        return rewardItems;
    }

    /**
     * @return if the player has reward items left
     */
    public boolean hasRewardItemsLeft() {
        return rewardItems != null;
    }

    /**
     * @param rewardItems the reward items to set
     */
    public void setRewardItems(List<ItemStack> rewardItems) {
        this.rewardItems = rewardItems;
    }

    /**
     * @param permission the permission to check
     * @return if the player has the permission
     */
    public boolean hasPermission(String permission) {
        return DPermission.hasPermission(player, permission);
    }

    /* Actions */
    /**
     * Sends a message to the player
     *
     * @param message the message to send
     */
    public void sendMessage(String message) {
        MessageUtil.sendMessage(player, message);
    }

    /**
     * Respawns the player at his old position before he was in a dungeon
     *
     * @param keepInventory if the inventory shall be reset
     */
    public void reset(boolean keepInventory) {
        final Location tpLoc = data.getOldLocation().getWorld() != null ? data.getOldLocation() : Bukkit.getWorlds().get(0).getSpawnLocation();
        if (player.isDead()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    PlayerUtil.respawn(player);
                    reset(tpLoc, keepInventory);
                }
            }.runTaskLater(plugin, 1L);
        } else {
            reset(tpLoc, keepInventory);
        }
    }

    private void reset(Location tpLoc, boolean keepInventory) {
        try {
            PlayerUtil.secureTeleport(player, tpLoc);
            player.setGameMode(data.getOldGameMode());
            if (!keepInventory) {
                while (data.getOldInventory().size() > 36) {
                    data.getOldInventory().remove(36);
                }
                player.getInventory().setContents(data.getOldInventory().toArray(new ItemStack[36]));
                player.getInventory().setArmorContents(data.getOldArmor().toArray(new ItemStack[4]));
                if (is1_9) {
                    player.getInventory().setItemInOffHand(data.getOldOffHand());
                }
                player.setLevel(data.getOldLevel());
                player.setExp(data.getOldExp());
                if (is1_9) {
                    player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(data.getOldMaxHealth());
                    player.setHealth(data.getOldHealth() <= data.getOldMaxHealth() ? data.getOldHealth() : data.getOldMaxHealth());
                } else {
                    player.setHealth(player.getMaxHealth());
                }
                player.setFoodLevel(data.getOldFoodLevel());
                player.setFireTicks(data.getOldFireTicks());
                for (PotionEffect effect : player.getActivePotionEffects()) {
                    player.removePotionEffect(effect.getType());
                }

                player.addPotionEffects(data.getOldPotionEffects());

                if (is1_9) {
                    player.setCollidable(data.getOldCollidabilityState());
                    player.setInvulnerable(data.getOldInvulnerabilityState());
                }
                player.setAllowFlight(data.getOldFlyingState());

            } else {
                for (ItemStack item : player.getInventory().getContents()) {
                    if (item == null) {
                        continue;
                    }
                    if (NBTUtil.isDungeonItem(item)) {
                        item.setAmount(0);
                    }
                }
            }

        } catch (NullPointerException exception) {
            exception.printStackTrace();
            player.setHealth(0);
            MessageUtil.log(plugin, "&4Killed player &6" + player.getName() + "&4 because the data to restore his main inventory is corrupted :(");
        }

        data.clearPlayerState();
    }

    /**
     * Starts the tutorial
     */
    public void startTutorial() {
        Dungeon dungeon = plugin.getMainConfig().getTutorialDungeon();
        if (dungeon == null) {
            MessageUtil.sendMessage(player, DMessage.ERROR_TUTORIAL_DOES_NOT_EXIST.getMessage());
            return;
        }

        if (plugin.getPermissionProvider() != null && plugin.getPermissionProvider().hasGroupSupport()) {
            String startGroup = plugin.getMainConfig().getTutorialStartGroup();
            if (startGroup == null) {
                return;
            }
            if (plugin.isGroupEnabled(startGroup)) {
                plugin.getPermissionProvider().playerAddGroup(player, startGroup);
            }
        }

        DGroup dGroup = new DGroup(plugin, "Tutorial", player, dungeon);

        DGroupCreateEvent createEvent = new DGroupCreateEvent(dGroup, player, DGroupCreateEvent.Cause.GROUP_SIGN);
        Bukkit.getPluginManager().callEvent(createEvent);
        if (createEvent.isCancelled()) {
            dGroup = null;
            return;
        }

        // The maxInstances check is already done in the listener
        DGameWorld gameWorld = dungeon.getMap().instantiateAsGameWorld(true);
        dGroup.setGameWorld(gameWorld);
        new Game(plugin, dGroup, gameWorld).setTutorial(true);
        new DGamePlayer(plugin, player, gameWorld);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{player=" + player + "}";
    }

}
