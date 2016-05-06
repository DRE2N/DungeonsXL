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
package io.github.dre2n.dungeonsxl;

import io.github.dre2n.commons.command.BRCommands;
import io.github.dre2n.commons.compatibility.Internals;
import io.github.dre2n.commons.config.MessageConfig;
import io.github.dre2n.commons.javaplugin.BRPlugin;
import io.github.dre2n.commons.javaplugin.BRPluginSettings;
import io.github.dre2n.commons.util.FileUtil;
import io.github.dre2n.dungeonsxl.command.*;
import io.github.dre2n.dungeonsxl.config.DMessages;
import io.github.dre2n.dungeonsxl.config.DataConfig;
import io.github.dre2n.dungeonsxl.config.MainConfig;
import io.github.dre2n.dungeonsxl.config.WorldConfig;
import io.github.dre2n.dungeonsxl.dungeon.Dungeons;
import io.github.dre2n.dungeonsxl.game.Game;
import io.github.dre2n.dungeonsxl.game.GameTypes;
import io.github.dre2n.dungeonsxl.global.GlobalProtections;
import io.github.dre2n.dungeonsxl.listener.*;
import io.github.dre2n.dungeonsxl.mob.ExternalMobProviders;
import io.github.dre2n.dungeonsxl.player.DGamePlayer;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.player.DPermissions;
import io.github.dre2n.dungeonsxl.player.DPlayers;
import io.github.dre2n.dungeonsxl.player.DSavePlayer;
import io.github.dre2n.dungeonsxl.requirement.RequirementTypes;
import io.github.dre2n.dungeonsxl.reward.DLootInventory;
import io.github.dre2n.dungeonsxl.reward.RewardTypes;
import io.github.dre2n.dungeonsxl.sign.DSignTypes;
import io.github.dre2n.dungeonsxl.task.LazyUpdateTask;
import io.github.dre2n.dungeonsxl.task.SecureModeTask;
import io.github.dre2n.dungeonsxl.task.UpdateTask;
import io.github.dre2n.dungeonsxl.task.WorldUnloadTask;
import io.github.dre2n.dungeonsxl.trigger.TriggerTypes;
import io.github.dre2n.dungeonsxl.world.EditWorld;
import io.github.dre2n.dungeonsxl.world.GameWorld;
import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitTask;

/**
 * @author Frank Baumann, Tobias Schmitz, Daniel Saukel
 */
public class DungeonsXL extends BRPlugin {

    private static DungeonsXL instance;

    public static final String[] EXCLUDED_FILES = {"config.yml", "uid.dat", "DXLData.data"};

    private DataConfig dataConfig;
    private MainConfig mainConfig;
    private MessageConfig messageConfig;

    private BRCommands dCommands;
    private DSignTypes dSigns;
    private GameTypes gameTypes;
    private RequirementTypes requirementTypes;
    private RewardTypes rewardTypes;
    private TriggerTypes triggers;
    private Dungeons dungeons;
    private GlobalProtections protections;
    private ExternalMobProviders dMobProviders;
    private DPlayers dPlayers;

    private BukkitTask worldUnloadTask;
    private BukkitTask lazyUpdateTask;
    private BukkitTask updateTask;
    private BukkitTask secureModeTask;

    private CopyOnWriteArrayList<DLootInventory> dLootInventories = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<EditWorld> editWorlds = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<GameWorld> gameWorlds = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<Game> games = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<DGroup> dGroups = new CopyOnWriteArrayList<>();

    public DungeonsXL() {
        /*
         * ##########################
         * ####~BRPluginSettings~####
         * ##########################
         * #~Internals~##~~v1_7_R3+~#
         * #~SpigotAPI~##~~~false~~~#
         * #~~~~UUID~~~##~~~~true~~~#
         * #~~Economy~~##~~~~true~~~#
         * #Permissions##~~~~true~~~#
         * #~~Metrics~~##~~~~true~~~#
         * ##########################
         */

        settings = new BRPluginSettings(false, true, true, true, true, Internals.andHigher(Internals.v1_7_R3));
    }

    @Override
    public void onEnable() {
        super.onEnable();

        instance = this;

        // InitFolders
        initFolders();

        // Load Language
        loadMessageConfig(new File(getDataFolder(), "languages/en.yml"));
        // Load Config
        loadDataConfig(new File(getDataFolder(), "data.yml"));
        loadMainConfig(new File(getDataFolder(), "config.yml"));
        // Load Language 2
        loadMessageConfig(new File(getDataFolder(), "languages/" + mainConfig.getLanguage() + ".yml"));
        loadDCommands();
        DPermissions.register();
        loadGameTypes();
        loadRequirementTypes();
        loadRewardTypes();
        loadTriggers();
        loadDSigns();
        loadDungeons();
        loadGlobalProtections();
        loadExternalMobProviders();
        loadDPlayers();

        manager.registerEvents(new EntityListener(), this);
        manager.registerEvents(new PlayerListener(), this);
        manager.registerEvents(new BlockListener(), this);
        manager.registerEvents(new WorldListener(), this);
        manager.registerEvents(new HangingListener(), this);
        if (manager.getPlugin("Citizens") != null) {
            manager.registerEvents(new CitizensListener(), this);
        }

        // Load All
        loadAll();

        // Tasks
        startWorldUnloadTask(1200L);
        startLazyUpdateTask(20L);
        startUpdateTask(20L);
        if (mainConfig.isSecureModeEnabled()) {
            startSecureModeTask(mainConfig.getSecureModeCheckInterval());
        }
    }

    @Override
    public void onDisable() {
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

        // Delete Worlds
        GameWorld.deleteAll();
        gameWorlds.clear();
        EditWorld.deleteAll();
        editWorlds.clear();

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

        File dungeons = new File(getDataFolder() + "/dungeons");
        if (!dungeons.exists()) {
            dungeons.mkdir();
        }

        File languages = new File(getDataFolder() + "/languages");
        if (!languages.exists()) {
            languages.mkdir();
        }

        File maps = new File(getDataFolder() + "/maps");
        if (!maps.exists()) {
            maps.mkdir();
        }
    }

    // Save and load
    public void saveData() {
        protections.saveAll();
        DSavePlayer.save();
        for (EditWorld editWorld : editWorlds) {
            editWorld.save();
        }
    }

    public void loadAll() {
        protections.loadAll();
        dPlayers.loadAll();
        DSavePlayer.load();
        checkWorlds();
    }

    public void checkWorlds() {
        File serverDir = new File(".");

        for (File file : serverDir.listFiles()) {
            if (file.getName().contains("DXL_Edit_") && file.isDirectory()) {
                for (File dungeonFile : file.listFiles()) {
                    if (dungeonFile.getName().contains(".id_")) {
                        String dungeonName = dungeonFile.getName().substring(4);
                        FileUtil.copyDirectory(file, new File(getDataFolder(), "/maps/" + dungeonName), EXCLUDED_FILES);
                        FileUtil.deleteUnusedFiles(new File(getDataFolder(), "/maps/" + dungeonName));
                    }
                }

                FileUtil.removeDirectory(file);

            } else if (file.getName().contains("DXL_Game_") && file.isDirectory()) {
                FileUtil.removeDirectory(file);
            }
        }
    }

    /* Getters and loaders */
    /**
     * @return the plugin instance
     */
    public static DungeonsXL getInstance() {
        return instance;
    }

    /**
     * @return the loaded instance of DataConfig
     */
    public DataConfig getDataConfig() {
        return dataConfig;
    }

    /**
     * load / reload a new instance of MainConfig
     */
    public void loadDataConfig(File file) {
        dataConfig = new DataConfig(file);
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
     * @return the loaded instance of BRCommands
     */
    @Override
    public BRCommands getCommands() {
        return dCommands;
    }

    /**
     * load / reload a new instance of DCommands
     */
    public void loadDCommands() {
        dCommands = new BRCommands(
                "dungeonsxl",
                this,
                new HelpCommand(),
                new BreakCommand(),
                new ChatCommand(),
                new ChatSpyCommand(),
                new CreateCommand(),
                new EditCommand(),
                new EscapeCommand(),
                new GameCommand(),
                new GroupCommand(),
                new InviteCommand(),
                new EnterCommand(),
                new LeaveCommand(),
                new ListCommand(),
                new LivesCommand(),
                new MainCommand(),
                new UninviteCommand(),
                new MsgCommand(),
                new PlayCommand(),
                new PortalCommand(),
                new DeletePortalCommand(),
                new ReloadCommand(),
                new SaveCommand(),
                new TestCommand()
        );

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
    public void loadDungeons() {
        dungeons = new Dungeons();
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
     * @return the worldUnloadTask
     */
    public BukkitTask getWorldUnloadTask() {
        return worldUnloadTask;
    }

    /**
     * start a new WorldUnloadTask
     */
    public void startWorldUnloadTask(long period) {
        worldUnloadTask = new WorldUnloadTask().runTaskTimer(this, 0L, period);
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
        lazyUpdateTask = new LazyUpdateTask().runTaskTimer(this, 0L, period);
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
        updateTask = new UpdateTask().runTaskTimer(this, 0L, period);
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
        updateTask = new SecureModeTask().runTaskTimer(this, 0L, period);
    }

    /**
     * @return the dLootInventories
     */
    public List<DLootInventory> getDLootInventories() {
        return dLootInventories;
    }

    /**
     * @return the editWorlds
     */
    public List<EditWorld> getEditWorlds() {
        return editWorlds;
    }

    /**
     * @return the defaultConfig
     */
    public WorldConfig getDefaultConfig() {
        return WorldConfig.defaultConfig;// TODO
    }

    /**
     * @return the gameWorlds
     */
    public List<GameWorld> getGameWorlds() {
        return gameWorlds;
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
