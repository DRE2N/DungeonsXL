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
package de.erethon.dungeonsxl;

import de.erethon.caliburn.CaliburnAPI;
import de.erethon.caliburn.loottable.LootTable;
import de.erethon.commons.compatibility.Internals;
import de.erethon.commons.compatibility.Version;
import de.erethon.commons.javaplugin.DREPlugin;
import de.erethon.commons.javaplugin.DREPluginSettings;
import de.erethon.commons.misc.FileUtil;
import de.erethon.commons.spiget.comparator.VersionComparator;
import de.erethon.dungeonsxl.adapter.block.BlockAdapter;
import de.erethon.dungeonsxl.adapter.block.BlockAdapterBlockData;
import de.erethon.dungeonsxl.adapter.block.BlockAdapterMagicValues;
import de.erethon.dungeonsxl.announcer.AnnouncerCache;
import de.erethon.dungeonsxl.command.DCommandCache;
import de.erethon.dungeonsxl.config.MainConfig;
import de.erethon.dungeonsxl.dungeon.DungeonCache;
import de.erethon.dungeonsxl.game.Game;
import de.erethon.dungeonsxl.game.GameTypeCache;
import de.erethon.dungeonsxl.global.GlobalData;
import de.erethon.dungeonsxl.global.GlobalProtectionCache;
import de.erethon.dungeonsxl.global.GlobalProtectionListener;
import de.erethon.dungeonsxl.mob.DMobListener;
import de.erethon.dungeonsxl.mob.DMobType;
import de.erethon.dungeonsxl.mob.ExternalMobProviderCache;
import de.erethon.dungeonsxl.player.DClassCache;
import de.erethon.dungeonsxl.player.DGroup;
import de.erethon.dungeonsxl.player.DPermission;
import de.erethon.dungeonsxl.player.DPlayerCache;
import de.erethon.dungeonsxl.requirement.RequirementTypeCache;
import de.erethon.dungeonsxl.reward.RewardListener;
import de.erethon.dungeonsxl.reward.RewardTypeCache;
import de.erethon.dungeonsxl.sign.DSignTypeCache;
import de.erethon.dungeonsxl.sign.SignScriptCache;
import de.erethon.dungeonsxl.trigger.TriggerListener;
import de.erethon.dungeonsxl.trigger.TriggerTypeCache;
import de.erethon.dungeonsxl.util.PlaceholderUtil;
import de.erethon.dungeonsxl.world.DWorldCache;
import de.erethon.vignette.api.VignetteAPI;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;

/**
 * The main class of DungeonsXL. It contains several important instances and the actions when the plugin is enabled / disabled.
 *
 * @author Frank Baumann, Tobias Schmitz, Daniel Saukel
 */
public class DungeonsXL extends DREPlugin {

    private static DungeonsXL instance;
    private CaliburnAPI caliburn;

    public static final BlockAdapter BLOCK_ADAPTER = Version.isAtLeast(Version.MC1_13) ? new BlockAdapterBlockData() : new BlockAdapterMagicValues();

    public static final String[] EXCLUDED_FILES = {"config.yml", "uid.dat", "DXLData.data", "data"};
    public static File BACKUPS;
    public static File MAPS;
    public static File PLAYERS;
    public static File SCRIPTS;
    public static File ANNOUNCERS;
    public static File CLASSES;
    public static File DUNGEONS;
    public static File LOOT_TABLES;
    public static File MOBS;
    public static File SIGNS;

    private boolean loadingWorld;

    private GlobalData globalData;
    private MainConfig mainConfig;

    private DCommandCache dCommands;
    private DSignTypeCache dSigns;
    private GameTypeCache gameTypes;
    private RequirementTypeCache requirementTypes;
    private RewardTypeCache rewardTypes;
    private TriggerTypeCache triggers;
    private DungeonCache dungeons;
    private GlobalProtectionCache protections;
    private ExternalMobProviderCache dMobProviders;
    private DPlayerCache dPlayers;
    private AnnouncerCache announcers;
    private DClassCache dClasses;
    private SignScriptCache signScripts;
    private DWorldCache dWorlds;

    private CopyOnWriteArrayList<Game> games = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<DGroup> dGroups = new CopyOnWriteArrayList<>();

    public DungeonsXL() {
        settings = DREPluginSettings.builder()
                .internals(Internals.andHigher(Internals.v1_8_R3))
                .economy(true)
                .permissions(true)
                .metrics(true)
                .spigotMCResourceId(9488)
                .versionComparator(VersionComparator.EQUAL)
                .build();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        instance = this;
        initFolders();
        loadCaliburnAPI();
        DPermission.register();
        loadConfig();
        createCaches();
        initCaches();
        loadData();
        if (manager.isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderUtil(this, "dxl").register();
        }
        VignetteAPI.init(this);
    }

    @Override
    public void onDisable() {
        saveData();
        dGroups.clear();
        dWorlds.deleteAllInstances();
        HandlerList.unregisterAll(this);
        getServer().getScheduler().cancelTasks(this);
    }

    // Init.
    public void initFolders() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        BACKUPS = new File(getDataFolder(), "backups");
        if (!BACKUPS.exists()) {
            BACKUPS.mkdir();
        }

        MAPS = new File(getDataFolder(), "maps");
        if (!MAPS.exists()) {
            MAPS.mkdir();
        }

        PLAYERS = new File(getDataFolder(), "players");
        if (!PLAYERS.exists()) {
            PLAYERS.mkdir();
        }

        SCRIPTS = new File(getDataFolder(), "scripts");
        if (!SCRIPTS.exists()) {
            SCRIPTS.mkdir();
        }

        ANNOUNCERS = new File(SCRIPTS, "announcers");
        if (!ANNOUNCERS.exists()) {
            ANNOUNCERS.mkdir();
        }

        CLASSES = new File(SCRIPTS, "classes");
        if (!CLASSES.exists()) {
            CLASSES.mkdir();
        }

        DUNGEONS = new File(SCRIPTS, "dungeons");
        if (!DUNGEONS.exists()) {
            DUNGEONS.mkdir();
        }

        LOOT_TABLES = new File(SCRIPTS, "loottables");
        if (!LOOT_TABLES.exists()) {
            LOOT_TABLES.mkdir();
        }

        MOBS = new File(SCRIPTS, "mobs");
        if (!MOBS.exists()) {
            MOBS.mkdir();
        }

        SIGNS = new File(SCRIPTS, "signs");
        if (!SIGNS.exists()) {
            SIGNS.mkdir();
        }
    }

    public void loadConfig() {
        mainConfig = new MainConfig(this, new File(getDataFolder(), "config.yml"));
    }

    public void createCaches() {
        gameTypes = new GameTypeCache();
        requirementTypes = new RequirementTypeCache();
        rewardTypes = new RewardTypeCache();
        triggers = new TriggerTypeCache();
        dSigns = new DSignTypeCache(this);
        dWorlds = new DWorldCache(this);
        dungeons = new DungeonCache(this);
        protections = new GlobalProtectionCache(this);
        dMobProviders = new ExternalMobProviderCache(this);
        dPlayers = new DPlayerCache(this);
        announcers = new AnnouncerCache(this);
        dClasses = new DClassCache(this);
        signScripts = new SignScriptCache();
        dCommands = new DCommandCache(this);
    }

    public void initCaches() {
        // Game types
        // Requirements
        Bukkit.getPluginManager().registerEvents(new RewardListener(this), this);
        Bukkit.getPluginManager().registerEvents(new TriggerListener(this), this);
        dSigns.init();
        dWorlds.init(MAPS);
        dungeons.init(DUNGEONS);
        Bukkit.getPluginManager().registerEvents(new GlobalProtectionListener(this), this);
        globalData = new GlobalData(this, new File(getDataFolder(), "data.yml"));
        globalData.load();
        dMobProviders.init();
        dPlayers.init();
        announcers.init(ANNOUNCERS);
        dClasses.init(CLASSES);
        Bukkit.getPluginManager().registerEvents(new DMobListener(), this);
        signScripts.init(SIGNS);
        dCommands.register(this);
    }

    // Save and load
    public void saveData() {
        protections.saveAll();
        dWorlds.saveAll();
    }

    public void loadData() {
        dPlayers.loadAll();
        dWorlds.check();
    }

    /* Getters and loaders */
    /**
     * @return the plugin instance
     */
    public static DungeonsXL getInstance() {
        return instance;
    }

    /**
     * @return the loaded instance of CaliburnAPI
     */
    public CaliburnAPI getCaliburn() {
        return caliburn;
    }

    private void loadCaliburnAPI() {
        caliburn = CaliburnAPI.getInstance() == null ? new CaliburnAPI(this) : CaliburnAPI.getInstance();
        if (LOOT_TABLES.isDirectory()) {
            FileUtil.getFilesForFolder(LOOT_TABLES).forEach(s -> new LootTable(caliburn, s));
        }
        if (MOBS.isDirectory()) {
            FileUtil.getFilesForFolder(MOBS).forEach(s -> caliburn.getExMobs().add(new DMobType(this, s)));
        }
    }

    /**
     * Returns true if the plugin is currently loading a world, false if not.<p>
     * If the plugin is loading a world, it is locked in order to prevent loading two at once.
     *
     * @return true if the plugin is currently loading a world, false if not
     */
    public boolean isLoadingWorld() {
        return loadingWorld;
    }

    /**
     * Notifies the plugin that a world is being loaded.<p>
     * If the plugin is loading a world, it is locked in order to prevent loading two at once.
     *
     * @param loadingWorld if a world is being loaded
     */
    public void setLoadingWorld(boolean loadingWorld) {
        this.loadingWorld = loadingWorld;
    }

    /**
     * @return the loaded instance of GlobalData
     */
    public GlobalData getGlobalData() {
        return globalData;
    }

    @Override
    public DCommandCache getCommandCache() {
        return dCommands;
    }

    /**
     * @return the loaded instance of MainConfig
     */
    public MainConfig getMainConfig() {
        return mainConfig;
    }

    /**
     * @return the dSigns
     */
    public DSignTypeCache getDSignCache() {
        return dSigns;
    }

    /**
     * @return the game types
     */
    public GameTypeCache getGameTypeCache() {
        return gameTypes;
    }

    /**
     * @return the requirement types
     */
    public RequirementTypeCache getRequirementTypeCache() {
        return requirementTypes;
    }

    /**
     * @return the reward types
     */
    public RewardTypeCache getRewardTypeCache() {
        return rewardTypes;
    }

    /**
     * @return the triggers
     */
    public TriggerTypeCache getTriggerCache() {
        return triggers;
    }

    /**
     * @return the loaded instance of DungeonCache
     */
    public DungeonCache getDungeonCache() {
        return dungeons;
    }

    /**
     * @return the loaded instance of GlobalProtectionCache
     */
    public GlobalProtectionCache getGlobalProtectionCache() {
        return protections;
    }

    /**
     * @return the loaded instance of ExternalMobProviderCache
     */
    public ExternalMobProviderCache getExternalMobProviderCache() {
        return dMobProviders;
    }

    /**
     * @return the loaded instance of DPlayerCache
     */
    public DPlayerCache getDPlayerCache() {
        return dPlayers;
    }

    /**
     * @return the loaded instance of AnnouncerCache
     */
    public AnnouncerCache getAnnouncerCache() {
        return announcers;
    }

    /**
     * @return the loaded instance of DClassCache
     */
    public DClassCache getDClassCache() {
        return dClasses;
    }

    /**
     * @return the loaded instance of SignScriptCache
     */
    public SignScriptCache getSignScriptCache() {
        return signScripts;
    }

    /**
     * @return the loaded instance of DWorldCache
     */
    public DWorldCache getDWorldCache() {
        return dWorlds;
    }

    /**
     * @return the games
     */
    public List<Game> getGameCache() {
        return games;
    }

    /**
     * @return the dGroups
     */
    public List<DGroup> getDGroupCache() {
        return dGroups;
    }

    @Deprecated
    private Set<Inventory> guis = new HashSet<>();

    @Deprecated
    public Set<Inventory> getGUIs() {
        return guis;
    }

}
