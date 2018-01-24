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
package io.github.dre2n.dungeonsxl;

import io.github.dre2n.caliburn.CaliburnAPI;
import io.github.dre2n.commons.compatibility.Internals;
import io.github.dre2n.commons.config.MessageConfig;
import io.github.dre2n.commons.javaplugin.DREPlugin;
import io.github.dre2n.commons.javaplugin.DREPluginSettings;
import io.github.dre2n.dungeonsxl.announcer.AnnouncerCache;
import io.github.dre2n.dungeonsxl.command.DCommandCache;
import io.github.dre2n.dungeonsxl.config.DMessage;
import io.github.dre2n.dungeonsxl.config.GlobalData;
import io.github.dre2n.dungeonsxl.config.MainConfig;
import io.github.dre2n.dungeonsxl.dungeon.DungeonCache;
import io.github.dre2n.dungeonsxl.game.Game;
import io.github.dre2n.dungeonsxl.game.GameTypeCache;
import io.github.dre2n.dungeonsxl.global.GlobalProtectionCache;
import io.github.dre2n.dungeonsxl.loottable.DLootTableCache;
import io.github.dre2n.dungeonsxl.mob.DMobTypeCache;
import io.github.dre2n.dungeonsxl.mob.ExternalMobProviderCache;
import io.github.dre2n.dungeonsxl.player.DClassCache;
import io.github.dre2n.dungeonsxl.player.DGamePlayer;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.player.DPermission;
import io.github.dre2n.dungeonsxl.player.DPlayerCache;
import io.github.dre2n.dungeonsxl.requirement.RequirementTypeCache;
import io.github.dre2n.dungeonsxl.reward.RewardTypeCache;
import io.github.dre2n.dungeonsxl.sign.DSignTypeCache;
import io.github.dre2n.dungeonsxl.sign.SignScriptCache;
import io.github.dre2n.dungeonsxl.trigger.TriggerTypeCache;
import io.github.dre2n.dungeonsxl.util.NoReload;
import io.github.dre2n.dungeonsxl.util.PageGUICache;
import io.github.dre2n.dungeonsxl.world.DWorldCache;
import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.bukkit.event.HandlerList;

/**
 * The main class of DungeonsXL.
 * It contains several important instances and the actions when the plugin is enabled / disabled.
 *
 * @author Frank Baumann, Tobias Schmitz, Daniel Saukel
 */
public class DungeonsXL extends DREPlugin {

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
    private DLootTableCache dLootTables;
    private DMobTypeCache dMobTypes;
    private SignScriptCache signScripts;
    private DWorldCache dWorlds;
    private PageGUICache pageGUIs;

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

        settings = new DREPluginSettings(true, true, true, true, true, 9488, Internals.andHigher(Internals.v1_7_R3));
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
        mainConfig.setTweaksEnabled(false);
        // Save
        saveData();
        messageConfig.save();

        // DGamePlayer leaves World
        for (DGamePlayer dPlayer : dPlayers.getDGamePlayers()) {
            dPlayer.leave();
        }

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
        loadDCommandCache();
        loadPageGUICache();
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
     */
    public void loadDungeons(File file) {
        dungeons = new DungeonCache(file);
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
     */
    public void loadAnnouncers(File file) {
        announcers = new AnnouncerCache(file);
    }

    /**
     * @return the loaded instance of DClasseCache
     */
    public DClassCache getDClasses() {
        return dClasses;
    }

    /**
     * load / reload a new instance of DClasseCache
     */
    public void loadDClasses(File file) {
        dClasses = new DClassCache(file);
    }

    /**
     * @return the loaded instance of DLootTableCache
     */
    public DLootTableCache getDLootTables() {
        return dLootTables;
    }

    /**
     * load / reload a new instance of DLootTableCache
     */
    public void loadDLootTables(File file) {
        dLootTables = new DLootTableCache(file);
    }

    /**
     * @return the loaded instance of DMobTypeCache
     */
    public DMobTypeCache getDMobTypes() {
        return dMobTypes;
    }

    /**
     * load / reload a new instance of DMobTypeCache
     */
    public void loadDMobTypes(File file) {
        dMobTypes = new DMobTypeCache(file);
    }

    /**
     * @return the loaded instance of SignScriptCache
     */
    public SignScriptCache getSignScripts() {
        return signScripts;
    }

    /**
     * load / reload a new instance of SignScriptCache
     */
    public void loadSignScripts(File file) {
        signScripts = new SignScriptCache(file);
    }

    /**
     * @return the loaded instance of DWorldCache
     */
    public DWorldCache getDWorlds() {
        return dWorlds;
    }

    /**
     * load / reload a new instance of DWorldCache
     */
    public void loadDWorlds(File folder) {
        dWorlds = new DWorldCache(MAPS);
    }

    public PageGUICache getPageGUICache() {
        return pageGUIs;
    }

    /**
     * load / reload a new instance of PageGUICache
     */
    public void loadPageGUICache() {
        if (pageGUIs != null) {
            HandlerList.unregisterAll(pageGUIs);
        }
        pageGUIs = new PageGUICache();
        manager.registerEvents(pageGUIs, this);
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
