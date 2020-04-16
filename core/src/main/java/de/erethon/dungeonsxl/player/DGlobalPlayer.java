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
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.api.Requirement;
import de.erethon.dungeonsxl.api.Reward;
import de.erethon.dungeonsxl.api.dungeon.Dungeon;
import de.erethon.dungeonsxl.api.dungeon.GameRule;
import de.erethon.dungeonsxl.api.dungeon.GameRuleContainer;
import de.erethon.dungeonsxl.api.event.requirement.RequirementCheckEvent;
import de.erethon.dungeonsxl.api.player.GlobalPlayer;
import de.erethon.dungeonsxl.api.player.PlayerGroup;
import de.erethon.dungeonsxl.api.world.GameWorld;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.dungeon.DGame;
import de.erethon.dungeonsxl.event.dgroup.DGroupCreateEvent;
import de.erethon.dungeonsxl.global.DPortal;
import de.erethon.dungeonsxl.util.LocationString;
import de.erethon.dungeonsxl.util.NBTUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Daniel Saukel
 */
public class DGlobalPlayer implements GlobalPlayer {

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

        plugin.getPlayerCache().add(player, this);
    }

    public DGlobalPlayer(DGlobalPlayer dPlayer) {
        plugin = dPlayer.plugin;
        player = dPlayer.getPlayer();
        data = dPlayer.getData();
        breakMode = dPlayer.isInBreakMode();
        chatSpyMode = dPlayer.isInChatSpyMode();
        creatingPortal = dPlayer.getPortal();
        announcerEnabled = dPlayer.isAnnouncerEnabled();

        plugin.getPlayerCache().add(player, this);
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
     * Returns the saved data.
     *
     * @return the saved data
     */
    public DPlayerData getData() {
        return data;
    }

    /**
     * Load / reload a new instance of DPlayerData.
     *
     * @param file the file to load from
     */
    public void loadPlayerData(File file) {
        data = new DPlayerData(file);
    }

    @Override
    public PlayerGroup getGroup() {
        return plugin.getPlayerGroup(player);
    }

    @Override
    public boolean isInGroupChat() {
        if (!plugin.getMainConfig().isChatEnabled()) {
            return false;
        }
        return groupChat;
    }

    @Override
    public void setInGroupChat(boolean groupChat) {
        this.groupChat = groupChat;
    }

    @Override
    public boolean isInChatSpyMode() {
        if (!plugin.getMainConfig().isChatEnabled()) {
            return false;
        }
        return chatSpyMode;
    }

    @Override
    public void setInChatSpyMode(boolean chatSpyMode) {
        this.chatSpyMode = chatSpyMode;
    }

    @Override
    public boolean isInBreakMode() {
        return breakMode;
    }

    @Override
    public void setInBreakMode(boolean breakMode) {
        this.breakMode = breakMode;
    }

    /**
     * Returns if the player is creating a DPortal.
     *
     * @return if the player is creating a DPortal
     */
    public boolean isCreatingPortal() {
        return creatingPortal != null;
    }

    /**
     * Returns the portal the player is creating.
     *
     * @return the portal the player is creating
     */
    public DPortal getPortal() {
        return creatingPortal;
    }

    /**
     * Sets the portal the player is creating.
     *
     * @param dPortal the portal to create
     */
    public void setCreatingPortal(DPortal dPortal) {
        creatingPortal = dPortal;
    }

    /**
     * Returns the item the player had in his hand before he started to create a portal.
     *
     * @return the item the player had in his hand before he started to create a portal
     */
    public ItemStack getCachedItem() {
        return cachedItem;
    }

    /**
     * Sets the cached item.
     *
     * @param item the cached item to set
     */
    public void setCachedItem(ItemStack item) {
        cachedItem = item;
    }

    /**
     * Returns if the player has announcers enabled.
     *
     * @return if the players receives announcer messages
     */
    public boolean isAnnouncerEnabled() {
        return announcerEnabled;
    }

    /**
     * Sets if the player has announcers enabled.
     *
     * @param enabled set if the players receives announcer messages
     */
    public void setAnnouncerEnabled(boolean enabled) {
        announcerEnabled = enabled;
    }

    @Override
    public List<ItemStack> getRewardItems() {
        return rewardItems;
    }

    @Override
    public boolean hasRewardItemsLeft() {
        return rewardItems != null;
    }

    @Override
    public void setRewardItems(List<ItemStack> rewardItems) {
        this.rewardItems = rewardItems;
    }

    @Override
    public boolean hasPermission(String permission) {
        return DPermission.hasPermission(player, permission);
    }

    public boolean hasPermission(DPermission permission) {
        return DPermission.hasPermission(player, permission);
    }

    @Override
    public boolean checkRequirements(Dungeon dungeon) {
        boolean fulfilled = true;
        GameRuleContainer rules = dungeon.getRules();

        if (!checkTimeAfterStart(dungeon) && !checkTimeAfterFinish(dungeon)) {
            int longestTime = rules.getState(GameRule.TIME_TO_NEXT_PLAY_AFTER_START) >= rules.getState(GameRule.TIME_TO_NEXT_PLAY_AFTER_FINISH)
                    ? rules.getState(GameRule.TIME_TO_NEXT_PLAY_AFTER_START) : rules.getState(GameRule.TIME_TO_NEXT_PLAY_AFTER_FINISH);
            MessageUtil.sendMessage(player, DMessage.ERROR_COOLDOWN.getMessage(String.valueOf(longestTime)));
            fulfilled = false;

        } else if (!checkTimeAfterStart(dungeon)) {
            MessageUtil.sendMessage(player, DMessage.ERROR_COOLDOWN.getMessage(String.valueOf(rules.getState(GameRule.TIME_TO_NEXT_PLAY_AFTER_START))));
            fulfilled = false;

        } else if (!checkTimeAfterFinish(dungeon)) {
            MessageUtil.sendMessage(player, DMessage.ERROR_COOLDOWN.getMessage(String.valueOf(rules.getState(GameRule.TIME_TO_NEXT_PLAY_AFTER_FINISH))));
            fulfilled = false;
        }

        boolean genericReqs = true;
        List<BaseComponent[]> msgs = new ArrayList<>(rules.getState(GameRule.REQUIREMENTS).size());
        for (Requirement requirement : rules.getState(GameRule.REQUIREMENTS)) {
            RequirementCheckEvent event = new RequirementCheckEvent(requirement, dungeon, player, rules.getState(GameRule.KEEP_INVENTORY_ON_ENTER));
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                continue;
            }

            msgs.add(event.getCheckMessage());
            if (!requirement.check(player)) {
                fulfilled = false;
                genericReqs = false;
            }
        }
        if (!genericReqs) {
            MessageUtil.sendMessage(player, DMessage.ERROR_REQUIREMENTS.getMessage());
            msgs.forEach(msg -> MessageUtil.sendMessage(player, msg));
        }

        if (rules.getState(GameRule.MUST_FINISH_ALL) != null) {
            List<String> finished = new ArrayList<>(rules.getState(GameRule.MUST_FINISH_ALL));
            if (rules.getState(GameRule.MUST_FINISH_ONE) != null) {
                finished.addAll(rules.getState(GameRule.MUST_FINISH_ONE));
            }

            if (!finished.isEmpty()) {

                long bestTime = 0;
                int numOfNeeded = 0;
                boolean doneTheOne = false;

                if (finished.size() == rules.getState(GameRule.MUST_FINISH_ALL).size()) {
                    doneTheOne = true;
                }

                for (String played : finished) {
                    for (String dungeonName : DungeonsXL.MAPS.list()) {
                        if (new File(DungeonsXL.MAPS, dungeonName).isDirectory()) {
                            if (played.equalsIgnoreCase(dungeonName) || played.equalsIgnoreCase("any")) {

                                Long time = getData().getTimeLastFinished(dungeonName);
                                if (time != -1) {
                                    if (rules.getState(GameRule.MUST_FINISH_ALL).contains(played)) {
                                        numOfNeeded++;
                                    } else {
                                        doneTheOne = true;
                                    }
                                    if (bestTime < time) {
                                        bestTime = time;
                                    }
                                }
                                break;

                            }
                        }
                    }
                }

                if (bestTime == 0) {
                    fulfilled = false;

                } else if (rules.getState(GameRule.TIME_LAST_PLAYED_REQUIRED_DUNGEONS) != 0) {
                    if (System.currentTimeMillis() - bestTime > rules.getState(GameRule.TIME_LAST_PLAYED_REQUIRED_DUNGEONS) * (long) 3600000) {
                        fulfilled = false;
                    }
                }

                if (numOfNeeded < rules.getState(GameRule.MUST_FINISH_ALL).size() || !doneTheOne) {
                    fulfilled = false;
                }

            }
        }

        return fulfilled || DPermission.hasPermission(player, DPermission.IGNORE_REQUIREMENTS);
    }

    public boolean checkTimeAfterStart(Dungeon dungeon) {
        return checkTime(dungeon, dungeon.getRules().getState(GameRule.TIME_TO_NEXT_PLAY_AFTER_START),
                getData().getTimeLastStarted(dungeon.getName()));
    }

    public boolean checkTimeAfterFinish(Dungeon dungeon) {
        return checkTime(dungeon, dungeon.getRules().getState(GameRule.TIME_TO_NEXT_PLAY_AFTER_FINISH),
                getData().getTimeLastFinished(dungeon.getName()));
    }

    public boolean checkTime(Dungeon dungeon, int requirement, long dataTime) {
        if (DPermission.hasPermission(player, DPermission.IGNORE_TIME_LIMIT)) {
            return true;
        }

        return dataTime == -1 || dataTime + requirement * 1000 * 60 * 60 <= System.currentTimeMillis();
    }

    public void giveLoot(Dungeon dungeon, List<Reward> ruleRewards, List<Reward> groupRewards) {
        if (!canLoot(dungeon)) {
            return;
        }
        ruleRewards.forEach(r -> r.giveTo(player.getPlayer()));
        groupRewards.forEach(r -> r.giveTo(player.getPlayer()));
        if (getGroup() != null && getGroup().getDungeon() != null) {
            getData().logTimeLastLoot(getGroup().getDungeon().getName());
        }
    }

    public boolean canLoot(Dungeon dungeon) {
        return getTimeNextLoot(dungeon) <= getData().getTimeLastStarted(dungeon.getName());
    }

    public long getTimeNextLoot(Dungeon dungeon) {
        return dungeon.getRules().getState(GameRule.TIME_TO_NEXT_LOOT) * 60 * 60 * 1000 + getData().getTimeLastLoot(dungeon.getName());
    }

    /* Actions */
    @Override
    public void sendMessage(String message) {
        MessageUtil.sendMessage(player, message);
    }

    public void heal() {
        if (is1_9) {
            player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        } else {
            player.setHealth(player.getMaxHealth());
        }
        player.setFoodLevel(20);
        player.setFireTicks(0);
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
    }

    @Override
    public void reset(boolean gameFinished) {
        Location tpLoc;
        boolean keepInventory;
        if (getGroup() != null) {
            Dungeon dungeon = getGroup().getDungeon();
            GameRuleContainer rules = dungeon.getRules();
            keepInventory = rules.getState(gameFinished ? GameRule.KEEP_INVENTORY_ON_FINISH : GameRule.KEEP_INVENTORY_ON_ESCAPE);
            LocationString tpLocString = LocationString.fromString(rules.getState(gameFinished ? GameRule.FINISH_LOCATION : GameRule.ESCAPE_LOCATION));
            if (tpLocString != null && tpLocString.getLocation() != null) {
                tpLoc = tpLocString.getLocation();
            } else {
                if (data.getOldLocation().getWorld() != null) {
                    tpLoc = data.getOldLocation();
                } else {
                    tpLoc = Bukkit.getWorlds().get(0).getSpawnLocation();
                }
            }

        } else {
            if (data.getOldLocation().getWorld() != null) {
                tpLoc = data.getOldLocation();
            } else {
                tpLoc = Bukkit.getWorlds().get(0).getSpawnLocation();
            }
            keepInventory = false;
        }

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

    @Override
    public void reset(Location tpLoc, boolean keepInventory) {
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
        GameWorld gameWorld = dungeon.getMap().instantiateGameWorld(true);
        dGroup.setGameWorld(gameWorld);
        new DGame(plugin, dGroup, gameWorld).setTutorial(true);
        new DGamePlayer(plugin, player, gameWorld);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{player=" + player + "}";
    }

}
