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
import de.erethon.caliburn.mob.ExMob;
import de.erethon.commons.chat.MessageUtil;
import de.erethon.commons.compatibility.Internals;
import de.erethon.commons.compatibility.Version;
import de.erethon.commons.javaplugin.DREPlugin;
import de.erethon.commons.javaplugin.DREPluginSettings;
import de.erethon.commons.misc.FileUtil;
import de.erethon.commons.misc.Registry;
import de.erethon.commons.spiget.comparator.VersionComparator;
import de.erethon.dungeonsxl.adapter.block.BlockAdapter;
import de.erethon.dungeonsxl.adapter.block.BlockAdapterBlockData;
import de.erethon.dungeonsxl.adapter.block.BlockAdapterMagicValues;
import de.erethon.dungeonsxl.announcer.AnnouncerCache;
import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.Requirement;
import de.erethon.dungeonsxl.api.Reward;
import de.erethon.dungeonsxl.api.dungeon.Dungeon;
import de.erethon.dungeonsxl.api.dungeon.Game;
import de.erethon.dungeonsxl.api.dungeon.GameRule;
import de.erethon.dungeonsxl.api.dungeon.GameRuleContainer;
import de.erethon.dungeonsxl.api.mob.DungeonMob;
import de.erethon.dungeonsxl.api.mob.ExternalMobProvider;
import de.erethon.dungeonsxl.api.player.GroupAdapter;
import de.erethon.dungeonsxl.api.player.PlayerCache;
import de.erethon.dungeonsxl.api.player.PlayerClass;
import de.erethon.dungeonsxl.api.player.PlayerGroup;
import de.erethon.dungeonsxl.api.sign.DungeonSign;
import de.erethon.dungeonsxl.api.world.EditWorld;
import de.erethon.dungeonsxl.api.world.GameWorld;
import de.erethon.dungeonsxl.api.world.InstanceWorld;
import de.erethon.dungeonsxl.api.world.ResourceWorld;
import de.erethon.dungeonsxl.command.DCommandCache;
import de.erethon.dungeonsxl.config.MainConfig;
import de.erethon.dungeonsxl.config.MainConfig.BackupMode;
import de.erethon.dungeonsxl.dungeon.DDungeon;
import de.erethon.dungeonsxl.global.GlobalData;
import de.erethon.dungeonsxl.global.GlobalProtectionCache;
import de.erethon.dungeonsxl.global.GlobalProtectionListener;
import de.erethon.dungeonsxl.mob.CitizensMobProvider;
import de.erethon.dungeonsxl.mob.CustomExternalMobProvider;
import de.erethon.dungeonsxl.mob.DMob;
import de.erethon.dungeonsxl.mob.DMobListener;
import de.erethon.dungeonsxl.mob.DMobType;
import de.erethon.dungeonsxl.mob.ExternalMobPlugin;
import de.erethon.dungeonsxl.player.DGamePlayer;
import de.erethon.dungeonsxl.player.DGlobalPlayer;
import de.erethon.dungeonsxl.player.DGroup;
import de.erethon.dungeonsxl.player.DPermission;
import de.erethon.dungeonsxl.player.DPlayerListener;
import de.erethon.dungeonsxl.player.SecureModeTask;
import de.erethon.dungeonsxl.player.groupadapter.*;
import de.erethon.dungeonsxl.requirement.*;
import de.erethon.dungeonsxl.reward.*;
import de.erethon.dungeonsxl.sign.DSignListener;
import de.erethon.dungeonsxl.sign.button.*;
import de.erethon.dungeonsxl.sign.passive.*;
import de.erethon.dungeonsxl.sign.rocker.*;
import de.erethon.dungeonsxl.sign.windup.*;
import de.erethon.dungeonsxl.trigger.TriggerListener;
import de.erethon.dungeonsxl.trigger.TriggerTypeCache;
import de.erethon.dungeonsxl.util.LWCUtil;
import de.erethon.dungeonsxl.util.PlaceholderUtil;
import de.erethon.dungeonsxl.world.DEditWorld;
import de.erethon.dungeonsxl.world.DResourceWorld;
import de.erethon.dungeonsxl.world.DWorldListener;
import de.erethon.dungeonsxl.world.LWCIntegration;
import de.erethon.dungeonsxl.world.WorldConfig;
import de.erethon.vignette.api.VignetteAPI;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Frank Baumann, Tobias Schmitz, Daniel Saukel
 */
public class DungeonsXL extends DREPlugin implements DungeonsAPI {

    private static DungeonsXL instance;
    private CaliburnAPI caliburn;

    public static final BlockAdapter BLOCK_ADAPTER = Version.isAtLeast(Version.MC1_13) ? new BlockAdapterBlockData() : new BlockAdapterMagicValues();

    public static final String[] EXCLUDED_FILES = {"config.yml", "uid.dat", "DXLData.data", "data"};
    public static final File ANNOUNCERS = new File(SCRIPTS, "announcers");
    public static final File LOOT_TABLES = new File(SCRIPTS, "loottables");
    public static final File MOBS = new File(SCRIPTS, "mobs");
    public static final File SIGNS = new File(SCRIPTS, "signs");
    public static final File COMMANDS = new File(SCRIPTS, "commands");
    public static final Map<String, Class<? extends DungeonSign>> LEGACY_SIGNS = new HashMap<>();

    static {
        LEGACY_SIGNS.put("CHEST", RewardChestSign.class);
        LEGACY_SIGNS.put("EXTERNALMOB", MobSign.class);
        LEGACY_SIGNS.put("FLOOR", EndSign.class);
    }

    private PlayerCache playerCache = new PlayerCache();
    private Collection<Game> gameCache = new ArrayList<>();
    private Registry<String, PlayerClass> classRegistry = new Registry<>();
    private Registry<String, Class<? extends DungeonSign>> signRegistry = new SignRegistry();
    private Registry<String, Class<? extends Requirement>> requirementRegistry = new Registry<>();
    private Registry<String, Class<? extends Reward>> rewardRegistry = new Registry<>();
    private Registry<String, Dungeon> dungeonRegistry = new Registry<>();
    private Registry<String, ResourceWorld> mapRegistry = new Registry<>();
    private Registry<Integer, InstanceWorld> instanceCache = new Registry<>();
    private Registry<String, GameRule> gameRuleRegistry = new GameRuleRegistry();
    private Registry<String, ExternalMobProvider> externalMobProviderRegistry = new Registry<>();
    private Registry<String, PlayerGroup> playerGroupCache = new Registry<>();
    private Collection<GroupAdapter> groupAdapters = new ArrayList<>();

    @Deprecated
    private class SignRegistry extends Registry<String, Class<? extends DungeonSign>> {

        @Override
        public Class<? extends DungeonSign> get(String key) {
            Class<? extends DungeonSign> clss = super.get(key);
            if (clss == null) {
                return LEGACY_SIGNS.get(key);
            }
            return clss;
        }

    }

    private class GameRuleRegistry extends Registry<String, GameRule> {

        @Override
        public void add(String key, GameRule rule) {
            super.add(key, rule);
            if (loaded) {
                GameRuleContainer.DEFAULT_VALUES.setState(rule, rule.getDefaultValue());
                mainConfig.getDefaultWorldConfig().updateGameRule(rule);
                for (Dungeon apiDungeon : dungeonRegistry) {
                    DDungeon dungeon = ((DDungeon) apiDungeon);
                    if (dungeon.isMultiFloor()) {
                        dungeon.getConfig().getDefaultValues().updateGameRule(rule);
                        dungeon.getConfig().getOverrideValues().updateGameRule(rule);
                    } else {
                        WorldConfig cfg = ((DResourceWorld) dungeon.getMap()).getConfig(false);
                        cfg.updateGameRule(rule);
                    }
                }
                dungeonRegistry.forEach(Dungeon::setupRules);
            }
        }

    }

    private boolean loaded, loadingWorld;

    private GlobalData globalData;
    private MainConfig mainConfig;

    private DCommandCache dCommands;
    private TriggerTypeCache triggers;
    private GlobalProtectionCache protections;
    private AnnouncerCache announcers;
    private Registry<String, SignScript> signScriptRegistry;
    private Registry<String, CommandScript> commandScriptRegistry;

    public DungeonsXL() {
        settings = DREPluginSettings.builder()
                .internals(Internals.andHigher(Internals.v1_8_R1))
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
        if (Internals.andHigher(Internals.v1_14_R1).contains(compat.getInternals())) {
            getLogger().warning("Support for Minecraft 1.14 and higher is experimental. Do not use this in a production environment.");
        }

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
        if (manager.isPluginEnabled("Parties")) {
            registerGroupAdapter(new PartiesAdapter(this));
        }
        VignetteAPI.init(this);
    }

    @Override
    public void onDisable() {
        if (!loaded) {
            return;
        }
        saveData();
        deleteAllInstances();
        HandlerList.unregisterAll(this);
        getServer().getScheduler().cancelTasks(this);
        loaded = false;
    }

    public void initFolders() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        BACKUPS.mkdir();
        MAPS.mkdir();
        PLAYERS.mkdir();
        SCRIPTS.mkdir();
        ANNOUNCERS.mkdir();
        CLASSES.mkdir();
        DUNGEONS.mkdir();
        LOOT_TABLES.mkdir();
        MOBS.mkdir();
        SIGNS.mkdir();
        COMMANDS.mkdir();
    }

    public void loadConfig() {
        mainConfig = new MainConfig(this, new File(getDataFolder(), "config.yml"));
    }

    public void createCaches() {
        requirementRegistry.add("feeLevel", FeeLevelRequirement.class);
        requirementRegistry.add("feeMoney", FeeMoneyRequirement.class);
        requirementRegistry.add("forbiddenItems", ForbiddenItemsRequirement.class);
        requirementRegistry.add("groupSize", GroupSizeRequirement.class);
        requirementRegistry.add("keyItems", KeyItemsRequirement.class);
        requirementRegistry.add("permission", PermissionRequirement.class);
        requirementRegistry.add("timeframe", TimeframeRequirement.class);

        rewardRegistry.add("item", ItemReward.class);
        rewardRegistry.add("money", MoneyReward.class);
        rewardRegistry.add("level", LevelReward.class);

        triggers = new TriggerTypeCache();
        protections = new GlobalProtectionCache(this);
        announcers = new AnnouncerCache(this);
        signScriptRegistry = new Registry<>();
        commandScriptRegistry = new Registry<>();
        dCommands = new DCommandCache(this);
    }

    public void initCaches() {
        // Requirements
        Bukkit.getPluginManager().registerEvents(new RewardListener(this), this);
        Bukkit.getPluginManager().registerEvents(new TriggerListener(this), this);

        // Signs
        signRegistry.add("ACTIONBAR", ActionBarSign.class);
        signRegistry.add("BED", BedSign.class);
        signRegistry.add("BLOCK", BlockSign.class);
        signRegistry.add("BOSSSHOP", BossShopSign.class);
        signRegistry.add("CHECKPOINT", CheckpointSign.class);
        signRegistry.add("CLASSES", ClassesSign.class);
        //signRegistry.add("CMD", CommandSign.class); TODO: REIMPLEMENT
        signRegistry.add("DROP", DropSign.class);
        signRegistry.add("DUNGEONCHEST", DungeonChestSign.class);
        signRegistry.add("END", EndSign.class);
        signRegistry.add("FLAG", FlagSign.class);
        signRegistry.add("HOLOGRAM", HologramSign.class);
        signRegistry.add("INTERACT", InteractSign.class);
        signRegistry.add("LEAVE", LeaveSign.class);
        signRegistry.add("LIVES", LivesModifierSign.class);
        signRegistry.add("LOBBY", LobbySign.class);
        signRegistry.add("MOB", MobSign.class);
        signRegistry.add("MSG", ChatMessageSign.class);
        signRegistry.add("NOTE", NoteSign.class);
        signRegistry.add("DOOR", OpenDoorSign.class);
        signRegistry.add("PLACE", PlaceSign.class);
        signRegistry.add("PROTECTION", ProtectionSign.class);
        signRegistry.add("READY", ReadySign.class);
        signRegistry.add("REDSTONE", RedstoneSign.class);
        signRegistry.add("RESOURCEPACK", ResourcePackSign.class);
        signRegistry.add("REWARDCHEST", RewardChestSign.class);
        signRegistry.add("SCRIPT", ScriptSign.class);
        signRegistry.add("SOUNDMSG", SoundMessageSign.class);
        signRegistry.add("START", StartSign.class);
        signRegistry.add("TELEPORT", TeleportSign.class);
        signRegistry.add("TITLE", TitleSign.class);
        signRegistry.add("TRIGGER", TriggerSign.class);
        signRegistry.add("WAVE", WaveSign.class);
        Bukkit.getPluginManager().registerEvents(new DSignListener(this), this);

        for (GameRule rule : GameRule.VALUES) {
            gameRuleRegistry.add(rule.getKey(), rule);
        }

        // Maps
        for (File file : MAPS.listFiles()) {
            if (file.isDirectory() && !file.getName().equals(".raw")) {
                mapRegistry.add(file.getName(), new DResourceWorld(this, file));
            }
        }
        if (!DResourceWorld.RAW.exists()) {
            DResourceWorld.createRaw();
        }
        Bukkit.getPluginManager().registerEvents(new DWorldListener(this), this);
        if (LWCUtil.isLWCLoaded()) {
            new LWCIntegration(this);
        }

        // Dungeons - Linked dungeons
        for (File file : DUNGEONS.listFiles()) {
            Dungeon dungeon = new DDungeon(this, file);

            if (dungeon.isSetupCorrect()) {
                dungeonRegistry.add(dungeon.getName(), dungeon);
            } else {
                MessageUtil.log(this, "&4The setup of dungeon &6" + file.getName()
                        + "&4 is incorrect. See https://github.com/DRE2N/DungeonsXL/wiki/dungeon-configuration for reference.");
            }
        }
        // Dungeons - Map dungeons
        for (ResourceWorld resource : mapRegistry) {
            dungeonRegistry.add(resource.getName(), new DDungeon(this, resource));
        }

        // Global
        Bukkit.getPluginManager().registerEvents(new GlobalProtectionListener(this), this);
        globalData = new GlobalData(this, new File(getDataFolder(), "data.yml"));
        globalData.load();

        // Mobs - Supported providers
        for (ExternalMobPlugin externalMobPlugin : ExternalMobPlugin.values()) {
            externalMobProviderRegistry.add(externalMobPlugin.getIdentifier(), externalMobPlugin);
        }
        if (Bukkit.getPluginManager().getPlugin("Citizens") != null) {
            CitizensMobProvider citizensMobProvider = new CitizensMobProvider();
            externalMobProviderRegistry.add("CI", citizensMobProvider);
            Bukkit.getPluginManager().registerEvents(citizensMobProvider, this);
        } else {
            MessageUtil.log(this, "Could not find compatible Citizens plugin. The mob provider Citizens (\"CI\") will not get enabled...");
        }
        // Mobs - Custom providers
        for (Entry<String, Object> customExternalMobProvider : mainConfig.getExternalMobProviders().entrySet()) {
            externalMobProviderRegistry.add(customExternalMobProvider.getKey(), new CustomExternalMobProvider(customExternalMobProvider));
        }

        // Players
        if (mainConfig.isSecureModeEnabled()) {
            new SecureModeTask(this).runTaskTimer(this, mainConfig.getSecureModeCheckInterval(), mainConfig.getSecureModeCheckInterval());
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                playerCache.getAllGamePlayers().forEach(p -> ((DGamePlayer) p).update(false));
            }
        }.runTaskTimer(this, 2L, 2L);
        new BukkitRunnable() {
            @Override
            public void run() {
                playerCache.getAllGamePlayers().forEach(p -> ((DGamePlayer) p).update(true));
            }
        }.runTaskTimer(this, 20L, 20L);

        Bukkit.getPluginManager().registerEvents(new DPlayerListener(this), this);

        announcers.init(ANNOUNCERS);
        for (File script : FileUtil.getFilesForFolder(CLASSES)) {
            PlayerClass clss = new PlayerClass(caliburn, script);
            classRegistry.add(clss.getName(), clss);
        }
        Bukkit.getPluginManager().registerEvents(new DMobListener(this), this);

        for (File script : FileUtil.getFilesForFolder(SIGNS)) {
            SignScript sign = new SignScript(script);
            signScriptRegistry.add(sign.getName(), sign);
        }

        for (File script : FileUtil.getFilesForFolder(COMMANDS)) {
            CommandScript cmd = new CommandScript(script);
            commandScriptRegistry.add(cmd.getName(), cmd);
        }

        dCommands.register(this);
        loaded = true;
    }

    public void saveData() {
        protections.saveAll();
        instanceCache.getAllIf(i -> i instanceof EditWorld).forEach(i -> ((DEditWorld) i).forceSave());
    }

    public void loadData() {
        Bukkit.getOnlinePlayers().forEach(p -> new DGlobalPlayer(this, p));

        for (File file : Bukkit.getWorldContainer().listFiles()) {
            if (!file.getName().startsWith("DXL_") || !file.isDirectory()) {
                continue;
            }

            if (file.getName().startsWith("DXL_Edit_")) {
                for (File mapFile : file.listFiles()) {
                    if (!mapFile.getName().startsWith(".id_")) {
                        continue;
                    }

                    String name = mapFile.getName().substring(4);

                    File resource = new File(DungeonsXL.MAPS, name);
                    File backup = new File(DungeonsXL.BACKUPS, resource.getName() + "-" + System.currentTimeMillis() + "_crashbackup");
                    FileUtil.copyDir(resource, backup);
                    // Remove all files from the backupped resource world but not the config & data that we cannot fetch from the instance.
                    remove:
                    for (File remove : FileUtil.getFilesForFolder(resource)) {
                        for (String nope : DungeonsXL.EXCLUDED_FILES) {
                            if (remove.getName().equals(nope)) {
                                continue remove;
                            }
                        }
                        remove.delete();
                    }
                    DResourceWorld.deleteUnusedFiles(file);
                    FileUtil.copyDir(file, resource, DungeonsXL.EXCLUDED_FILES);
                }
            }

            FileUtil.removeDir(file);
        }
    }

    /* Getters and loaders */
    /**
     * @return the plugin instance
     */
    public static DungeonsXL getInstance() {
        return instance;
    }

    @Override
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

    @Override
    public PlayerCache getPlayerCache() {
        return playerCache;
    }

    @Override
    public Collection<Game> getGameCache() {
        return gameCache;
    }

    @Override
    public Registry<String, PlayerClass> getClassRegistry() {
        return classRegistry;
    }

    @Override
    public Registry<String, Class<? extends DungeonSign>> getSignRegistry() {
        return signRegistry;
    }

    @Override
    public Registry<String, Class<? extends Requirement>> getRequirementRegistry() {
        return requirementRegistry;
    }

    @Override
    public Registry<String, Class<? extends Reward>> getRewardRegistry() {
        return rewardRegistry;
    }

    @Override
    public Registry<String, Dungeon> getDungeonRegistry() {
        return dungeonRegistry;
    }

    @Override
    public Registry<String, ResourceWorld> getMapRegistry() {
        return mapRegistry;
    }

    @Override
    public Registry<Integer, InstanceWorld> getInstanceCache() {
        return instanceCache;
    }

    @Override
    public Registry<String, GameRule> getGameRuleRegistry() {
        return gameRuleRegistry;
    }

    @Override
    public Registry<String, ExternalMobProvider> getExternalMobProviderRegistry() {
        return externalMobProviderRegistry;
    }

    @Override
    public Registry<String, PlayerGroup> getGroupCache() {
        return playerGroupCache;
    }

    @Override
    public void registerGroupAdapter(GroupAdapter groupAdapter) {
        if (mainConfig.areGroupAdaptersEnabled()) {
            groupAdapters.add(groupAdapter);
        } else {
            MessageUtil.log(this, "&4The group adapter &6" + groupAdapter.getClass().getName() + " &4was not registered because the feature is disabled.");
        }
    }

    /**
     * Returns a collection of the loadedGroupAdapters
     *
     * @return a collection of GroupAdapters
     */
    public Collection<GroupAdapter> getGroupAdapters() {
        return groupAdapters;
    }

    /**
     * Returns true if the plugin is currently loading a world, false if not.
     * <p>
     * If the plugin is loading a world, it is locked in order to prevent loading two at once.
     *
     * @return true if the plugin is currently loading a world, false if not
     */
    public boolean isLoadingWorld() {
        return loadingWorld;
    }

    /**
     * Notifies the plugin that a world is being loaded.
     * <p>
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
     * @return the triggers
     */
    public TriggerTypeCache getTriggerCache() {
        return triggers;
    }

    /**
     * @return the loaded instance of GlobalProtectionCache
     */
    public GlobalProtectionCache getGlobalProtectionCache() {
        return protections;
    }

    /**
     * @return the loaded instance of AnnouncerCache
     */
    public AnnouncerCache getAnnouncerCache() {
        return announcers;
    }

    /**
     * Returns a registry of the loaded sign scripts.
     *
     * @return a registry of the loaded sign scripts
     */
    public Registry<String, SignScript> getSignScriptRegistry() {
        return signScriptRegistry;
    }

    /**
     * Returns a registry of the loaded command scripts.
     *
     * @return a registry of the loaded command scripts
     */
    public Registry<String, CommandScript> getCommandScriptRegistry() {
        return commandScriptRegistry;
    }

    @Deprecated
    private Set<Inventory> guis = new HashSet<>();

    @Deprecated
    public Set<Inventory> getGUIs() {
        return guis;
    }

    /* Object initialization */
    @Override
    public PlayerGroup createGroup(Player leader) {
        return new DGroup(this, leader);
    }

    @Override
    public PlayerGroup createGroup(Player leader, PlayerGroup.Color color) {
        return new DGroup(this, leader, color);
    }

    @Override
    public PlayerGroup createGroup(Player leader, String name) {
        return new DGroup(this, name, leader);
    }

    @Override
    public PlayerGroup createGroup(Player leader, Dungeon dungeon) {
        return new DGroup(this, leader, dungeon);
    }

    @Override
    public PlayerGroup createGroup(Player leader, Collection<Player> members, String name, Dungeon dungeon) {
        return new DGroup(this, name, leader, members, dungeon);
    }

    @Override
    public DungeonMob wrapEntity(LivingEntity entity, GameWorld gameWorld, String triggerId) {
        DungeonMob mob = getDungeonMob(entity);
        if (mob != null) {
            return mob;
        } else {
            return new DMob(entity, gameWorld, triggerId);
        }
    }

    @Override
    public DungeonMob wrapEntity(LivingEntity entity, GameWorld gameWorld, ExMob type) {
        DungeonMob mob = getDungeonMob(entity);
        if (mob != null) {
            return mob;
        } else {
            return new DMob(entity, gameWorld, type);
        }
    }

    @Override
    public DungeonMob wrapEntity(LivingEntity entity, GameWorld gameWorld, ExMob type, String triggerId) {
        DungeonMob mob = getDungeonMob(entity);
        if (mob != null) {
            return mob;
        } else {
            return new DMob(entity, gameWorld, type, triggerId);
        }
    }

    /* Getters */
    @Override
    public DungeonMob getDungeonMob(LivingEntity entity) {
        GameWorld gameWorld = getGameWorld(entity.getWorld());
        if (gameWorld == null) {
            return null;
        }
        for (DungeonMob mob : gameWorld.getMobs()) {
            if (mob.getEntity() == entity) {
                return mob;
            }
        }
        return null;
    }

    @Override
    public PlayerGroup getPlayerGroup(Player member) {
        for (PlayerGroup group : playerGroupCache) {
            if (group.getMembers().contains(member)) {
                return group;
            }
        }
        return null;
    }

    @Override
    public Game getGame(Player player) {
        for (Game game : gameCache) {
            if (game.getPlayers().contains(player)) {
                return game;
            }
        }
        return null;
    }

    @Override
    public Game getGame(World world) {
        GameWorld gameWorld = getGameWorld(world);
        return gameWorld != null ? gameWorld.getGame() : null;
    }

    @Override
    public GameWorld getGameWorld(World world) {
        InstanceWorld instance = getInstanceWorld(world);
        return instance instanceof GameWorld ? (GameWorld) instance : null;
    }

    @Override
    public EditWorld getEditWorld(World world) {
        InstanceWorld instance = getInstanceWorld(world);
        return instance instanceof EditWorld ? (EditWorld) instance : null;
    }

    public InstanceWorld getInstanceWorld(World world) {
        for (InstanceWorld instance : instanceCache) {
            if (world.equals(instance.getWorld())) {
                return instance;
            }
        }
        return null;
    }

    @Override
    public boolean isInstance(World world) {
        return world.getName().startsWith("DXL_Game_") || world.getName().startsWith("DXL_Edit_");
    }

    /**
     * Clean up all instances.
     */
    public void deleteAllInstances() {
        BackupMode backupMode = mainConfig.getBackupMode();
        for (InstanceWorld instance : instanceCache.getAll()) {
            if (backupMode == BackupMode.ON_DISABLE | backupMode == BackupMode.ON_DISABLE_AND_SAVE && instance instanceof EditWorld) {
                instance.getResource().backup();
            }

            instance.delete();
        }
    }

    /**
     * Checks if an old player wrapper instance of the user exists. If yes, the old Player of the user is replaced with the new object.
     *
     * @param player the player to check
     * @return if the player exists
     */
    public boolean checkPlayer(Player player) {
        DGamePlayer dPlayer = (DGamePlayer) playerCache.getFirstGamePlayerIf(p -> p.getUniqueId().equals(player.getUniqueId()));
        if (dPlayer == null) {
            return false;
        }

        dPlayer.setPlayer(player);
        dPlayer.setOfflineTimeMillis(0);
        return true;
    }

}
