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
package de.erethon.dungeonsxl;

import de.erethon.caliburn.CaliburnAPI;
import de.erethon.caliburn.loottable.LootTable;
import de.erethon.commons.compatibility.Internals;
import de.erethon.commons.config.MessageConfig;
import de.erethon.commons.javaplugin.DREPlugin;
import de.erethon.commons.javaplugin.DREPluginSettings;
import de.erethon.commons.misc.FileUtil;
import de.erethon.dungeonsxl.announcer.AnnouncerCache;
import de.erethon.dungeonsxl.command.DCommandCache;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.config.GlobalData;
import de.erethon.dungeonsxl.config.MainConfig;
import de.erethon.dungeonsxl.dungeon.DungeonCache;
import de.erethon.dungeonsxl.game.Game;
import de.erethon.dungeonsxl.game.GameTypeCache;
import de.erethon.dungeonsxl.global.GlobalProtectionCache;
import de.erethon.dungeonsxl.mob.DMobListener;
import de.erethon.dungeonsxl.mob.DMobType;
import de.erethon.dungeonsxl.mob.ExternalMobProviderCache;
import de.erethon.dungeonsxl.player.DClassCache;
import de.erethon.dungeonsxl.player.DGroup;
import de.erethon.dungeonsxl.player.DPermission;
import de.erethon.dungeonsxl.player.DPlayerCache;
import de.erethon.dungeonsxl.requirement.RequirementTypeCache;
import de.erethon.dungeonsxl.reward.RewardTypeCache;
import de.erethon.dungeonsxl.sign.DSignTypeCache;
import de.erethon.dungeonsxl.sign.SignScriptCache;
import de.erethon.dungeonsxl.trigger.TriggerTypeCache;
import de.erethon.dungeonsxl.util.NoReload;
import de.erethon.dungeonsxl.world.DWorldCache;
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
        /*
         * ##########################
         * ####~DREPluginSettings~###
         * ##########################
         * #~Internals~##~~v1_8_R3+~#
         * #~SpigotAPI~##~~~~true~~~#
         * #~~~~UUID~~~##~~~~true~~~#
         * #~~Economy~~##~~~~true~~~#
         * #Permissions##~~~~true~~~#
         * #~~Metrics~~##~~~~true~~~#
         * #Resource ID##~~~~9488~~~#
         * ##########################
         */

        settings = new DREPluginSettings(true, true, true, true, true, 9488, Internals.andHigher(Internals.v1_8_R3));
    }

    @Override
    public void onEnable() {
        super.onEnable();
        instance = this;

        DPermission.register();
        initFolders();
        loadCore();
        loadData();

        new NoReload(this);
    }

    @Override
    public void onDisable() {
        // Save
        saveData();
        messageConfig.save();

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
        loadCaliburnAPI();
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
        loadLootTables(LOOT_TABLES);
        loadMobs(MOBS);
        loadSignScripts(SIGNS);
        loadDCommandCache();
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
     * @return the loaded instance of CaliburnAPI
     */
    public CaliburnAPI getCaliburn() {
        return caliburn;
    }

    /**
     * load / reload a new instance of CaliburnAPI if none exists
     */
    private void loadCaliburnAPI() {
        caliburn = CaliburnAPI.getInstance() == null ? new CaliburnAPI(this) : CaliburnAPI.getInstance();
    }

    /**
     * @return the loaded instance of GlobalData
     */
    public GlobalData getGlobalData() {
        return globalData;
    }

    /**
     * load / reload a new instance of GlobalData
     *
     * @param file the file to load from
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
     *
     * @param file the file to load from
     */
    public void loadMainConfig(File file) {
        mainConfig = new MainConfig(file);
    }

    /**
     * load / reload a new instance of MessageConfig
     *
     * @param file the file to load from
     */
    public void loadMessageConfig(File file) {
        messageConfig = new MessageConfig(DMessage.class, file);
    }

    /**
     * @return the loaded instance of DCommandCache
     */
    @Override
    public DCommandCache getCommandCache() {
        return dCommands;
    }

    /**
     * load / reload a new instance of DCommandCache
     */
    public void loadDCommandCache() {
        dCommands = new DCommandCache(this);
        dCommands.register(this);
    }

    /**
     * @return the dSigns
     */
    public DSignTypeCache getDSigns() {
        return dSigns;
    }

    /**
     * load / reload a new instance of DSignTypeCache
     */
    public void loadDSigns() {
        dSigns = new DSignTypeCache();
    }

    /**
     * @return the game types
     */
    public GameTypeCache getGameTypes() {
        return gameTypes;
    }

    /**
     * load / reload a new instance of GameTypeCache
     */
    public void loadGameTypes() {
        gameTypes = new GameTypeCache();
    }

    /**
     * @return the requirement types
     */
    public RequirementTypeCache getRequirementTypes() {
        return requirementTypes;
    }

    /**
     * load / reload a new instance of RequirementTypeCache
     */
    public void loadRequirementTypes() {
        requirementTypes = new RequirementTypeCache();
    }

    /**
     * @return the reward types
     */
    public RewardTypeCache getRewardTypes() {
        return rewardTypes;
    }

    /**
     * load / reload a new instance of RewardTypeCache
     */
    public void loadRewardTypes() {
        rewardTypes = new RewardTypeCache();
    }

    /**
     * @return the triggers
     */
    public TriggerTypeCache getTriggers() {
        return triggers;
    }

    /**
     * load / reload a new instance of TriggerTypeCache
     */
    public void loadTriggers() {
        triggers = new TriggerTypeCache();
    }

    /**
     * @return the loaded instance of DungeonCache
     */
    public DungeonCache getDungeons() {
        return dungeons;
    }

    /**
     * load / reload a new instance of DungeonCache
     *
     * @param folder the folder to load from
     */
    public void loadDungeons(File folder) {
        dungeons = new DungeonCache(folder);
    }

    /**
     * @return the loaded instance of GlobalProtectionCache
     */
    public GlobalProtectionCache getGlobalProtections() {
        return protections;
    }

    /**
     * load / reload a new instance of GlobalProtectionCache
     */
    public void loadGlobalProtections() {
        protections = new GlobalProtectionCache();
    }

    /**
     * @return the loaded instance of ExternalMobProviderCache
     */
    public ExternalMobProviderCache getExternalMobProviders() {
        return dMobProviders;
    }

    /**
     * load / reload a new instance of ExternalMobProviderCache
     */
    public void loadExternalMobProviders() {
        dMobProviders = new ExternalMobProviderCache();
    }

    /**
     * @return the loaded instance of DPlayerCache
     */
    public DPlayerCache getDPlayers() {
        return dPlayers;
    }

    /**
     * load / reload a new instance of DPlayerCache
     */
    public void loadDPlayers() {
        dPlayers = new DPlayerCache();
    }

    /**
     * @return the loaded instance of AnnouncerCache
     */
    public AnnouncerCache getAnnouncers() {
        return announcers;
    }

    /**
     * load / reload a new instance of AnnouncerCache
     *
     * @param folder the folder to load from
     */
    public void loadAnnouncers(File folder) {
        announcers = new AnnouncerCache(folder);
    }

    /**
     * @return the loaded instance of DClasseCache
     */
    public DClassCache getDClasses() {
        return dClasses;
    }

    /**
     * load / reload a new instance of DClasseCache
     *
     * @param folder the folder to load from
     */
    public void loadDClasses(File folder) {
        dClasses = new DClassCache(folder);
    }

    /**
     * load / reload loot tables
     *
     * @param folder the folder to load from
     */
    public void loadLootTables(File folder) {
        for (File script : FileUtil.getFilesForFolder(folder)) {
            new LootTable(caliburn, script);
        }
    }

    /**
     * load / reload DMob types
     *
     * @param folder the folder to load from
     */
    public void loadMobs(File folder) {
        if (folder.isDirectory()) {
            for (File script : FileUtil.getFilesForFolder(folder)) {
                caliburn.getExMobs().add(new DMobType(script));
            }
        }
        Bukkit.getPluginManager().registerEvents(new DMobListener(), this);
    }

    /**
     * @return the loaded instance of SignScriptCache
     */
    public SignScriptCache getSignScripts() {
        return signScripts;
    }

    /**
     * load / reload a new instance of SignScriptCache
     *
     * @param folder the folder to load from
     */
    public void loadSignScripts(File folder) {
        signScripts = new SignScriptCache(folder);
    }

    /**
     * @return the loaded instance of DWorldCache
     */
    public DWorldCache getDWorlds() {
        return dWorlds;
    }

    /**
     * load / reload a new instance of DWorldCache
     *
     * @param folder the folder to load from
     */
    public void loadDWorlds(File folder) {
        dWorlds = new DWorldCache(MAPS);
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

    @Deprecated
    private Set<Inventory> guis = new HashSet<>();

    @Deprecated
    public Set<Inventory> getGUIs() {
        return guis;
    }

}
