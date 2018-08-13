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
package de.erethon.dungeonsxl.config;

import de.erethon.commons.config.DREConfig;
import de.erethon.commons.misc.EnumUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.dungeon.Dungeon;
import de.erethon.dungeonsxl.util.DColor;
import static de.erethon.dungeonsxl.util.DColor.*;
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

    public enum BackupMode {
        ON_DISABLE,
        ON_DISABLE_AND_SAVE,
        ON_SAVE,
        NEVER
    }

    public static final int CONFIG_VERSION = 16;

    private String language = "english";
    private boolean enableEconomy = false;

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
    private boolean globalDeathMessagesDisabled = true;
    private Map<String, Object> externalMobProviders = new HashMap<>();
    private Map<String, Object> resourcePacks = new HashMap<>();

    /* Performance */
    private int maxInstances = 10;

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
            tutorialDungeon = DungeonsXL.getInstance().getDungeons().getByName(tutorialDungeonName, true);
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
    public List<DColor> getGroupColorPriority() {
        return groupColorPriority;
    }

    /**
     * @param colors the colors to set
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

        if (config.contains("chatFormat.edit")) {
            chatFormatEdit = config.getString("chatFormat.edit");
        }

        if (config.contains("chatFormat.game")) {
            chatFormatGame = config.getString("chatFormat.game");
        }

        if (config.contains("chatFormat.group")) {
            chatFormatGroup = config.getString("chatFormat.group");
        }

        if (config.contains("chatFormat.spy")) {
            chatFormatSpy = config.getString("chatFormat.spy");
        }

        if (config.contains("chatEnabled")) {
            chatEnabled = config.getBoolean("chatEnabled");
        }

        if (config.contains("tutorial.activated")) {
            tutorialActivated = config.getBoolean("tutorial.activated");
        }

        if (config.contains("tutorial.dungeon")) {
            tutorialDungeonName = config.getString("tutorial.dungeon");
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

        if (config.contains("globalDeathMessagesDisabled")) {
            globalDeathMessagesDisabled = config.getBoolean("globalDeathMessagesDisabled");
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
