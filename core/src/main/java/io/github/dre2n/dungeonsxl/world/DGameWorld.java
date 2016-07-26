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
package io.github.dre2n.dungeonsxl.world;

import io.github.dre2n.commons.util.FileUtil;
import io.github.dre2n.dungeonsxl.dungeon.Dungeon;
import io.github.dre2n.dungeonsxl.event.gameworld.GameWorldStartGameEvent;
import io.github.dre2n.dungeonsxl.event.gameworld.GameWorldUnloadEvent;
import io.github.dre2n.dungeonsxl.game.Game;
import io.github.dre2n.dungeonsxl.mob.DMob;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.reward.RewardChest;
import io.github.dre2n.dungeonsxl.sign.DSign;
import io.github.dre2n.dungeonsxl.sign.DSignType;
import io.github.dre2n.dungeonsxl.sign.DSignTypeDefault;
import io.github.dre2n.dungeonsxl.sign.MobSign;
import io.github.dre2n.dungeonsxl.sign.StartSign;
import io.github.dre2n.dungeonsxl.trigger.FortuneTrigger;
import io.github.dre2n.dungeonsxl.trigger.ProgressTrigger;
import io.github.dre2n.dungeonsxl.trigger.RedstoneTrigger;
import io.github.dre2n.dungeonsxl.trigger.Trigger;
import io.github.dre2n.dungeonsxl.trigger.TriggerType;
import io.github.dre2n.dungeonsxl.trigger.TriggerTypeDefault;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Spider;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class DGameWorld extends DInstanceWorld {

    // Variables
    private boolean tutorial;
    private boolean isPlaying = false;

    // TO DO: Which lists actually need to be CopyOnWriteArrayLists?
    private CopyOnWriteArrayList<GamePlaceableBlock> placeableBlocks = new CopyOnWriteArrayList<>();
    private List<ItemStack> secureObjects = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<Chunk> loadedChunks = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<Sign> classesSigns = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<DMob> dMobs = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<RewardChest> rewardChests = new CopyOnWriteArrayList<>();
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
        for (Game game : plugin.getGames()) {
            if (game.getWorld() == this) {
                return game;
            }
        }

        return null;
    }

    /**
     * @return the tutorial
     */
    public boolean isTutorial() {
        return tutorial;
    }

    /**
     * @param tutorial
     * if the DGameWorld is the tutorial
     */
    public void setTutorial(boolean tutorial) {
        this.tutorial = tutorial;
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
    public CopyOnWriteArrayList<GamePlaceableBlock> getPlaceableBlocks() {
        return placeableBlocks;
    }

    /**
     * @param placeableBlocks
     * the placeableBlocks to set
     */
    public void setPlaceableBlocks(CopyOnWriteArrayList<GamePlaceableBlock> placeableBlocks) {
        this.placeableBlocks = placeableBlocks;
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
     * @return the rewardChests
     */
    public CopyOnWriteArrayList<RewardChest> getRewardChests() {
        return rewardChests;
    }

    /**
     * @param rewardChests
     * the rewardChests to set
     */
    public void setRewardChests(CopyOnWriteArrayList<RewardChest> rewardChests) {
        this.rewardChests = rewardChests;
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
        plugin.debug.start("DGameWorld#getMobCount");
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

        plugin.debug.end("DGameWorld#getMobCount", true);
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
        plugin.getServer().getPluginManager().callEvent(event);

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
        plugin.debug.start("DGameWorld#delete");
        GameWorldUnloadEvent event = new GameWorldUnloadEvent(this);
        plugin.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        if (!plugin.getMainConfig().areTweaksEnabled()) {
            plugin.getServer().unloadWorld(getWorld(), false);
            FileUtil.removeDirectory(getFolder());
            worlds.removeInstance(this);

        } else {
            final DGameWorld gameWorld = this;
            new BukkitRunnable() {
                @Override
                public void run() {
                    plugin.getServer().unloadWorld(getWorld(), false);
                    FileUtil.removeDirectory(getFolder());
                    worlds.removeInstance(gameWorld);
                }
            }.runTaskAsynchronously(plugin);
        }
        plugin.debug.end("DGameWorld#delete", true);
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

    /* Statics */
    /**
     * @param world
     * the instance
     * @return
     * the EditWorld that represents the world
     */
    public static DGameWorld getByWorld(World world) {
        DInstanceWorld instance = plugin.getDWorlds().getInstanceByName(world.getName());

        if (instance instanceof DGameWorld) {
            return (DGameWorld) instance;

        } else {
            return null;
        }
    }

}
