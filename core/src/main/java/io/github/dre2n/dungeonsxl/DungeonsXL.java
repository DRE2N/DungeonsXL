/*
 * Copyright (C) 2012-2017 Frank Baumann
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
package io.github.dre2n.dungeonsxl;

import io.github.dre2n.caliburn.CaliburnAPI;
import io.github.dre2n.commons.compatibility.Internals;
import io.github.dre2n.commons.config.MessageConfig;
import io.github.dre2n.commons.javaplugin.BRPlugin;
import io.github.dre2n.commons.javaplugin.BRPluginSettings;
import io.github.dre2n.dungeonsxl.announcer.Announcers;
import io.github.dre2n.dungeonsxl.command.*;
import io.github.dre2n.dungeonsxl.config.DMessages;
import io.github.dre2n.dungeonsxl.config.GlobalData;
import io.github.dre2n.dungeonsxl.config.MainConfig;
import io.github.dre2n.dungeonsxl.dungeon.Dungeons;
import io.github.dre2n.dungeonsxl.game.Game;
import io.github.dre2n.dungeonsxl.game.GameTypes;
import io.github.dre2n.dungeonsxl.global.GlobalProtections;
import io.github.dre2n.dungeonsxl.listener.*;
import io.github.dre2n.dungeonsxl.loottable.DLootTables;
import io.github.dre2n.dungeonsxl.mob.DMobTypes;
import io.github.dre2n.dungeonsxl.mob.ExternalMobProviders;
import io.github.dre2n.dungeonsxl.player.DClasses;
import io.github.dre2n.dungeonsxl.player.DGamePlayer;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.player.DPermissions;
import io.github.dre2n.dungeonsxl.player.DPlayers;
import io.github.dre2n.dungeonsxl.requirement.RequirementTypes;
import io.github.dre2n.dungeonsxl.reward.DLootInventory;
import io.github.dre2n.dungeonsxl.reward.RewardTypes;
import io.github.dre2n.dungeonsxl.sign.DSignTypes;
import io.github.dre2n.dungeonsxl.sign.SignScripts;
import io.github.dre2n.dungeonsxl.task.AnnouncerTask;
import io.github.dre2n.dungeonsxl.task.LazyUpdateTask;
import io.github.dre2n.dungeonsxl.task.SecureModeTask;
import io.github.dre2n.dungeonsxl.task.UpdateTask;
import io.github.dre2n.dungeonsxl.task.WorldUnloadTask;
import io.github.dre2n.dungeonsxl.trigger.TriggerTypes;
import io.github.dre2n.dungeonsxl.util.NoReload;
import io.github.dre2n.dungeonsxl.world.DWorlds;
import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitTask;

/**
 * The main class of DungeonsXL.
 * It contains several important instances and the actions when the plugin is enabled / disabled.
 *
 * @author Frank Baumann, Tobias Schmitz, Daniel Saukel
 */
public class DungeonsXL extends BRPlugin {

    private static DungeonsXL instance;

    public static final String[] EXCLUDED_FILES = {"config.yml", "uid.dat", "DXLData.data", "data"};
    public static File BACKUPS;
    public static File LANGUAGES;
    public static File MAPS;
    public static File PLAYERS;
    public static File SCRIPTS;
    public static File ANNOUNCERS;
    public static File CLASSES;
    public static File DUNGEONS;
    public static File LOOT_TABLES;
    public static File MOBS;
    public static File SIGNS;

    private GlobalData globalData;
    private MainConfig mainConfig;
    private MessageConfig messageConfig;

    private DCommands dCommands;
    private DSignTypes dSigns;
    private GameTypes gameTypes;
    private RequirementTypes requirementTypes;
    private RewardTypes rewardTypes;
    private TriggerTypes triggers;
    private Dungeons dungeons;
    private GlobalProtections protections;
    private ExternalMobProviders dMobProviders;
    private DPlayers dPlayers;
    private Announcers announcers;
    private DClasses dClasses;
    private DLootTables dLootTables;
    private DMobTypes dMobTypes;
    private SignScripts signScripts;
    private DWorlds dWorlds;

    private BukkitTask announcerTask;
    private BukkitTask worldUnloadTask;
    private BukkitTask lazyUpdateTask;
    private BukkitTask updateTask;
    private BukkitTask secureModeTask;

    private CopyOnWriteArrayList<DLootInventory> dLootInventories = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<Game> games = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<DGroup> dGroups = new CopyOnWriteArrayList<>();

    public DungeonsXL() {
        /*
         * ##########################
         * ####~BRPluginSettings~####
         * ##########################
         * #~Internals~##~~v1_7_R3+~#
         * #~SpigotAPI~##~~~~true~~~#
         * #~~~~UUID~~~##~~~~true~~~#
         * #~~Economy~~##~~~~true~~~#
         * #Permissions##~~~~true~~~#
         * #~~Metrics~~##~~~~true~~~#
         * #Resource ID##~~~~9488~~~#
         * ##########################
         */

        settings = new BRPluginSettings(true, true, true, true, true, 9488, Internals.andHigher(Internals.v1_7_R3));
    }

    @Override
    public void onEnable() {
        super.onEnable();
        instance = this;

        DPermissions.register();
        initFolders();
        loadCore();

        manager.registerEvents(new EntityListener(), this);
        manager.registerEvents(new GUIListener(), this);
        manager.registerEvents(new PlayerListener(), this);
        manager.registerEvents(new BlockListener(), this);
        manager.registerEvents(new WorldListener(), this);
        manager.registerEvents(new HangingListener(), this);
        if (manager.getPlugin("Citizens") != null) {
            manager.registerEvents(new CitizensListener(), this);
        }

        // Load All
        loadData();

        // Tasks
        startAnnouncerTask(mainConfig.getAnnouncmentInterval());
        startWorldUnloadTask(1200L);
        startLazyUpdateTask(20L);
        startUpdateTask(20L);
        if (mainConfig.isSecureModeEnabled()) {
            startSecureModeTask(mainConfig.getSecureModeCheckInterval());
        }

        new NoReload(this);
    }

    @Override
    public void onDisable() {
        mainConfig.setTweaksEnabled(false);
        // Save
        saveData();
        messageConfig.save();

        // DGamePlayer leaves World
        for (DGamePlayer dPlayer : dPlayers.getDGamePlayers()) {
            dPlayer.leave();
        }

        // Delete all Data
        dLootInventories.clear();
        dGroups.clear();

        // Delete DWorlds
        dWorlds.deleteAllInstances();

        // Disable listeners
        HandlerList.unregisterAll(this);

        // Stop shedulers
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

        LANGUAGES = new File(getDataFolder(), "languages");
        if (!LANGUAGES.exists()) {
            LANGUAGES.mkdir();
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

    public void loadCore() {
        if (Internals.andHigher(Internals.v1_9_R1).contains(compat.getInternals())) {
            loadCaliburnAPI();
        }
        // Load Language
        loadMessageConfig(new File(LANGUAGES, "english.yml"));
        // Load Config
        loadGlobalData(new File(getDataFolder(), "data.yml"));
        loadMainConfig(new File(getDataFolder(), "config.yml"));
        // Load Language 2
        loadMessageConfig(new File(LANGUAGES, mainConfig.getLanguage() + ".yml"));
        loadGameTypes();
        loadRequirementTypes();
        loadRewardTypes();
        loadTriggers();
        loadDSigns();
        loadDWorlds(MAPS);
        loadDungeons(DUNGEONS);
        loadGlobalProtections();
        loadExternalMobProviders();
        loadDPlayers();
        loadAnnouncers(ANNOUNCERS);
        loadDClasses(CLASSES);
        loadDLootTables(LOOT_TABLES);
        loadDMobTypes(MOBS);
        loadSignScripts(SIGNS);
        loadDCommands();
    }

    // Save and load
    public void saveData() {
        protections.saveAll();
        dWorlds.saveAll();
    }

    public void loadData() {
        protections.loadAll();
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
     * load / reload a new instance of CaliburnAPI if none exists
     */
    private void loadCaliburnAPI() {
        if (CaliburnAPI.getInstance() == null) {
            new CaliburnAPI(this).setupClean();
        }
    }

    /**
     * @return the loaded instance of GlobalData
     */
    public GlobalData getGlobalData() {
        return globalData;
    }

    /**
     * load / reload a new instance of GlobalData
     */
    public void loadGlobalData(File file) {
        globalData = new GlobalData(file);
    }

    /**
     * @return the loaded instance of MainConfig
     */
    public MainConfig getMainConfig() {
        return mainConfig;
    }

    /**
     * load / reload a new instance of MainConfig
     */
    public void loadMainConfig(File file) {
        mainConfig = new MainConfig(file);
    }

    /**
     * @return the loaded instance of MessageConfig
     */
    public MessageConfig getMessageConfig() {
        return messageConfig;
    }

    /**
     * load / reload a new instance of MessageConfig
     */
    public void loadMessageConfig(File file) {
        messageConfig = new MessageConfig(DMessages.class, file);
    }

    /**
     * @return the loaded instance of DCommands
     */
    @Override
    public DCommands getCommandCache() {
        return dCommands;
    }

    /**
     * load / reload a new instance of DCommands
     */
    public void loadDCommands() {
        dCommands = new DCommands(this);
        dCommands.register(this);
    }

    /**
     * @return the dSigns
     */
    public DSignTypes getDSigns() {
        return dSigns;
    }

    /**
     * load / reload a new instance of DSignTypes
     */
    public void loadDSigns() {
        dSigns = new DSignTypes();
    }

    /**
     * @return the game types
     */
    public GameTypes getGameTypes() {
        return gameTypes;
    }

    /**
     * load / reload a new instance of GameTypes
     */
    public void loadGameTypes() {
        gameTypes = new GameTypes();
    }

    /**
     * @return the requirement types
     */
    public RequirementTypes getRequirementTypes() {
        return requirementTypes;
    }

    /**
     * load / reload a new instance of RequirementTypes
     */
    public void loadRequirementTypes() {
        requirementTypes = new RequirementTypes();
    }

    /**
     * @return the reward types
     */
    public RewardTypes getRewardTypes() {
        return rewardTypes;
    }

    /**
     * load / reload a new instance of RewardTypes
     */
    public void loadRewardTypes() {
        rewardTypes = new RewardTypes();
    }

    /**
     * @return the triggers
     */
    public TriggerTypes getTriggers() {
        return triggers;
    }

    /**
     * load / reload a new instance of TriggerTypes
     */
    public void loadTriggers() {
        triggers = new TriggerTypes();
    }

    /**
     * @return the loaded instance of Dungeons
     */
    public Dungeons getDungeons() {
        return dungeons;
    }

    /**
     * load / reload a new instance of Dungeons
     */
    public void loadDungeons(File file) {
        dungeons = new Dungeons(file);
    }

    /**
     * @return the loaded instance of GlobalProtections
     */
    public GlobalProtections getGlobalProtections() {
        return protections;
    }

    /**
     * load / reload a new instance of GlobalProtections
     */
    public void loadGlobalProtections() {
        protections = new GlobalProtections();
    }

    /**
     * @return the loaded instance of ExternalMobProviders
     */
    public ExternalMobProviders getExternalMobProviders() {
        return dMobProviders;
    }

    /**
     * load / reload a new instance of ExternalMobProviders
     */
    public void loadExternalMobProviders() {
        dMobProviders = new ExternalMobProviders();
    }

    /**
     * @return the loaded instance of DPlayers
     */
    public DPlayers getDPlayers() {
        return dPlayers;
    }

    /**
     * load / reload a new instance of DPlayers
     */
    public void loadDPlayers() {
        dPlayers = new DPlayers();
    }

    /**
     * @return the loaded instance of Announcers
     */
    public Announcers getAnnouncers() {
        return announcers;
    }

    /**
     * load / reload a new instance of Announcers
     */
    public void loadAnnouncers(File file) {
        announcers = new Announcers(file);
    }

    /**
     * @return the loaded instance of DClasses
     */
    public DClasses getDClasses() {
        return dClasses;
    }

    /**
     * load / reload a new instance of DClasses
     */
    public void loadDClasses(File file) {
        dClasses = new DClasses(file);
    }

    /**
     * @return the loaded instance of DLootTables
     */
    public DLootTables getDLootTables() {
        return dLootTables;
    }

    /**
     * load / reload a new instance of DLootTables
     */
    public void loadDLootTables(File file) {
        dLootTables = new DLootTables(file);
    }

    /**
     * @return the loaded instance of DMobTypes
     */
    public DMobTypes getDMobTypes() {
        return dMobTypes;
    }

    /**
     * load / reload a new instance of DMobTypes
     */
    public void loadDMobTypes(File file) {
        dMobTypes = new DMobTypes(file);
    }

    /**
     * @return the loaded instance of SignScripts
     */
    public SignScripts getSignScripts() {
        return signScripts;
    }

    /**
     * load / reload a new instance of SignScripts
     */
    public void loadSignScripts(File file) {
        signScripts = new SignScripts(file);
    }

    /**
     * @return the loaded instance of DWorlds
     */
    public DWorlds getDWorlds() {
        return dWorlds;
    }

    /**
     * load / reload a new instance of DWorlds
     */
    public void loadDWorlds(File folder) {
        dWorlds = new DWorlds(MAPS);
    }

    /**
     * @return the AnnouncerTask
     */
    public BukkitTask getAnnouncerTask() {
        return announcerTask;
    }

    /**
     * start a new AnnouncerTask
     */
    public void startAnnouncerTask(long period) {
        if (!announcers.getAnnouncers().isEmpty()) {
            announcerTask = new AnnouncerTask(announcers).runTaskTimer(this, period, period);
        }
    }

    /**
     * @return the worldUnloadTask
     */
    public BukkitTask getWorldUnloadTask() {
        return worldUnloadTask;
    }

    /**
     * start a new WorldUnloadTask
     */
    public void startWorldUnloadTask(long period) {
        worldUnloadTask = new WorldUnloadTask().runTaskTimer(this, period, period);
    }

    /**
     * @return the lazyUpdateTask
     */
    public BukkitTask getLazyUpdateTask() {
        return lazyUpdateTask;
    }

    /**
     * start a new LazyUpdateTask
     */
    public void startLazyUpdateTask(long period) {
        lazyUpdateTask = new LazyUpdateTask().runTaskTimer(this, period, period);
    }

    /**
     * @return the updateTask
     */
    public BukkitTask getUpdateTask() {
        return updateTask;
    }

    /**
     * start a new LazyUpdateTask
     */
    public void startUpdateTask(long period) {
        updateTask = new UpdateTask().runTaskTimer(this, period, period);
    }

    /**
     * @return the secureModeTask
     */
    public BukkitTask getSecureModeTask() {
        return secureModeTask;
    }

    /**
     * start a new SecureModeTask
     */
    public void startSecureModeTask(long period) {
        updateTask = new SecureModeTask().runTaskTimer(this, period, period);
    }

    /**
     * @return the dLootInventories
     */
    public List<DLootInventory> getDLootInventories() {
        return dLootInventories;
    }

    /**
     * @return the games
     */
    public List<Game> getGames() {
        return games;
    }

    /**
     * @return the dGroups
     */
    public List<DGroup> getDGroups() {
        return dGroups;
    }

}
