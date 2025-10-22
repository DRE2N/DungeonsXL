package com.github.linghun91.dungeonsxl;

import com.github.linghun91.dungeonsxl.api.DungeonsAPI;
import com.github.linghun91.dungeonsxl.command.CommandManager;
import com.github.linghun91.dungeonsxl.config.MainConfig;
import com.github.linghun91.dungeonsxl.config.MessageConfig;
import com.github.linghun91.dungeonsxl.dungeon.DungeonManager;
import com.github.linghun91.dungeonsxl.integration.PlaceholderAPIIntegration;
import com.github.linghun91.dungeonsxl.integration.VaultIntegration;
import com.github.linghun91.dungeonsxl.listener.*;
import com.github.linghun91.dungeonsxl.mob.MobManager;
import com.github.linghun91.dungeonsxl.player.PlayerManager;
import com.github.linghun91.dungeonsxl.registry.*;
import com.github.linghun91.dungeonsxl.sign.SignManager;
import com.github.linghun91.dungeonsxl.trigger.TriggerManager;
import com.github.linghun91.dungeonsxl.world.WorldManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

/**
 * Main plugin class for DungeonsXL
 * Modern rewrite using Paper API 1.21.8 and Java 21
 *
 * @author linghun91
 */
public final class DungeonsXL extends JavaPlugin implements DungeonsAPI {

    private static DungeonsXL instance;
    private Logger logger;

    // Configuration
    private MainConfig mainConfig;
    private MessageConfig messageConfig;

    // Core Managers
    private WorldManager worldManager;
    private DungeonManager dungeonManager;
    private PlayerManager playerManager;
    private SignManager signManager;
    private TriggerManager triggerManager;
    private MobManager mobManager;
    private CommandManager commandManager;

    // Registries
    private GameRuleRegistry gameRuleRegistry;
    private RequirementRegistry requirementRegistry;
    private RewardRegistry rewardRegistry;
    private SignRegistry signRegistry;
    private TriggerRegistry triggerRegistry;

    // Integrations
    private VaultIntegration vaultIntegration;
    private PlaceholderAPIIntegration placeholderAPIIntegration;

    @Override
    public void onLoad() {
        instance = this;
        logger = getLogger();
        logger.info("Loading DungeonsXL 2.0...");
    }

    @Override
    public void onEnable() {
        logger.info("Enabling DungeonsXL 2.0...");

        try {
            // Initialize configurations
            initializeConfigs();

            // Initialize registries
            initializeRegistries();

            // Initialize managers
            initializeManagers();

            // Register listeners
            registerListeners();

            // Register commands
            registerCommands();

            // Initialize integrations
            initializeIntegrations();

            logger.info("DungeonsXL 2.0 successfully enabled!");
            logger.info("Loaded " + dungeonManager.getDungeonCount() + " dungeons");
            logger.info("Using Paper API " + getServer().getMinecraftVersion());

        } catch (Exception e) {
            logger.severe("Failed to enable DungeonsXL: " + e.getMessage());
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        logger.info("Disabling DungeonsXL 2.0...");

        try {
            // Save all player data
            if (playerManager != null) {
                playerManager.saveAllPlayers();
            }

            // Unload all instance worlds
            if (worldManager != null) {
                worldManager.unloadAllInstances();
            }

            // Save all configurations
            if (mainConfig != null) {
                mainConfig.save();
            }

            logger.info("DungeonsXL 2.0 successfully disabled!");

        } catch (Exception e) {
            logger.severe("Error during plugin disable: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeConfigs() {
        logger.info("Initializing configurations...");

        // Create plugin folder if it doesn't exist
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        // Initialize main config
        mainConfig = new MainConfig(this);
        mainConfig.load();

        // Initialize message config
        messageConfig = new MessageConfig(this);
        messageConfig.load();

        logger.info("Configurations initialized");
    }

    private void initializeRegistries() {
        logger.info("Initializing registries...");

        gameRuleRegistry = new GameRuleRegistry();
        requirementRegistry = new RequirementRegistry();
        rewardRegistry = new RewardRegistry();
        signRegistry = new SignRegistry();
        triggerRegistry = new TriggerRegistry();

        // Register default game rules
        gameRuleRegistry.registerDefaults();

        // Register default requirements
        requirementRegistry.registerDefaults();

        // Register default rewards
        rewardRegistry.registerDefaults();

        // Register all sign types
        signRegistry.registerAllSigns();

        // Register all trigger types
        triggerRegistry.registerAllTriggers();

        logger.info("Registries initialized");
        logger.info("Registered " + signRegistry.getSignCount() + " sign types");
        logger.info("Registered " + triggerRegistry.getTriggerCount() + " trigger types");
        logger.info("Registered " + gameRuleRegistry.getGameRuleCount() + " game rules");
    }

    private void initializeManagers() {
        logger.info("Initializing managers...");

        worldManager = new WorldManager(this);
        dungeonManager = new DungeonManager(this);
        playerManager = new PlayerManager(this);
        signManager = new SignManager(this);
        triggerManager = new TriggerManager(this);
        mobManager = new MobManager(this);
        commandManager = new CommandManager(this);

        // Load all dungeons
        dungeonManager.loadDungeons();

        logger.info("Managers initialized");
    }

    private void registerListeners() {
        logger.info("Registering event listeners...");

        var pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(new PlayerListener(this), this);
        pluginManager.registerEvents(new WorldListener(this), this);
        pluginManager.registerEvents(new SignListener(this), this);
        pluginManager.registerEvents(new MobListener(this), this);
        pluginManager.registerEvents(new BlockListener(this), this);

        logger.info("Event listeners registered");
    }

    private void registerCommands() {
        logger.info("Registering commands...");

        commandManager.registerAllCommands();

        logger.info("Commands registered");
    }

    private void initializeIntegrations() {
        logger.info("Initializing integrations...");

        // Vault integration
        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            vaultIntegration = new VaultIntegration(this);
            if (vaultIntegration.init()) {
                logger.info("Vault integration enabled");
            } else {
                logger.warning("Vault found but economy not available");
            }
        } else {
            logger.info("Vault not found - economy features disabled");
        }

        // PlaceholderAPI integration
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            placeholderAPIIntegration = new PlaceholderAPIIntegration(this);
            if (placeholderAPIIntegration.register()) {
                logger.info("PlaceholderAPI integration enabled");
            }
        } else {
            logger.info("PlaceholderAPI not found - placeholder features disabled");
        }
    }

    // Static getter
    public static DungeonsXL getInstance() {
        return instance;
    }

    // API Implementation
    @Override
    public WorldManager getWorldManager() {
        return worldManager;
    }

    @Override
    public DungeonManager getDungeonManager() {
        return dungeonManager;
    }

    @Override
    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    @Override
    public SignManager getSignManager() {
        return signManager;
    }

    @Override
    public TriggerManager getTriggerManager() {
        return triggerManager;
    }

    @Override
    public MobManager getMobManager() {
        return mobManager;
    }

    // Getters
    public MainConfig getMainConfig() {
        return mainConfig;
    }

    public MessageConfig getMessageConfig() {
        return messageConfig;
    }

    public GameRuleRegistry getGameRuleRegistry() {
        return gameRuleRegistry;
    }

    public RequirementRegistry getRequirementRegistry() {
        return requirementRegistry;
    }

    public RewardRegistry getRewardRegistry() {
        return rewardRegistry;
    }

    public SignRegistry getSignRegistry() {
        return signRegistry;
    }

    public TriggerRegistry getTriggerRegistry() {
        return triggerRegistry;
    }

    public VaultIntegration getVaultIntegration() {
        return vaultIntegration;
    }

    public PlaceholderAPIIntegration getPlaceholderAPIIntegration() {
        return placeholderAPIIntegration;
    }

    public boolean hasVault() {
        return vaultIntegration != null && vaultIntegration.isEnabled();
    }

    public boolean hasPlaceholderAPI() {
        return placeholderAPIIntegration != null && placeholderAPIIntegration.isEnabled();
    }
}
