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
package de.erethon.dungeonsxl.config;

import de.erethon.commons.config.DREConfig;
import de.erethon.commons.misc.EnumUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.api.dungeon.Dungeon;
import de.erethon.dungeonsxl.api.player.PlayerGroup.Color;
import static de.erethon.dungeonsxl.api.player.PlayerGroup.Color.*;
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

    public static final int CONFIG_VERSION = 18;

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

    /* Secure Mode */
    private boolean secureModeEnabled = false;
    private double secureModeCheckInterval = 5;
    private boolean openInventories = false;
    private boolean dropItems = false;
    private List<String> editCommandWhitelist;
    private BackupMode backupMode = BackupMode.ON_DISABLE_AND_SAVE;

    /* Permissions bridge */
    private List<String> editPermissions;

    /* Default DDungeon Settings */
    private WorldConfig defaultWorldConfig;

    public MainConfig(DungeonsXL plugin, File file) {
        super(file, CONFIG_VERSION);

        this.plugin = plugin;

        if (initialize) {
            initialize();
        }
        load();
    }

    /**
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param language the language to set
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * @return if DungeonsXL should use economy features provided by Vault
     */
    public boolean isEconomyEnabled() {
        return enableEconomy;
    }

    /**
     * @param enabled if DungeonsXL should use economy features provided by Vault
     */
    public void setEconomyEnabled(boolean enabled) {
        enableEconomy = enabled;
    }

    /**
     * @return if DungeonsXL should use group adapters
     */
    public boolean areGroupAdaptersEnabled() {
        return groupAdaptersEnabled;
    }

    /**
     * @param enabled if DungeonsXL should use group adapters
     */
    public void setGroupAdaptersEnabled(boolean enabled) {
        groupAdaptersEnabled = enabled;
    }

    /**
     * @return if the dungeon chat is enabled
     */
    public boolean isChatEnabled() {
        return chatEnabled;
    }

    /**
     * @param enabled if the dungeon chat is enabled
     */
    public void setChatEnabled(boolean enabled) {
        chatEnabled = enabled;
    }

    /**
     * @return the edit chat format
     */
    public String getChatFormatEdit() {
        return chatFormatEdit;
    }

    /**
     * @param string the edit chat format to set
     */
    public void setEditFormatEdit(String string) {
        chatFormatEdit = string;
    }

    /**
     * @return the game chat format
     */
    public String getChatFormatGame() {
        return chatFormatGame;
    }

    /**
     * @param string the game chat format to set
     */
    public void setChatFormatGame(String string) {
        chatFormatGame = string;
    }

    /**
     * @return the group chat format
     */
    public String getChatFormatGroup() {
        return chatFormatGroup;
    }

    /**
     * @param string the group chat format to set
     */
    public void setChatFormatGroup(String string) {
        chatFormatGroup = string;
    }

    /**
     * @return the chat spy format
     */
    public String getChatFormatSpy() {
        return chatFormatSpy;
    }

    /**
     * @param string the chat spy format to set
     */
    public void setChatFormatSpy(String string) {
        chatFormatSpy = string;
    }

    /**
     * @return if the tutorial is activated
     */
    public boolean isTutorialActivated() {
        return tutorialActivated;
    }

    /**
     * @param activated if new players start in a tutorial
     */
    public void setTutorialActivated(boolean activated) {
        tutorialActivated = activated;
    }

    /**
     * @return the tutorial dungeon
     */
    public Dungeon getTutorialDungeon() {
        if (tutorialDungeon == null) {
            tutorialDungeon = plugin.getDungeonRegistry().get(tutorialDungeonName);
        }
        return tutorialDungeon;
    }

    /**
     * @param dungeon the tutorial dungeon to set
     */
    public void setTutorialDungeon(Dungeon dungeon) {
        tutorialDungeon = dungeon;
    }

    /**
     * @return the tutorialStartGroup
     */
    public String getTutorialStartGroup() {
        return tutorialStartGroup;
    }

    /**
     * @param group the group the player gets when he plays the tutorial
     */
    public void setTutorialStartGroup(String group) {
        tutorialStartGroup = group;
    }

    /**
     * @return the tutorialEndGroup
     */
    public String getTutorialEndGroup() {
        return tutorialEndGroup;
    }

    /**
     * @param group the group the player gets when he finshs the tutorial
     */
    public void setTutorialEndGroup(String group) {
        tutorialEndGroup = group;
    }

    /**
     * @return the group colors
     */
    public List<Color> getGroupColorPriority() {
        return groupColorPriority;
    }

    /**
     * @param count the group count
     * @return the group color for the count
     */
    public Color getGroupColorPriority(int count) {
        return (count < groupColorPriority.size() && count >= 0) ? groupColorPriority.get(count) : Color.WHITE;
    }

    /**
     * @param colors the colors to set
     */
    public void setGroupColorPriority(List<Color> colors) {
        groupColorPriority = colors;
    }

    /**
     * @return the announcement interval
     */
    public long getAnnouncmentInterval() {
        return (long) (announcementInterval * 20);
    }

    /**
     * @param interval the interval to set
     */
    public void setAnnouncementInterval(double interval) {
        announcementInterval = interval;
    }

    /**
     * @return false if death messages shall be sent to players who are not in the dungeon, true if not
     */
    public boolean areGlobalDeathMessagesDisabled() {
        return globalDeathMessagesDisabled;
    }

    /**
     * @param disabled set if death messages shall be sent to players who are not in the dungeon
     */
    public void setGlobalDeathMessagesDisabled(boolean disabled) {
        globalDeathMessagesDisabled = false;
    }

    /**
     * @return if the floor title shall be sent
     */
    public boolean isSendFloorTitleEnabled() {
        return sendFloorTitle;
    }

    /**
     * @param enabled if the floor title shall be sent
     */
    public void setSendFloorTitleEnabled(boolean enabled) {
        sendFloorTitle = enabled;
    }

    /**
     * @return the custom external mob providers
     */
    public Map<String, Object> getExternalMobProviders() {
        return externalMobProviders;
    }

    /**
     * @return the resource pack index
     */
    public Map<String, Object> getResourcePacks() {
        return resourcePacks;
    }

    /**
     * @return the maximum amount of worlds to instantiate at once
     */
    public int getMaxInstances() {
        return maxInstances;
    }

    /**
     * @param maxInstances the maximum amount of worlds to instantiate at once
     */
    public void setMaxInstances(int maxInstances) {
        this.maxInstances = maxInstances;
    }

    /**
     * @return the delay in seconds until an edit world without players is saved and removed
     */
    public int getEditInstanceRemovalDelay() {
        return editInstanceRemovalDelay;
    }

    /**
     * @param delay the delay in seconds until an edit world without players is saved and removed
     */
    public void setEditInstanceRemovalDelay(int delay) {
        editInstanceRemovalDelay = delay;
    }

    /**
     * @return if the secure mode is enabled
     */
    public boolean isSecureModeEnabled() {
        return secureModeEnabled;
    }

    /**
     * @param enabled if the secure mode is enabled
     */
    public void setSecureModeEnabled(boolean enabled) {
        secureModeEnabled = enabled;
    }

    /**
     * @return if players may open inventories while editing; false if secure mode disabled
     */
    public boolean getOpenInventories() {
        return openInventories && secureModeEnabled;
    }

    /**
     * @param openInventories if inventories can be opened in edit mode
     */
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

    /**
     * @return the interval for the check task
     */
    public long getSecureModeCheckInterval() {
        return (long) (secureModeCheckInterval * 20);
    }

    /**
     * @param interval the interval for the check task
     */
    public void setSecureModeCheckInterval(double interval) {
        secureModeCheckInterval = interval;
    }

    /**
     * @return the editCommandWhitelist
     */
    public List<String> getEditCommandWhitelist() {
        return editCommandWhitelist;
    }

    /**
     * @return the backup mode
     */
    public BackupMode getBackupMode() {
        return backupMode;
    }

    /**
     * @param mode the BackupMode to set
     */
    public void setBackupMode(BackupMode mode) {
        backupMode = mode;
    }

    /**
     * @return the edit mode permissions
     */
    public List<String> getEditPermissions() {
        return editPermissions;
    }

    /**
     * @return the defaultWorldConfig
     */
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

        if (!config.contains("editPermissions")) {
            config.set("editPermissions", editPermissions);
        }

        /* Default DDungeon Config */
        if (!config.contains("default")) {
            config.createSection("default");
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
        secureModeEnabled = config.getBoolean("secureMode.enabled", secureModeEnabled);
        openInventories = config.getBoolean("secureMode.openInventories", openInventories);
        dropItems = config.getBoolean("secureMode.dropItems", dropItems);
        secureModeCheckInterval = config.getDouble("secureMode.checkInterval", secureModeCheckInterval);
        editCommandWhitelist = config.getStringList("secureMode.editCommandWhitelist");

        String mode = config.getString("backupMode");
        if (EnumUtil.isValidEnum(BackupMode.class, mode)) {
            backupMode = BackupMode.valueOf(mode);
        }

        editPermissions = config.getStringList("editPermissions");

        ConfigurationSection defaultWorldSection = config.getConfigurationSection("default");
        if (defaultWorldSection != null) {
            defaultWorldConfig = new WorldConfig(plugin, defaultWorldSection);
        }
    }

}
