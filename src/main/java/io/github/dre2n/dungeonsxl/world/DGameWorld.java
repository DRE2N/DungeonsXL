/*
 * Copyright (C) 2012-2018 Frank Baumann
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
package io.github.dre2n.dungeonsxl.world;

import io.github.dre2n.commons.misc.FileUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.dungeon.Dungeon;
import io.github.dre2n.dungeonsxl.event.gameworld.GameWorldStartGameEvent;
import io.github.dre2n.dungeonsxl.event.gameworld.GameWorldUnloadEvent;
import io.github.dre2n.dungeonsxl.game.Game;
import io.github.dre2n.dungeonsxl.game.GameRuleProvider;
import io.github.dre2n.dungeonsxl.mob.DMob;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.sign.DSign;
import io.github.dre2n.dungeonsxl.sign.DSignType;
import io.github.dre2n.dungeonsxl.sign.DSignTypeDefault;
import io.github.dre2n.dungeonsxl.sign.lobby.StartSign;
import io.github.dre2n.dungeonsxl.sign.mob.MobSign;
import io.github.dre2n.dungeonsxl.trigger.FortuneTrigger;
import io.github.dre2n.dungeonsxl.trigger.ProgressTrigger;
import io.github.dre2n.dungeonsxl.trigger.RedstoneTrigger;
import io.github.dre2n.dungeonsxl.trigger.Trigger;
import io.github.dre2n.dungeonsxl.trigger.TriggerType;
import io.github.dre2n.dungeonsxl.trigger.TriggerTypeDefault;
import io.github.dre2n.dungeonsxl.world.block.GameBlock;
import io.github.dre2n.dungeonsxl.world.block.LockedDoor;
import io.github.dre2n.dungeonsxl.world.block.MultiBlock;
import io.github.dre2n.dungeonsxl.world.block.PlaceableBlock;
import io.github.dre2n.dungeonsxl.world.block.RewardChest;
import io.github.dre2n.dungeonsxl.world.block.TeamBed;
import io.github.dre2n.dungeonsxl.world.block.TeamFlag;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Spider;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * A playable resource instance.
 * There may be any amount of DGameWorlds per DResourceWorld.
 *
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class DGameWorld extends DInstanceWorld {

    public enum Type {
        START_FLOOR,
        END_FLOOR,
        DEFAULT
    }

    Game game;

    // Variables
    private Type type = Type.DEFAULT;

    private boolean isPlaying = false;

    // TO DO: Which lists actually need to be CopyOnWriteArrayLists?
    private List<Block> placedBlocks = new LinkedList<>();

    private Set<GameBlock> gameBlocks = new HashSet<>();
    private Set<LockedDoor> lockedDoors = new HashSet<>();
    private Set<PlaceableBlock> placeableBlocks = new HashSet<>();
    private Set<RewardChest> rewardChests = new HashSet<>();
    private Set<TeamBed> teamBeds = new HashSet<>();
    private Set<TeamFlag> teamFlags = new HashSet<>();

    private List<ItemStack> secureObjects = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<Chunk> loadedChunks = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<Sign> classesSigns = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<DMob> dMobs = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<DSign> dSigns = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<Trigger> triggers = new CopyOnWriteArrayList<>();

    DGameWorld(DResourceWorld resourceWorld, File folder, World world, int id) {
        super(resourceWorld, folder, world, id);
    }

    DGameWorld(DResourceWorld resourceWorld, File folder, int id) {
        this(resourceWorld, folder, null, id);
    }

    /**
     * @return
     * the Game connected to the DGameWorld
     */
    public Game getGame() {
        if (game == null) {
            for (Game game : plugin.getGames()) {
                if (game.getWorld() == this) {
                    this.game = game;
                }
            }
        }

        return game;
    }

    /**
     * @return
     * the type of the floor
     */
    public Type getType() {
        return type;
    }

    /**
     * @param type
     * the type to set
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * @return the isPlaying
     */
    public boolean isPlaying() {
        return isPlaying;
    }

    /**
     * @param isPlaying
     * the isPlaying to set
     */
    public void setPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    /**
     * @return the start location
     */
    public Location getStartLocation(DGroup dGroup) {
        int index = getGame().getDGroups().indexOf(dGroup);

        // Try the matching location
        for (DSign dSign : dSigns) {
            if (dSign.getType() == DSignTypeDefault.START) {
                if (((StartSign) dSign).getId() == index) {
                    return dSign.getSign().getLocation();
                }
            }
        }

        // Try any location
        for (DSign dSign : dSigns) {
            if (dSign.getType() == DSignTypeDefault.START) {
                return dSign.getSign().getLocation();
            }
        }

        // Lobby location as fallback
        if (getLobbyLocation() != null) {
            return getLobbyLocation();
        }

        return getWorld().getSpawnLocation();
    }

    /**
     * @return the placeableBlocks
     */
    public Set<GameBlock> getGameBlocks() {
        return gameBlocks;
    }

    /**
     * @param gameBlock
     * the gameBlock to add
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
     * @param gameBlock
     * the gameBlock to remove
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
     * @param secureObjects
     * the secureObjects to set
     */
    public void setSecureObjects(List<ItemStack> secureObjects) {
        this.secureObjects = secureObjects;
    }

    /**
     * @return the loadedChunks
     */
    public CopyOnWriteArrayList<Chunk> getLoadedChunks() {
        return loadedChunks;
    }

    /**
     * @param loadedChunks
     * the loadedChunks to set
     */
    public void setLoadedChunks(CopyOnWriteArrayList<Chunk> loadedChunks) {
        this.loadedChunks = loadedChunks;
    }

    /**
     * @return the classes signs
     */
    public CopyOnWriteArrayList<Sign> getClassesSigns() {
        return classesSigns;
    }

    /**
     * @param classes signs
     * the classes signs to set
     */
    public void setClasses(CopyOnWriteArrayList<Sign> signs) {
        classesSigns = signs;
    }

    /**
     * @return the dMobs
     */
    public CopyOnWriteArrayList<DMob> getDMobs() {
        return dMobs;
    }

    /**
     * @param dMob
     * the dMob to add
     */
    public void addDMob(DMob dMob) {
        dMobs.add(dMob);
    }

    /**
     * @param dMob
     * the dMob to remove
     */
    public void removeDMob(DMob dMob) {
        dMobs.remove(dMob);
    }

    /**
     * @return the dSigns
     */
    public CopyOnWriteArrayList<DSign> getDSigns() {
        return dSigns;
    }

    /**
     * @return the triggers with the type
     */
    public List<DSign> getDSigns(DSignType type) {
        List<DSign> dSignsOfType = new ArrayList<>();
        for (DSign dSign : dSigns) {
            if (dSign.getType() == type) {
                dSignsOfType.add(dSign);
            }
        }
        return dSignsOfType;
    }

    /**
     * @param dSigns
     * the dSigns to set
     */
    public void setDSigns(CopyOnWriteArrayList<DSign> dSigns) {
        this.dSigns = dSigns;
    }

    /**
     * @return the triggers
     */
    public CopyOnWriteArrayList<Trigger> getTriggers() {
        return triggers;
    }

    /**
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
     * @param trigger
     * the trigger to add
     */
    public void addTrigger(Trigger trigger) {
        triggers.add(trigger);
    }

    /**
     * @param trigger
     * the trigger to remove
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
        for (DSign dSign : dSigns) {
            if (!(dSign instanceof MobSign)) {
                continue;
            }

            for (Trigger trigger : dSign.getTriggers()) {
                if (trigger.getType() == TriggerTypeDefault.PROGRESS) {
                    if (((ProgressTrigger) trigger).getFloorCount() > getGame().getFloorCount()) {
                        break signs;
                    }
                }
            }

            mobCount += ((MobSign) dSign).getInitialAmount();
        }

        return mobCount;
    }

    /**
     * @return the Dungeon that contains the DGameWorld
     */
    public Dungeon getDungeon() {
        for (Dungeon dungeon : plugin.getDungeons().getDungeons()) {
            if (dungeon.getConfig().containsFloor(getResource())) {
                return dungeon;
            }
        }

        return null;
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

        isPlaying = true;

        for (DSign dSign : dSigns) {
            if (dSign != null) {
                if (!dSign.getType().isOnDungeonInit()) {
                    dSign.onInit();
                }
            }
        }

        for (Trigger trigger : getTriggers(TriggerTypeDefault.REDSTONE)) {
            ((RedstoneTrigger) trigger).onTrigger();
        }

        for (Trigger trigger : getTriggers(TriggerTypeDefault.FORTUNE)) {
            ((FortuneTrigger) trigger).onTrigger();
        }

        for (DSign dSign : dSigns) {
            if (dSign != null) {
                if (!dSign.hasTriggers()) {
                    dSign.onTrigger();
                }
            }
        }
    }

    /**
     * Delete this instance.
     */
    @Override
    public void delete() {
        GameWorldUnloadEvent event = new GameWorldUnloadEvent(this);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        if (!plugin.getMainConfig().areTweaksEnabled()) {
            Bukkit.unloadWorld(getWorld(), false);
            FileUtil.removeDirectory(getFolder());
            worlds.removeInstance(this);

        } else {
            final DGameWorld gameWorld = this;
            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.unloadWorld(getWorld(), false);
                    FileUtil.removeDirectory(getFolder());
                    worlds.removeInstance(gameWorld);
                }
            }.runTaskAsynchronously(plugin);
        }
    }

    /**
     * Ongoing updates
     */
    public void update() {
        if (getWorld() == null) {
            return;
        }

        // Update Spiders
        for (LivingEntity mob : getWorld().getLivingEntities()) {
            if (mob.getType() == EntityType.SPIDER || mob.getType() == EntityType.CAVE_SPIDER) {
                Spider spider = (Spider) mob;
                if (spider.getTarget() != null) {
                    if (spider.getTarget().getType() == EntityType.PLAYER) {
                        continue;
                    }
                }

                for (Entity player : spider.getNearbyEntities(10, 10, 10)) {
                    if (player.getType() == EntityType.PLAYER) {
                        spider.setTarget((LivingEntity) player);
                    }
                }
            }
        }
    }

    /**
     * Handles what happens when a player breaks a block.
     *
     * @param event
     * the passed Bukkit event
     * @return if the event is cancelled
     */
    public boolean onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        for (DSign dSign : dSigns) {
            if (block.equals(dSign.getSign().getBlock()) && dSign.getType().isProtected()) {
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

        GameRuleProvider rules = game.getRules();
        if (!rules.canBreakBlocks() && !rules.canBreakPlacedBlocks()) {
            return true;
        }

        Map<Material, HashSet<Material>> whitelist = rules.getBreakWhitelist();
        Material material = block.getType();
        Material breakTool = player.getItemInHand().getType();

        if (whitelist == null) {
            if (rules.canBreakPlacedBlocks()) {
                return (!placedBlocks.contains(block));
            } else if (rules.canBreakBlocks()) {
                return false;
            }

        } else if (whitelist.containsKey(material) && whitelist.get(material) == null | whitelist.get(material).isEmpty() | whitelist.get(material).contains(breakTool)) {
            if (rules.canBreakPlacedBlocks()) {
                return (!placedBlocks.contains(block));
            } else if (rules.canBreakBlocks()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Handles what happens when a player places a block.
     *
     * @param player
     * @param block
     * @param against
     * @param hand
     * the event parameters.
     * @return if the event is cancelled
     */
    public boolean onPlace(Player player, Block block, Block against, ItemStack hand) {
        Game game = getGame();
        if (game == null) {
            return true;
        }

        GameRuleProvider rules = game.getRules();
        if (!rules.canPlaceBlocks() && !PlaceableBlock.canBuildHere(block, block.getFace(against), hand.getType(), this)) {
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

        Set<Material> whitelist = rules.getPlaceWhitelist();
        if (whitelist == null || whitelist.contains(block.getType())) {
            placedBlocks.add(block);
            return false;
        }

        return true;
    }

    /* Statics */
    /**
     * @param world
     * the instance
     * @return
     * the EditWorld that represents the world
     */
    public static DGameWorld getByWorld(World world) {
        DInstanceWorld instance = DungeonsXL.getInstance().getDWorlds().getInstanceByName(world.getName());

        if (instance instanceof DGameWorld) {
            return (DGameWorld) instance;

        } else {
            return null;
        }
    }

}
