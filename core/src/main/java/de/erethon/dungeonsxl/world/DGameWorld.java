/*
 * Copyright (C) 2012-2013 Frank Baumann; 2015-2026 Daniel Saukel
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

import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.api.dungeon.Game;
import de.erethon.dungeonsxl.api.dungeon.GameRule;
import de.erethon.dungeonsxl.api.dungeon.GameRuleContainer;
import de.erethon.dungeonsxl.api.event.trigger.TriggerRegistrationEvent;
import de.erethon.dungeonsxl.api.event.world.GameWorldStartGameEvent;
import de.erethon.dungeonsxl.api.event.world.InstanceWorldPostUnloadEvent;
import de.erethon.dungeonsxl.api.event.world.InstanceWorldUnloadEvent;
import de.erethon.dungeonsxl.api.mob.DungeonMob;
import de.erethon.dungeonsxl.api.mob.MobSet;
import de.erethon.dungeonsxl.api.player.PlayerGroup;
import de.erethon.dungeonsxl.api.sign.DungeonSign;
import de.erethon.dungeonsxl.api.trigger.LogicalExpression;
import de.erethon.dungeonsxl.api.trigger.Trigger;
import de.erethon.dungeonsxl.api.trigger.TriggerListener;
import de.erethon.dungeonsxl.api.trigger.TriggerTypeKey;
import de.erethon.dungeonsxl.api.world.GameWorld;
import de.erethon.dungeonsxl.dungeon.DDungeon;
import de.erethon.dungeonsxl.mob.CitizensMobProvider;
import de.erethon.dungeonsxl.sign.button.ReadySign;
import de.erethon.dungeonsxl.sign.passive.StartSign;
import de.erethon.dungeonsxl.sign.windup.MobSign;
import de.erethon.dungeonsxl.trigger.FortuneTrigger;
import de.erethon.dungeonsxl.trigger.RedstoneTrigger;
import de.erethon.dungeonsxl.world.block.GameBlock;
import de.erethon.dungeonsxl.world.block.LockedDoor;
import de.erethon.dungeonsxl.world.block.PlaceableBlock;
import de.erethon.dungeonsxl.world.block.RewardChest;
import de.erethon.dungeonsxl.world.block.TeamBed;
import de.erethon.dungeonsxl.world.block.TeamFlag;
import de.erethon.xlib.chat.MessageUtil;
import de.erethon.xlib.compatibility.Version;
import de.erethon.xlib.util.FileUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

/**
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class DGameWorld extends DInstanceWorld implements GameWorld {

    private Game game;

    private boolean isPlaying = false;
    private boolean classes = false;

    private Set<Block> placedBlocks = new HashSet<>();

    private Set<GameBlock> gameBlocks = new HashSet<>();
    private Set<LockedDoor> lockedDoors = new HashSet<>();
    private Set<PlaceableBlock> placeableBlocks = new HashSet<>();
    private Set<RewardChest> rewardChests = new HashSet<>();
    private Set<TeamBed> teamBeds = new HashSet<>();
    private Set<TeamFlag> teamFlags = new HashSet<>();

    private List<ItemStack> secureObjects = new ArrayList<>();
    private List<DungeonMob> mobs = new ArrayList<>();
    private Map<String, MobSet> mobSets = new HashMap<>();
    private List<Trigger> triggers = new ArrayList<>();

    private boolean readySign;

    public DGameWorld(DungeonsXL plugin, DDungeon dungeon, File folder, Game game) {
        super(plugin, dungeon, folder);
        if (game == null) {
            throw new IllegalArgumentException("Game must not be null");
        }
        this.game = game;
        mobSets.put(MobSet.ALL, new MobSet(MobSet.ALL));
    }

    @Override
    public Game getGame() {
        return game;
    }

    @Override
    public Location getStartLocation(PlayerGroup dGroup) {
        int index = getGame().getGroups().indexOf(dGroup);

        // Try the matching location
        StartSign anyStartSign = null;
        for (DungeonSign sign : getDungeonSigns()) {
            if (sign instanceof StartSign) {
                anyStartSign = (StartSign) sign;
                if (anyStartSign.getId() == index) {
                    return anyStartSign.getTargetLocation();
                }
            }
        }

        // Try any start sign
        if (anyStartSign != null) {
            return anyStartSign.getTargetLocation();
        }

        // Lobby location as fallback
        if (getLobbyLocation() != null) {
            return getLobbyLocation();
        }

        return getWorld().getSpawnLocation();
    }

    @Override
    public boolean areClassesEnabled() {
        return classes;
    }

    @Override
    public void setClassesEnabled(boolean enabled) {
        classes = enabled;
    }

    @Override
    public DungeonSign createDungeonSign(Sign sign, String[] lines) {
        DungeonSign dSign = super.createDungeonSign(sign, lines);
        if (dSign == null) {
            return null;
        }

        for (Trigger trigger : dSign.getTriggers()) {
            trigger.addListener(dSign);
        }

        if (dSign.isOnDungeonInit()) {
            try {
                dSign.initialize();
            } catch (Exception exception) {
                dSign.markAsErroneous("An error occurred while initializing a sign of the type " + dSign.getName()
                        + ". This is not a user error. Please report the following stacktrace to the developer of the plugin:");
                exception.printStackTrace();
            }
            if (!dSign.isErroneous() && dSign.isSetToAir()) {
                dSign.setToAir();
            }
            if (dSign.getTriggers().isEmpty()) {
                dSign.trigger(null);
            }
        }

        return dSign;
    }

    @Override
    public Trigger createTrigger(TriggerListener owner, LogicalExpression expression) {
        if (!expression.isAtomic()) {
            throw new IllegalArgumentException("Expression is not atomic");
        }

        Trigger trigger;
        if (expression.isEmpty()) {
            trigger = owner.getDefaultTrigger();
            registerTrigger(trigger);
            return trigger;
        }

        String text = expression.getText();
        char key = Character.toUpperCase(text.charAt(0));
        if (TriggerTypeKey.hasValue(key) && text.length() < 2) {
            MessageUtil.debug(plugin, "Erroneous trigger: " + expression + " / of listener: " + owner);
            return Trigger.error(owner, expression, text);
        }
        String value;
        if (plugin.getTriggerRegistry().containsKey(key)) {
            value = text.substring(1, text.length() - 1);
        } else {
            key = TriggerTypeKey.GENERIC;
            value = text;
        }

        trigger = getTrigger(key, value);
        if (trigger != null) {
            registerTrigger(trigger);
            return trigger;
        }

        Class<? extends Trigger> clss = plugin.getTriggerRegistry().get(key);
        if (clss == null) {
            throw new IllegalStateException("Could not find trigger implementation for trigger " + key);
        }

        trigger = Trigger.construct(key, plugin, owner, expression, value);
        if (trigger == null) {
            MessageUtil.debug(plugin, "Erroneous trigger: " + expression + " / of listener:_" + owner);
            return Trigger.error(owner, expression, text);
        }

        registerTrigger(trigger);
        return trigger;
    }

    private void registerTrigger(Trigger trigger) {
        TriggerRegistrationEvent event = new TriggerRegistrationEvent(trigger);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        triggers.add(trigger);
        MessageUtil.debug(plugin, "Trigger registered: " + trigger);
    }

    public Trigger getTrigger(char key, String value) {
        if (!Trigger.IDENTIFIABLE.contains(key)) {
            return null;
        }
        for (Trigger trigger : triggers) {
            if (trigger.getKey() != key) {
                continue;
            }
            if (trigger.getValue().equalsIgnoreCase(value)) {
                return trigger;
            }
        }
        return null;
    }

    @Override
    public Collection<Block> getPlacedBlocks() {
        return placedBlocks;
    }

    /**
     * @return the placeableBlocks
     */
    public Set<GameBlock> getGameBlocks() {
        return gameBlocks;
    }

    /**
     * @param gameBlock the gameBlock to add
     */
    public void addGameBlock(GameBlock gameBlock) {
        gameBlocks.add(gameBlock);

        if (gameBlock instanceof LockedDoor) {
            lockedDoors.add((LockedDoor) gameBlock);
        } else if (gameBlock instanceof PlaceableBlock) {
            placeableBlocks.add((PlaceableBlock) gameBlock);
        } else if (gameBlock instanceof RewardChest) {
            rewardChests.add((RewardChest) gameBlock);
        } else if (gameBlock instanceof TeamBed) {
            teamBeds.add((TeamBed) gameBlock);
        } else if (gameBlock instanceof TeamFlag) {
            teamFlags.add((TeamFlag) gameBlock);
        }
    }

    /**
     * @param gameBlock the gameBlock to remove
     */
    public void removeGameBlock(GameBlock gameBlock) {
        gameBlocks.remove(gameBlock);

        if (gameBlock instanceof LockedDoor) {
            lockedDoors.remove((LockedDoor) gameBlock);
        } else if (gameBlock instanceof PlaceableBlock) {
            placeableBlocks.remove((PlaceableBlock) gameBlock);
        } else if (gameBlock instanceof RewardChest) {
            rewardChests.remove((RewardChest) gameBlock);
        } else if (gameBlock instanceof TeamBed) {
            teamBeds.remove((TeamBed) gameBlock);
        } else if (gameBlock instanceof TeamFlag) {
            teamFlags.remove((TeamFlag) gameBlock);
        }
    }

    /**
     * @return the rewardChests
     */
    public Set<RewardChest> getRewardChests() {
        return rewardChests;
    }

    /**
     * @return the locked doors
     */
    public Set<LockedDoor> getLockedDoors() {
        return lockedDoors;
    }

    /**
     * @return the placeable blocks
     */
    public Set<PlaceableBlock> getPlaceableBlocks() {
        return placeableBlocks;
    }

    /**
     * @return the team beds
     */
    public Set<TeamBed> getTeamBeds() {
        return teamBeds;
    }

    /**
     * @return the team flags
     */
    public Set<TeamFlag> getTeamFlags() {
        return teamFlags;
    }

    /**
     * @return the secureObjects
     */
    public List<ItemStack> getSecureObjects() {
        return secureObjects;
    }

    /**
     * @param secureObjects the secureObjects to set
     */
    public void setSecureObjects(List<ItemStack> secureObjects) {
        this.secureObjects = secureObjects;
    }

    @Override
    public Collection<DungeonMob> getMobs() {
        return mobs;
    }

    @Override
    public void addMob(DungeonMob mob) {
        mobs.add(mob);
    }

    @Override
    public void removeMob(DungeonMob mob) {
        mobs.remove(mob);
    }

    @Override
    public MobSet getAllMobSet() {
        return mobSets.get(MobSet.ALL);
    }

    @Override
    public Collection<MobSet> getMobSets() {
        return new ArrayList<>(mobSets.values());
    }

    @Override
    public MobSet getOrCreateMobSet(String id) {
        MobSet mobSet = mobSets.get(id);
        if (mobSet == null) {
            mobSet = new MobSet(id);
            mobSets.put(id, mobSet);
        }
        return mobSet;
    }

    @Override
    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    @Override
    public Collection<Trigger> getTriggers() {
        return triggers;
    }

    @Override
    public Collection<Trigger> getTriggersFromKey(char key) {
        return triggers.stream()
                .filter(t -> t.getKey() == key)
                .collect(Collectors.toList());
    }

    @Override
    public boolean unregisterTrigger(Trigger trigger) {
        return triggers.remove(trigger);
    }

    /**
     * @return the potential amount of mobs in the world
     */
    public int getMobCount() {
        int mobCount = 0;

        for (DungeonSign sign : getDungeonSigns().toArray(new DungeonSign[getDungeonSigns().size()])) {
            if (!(sign instanceof MobSign)) {
                continue;
            }
            mobCount += ((MobSign) sign).getInitialAmount();
        }

        return mobCount;
    }

    public boolean hasReadySign() {
        return readySign;
    }

    public void setReadySign(ReadySign readySign) {
        this.readySign = readySign != null;
    }

    /**
     * Set up the instance for the game
     */
    public void startGame() {
        GameWorldStartGameEvent event = new GameWorldStartGameEvent(this, getGame());
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        getWorld().setDifficulty(getRules().getState(GameRule.DIFFICULTY));
        Boolean doFireTick = getRules().getState(GameRule.FIRE_TICK);
        if (Version.isAtLeast(Version.MC1_21_11)) {
            getWorld().setGameRule(org.bukkit.GameRule.FIRE_SPREAD_RADIUS_AROUND_PLAYER, (doFireTick ? -1 : 0));
        } else if (Version.isAtLeast(Version.MC1_13)) {
            getWorld().setGameRule((org.bukkit.GameRule<Boolean>) org.bukkit.GameRule.getByName("DO_FIRE_TICK"), doFireTick);
        }

        isPlaying = true;

        for (DungeonSign sign : getDungeonSigns().toArray(new DungeonSign[getDungeonSigns().size()])) {
            if (sign == null || sign.isOnDungeonInit()) {
                continue;
            }
            try {
                sign.initialize();
            } catch (Exception exception) {
                sign.markAsErroneous("An error occurred while initializing a sign of the type " + sign.getName()
                        + ". This is not a user error. Please report the following stacktrace to the developer of the plugin:");
                exception.printStackTrace();
            }
            if (sign.isErroneous()) {
                continue;
            }
            if (sign.isSetToAir()) {
                sign.setToAir();
            }
            if (!sign.hasTriggers()) {
                try {
                    sign.trigger(null);
                } catch (Exception exception) {
                    sign.markAsErroneous("An error occurred while triggering a sign of the type " + getName()
                            + ". This is not a user error. Please report the following stacktrace to the developer of the plugin:");
                    exception.printStackTrace();
                }
            }
        }

        for (Trigger trigger : getTriggersFromKey(TriggerTypeKey.REDSTONE)) {
            ((RedstoneTrigger) trigger).trigger(true, null);
        }

        for (Trigger trigger : getTriggersFromKey(TriggerTypeKey.FORTUNE)) {
            ((FortuneTrigger) trigger).trigger(true, null);
        }
    }

    /**
     * Delete this instance.
     */
    @Override
    public void delete() {
        InstanceWorldUnloadEvent event = new InstanceWorldUnloadEvent(this);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        if (Bukkit.getPluginManager().getPlugin("Citizens") != null) {
            ((CitizensMobProvider) plugin.getExternalMobProviderRegistry().get("CI")).removeSpawnedNPCs(getWorld());
        }

        kickAllPlayers();

        getWorld().getEntities().forEach(Entity::remove);
        String name = getWorld().getName();
        boolean unloaded = Bukkit.unloadWorld(getWorld(), /* SPIGOT-5225 */ !Version.isAtLeast(Version.MC1_14_4));
        if (unloaded) {
            FileUtil.removeDir(getFolder());
        } else {
            MessageUtil.debug(plugin, "Error: World could not be unloaded, players left in world: " + !getWorld().getPlayers().isEmpty());
        }
        plugin.getInstanceCache().remove(this);
        Bukkit.getPluginManager().callEvent(new InstanceWorldPostUnloadEvent(getDungeon(), name));
    }

    private GameRuleContainer getRules() {
        return getDungeon().getRules();
    }

}
