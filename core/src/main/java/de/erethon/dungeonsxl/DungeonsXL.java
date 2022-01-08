/*
 * Copyright (C) 2012-2022 Frank Baumann
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
import de.erethon.caliburn.mob.ExMob;
import de.erethon.dungeonsxl.adapter.block.BlockAdapter;
import de.erethon.dungeonsxl.adapter.block.BlockAdapterBlockData;
import de.erethon.dungeonsxl.adapter.block.BlockAdapterMagicValues;
import de.erethon.dungeonsxl.api.DungeonModule;
import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.Requirement;
import de.erethon.dungeonsxl.api.Reward;
import de.erethon.dungeonsxl.api.dungeon.Dungeon;
import de.erethon.dungeonsxl.api.dungeon.Game;
import de.erethon.dungeonsxl.api.dungeon.GameRule;
import de.erethon.dungeonsxl.api.event.group.GroupCreateEvent;
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
import de.erethon.dungeonsxl.global.GlobalProtectionCache;
import de.erethon.dungeonsxl.global.GlobalProtectionListener;
import de.erethon.dungeonsxl.mob.CitizensMobProvider;
import de.erethon.dungeonsxl.mob.CustomExternalMobProvider;
import de.erethon.dungeonsxl.mob.DMob;
import de.erethon.dungeonsxl.mob.DMobListener;
import de.erethon.dungeonsxl.mob.ExternalMobPlugin;
import de.erethon.dungeonsxl.player.DGamePlayer;
import de.erethon.dungeonsxl.player.DGlobalPlayer;
import de.erethon.dungeonsxl.player.DGroup;
import de.erethon.dungeonsxl.player.DInstancePlayer;
import de.erethon.dungeonsxl.player.DPermission;
import de.erethon.dungeonsxl.player.DPlayerListener;
import de.erethon.dungeonsxl.player.SecureModeTask;
import de.erethon.dungeonsxl.player.groupadapter.*;
import de.erethon.dungeonsxl.reward.RewardListener;
import de.erethon.dungeonsxl.sign.DSignListener;
import de.erethon.dungeonsxl.sign.button.EndSign;
import de.erethon.dungeonsxl.sign.passive.RewardChestSign;
import de.erethon.dungeonsxl.sign.passive.SignScript;
import de.erethon.dungeonsxl.sign.windup.CommandScript;
import de.erethon.dungeonsxl.sign.windup.MobSign;
import de.erethon.dungeonsxl.trigger.TriggerListener;
import de.erethon.dungeonsxl.trigger.TriggerTypeCache;
import de.erethon.dungeonsxl.util.LWCUtil;
import de.erethon.dungeonsxl.util.PlaceholderUtil;
import de.erethon.dungeonsxl.util.commons.chat.MessageUtil;
import de.erethon.dungeonsxl.util.commons.compatibility.Internals;
import de.erethon.dungeonsxl.util.commons.compatibility.Version;
import de.erethon.dungeonsxl.util.commons.javaplugin.DREPlugin;
import de.erethon.dungeonsxl.util.commons.javaplugin.DREPluginSettings;
import de.erethon.dungeonsxl.util.commons.misc.FileUtil;
import de.erethon.dungeonsxl.util.commons.misc.Registry;
import de.erethon.dungeonsxl.util.commons.spiget.comparator.VersionComparator;
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
import java.util.function.Predicate;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Frank Baumann, Tobias Schmitz, Daniel Saukel
 */
public class DungeonsXL extends DREPlugin implements DungeonsAPI {

    /* Plugin & lib instances */
    private static DungeonsXL instance;
    private CaliburnAPI caliburn;

    /* Util instances */
    public static final BlockAdapter BLOCK_ADAPTER = Version.isAtLeast(Version.MC1_13) ? new BlockAdapterBlockData() : new BlockAdapterMagicValues();

    /* Constants */
    public static final String LATEST_IXL = "1.0.3";
    public static final String[] EXCLUDED_FILES = {"config.yml", "uid.dat", "DXLData.data", "data"};

    /* Folders of internal features */
    public static final File SIGNS = new File(SCRIPTS, "signs");
    public static final File COMMANDS = new File(SCRIPTS, "commands");

    /* Legacy */
    public static final Map<String, Class<? extends DungeonSign>> LEGACY_SIGNS = new HashMap<>();

    static {
        LEGACY_SIGNS.put("CHEST", RewardChestSign.class);
        LEGACY_SIGNS.put("EXTERNALMOB", MobSign.class);
        LEGACY_SIGNS.put("FLOOR", EndSign.class);
    }

    /* Caches & registries */
    private Set<DungeonModule> modules = new HashSet<>();
    private Collection<GroupAdapter> groupAdapters = new HashSet<>();
    private PlayerCache playerCache;
    private Collection<Game> gameCache;
    private Registry<String, PlayerClass> classRegistry;
    private Registry<String, Class<? extends DungeonSign>> signRegistry;
    private Registry<String, Class<? extends Requirement>> requirementRegistry;
    private Registry<String, Class<? extends Reward>> rewardRegistry;
    private Registry<String, Dungeon> dungeonRegistry;
    private Registry<String, ResourceWorld> mapRegistry;
    private Registry<Integer, InstanceWorld> instanceCache;
    private Registry<String, GameRule> gameRuleRegistry;
    private Registry<String, ExternalMobProvider> externalMobProviderRegistry;
    private Registry<String, PlayerGroup> playerGroupCache;

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
                GameRule.DEFAULT_VALUES.setState(rule, rule.getDefaultValue());
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

    private class PlayerGroupCache extends Registry<String, PlayerGroup> {

        @Override
        public PlayerGroup get(String key) {
            PlayerGroup group = elements.get(key);
            if (group != null) {
                return group;
            }
            for (PlayerGroup value : elements.values()) {
                if (((DGroup) value).getUntaggedName().equalsIgnoreCase(key)) {
                    return value;
                }
            }
            return null;
        }

    }

    /* Global state variables */
    private boolean loaded, loadingWorld;

    private MainConfig mainConfig;

    /* Caches & registries of internal features */
    private DCommandCache dCommands;
    private TriggerTypeCache triggers;
    private GlobalProtectionCache protections;
    private Registry<String, SignScript> signScriptRegistry;
    private Registry<String, CommandScript> commandScriptRegistry;

    public DungeonsXL() {
        settings = DREPluginSettings.builder()
                .internals(Internals.andHigher(Internals.v1_8_R1))
                .economy(true)
                .permissions(true)
                .metrics(true)
                .spigotMCResourceId(9488)
                .bStatsResourceId(1039)
                .versionComparator(VersionComparator.EQUAL)
                .build();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        String ixlVersion = manager.isPluginEnabled("ItemsXL") ? manager.getPlugin("ItemsXL").getDescription().getVersion() : "";
        if (ixlVersion.startsWith("0.") || ixlVersion.matches("1.0[\\.]?[1-2]?")) {
            getLogger().log(Level.SEVERE, "DungeonsXL requires ItemsXL v" + LATEST_IXL + " or higher to run.");
            manager.disablePlugin(this);
            return;
        }
        if (Internals.andHigher(Internals.v1_14_R1).contains(compat.getInternals())) {
            getLogger().warning("Support for Minecraft 1.14 and higher is experimental. Do not use this in a production environment.");
        }

        instance = this;
        initFolders();
        loadCaliburn();
        DPermission.register();
        registerModule(new DXLModule());
        initCaches();
        checkState();
        if (manager.isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderUtil(this, "dxl").register();
        }
        if (manager.isPluginEnabled("Parties")) {
            registerGroupAdapter(new PartiesAdapter(this));
        }
        VignetteAPI.init(this);
        loaded = true;
    }

    @Override
    public void onDisable() {
        if (!loaded) {
            return;
        }
        loaded = false;
        saveData();
        deleteAllInstances();
        HandlerList.unregisterAll(this);
        getServer().getScheduler().cancelTasks(this);
        DPermission.unregister();
    }

    public void initFolders() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        BACKUPS.mkdir();
        MAPS.mkdir();
        PLAYERS.mkdir();
        SCRIPTS.mkdir();
        CLASSES.mkdir();
        DUNGEONS.mkdir();
        SIGNS.mkdir();
        COMMANDS.mkdir();
    }

    public void loadCaliburn() {
        if (CaliburnAPI.getInstance() == null) {
            caliburn = new CaliburnAPI(this);
            caliburn.loadDataFiles();
            caliburn.finishInitialization();
        } else {
            caliburn = CaliburnAPI.getInstance();
        }
    }

    public void initCaches() {
        /* Add default values */
        requirementRegistry = new Registry<>();
        modules.forEach(m -> m.initRequirements(requirementRegistry));

        rewardRegistry = new Registry<>();
        modules.forEach(m -> m.initRewards(rewardRegistry));

        signRegistry = new SignRegistry();
        modules.forEach(m -> m.initSigns(signRegistry));

        gameRuleRegistry = new GameRuleRegistry();
        modules.forEach(m -> m.initGameRules(gameRuleRegistry));

        triggers = new TriggerTypeCache();
        // modules.forEach(m -> m.initTriggers(triggerRegistry));

        mainConfig = new MainConfig(this, new File(getDataFolder(), "config.yml"));

        /* Maps & dungeons */
        // Maps
        mapRegistry = new Registry<>();
        for (File file : MAPS.listFiles()) {
            if (file.isDirectory() && !file.getName().equals(".raw")) {
                mapRegistry.add(file.getName(), new DResourceWorld(this, file));
            }
        }
        // Dungeons - Map dungeons
        dungeonRegistry = new Registry<>();
        for (ResourceWorld resource : mapRegistry) {
            dungeonRegistry.add(resource.getName(), new DDungeon(this, resource));
        }
        // Dungeons - Linked dungeons
        if (xlDevMode) {
            for (File file : DUNGEONS.listFiles()) {
                Dungeon dungeon = DDungeon.create(this, file);

                if (dungeon != null) {
                    dungeonRegistry.add(dungeon.getName(), dungeon);
                } else {
                    MessageUtil.log(this, "&4The setup of dungeon &6" + file.getName()
                            + "&4 is incorrect. See https://github.com/DRE2N/DungeonsXL/wiki/dungeon-configuration for reference.");
                }
            }
        } else if (DUNGEONS.listFiles().length != 0) {
            MessageUtil.log(this, "&4Multi floor dungeons are not part of the range of functions of this build.");
        }
        // Raw map to copy
        if (!DResourceWorld.RAW.exists()) {
            DResourceWorld.createRaw();
        }

        /* Scripts & global data */
        classRegistry = new Registry<>();
        for (File script : FileUtil.getFilesForFolder(CLASSES)) {
            PlayerClass clss = new PlayerClass(caliburn, script);
            classRegistry.add(clss.getName(), clss);
        }
        signScriptRegistry = new Registry<>();
        for (File script : FileUtil.getFilesForFolder(SIGNS)) {
            SignScript sign = new SignScript(script);
            signScriptRegistry.add(sign.getName(), sign);
        }
        commandScriptRegistry = new Registry<>();
        for (File script : FileUtil.getFilesForFolder(COMMANDS)) {
            CommandScript cmd = new CommandScript(script);
            commandScriptRegistry.add(cmd.getName(), cmd);
        }
        protections = new GlobalProtectionCache(this);

        /* Integrations */
        if (LWCUtil.isLWCLoaded()) {
            new LWCIntegration(this);
        }
        // Mobs - Supported providers
        externalMobProviderRegistry = new Registry<>();
        for (ExternalMobPlugin externalMobPlugin : ExternalMobPlugin.values()) {
            externalMobProviderRegistry.add(externalMobPlugin.getIdentifier(), externalMobPlugin);
        }
        if (manager.getPlugin("Citizens") != null) {
            CitizensMobProvider citizensMobProvider = new CitizensMobProvider(this);
            externalMobProviderRegistry.add("CI", citizensMobProvider);
            manager.registerEvents(citizensMobProvider, this);
        } else {
            MessageUtil.log(this, "Could not find compatible Citizens plugin. The mob provider Citizens (\"CI\") will not get enabled...");
        }
        // Mobs - Custom providers
        for (Entry<String, Object> customExternalMobProvider : mainConfig.getExternalMobProviders().entrySet()) {
            externalMobProviderRegistry.add(customExternalMobProvider.getKey(), new CustomExternalMobProvider(customExternalMobProvider));
        }

        /* Players */
        if (mainConfig.isSecureModeEnabled()) {
            new SecureModeTask(this).runTaskTimer(this, mainConfig.getSecureModeCheckInterval(), mainConfig.getSecureModeCheckInterval());
        }
        playerCache = new PlayerCache();
        playerGroupCache = new PlayerGroupCache();

        gameCache = new ArrayList<>();
        instanceCache = new Registry<>();

        /* Initialize commands */
        dCommands = new DCommandCache(this);
        dCommands.register(this);

        /* Following initializations are not to be repeated on reload */
        if (loaded) {
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                playerCache.getAllInstancePlayers().forEach(p -> ((DInstancePlayer) p).update());
            }
        }.runTaskTimer(this, 2L, 2L);

        /* Initialize listeners */
        manager.registerEvents(new DWorldListener(this), this);
        manager.registerEvents(new GlobalProtectionListener(this), this);
        manager.registerEvents(new RewardListener(this), this);
        manager.registerEvents(new TriggerListener(this), this);
        manager.registerEvents(new DSignListener(this), this);
        manager.registerEvents(new DMobListener(this), this);
        manager.registerEvents(new DPlayerListener(this), this);
    }

    public void saveData() {
        protections.saveAll();
        instanceCache.getAllIf(i -> i instanceof EditWorld).forEach(i -> ((DEditWorld) i).forceSave());
    }

    public void checkState() {
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
                    remove: for (File remove : FileUtil.getFilesForFolder(resource)) {
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
    public void registerModule(DungeonModule module) {
        modules.add(module);
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
     * Returns true if the plugin is not currently in the process of enabling or disabling or entirely disabled, otherwise false.
     *
     * @return true if the plugin is not currently in the process of enabling or disabling or entirely disabled, otherwise false
     */
    public boolean isLoaded() {
        return loaded;
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
        log("World loading is now " + (loadingWorld ? "LOCKED" : "UNLOCKED"));
        this.loadingWorld = loadingWorld;
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
        return DGroup.create(this, GroupCreateEvent.Cause.CUSTOM, leader, null, null, null);
    }

    @Override
    public PlayerGroup createGroup(Player leader, PlayerGroup.Color color) {
        return DGroup.create(this, GroupCreateEvent.Cause.CUSTOM, leader, null, color, null);
    }

    @Override
    public PlayerGroup createGroup(Player leader, String name) {
        return DGroup.create(this, GroupCreateEvent.Cause.CUSTOM, leader, name, null, null);
    }

    @Override
    public PlayerGroup createGroup(Player leader, Dungeon dungeon) {
        return DGroup.create(this, GroupCreateEvent.Cause.CUSTOM, leader, null, null, dungeon);
    }

    @Override
    public PlayerGroup createGroup(Player leader, Collection<Player> members, String name, Dungeon dungeon) {
        PlayerGroup group = DGroup.create(this, GroupCreateEvent.Cause.CUSTOM, leader, name, null, dungeon);
        if (members != null) {
            members.forEach(group::addMember);
        }
        return group;
    }

    @Override
    public DungeonMob wrapEntity(LivingEntity entity, GameWorld gameWorld, String triggerId) {
        DungeonMob mob = getDungeonMob(entity);
        if (mob != null) {
            return mob;
        } else {
            return new DMob(entity, gameWorld, caliburn.getExMob(triggerId), triggerId);
        }
    }

    @Override
    public DungeonMob wrapEntity(LivingEntity entity, GameWorld gameWorld, ExMob type) {
        DungeonMob mob = getDungeonMob(entity);
        if (mob != null) {
            return mob;
        } else {
            return new DMob(entity, gameWorld, type, type.getId());
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

    @Override
    public boolean isDungeonItem(ItemStack itemStack) {
        if (!Version.isAtLeast(Version.MC1_16_5)) {
            return false;
        }
        if (itemStack == null || !itemStack.hasItemMeta()) {
            return false;
        }
        return itemStack.getItemMeta().getPersistentDataContainer().has(NamespacedKey.fromString("dungeon_item", this), PersistentDataType.BYTE);
    }

    @Override
    public ItemStack setDungeonItem(ItemStack itemStack, boolean dungeonItem) {
        if (!Version.isAtLeast(Version.MC1_16_5)) {
            return null;
        }
        if (itemStack == null || itemStack.getItemMeta() == null) {
            return null;
        }
        ItemStack dIStack = itemStack.clone();
        ItemMeta meta = dIStack.getItemMeta();
        NamespacedKey key = NamespacedKey.fromString("dungeon_item", this);
        if (dungeonItem) {
            meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
        } else {
            meta.getPersistentDataContainer().remove(key);
        }
        dIStack.setItemMeta(meta);
        return dIStack;
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
        playerCache.remove(dPlayer);
        playerCache.add(player, dPlayer);
        dPlayer.setOfflineTimeMillis(0);
        return true;
    }

    private boolean xlDevMode = System.getProperty("XLDevMode") != null;

    public void log(String message) {
        if (xlDevMode) {
            MessageUtil.log(this, message);
        }
    }

    public <T> void log(String message, T t, Predicate<T> predicate) {
        if (xlDevMode && !predicate.test(t)) {
            throw new AssertionError(message);
        }
    }

}
