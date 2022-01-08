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
package de.erethon.dungeonsxl.config;

import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.api.dungeon.Dungeon;
import de.erethon.dungeonsxl.api.player.PlayerGroup.Color;
import static de.erethon.dungeonsxl.api.player.PlayerGroup.Color.*;
import de.erethon.dungeonsxl.util.commons.config.DREConfig;
import de.erethon.dungeonsxl.util.commons.misc.EnumUtil;
import de.erethon.dungeonsxl.world.WorldConfig;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Represents the main config.yml.
 *
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class MainConfig extends DREConfig {

    private DungeonsXL plugin;

    public enum BackupMode {
        ON_DISABLE,
        ON_DISABLE_AND_SAVE,
        ON_SAVE,
        NEVER
    }

    public static final int CONFIG_VERSION = 20;

    private String language = "english";
    private boolean enableEconomy = false;
    private boolean groupAdaptersEnabled = false;

    /* Chat */
    private boolean chatEnabled = true;
    private String chatFormatEdit = "&2[Edit] &r%player_name%: ";
    private String chatFormatGame = "&2[Game] %group_color%%player_name%: &r";
    private String chatFormatGroup = "&2%group_color%[%group_name%] %player_name%: &r";
    private String chatFormatSpy = "&2[Chat Spy] %player_name%: &r";

    /* Tutorial */
    private boolean tutorialActivated = false;
    private String tutorialDungeonName = "tutorial";
    private Dungeon tutorialDungeon;
    private String tutorialStartGroup = "default";
    private String tutorialEndGroup = "player";

    /* Announcers */
    private List<Color> groupColorPriority = new ArrayList<>(Arrays.asList(
            DARK_BLUE,
            LIGHT_RED,
            YELLOW,
            LIGHT_GREEN,
            PURPLE,
            ORANGE,
            BLACK,
            LIGHT_BLUE,
            DARK_GREEN,
            DARK_RED,
            LIGHT_GRAY,
            CYAN,
            MAGENTA,
            DARK_GRAY,
            PINK
    ));
    private double announcementInterval = 30;

    /* Misc */
    private boolean sendFloorTitle = true;
    private boolean globalDeathMessagesDisabled = true;
    private Map<String, Object> externalMobProviders = new HashMap<>();
    private Map<String, Object> resourcePacks = new HashMap<>();

    /* Performance */
    private int maxInstances = 10;
    private int editInstanceRemovalDelay = 5;
    private boolean strictMovementCheckEnabled = true;

    /* Secure Mode */
    private boolean secureModeEnabled = false;
    private double secureModeCheckInterval = 5;
    private boolean openInventories = false;
    private boolean dropItems = false;
    private List<String> editCommandWhitelist;
    private BackupMode backupMode = BackupMode.ON_DISABLE_AND_SAVE;
    private boolean lobbyContainersEnabled = false;

    /* Permissions bridge */
    private List<String> editPermissions;

    private WorldConfig defaultWorldConfig;

    public MainConfig(DungeonsXL plugin, File file) {
        super(file, CONFIG_VERSION);

        this.plugin = plugin;

        if (initialize) {
            initialize();
        }
        load();
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean isEconomyEnabled() {
        return enableEconomy;
    }

    public void setEconomyEnabled(boolean enabled) {
        enableEconomy = enabled;
    }

    public boolean areGroupAdaptersEnabled() {
        return groupAdaptersEnabled;
    }

    public void setGroupAdaptersEnabled(boolean enabled) {
        groupAdaptersEnabled = enabled;
    }

    public boolean isChatEnabled() {
        return chatEnabled;
    }

    public void setChatEnabled(boolean enabled) {
        chatEnabled = enabled;
    }

    public String getChatFormatEdit() {
        return chatFormatEdit;
    }

    public void setEditFormatEdit(String string) {
        chatFormatEdit = string;
    }

    public String getChatFormatGame() {
        return chatFormatGame;
    }

    public void setChatFormatGame(String string) {
        chatFormatGame = string;
    }

    public String getChatFormatGroup() {
        return chatFormatGroup;
    }

    public void setChatFormatGroup(String string) {
        chatFormatGroup = string;
    }

    public String getChatFormatSpy() {
        return chatFormatSpy;
    }

    public void setChatFormatSpy(String string) {
        chatFormatSpy = string;
    }

    public boolean isTutorialActivated() {
        return tutorialActivated;
    }

    public void setTutorialActivated(boolean activated) {
        tutorialActivated = activated;
    }

    public Dungeon getTutorialDungeon() {
        if (tutorialDungeon == null) {
            tutorialDungeon = plugin.getDungeonRegistry().get(tutorialDungeonName);
        }
        return tutorialDungeon;
    }

    public void setTutorialDungeon(Dungeon dungeon) {
        tutorialDungeon = dungeon;
    }

    public String getTutorialStartGroup() {
        return tutorialStartGroup;
    }

    public void setTutorialStartGroup(String group) {
        tutorialStartGroup = group;
    }

    public String getTutorialEndGroup() {
        return tutorialEndGroup;
    }

    public void setTutorialEndGroup(String group) {
        tutorialEndGroup = group;
    }

    public List<Color> getGroupColorPriority() {
        return groupColorPriority;
    }

    public Color getGroupColorPriority(int count) {
        return (count < groupColorPriority.size() && count >= 0) ? groupColorPriority.get(count) : Color.WHITE;
    }

    public void setGroupColorPriority(List<Color> colors) {
        groupColorPriority = colors;
    }

    public long getAnnouncmentInterval() {
        return (long) (announcementInterval * 20);
    }

    public void setAnnouncementInterval(double interval) {
        announcementInterval = interval;
    }

    public boolean areGlobalDeathMessagesDisabled() {
        return globalDeathMessagesDisabled;
    }

    public void setGlobalDeathMessagesDisabled(boolean disabled) {
        globalDeathMessagesDisabled = false;
    }

    public boolean isSendFloorTitleEnabled() {
        return sendFloorTitle;
    }

    public void setSendFloorTitleEnabled(boolean enabled) {
        sendFloorTitle = enabled;
    }

    public Map<String, Object> getExternalMobProviders() {
        return externalMobProviders;
    }

    public Map<String, Object> getResourcePacks() {
        return resourcePacks;
    }

    public int getMaxInstances() {
        return maxInstances;
    }

    public void setMaxInstances(int maxInstances) {
        this.maxInstances = maxInstances;
    }

    public int getEditInstanceRemovalDelay() {
        return editInstanceRemovalDelay;
    }

    public void setEditInstanceRemovalDelay(int delay) {
        editInstanceRemovalDelay = delay;
    }

    public boolean isStrictMovementCheckEnabled() {
        return strictMovementCheckEnabled;
    }

    public void setStrictMovementCheckEnabled(boolean enabled) {
        strictMovementCheckEnabled = enabled;
    }

    public boolean isSecureModeEnabled() {
        return secureModeEnabled;
    }

    public void setSecureModeEnabled(boolean enabled) {
        secureModeEnabled = enabled;
    }

    public boolean getOpenInventories() {
        return openInventories && secureModeEnabled;
    }

    public void setOpenInventories(boolean openInventories) {
        this.openInventories = openInventories;
    }

    /**
     * @return if players may drop items while editing; false if secure mode disabled
     */
    public boolean getDropItems() {
        return dropItems && secureModeEnabled;
    }

    /**
     * @param dropItems if items may be dropped in edit mode
     */
    public void setDropItems(boolean dropItems) {
        this.dropItems = dropItems;
    }

    public long getSecureModeCheckInterval() {
        return (long) (secureModeCheckInterval * 20);
    }

    public void setSecureModeCheckInterval(double interval) {
        secureModeCheckInterval = interval;
    }

    public List<String> getEditCommandWhitelist() {
        return editCommandWhitelist;
    }

    public BackupMode getBackupMode() {
        return backupMode;
    }

    public void setBackupMode(BackupMode mode) {
        backupMode = mode;
    }

    public boolean areLobbyContainersEnabled() {
        return lobbyContainersEnabled;
    }

    public void setLobbyContainersEnabled(boolean enabled) {
        lobbyContainersEnabled = enabled;
    }

    public List<String> getEditPermissions() {
        return editPermissions;
    }

    public WorldConfig getDefaultWorldConfig() {
        return defaultWorldConfig;
    }

    @Override
    public void initialize() {
        /* Main Config */
        if (!config.contains("language")) {
            config.set("language", language);
        }

        if (!config.contains("enableEconomy")) {
            config.set("enableEconomy", enableEconomy);
        }

        if (!config.contains("groupAdaptersEnabled")) {
            config.set("groupAdaptersEnabled", groupAdaptersEnabled);
        }

        if (!config.contains("chatEnabled")) {
            config.set("chatEnabled", chatEnabled);
        }

        if (!config.contains("chatFormat.edit")) {
            config.set("chatFormat.edit", chatFormatEdit);
        }

        if (!config.contains("chatFormat.game")) {
            config.set("chatFormat.game", chatFormatGame);
        }

        if (!config.contains("chatFormat.group")) {
            config.set("chatFormat.group", chatFormatGroup);
        }

        if (!config.contains("chatFormat.spy")) {
            config.set("chatFormat.spy", chatFormatSpy);
        }

        if (!config.contains("tutorial.activated")) {
            config.set("tutorial.activated", tutorialActivated);
        }

        if (!config.contains("tutorial.dungeon")) {
            config.set("tutorial.dungeon", tutorialDungeonName);
        }

        if (!config.contains("tutorial.startGroup")) {
            config.set("tutorial.startGroup", tutorialStartGroup);
        }

        if (!config.contains("tutorial.endGroup")) {
            config.set("tutorial.endgroup", tutorialEndGroup);
        }

        if (!config.contains("groupColorPriority")) {
            ArrayList<String> strings = new ArrayList<>();
            for (Color color : groupColorPriority) {
                strings.add(color.toString());
            }
            config.set("groupColorPriority", strings);
        }

        if (!config.contains("announcementInterval")) {
            config.set("announcementInterval", announcementInterval);
        }

        if (!config.contains("sendFloorTitle")) {
            config.set("sendFloorTitle", sendFloorTitle);
        }

        if (!config.contains("globalDeathMessagesDisabled")) {
            config.set("globalDeathMessagesDisabled", globalDeathMessagesDisabled);
        }

        if (!config.contains("externalMobProviders")) {
            config.createSection("externalMobProviders");
        }

        if (!config.contains("resourcePacks")) {
            config.createSection("resourcePacks");
        }

        if (!config.contains("maxInstances")) {
            config.set("maxInstances", maxInstances);
        }

        if (!config.contains("editInstanceRemovalDelay")) {
            config.set("editInstanceRemovalDelay", editInstanceRemovalDelay);
        }

        if (!config.contains("strictMovementCheckEnabled")) {
            config.set("strictMovementCheckEnabled", strictMovementCheckEnabled);
        }

        if (!config.contains("secureMode.enabled")) {
            config.set("secureMode.enabled", secureModeEnabled);
        }

        if (!config.contains("secureMode.openInventories")) {
            config.set("secureMode.openInventories", openInventories);
        }

        if (!config.contains("secureMode.dropItems")) {
            config.set("secureMode.dropItems", dropItems);
        }

        if (!config.contains("secureMode.checkInterval")) {
            config.set("secureMode.checkInterval", secureModeCheckInterval);
        }

        if (!config.contains("secureMode.editCommandWhitelist")) {
            config.set("secureMode.editCommandWhitelist", editCommandWhitelist);
        }

        if (!config.contains("backupMode")) {
            config.set("backupMode", backupMode.toString());
        }

        if (!config.contains("lobbyContainersEnabled")) {
            config.set("lobbyContainersEnabled", lobbyContainersEnabled);
        }

        if (!config.contains("editPermissions")) {
            config.set("editPermissions", editPermissions);
        }

        /* Default Dungeon Config */
        if (!config.contains("default")) {
            ConfigurationSection section = config.createSection("default");
            section.set("damageProtectedEntities", Arrays.asList("ARMOR_STAND", "ITEM_FRAME", "PAINTING"));
            section.set("interactionProtectedEntities", Arrays.asList("ARMOR_STAND", "ITEM_FRAME"));
        }

        save();
    }

    @Override
    public void load() {
        language = config.getString("language", language);
        plugin.getMessageHandler().setDefaultLanguage(language);
        enableEconomy = config.getBoolean("enableEconomy", enableEconomy);
        groupAdaptersEnabled = config.getBoolean("groupAdaptersEnabled", groupAdaptersEnabled);
        chatEnabled = config.getBoolean("chatEnabled", chatEnabled);
        chatFormatEdit = config.getString("chatFormat.edit", chatFormatEdit);
        chatFormatGame = config.getString("chatFormat.game", chatFormatGame);
        chatFormatGroup = config.getString("chatFormat.group", chatFormatGroup);
        chatFormatSpy = config.getString("chatFormat.spy", chatFormatSpy);
        chatEnabled = config.getBoolean("chatEnabled", chatEnabled);
        tutorialActivated = config.getBoolean("tutorial.activated", tutorialActivated);
        tutorialDungeonName = config.getString("tutorial.dungeon", tutorialDungeonName);
        tutorialStartGroup = config.getString("tutorial.startgroup", tutorialStartGroup);
        tutorialEndGroup = config.getString("tutorial.endgroup", tutorialEndGroup);

        if (config.getStringList("groupColorPriority").size() < 14) {
            ArrayList<String> strings = new ArrayList<>();
            for (Color color : groupColorPriority) {
                strings.add(color.toString());
            }
            config.set("groupColorPriority", strings);
            try {
                config.save(file);
            } catch (IOException exception) {
            }

        } else {
            groupColorPriority.clear();
            for (String color : config.getStringList("groupColorPriority")) {
                Color dColor = EnumUtil.getEnum(Color.class, color);
                if (dColor != null && dColor != Color.WHITE) {
                    groupColorPriority.add(dColor);
                }
            }
        }

        announcementInterval = config.getDouble("announcementInterval", announcementInterval);
        sendFloorTitle = config.getBoolean("sendFloorTitle", sendFloorTitle);
        globalDeathMessagesDisabled = config.getBoolean("globalDeathMessagesDisabled", globalDeathMessagesDisabled);

        ConfigurationSection externalMobProvidersSection = config.getConfigurationSection("externalMobProviders");
        if (externalMobProvidersSection != null) {
            externalMobProviders = externalMobProvidersSection.getValues(false);
        }
        ConfigurationSection resourcePacksSection = config.getConfigurationSection("resourcePacks");
        if (resourcePacksSection != null) {
            resourcePacks = resourcePacksSection.getValues(false);
        }

        maxInstances = config.getInt("maxInstances", maxInstances);
        editInstanceRemovalDelay = config.getInt("editInstanceRemovalDelay", editInstanceRemovalDelay);
        strictMovementCheckEnabled = config.getBoolean("strictMovementCheckEnabled", strictMovementCheckEnabled);
        secureModeEnabled = config.getBoolean("secureMode.enabled", secureModeEnabled);
        openInventories = config.getBoolean("secureMode.openInventories", openInventories);
        dropItems = config.getBoolean("secureMode.dropItems", dropItems);
        secureModeCheckInterval = config.getDouble("secureMode.checkInterval", secureModeCheckInterval);
        editCommandWhitelist = config.getStringList("secureMode.editCommandWhitelist");

        String mode = config.getString("backupMode");
        if (EnumUtil.isValidEnum(BackupMode.class, mode)) {
            backupMode = BackupMode.valueOf(mode);
        }
        lobbyContainersEnabled = config.getBoolean("lobbyContainersEnabled", lobbyContainersEnabled);

        editPermissions = config.getStringList("editPermissions");

        ConfigurationSection defaultWorldSection = config.getConfigurationSection("default");
        if (defaultWorldSection != null) {
            defaultWorldConfig = new WorldConfig(plugin, defaultWorldSection);
        }
    }

}
