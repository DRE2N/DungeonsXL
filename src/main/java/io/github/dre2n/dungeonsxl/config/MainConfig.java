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
package io.github.dre2n.dungeonsxl.config;

import io.github.dre2n.commons.chat.MessageUtil;
import io.github.dre2n.commons.compatibility.CompatibilityHandler;
import io.github.dre2n.commons.compatibility.Internals;
import io.github.dre2n.commons.config.DREConfig;
import io.github.dre2n.commons.misc.EnumUtil;
import io.github.dre2n.dungeonsxl.util.DColor;
import static io.github.dre2n.dungeonsxl.util.DColor.*;
import io.github.dre2n.dungeonsxl.world.WorldConfig;
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

    public enum BackupMode {
        ON_DISABLE,
        ON_DISABLE_AND_SAVE,
        ON_SAVE,
        NEVER
    }

    public static final int CONFIG_VERSION = 14;

    private String language = "english";
    private boolean enableEconomy = false;

    /* Chat */
    private boolean chatEnabled = true;
    private String chatFormatGame = "&2[Game] %group_color%%player_name%: &r";
    private String chatFormatGroup = "&2%group_color%[%group_name%] %player_name%: &r";
    private String chatFormatSpy = "&2[Chat Spy] %player_name%: &r";

    /* Tutorial */
    private boolean tutorialActivated = false;
    private String tutorialDungeon = "tutorial";
    private String tutorialStartGroup = "default";
    private String tutorialEndGroup = "player";

    /* Announcers */
    private List<DColor> groupColorPriority = new ArrayList<>(Arrays.asList(
            DARK_BLUE,
            LIGHT_RED,
            YELLOW,
            LIGHT_GREEN,
            PURPLE,
            ORANGE,
            WHITE,
            BLACK,
            LIGHT_BLUE,
            DARK_GREEN,
            DARK_RED,
            LIGHT_GRAY,
            CYAN,
            MAGENTA,
            DARK_GRAY
    ));
    private double announcementInterval = 30;

    /* Misc */
    private boolean sendFloorTitle = true;
    private Map<String, Object> externalMobProviders = new HashMap<>();
    private Map<String, Object> resourcePacks = new HashMap<>();

    /* Performance */
    private int maxInstances = 10;
    private boolean tweaksEnabled = false;

    /* Secure Mode */
    private boolean secureModeEnabled = false;
    private double secureModeCheckInterval = 5;
    private boolean openInventories = false;
    private boolean dropItems = false;
    private List<String> editCommandWhitelist = new ArrayList<>();
    private BackupMode backupMode = BackupMode.ON_DISABLE_AND_SAVE;

    /* Permissions bridge */
    private List<String> editPermissions = new ArrayList<>();

    /* Default Dungeon Settings */
    private WorldConfig defaultWorldConfig;

    public MainConfig(File file) {
        super(file, CONFIG_VERSION);

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
     * @param language
     * the language to set
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
     * @param enabled
     * if DungeonsXL should use economy features provided by Vault
     */
    public void setEconomyEnabled(boolean enabled) {
        enableEconomy = enabled;
    }

    /**
     * @return
     * if the dungeon chat is enabled
     */
    public boolean isChatEnabled() {
        return chatEnabled;
    }

    /**
     * @param enabled
     * if the dungeon chat is enabled
     */
    public void setChatEnabled(boolean enabled) {
        chatEnabled = enabled;
    }

    /**
     * @return
     * the game chat format
     */
    public String getChatFormatGame() {
        return chatFormatGame;
    }

    /**
     * @param string
     * the game chat format to set
     */
    public void setChatFormatGame(String string) {
        chatFormatGame = string;
    }

    /**
     * @return
     * the group chat format
     */
    public String getChatFormatGroup() {
        return chatFormatGroup;
    }

    /**
     * @param string
     * the group chat format to set
     */
    public void setChatFormatGroup(String string) {
        chatFormatGroup = string;
    }

    /**
     * @return
     * the chat spy format
     */
    public String getChatFormatSpy() {
        return chatFormatSpy;
    }

    /**
     * @param string
     * the chat spy format to set
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
     * @param activated
     * if new players start in a tutorial
     */
    public void setTutorialActivated(boolean activated) {
        tutorialActivated = activated;
    }

    /**
     * @return the tutorial dungeon
     */
    public String getTutorialDungeon() {
        return tutorialDungeon;
    }

    /**
     * @param dungeon
     * the tutorial dungeon to set
     */
    public void setTutorialDungeon(String dungeon) {
        tutorialDungeon = dungeon;
    }

    /**
     * @return the tutorialStartGroup
     */
    public String getTutorialStartGroup() {
        return tutorialStartGroup;
    }

    /**
     * @param group
     * the group the player gets when he plays the tutorial
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
     * @param group
     * the group the player gets when he finshs the tutorial
     */
    public void setTutorialEndGroup(String group) {
        tutorialEndGroup = group;
    }

    /**
     * @return the group colors
     */
    public List<DColor> getGroupColorPriority() {
        return groupColorPriority;
    }

    /**
     * @param colors
     * the colors to set
     */
    public void setGroupColorPriority(List<DColor> colors) {
        groupColorPriority = colors;
    }

    /**
     * @return the announcement interval
     */
    public long getAnnouncmentInterval() {
        return (long) (announcementInterval * 20);
    }

    /**
     * @param interval
     * the interval to set
     */
    public void setAnnouncementInterval(double interval) {
        announcementInterval = interval;
    }

    /**
     * @return if the floor title shall be sent
     */
    public boolean isSendFloorTitleEnabled() {
        return sendFloorTitle;
    }

    /**
     * @param enabled
     * if the floor title shall be sent
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
     * @param maxInstances
     * the maximum amount of worlds to instantiate at once
     */
    public void setMaxInstances(int maxInstances) {
        this.maxInstances = maxInstances;
    }

    /**
     * @return if the performance tweaks are enabled
     */
    public boolean areTweaksEnabled() {
        return tweaksEnabled;
    }

    /**
     * @param enabled
     * if the performance tweaks are enabled
     */
    public void setTweaksEnabled(boolean enabled) {
        tweaksEnabled = enabled;
    }

    /**
     * @return if the secure mode is enabled
     */
    public boolean isSecureModeEnabled() {
        return secureModeEnabled;
    }

    /**
     * @param enabled
     * if the secure mode is enabled
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
     * @param openInventories
     * if inventories can be opened in edit mode
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
     * @param dropItems
     * if items may be dropped in edit mode
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
     * @param interval
     * the interval for the check task
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
     * @param mode
     * the BackupMode to set
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

        if (!config.contains("chatEnabled")) {
            config.set("chatEnabled", chatEnabled);
        }

        if (!config.contains("chatFormatGame")) {
            config.set("chatFormatGame", chatFormatGame);
        }

        if (!config.contains("chatFormatGroup")) {
            config.set("chatFormatGroup", chatFormatGroup);
        }

        if (!config.contains("chatFormatSpy")) {
            config.set("chatFormatSpy", chatFormatSpy);
        }

        if (!config.contains("tutorial.activated")) {
            config.set("tutorial.activated", tutorialActivated);
        }

        if (!config.contains("tutorial.dungeon")) {
            config.set("tutorial.dungeon", tutorialDungeon);
        }

        if (!config.contains("tutorial.startGroup")) {
            config.set("tutorial.startGroup", tutorialStartGroup);
        }

        if (!config.contains("tutorial.endGroup")) {
            config.set("tutorial.endgroup", tutorialEndGroup);
        }

        if (!config.contains("groupColorPriority")) {
            ArrayList<String> strings = new ArrayList<>();
            for (DColor color : groupColorPriority) {
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

        if (!config.contains("externalMobProviders")) {
            config.createSection("externalMobProviders");
        }

        if (!config.contains("resourcePacks")) {
            config.createSection("resourcePacks");
        }

        if (!config.contains("maxInstances")) {
            config.set("maxInstances", maxInstances);
        }

        if (!config.contains("tweaksEnabled")) {
            config.set("tweaksEnabled", tweaksEnabled);
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

        /* Default Dungeon Config */
        if (!config.contains("default")) {
            config.createSection("default");
        }

        save();
    }

    @Override
    public void load() {
        /* Main Config */
        if (config.contains("language")) {
            language = config.getString("language");
        }

        if (config.contains("enableEconomy")) {
            enableEconomy = config.getBoolean("enableEconomy");
        }

        if (config.contains("chatEnabled")) {
            chatEnabled = config.getBoolean("chatEnabled");
        }

        if (config.contains("chatFormatGame")) {
            chatFormatGame = config.getString("chatFormatGame");
        }

        if (config.contains("chatFormatGroup")) {
            chatFormatGroup = config.getString("chatFormatGroup");
        }

        if (config.contains("chatFormatSpy")) {
            chatFormatSpy = config.getString("chatFormatSpy");
        }

        if (config.contains("chatEnabled")) {
            chatEnabled = config.getBoolean("chatEnabled");
        }

        if (config.contains("tutorial.activated")) {
            tutorialActivated = config.getBoolean("tutorial.activated");
        }

        if (config.contains("tutorial.dungeon")) {
            tutorialDungeon = config.getString("tutorial.dungeon");
        }

        if (config.contains("tutorial.startgroup")) {
            tutorialStartGroup = config.getString("tutorial.startgroup");
        }

        if (config.contains("tutorial.endgroup")) {
            tutorialEndGroup = config.getString("tutorial.endgroup");
        }

        if (config.contains("groupColorPriority")) {
            if (config.getStringList("groupColorPriority").size() < 15) {
                ArrayList<String> strings = new ArrayList<>();
                for (DColor color : groupColorPriority) {
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
                    if (EnumUtil.isValidEnum(DColor.class, color)) {
                        groupColorPriority.add(DColor.valueOf(color));
                    }
                }
            }
        }

        if (config.contains("announcementInterval")) {
            announcementInterval = config.getDouble("announcementInterval");
        }

        if (config.contains("sendFloorTitle")) {
            sendFloorTitle = config.getBoolean("sendFloorTitle");
        }

        if (config.contains("externalMobProviders")) {
            externalMobProviders = config.getConfigurationSection("externalMobProviders").getValues(false);
        }

        if (config.contains("resourcePacks")) {
            resourcePacks = config.getConfigurationSection("resourcePacks").getValues(false);
        }

        if (config.contains("maxInstances")) {
            maxInstances = config.getInt("maxInstances");
        }

        if (config.contains("tweaksEnabled")) {
            if (Internals.andHigher(Internals.v1_9_R1).contains(CompatibilityHandler.getInstance().getInternals())) {
                tweaksEnabled = config.getBoolean("tweaksEnabled");
            } else {
                tweaksEnabled = false;
                MessageUtil.log(DMessage.LOG_DISABLED_TWEAKS.getMessage());
            }
        }

        if (config.contains("secureMode.enabled")) {
            secureModeEnabled = config.getBoolean("secureMode.enabled");
        }

        if (config.contains("secureMode.openInventories")) {
            openInventories = config.getBoolean("secureMode.openInventories");
        }

        if (config.contains("secureMode.dropItems")) {
            dropItems = config.getBoolean("secureMode.dropItems");
        }

        if (config.contains("secureMode.checkInterval")) {
            secureModeCheckInterval = config.getDouble("secureMode.checkInterval");
        }

        if (config.contains("secureMode.editCommandWhitelist")) {
            editCommandWhitelist = config.getStringList("secureMode.editCommandWhitelist");
        }

        if (config.contains("backupMode")) {
            String mode = config.getString("backupMode");
            if (EnumUtil.isValidEnum(BackupMode.class, mode)) {
                backupMode = BackupMode.valueOf(mode);
            }
        }

        if (config.contains("editPermissions")) {
            editPermissions = config.getStringList("editPermissions");
        }

        /* Default Dungeon Config */
        ConfigurationSection configSection = config.getConfigurationSection("default");
        if (configSection != null) {
            defaultWorldConfig = new WorldConfig(configSection);
        }
    }

}
