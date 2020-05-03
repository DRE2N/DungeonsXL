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

import de.erethon.caliburn.CaliburnAPI;
import de.erethon.caliburn.item.ExItem;
import de.erethon.caliburn.item.VanillaItem;
import de.erethon.commons.compatibility.Internals;
import de.erethon.commons.compatibility.Version;
import de.erethon.commons.misc.BlockUtil;
import de.erethon.commons.misc.FileUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.api.dungeon.Dungeon;
import de.erethon.dungeonsxl.api.dungeon.Game;
import de.erethon.dungeonsxl.api.dungeon.GameRule;
import de.erethon.dungeonsxl.api.dungeon.GameRuleContainer;
import de.erethon.dungeonsxl.api.event.world.GameWorldStartGameEvent;
import de.erethon.dungeonsxl.api.event.world.InstanceWorldUnloadEvent;
import de.erethon.dungeonsxl.api.mob.DungeonMob;
import de.erethon.dungeonsxl.api.player.PlayerGroup;
import de.erethon.dungeonsxl.api.sign.DungeonSign;
import de.erethon.dungeonsxl.api.world.GameWorld;
import de.erethon.dungeonsxl.sign.button.ReadySign;
import de.erethon.dungeonsxl.sign.passive.StartSign;
import de.erethon.dungeonsxl.sign.windup.MobSign;
import de.erethon.dungeonsxl.trigger.FortuneTrigger;
import de.erethon.dungeonsxl.trigger.ProgressTrigger;
import de.erethon.dungeonsxl.trigger.RedstoneTrigger;
import de.erethon.dungeonsxl.trigger.Trigger;
import de.erethon.dungeonsxl.trigger.TriggerType;
import de.erethon.dungeonsxl.trigger.TriggerTypeDefault;
import de.erethon.dungeonsxl.world.block.GameBlock;
import de.erethon.dungeonsxl.world.block.LockedDoor;
import de.erethon.dungeonsxl.world.block.MultiBlock;
import de.erethon.dungeonsxl.world.block.PlaceableBlock;
import de.erethon.dungeonsxl.world.block.RewardChest;
import de.erethon.dungeonsxl.world.block.TeamBed;
import de.erethon.dungeonsxl.world.block.TeamFlag;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class DGameWorld extends DInstanceWorld implements GameWorld {

    private CaliburnAPI caliburn;
    private Game game;

    private Type type = Type.DEFAULT;

    private boolean isPlaying = false;
    private boolean classes = false;

    private List<Block> placedBlocks = new LinkedList<>();

    private Set<GameBlock> gameBlocks = new HashSet<>();
    private Set<LockedDoor> lockedDoors = new HashSet<>();
    private Set<PlaceableBlock> placeableBlocks = new HashSet<>();
    private Set<RewardChest> rewardChests = new HashSet<>();
    private Set<TeamBed> teamBeds = new HashSet<>();
    private Set<TeamFlag> teamFlags = new HashSet<>();

    private List<ItemStack> secureObjects = new ArrayList<>();
    private List<DungeonMob> mobs = new ArrayList<>();
    private List<Trigger> triggers = new ArrayList<>();

    private boolean readySign;

    DGameWorld(DungeonsXL plugin, DResourceWorld resourceWorld, File folder) {
        super(plugin, resourceWorld, folder);
        caliburn = plugin.getCaliburn();
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public Game getGame() {
        if (game == null) {
            for (Game game : plugin.getGameCache()) {
                if (game.getWorld() == this) {
                    this.game = game;
                }
            }
        }

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
                    return anyStartSign.getLocation();
                }
            }
        }

        // Try any start sign
        if (anyStartSign != null) {
            return anyStartSign.getLocation();
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

        String[] triggerTypes = lines[3].replaceAll("\\s", "").split(",");
        for (String triggerString : triggerTypes) {
            if (triggerString.isEmpty()) {
                continue;
            }

            String id = triggerString.substring(0, 1);
            String value = null;
            if (triggerString.length() > 1) {
                value = triggerString.substring(1);
            }

            Trigger trigger = Trigger.getOrCreate(plugin, id, value, dSign);
            if (trigger != null) {
                trigger.addListener(dSign);
                dSign.addTrigger(trigger);
            }
        }

        if (dSign.isOnDungeonInit()) {
            dSign.initialize();
            if (!dSign.isErroneous() && dSign.isSetToAir()) {
                dSign.setToAir();
            }
        }

        return dSign;
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
    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    /**
     * @return the triggers
     */
    public List<Trigger> getTriggers() {
        return triggers;
    }

    /**
     * @param type the type to filter
     * @return the triggers with the type
     */
    public List<Trigger> getTriggers(TriggerType type) {
        List<Trigger> triggersOfType = new ArrayList<>();
        for (Trigger trigger : triggers) {
            if (trigger.getType() == type) {
                triggersOfType.add(trigger);
            }
        }
        return triggersOfType;
    }

    /**
     * @param trigger the trigger to add
     */
    public void addTrigger(Trigger trigger) {
        triggers.add(trigger);
    }

    /**
     * @param trigger the trigger to remove
     */
    public void removeTrigger(Trigger trigger) {
        triggers.remove(trigger);
    }

    /**
     * @return the potential amount of mobs in the world
     */
    public int getMobCount() {
        int mobCount = 0;

        signs:
        for (DungeonSign sign : getDungeonSigns().toArray(new DungeonSign[getDungeonSigns().size()])) {
            if (!(sign instanceof MobSign)) {
                continue;
            }

            for (de.erethon.dungeonsxl.api.Trigger apiTrigger : sign.getTriggers()) {
                Trigger trigger = (Trigger) apiTrigger;
                if (trigger.getType() == TriggerTypeDefault.PROGRESS) {
                    if (((ProgressTrigger) trigger).getFloorCount() > getGame().getFloorCount()) {
                        break signs;
                    }
                }
            }

            mobCount += ((MobSign) sign).getInitialAmount();
        }

        return mobCount;
    }

    @Override
    public Dungeon getDungeon() {
        for (Dungeon dungeon : plugin.getDungeonRegistry()) {
            if (dungeon.containsFloor(getResource())) {
                return dungeon;
            }
        }

        return null;
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
        if (Internals.isAtLeast(Internals.v1_13_R1)) {
            getWorld().setGameRule(org.bukkit.GameRule.DO_FIRE_TICK, getRules().getState(GameRule.FIRE_TICK));
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
                sign.trigger(null);
            }
        }

        for (Trigger trigger : getTriggers(TriggerTypeDefault.REDSTONE)) {
            ((RedstoneTrigger) trigger).onTrigger();
        }

        for (Trigger trigger : getTriggers(TriggerTypeDefault.FORTUNE)) {
            ((FortuneTrigger) trigger).onTrigger();
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

        kickAllPlayers();

        Bukkit.unloadWorld(getWorld(), /* SPIGOT-5225 */ !Version.isAtLeast(Version.MC1_14_4));
        FileUtil.removeDir(getFolder());
        plugin.getInstanceCache().remove(this);
    }

    private GameRuleContainer getRules() {
        return getDungeon().getRules();
    }

    /**
     * Handles what happens when a player breaks a block.
     *
     * @param event the passed Bukkit event
     * @return if the event is cancelled
     */
    public boolean onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        for (DungeonSign sign : getDungeonSigns()) {
            if (sign == null) {
                continue;
            }
            if ((block.equals(sign.getSign().getBlock()) || block.equals(BlockUtil.getAttachedBlock(sign.getSign().getBlock()))) && sign.isProtected()) {
                return true;
            }
        }

        for (GameBlock gameBlock : gameBlocks) {
            if (block.equals(gameBlock.getBlock())) {
                if (gameBlock.onBreak(event)) {
                    return true;
                }

            } else if (gameBlock instanceof MultiBlock) {
                if (block.equals(((MultiBlock) gameBlock).getAttachedBlock())) {
                    if (gameBlock.onBreak(event)) {
                        return true;
                    }
                }
            }
        }

        Game game = getGame();
        if (game == null) {
            return true;
        }

        if (!getRules().getState(GameRule.BREAK_BLOCKS) && !getRules().getState(GameRule.BREAK_PLACED_BLOCKS)) {
            return true;
        }

        // Cancel if a protected entity is attached
        for (Entity entity : getWorld().getNearbyEntities(block.getLocation(), 2, 2, 2)) {
            if (!(entity instanceof Hanging)) {
                continue;
            }
            if (entity.getLocation().getBlock().getRelative(((Hanging) entity).getAttachedFace()).equals(block)) {
                Hanging hanging = (Hanging) entity;
                if (getRules().getState(GameRule.DAMAGE_PROTECTED_ENTITIES).contains(caliburn.getExMob(hanging))) {
                    event.setCancelled(true);
                    break;
                }
            }
        }

        Map<ExItem, HashSet<ExItem>> whitelist = getRules().getState(GameRule.BREAK_WHITELIST);
        ExItem material = VanillaItem.get(block.getType());
        ExItem breakTool = caliburn.getExItem(player.getItemInHand());

        if (getRules().getState(GameRule.BREAK_PLACED_BLOCKS) && placedBlocks.contains(block)) {
            return false;
        }
        if (whitelist != null && whitelist.containsKey(material)
                && (whitelist.get(material) == null
                || whitelist.get(material).isEmpty()
                || whitelist.get(material).contains(breakTool))) {
            return false;
        }

        return true;
    }

    /**
     * Handles what happens when a player places a block.
     *
     * @param player
     * @param block
     * @param against
     * @param hand    the event parameters.
     * @return if the event is cancelled
     */
    public boolean onPlace(Player player, Block block, Block against, ItemStack hand) {
        Game game = getGame();
        if (game == null) {
            return true;
        }

        PlaceableBlock placeableBlock = null;
        for (PlaceableBlock gamePlaceableBlock : placeableBlocks) {
            if (gamePlaceableBlock.canPlace(block, caliburn.getExItem(hand))) {
                placeableBlock = gamePlaceableBlock;
                break;
            }
        }
        if (!getRules().getState(GameRule.PLACE_BLOCKS) && placeableBlock == null) {
            // Workaround for a bug that would allow 3-Block-high jumping
            Location loc = player.getLocation();
            if (loc.getY() > block.getY() + 1.0 && loc.getY() <= block.getY() + 1.5) {
                if (loc.getX() >= block.getX() - 0.3 && loc.getX() <= block.getX() + 1.3) {
                    if (loc.getZ() >= block.getZ() - 0.3 && loc.getZ() <= block.getZ() + 1.3) {
                        loc.setX(block.getX() + 0.5);
                        loc.setY(block.getY());
                        loc.setZ(block.getZ() + 0.5);
                        player.teleport(loc);
                    }
                }
            }

            return true;
        }
        if (placeableBlock != null) {
            placeableBlock.onPlace();
        }

        Set<ExItem> whitelist = getRules().getState(GameRule.PLACE_WHITELIST);
        if (whitelist == null || whitelist.contains(VanillaItem.get(block.getType()))) {
            placedBlocks.add(block);
            return false;
        }

        return true;
    }

}
