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
package de.erethon.dungeonsxl.announcer;

import de.erethon.commons.chat.DefaultFontInfo;
import de.erethon.commons.chat.MessageUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.api.player.PlayerGroup.Color;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.dungeon.Dungeon;
import de.erethon.dungeonsxl.event.dgroup.DGroupCreateEvent;
import de.erethon.dungeonsxl.player.DGroup;
import de.erethon.dungeonsxl.util.GUIUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Represents a game announcement.
 *
 * @author Daniel Saukel
 */
public class Announcer {

    private DungeonsXL plugin;

    private String name;

    private List<String> description;
    private List<String> worlds;

    private String dungeonName;
    private String mapName;

    private int minGroupsPerGame;
    private int minPlayersPerGroup;

    private short maxGroupsPerGame;
    private int maxPlayersPerGroup;

    private List<DGroup> dGroups;
    private List<ItemStack> buttons;

    private AnnouncerStartGameTask startTask;

    /**
     * @param plugin the plugin instance
     * @param file   the script file
     */
    public Announcer(DungeonsXL plugin, File file) {
        this(plugin, file.getName().substring(0, file.getName().length() - 4), YamlConfiguration.loadConfiguration(file));
    }

    /**
     * @param plugin the plugin instance
     * @param name   the name of the Announcer
     * @param config the config that stores the information
     */
    public Announcer(DungeonsXL plugin, String name, FileConfiguration config) {
        this.plugin = plugin;

        this.name = name;

        description = config.getStringList("description");
        worlds = config.getStringList("worlds");

        String identifier = config.getString("identifier");
        boolean multiFloor = config.getBoolean("multiFloor");
        if (multiFloor) {
            dungeonName = identifier;

            Dungeon dungeon = plugin.getDungeonCache().getByName(identifier);
            if (dungeon != null) {
                mapName = dungeon.getConfig().getStartFloor().getName();
            }

        } else {
            mapName = identifier;
        }

        minGroupsPerGame = config.getInt("minGroupsPerGame");
        minPlayersPerGroup = config.getInt("minPlayersPerGroup");

        maxGroupsPerGame = (short) config.getInt("maxGroupsPerGame");
        dGroups = new ArrayList<>(Collections.nCopies(maxGroupsPerGame + 1, (DGroup) null));
        maxPlayersPerGroup = config.getInt("maxPlayersPerGroup");
    }

    /**
     * @param name               the name of the Announcer
     * @param description        the description messages
     * @param worlds             the names of the worlds where the announcement will be seen or null to broadcast it to all worlds
     * @param identifier         the dungeon identifier
     * @param multiFloor         if the identifier refers to an MFD (true) or an SFD (false)
     * @param maxGroupsPerGame   the amount of groups in one game
     * @param maxPlayersPerGroup the amount of players in one group
     */
    public Announcer(String name, List<String> description, List<String> worlds, String identifier, boolean multiFloor, short maxGroupsPerGame, int maxPlayersPerGroup) {
        this.name = name;
        this.description = description;
        this.worlds = worlds;

        if (multiFloor) {
            dungeonName = identifier;

            Dungeon dungeon = plugin.getDungeonCache().getByName(identifier);
            if (dungeon != null) {
                mapName = dungeon.getConfig().getStartFloor().getName();
            }

        } else {
            mapName = identifier;
        }

        this.maxGroupsPerGame = maxGroupsPerGame;
        this.dGroups = new ArrayList<>(Collections.nCopies(maxGroupsPerGame + 1, (DGroup) null));
        this.maxPlayersPerGroup = maxPlayersPerGroup;
    }

    /* Getters and setters */
    /**
     * @return the name of the announcer
     */
    public String getName() {
        return name;
    }

    /**
     * @return the description messages
     */
    public List<String> getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(List<String> description) {
        this.description = description;
    }

    /**
     * @return the names of the worlds where the announcement will be seen or null to broadcast it to all worlds
     */
    public List<String> getWorlds() {
        return worlds;
    }

    /**
     * @param worlds the worlds to set
     */
    public void setWorlds(List<String> worlds) {
        this.worlds = worlds;
    }

    /**
     * @return the name of the dungeon
     */
    public String getDungeonName() {
        return dungeonName;
    }

    /**
     * @param dungeonName the name of the dungeon to set
     */
    public void setDungeonName(String dungeonName) {
        this.dungeonName = dungeonName;
    }

    /**
     * @return the name of the first or only floor
     */
    public String getMapName() {
        return mapName;
    }

    /**
     * @param mapName the name of the map to set
     */
    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    /**
     * @return the minimum amount of filled groups per game
     */
    public int getMinGroupsPerGame() {
        return minGroupsPerGame;
    }

    /**
     * @param amount the amount to set
     */
    public void setMinGroupsPerGame(int amount) {
        minGroupsPerGame = amount;
    }

    /**
     * @return the minimum amount of filled groups per game
     */
    public int getMinPlayersPerGroup() {
        return minPlayersPerGroup;
    }

    /**
     * @param amount the amount to set
     */
    public void setMinPlayersPerGroup(int amount) {
        minPlayersPerGroup = amount;
    }

    /**
     * @return the maximum amount of groups per game
     */
    public short getMaxGroupsPerGame() {
        return maxGroupsPerGame;
    }

    /**
     * @param amount the amount to set
     */
    public void setMaxGroupsPerGame(short amount) {
        maxGroupsPerGame = amount;
    }

    /**
     * @return the maximum amount of players per group
     */
    public int getMaxPlayersPerGroup() {
        return maxPlayersPerGroup;
    }

    /**
     * @return the DGroups
     */
    public List<DGroup> getDGroups() {
        return dGroups;
    }

    /**
     * @return the buttons that represent the DGroups
     */
    public List<ItemStack> getButtons() {
        return buttons;
    }

    /**
     * @param amount the amount to set
     */
    public void setMaxPlayersPerGroup(int amount) {
        maxPlayersPerGroup = amount;
    }

    /**
     * @return the start task
     */
    public AnnouncerStartGameTask getStartTask() {
        return startTask;
    }

    /**
     * @return whether enough players and groups joined the announced game to start
     */
    public boolean areRequirementsFulfilled() {
        int i = 0;
        for (DGroup group : dGroups) {
            if (group != null && group.getPlayers().size() >= minPlayersPerGroup) {
                i++;
            }
        }
        return i >= minGroupsPerGame;
    }

    /* Actions */
    /**
     * Cancels the start task and sets it to null.
     */
    public void endStartTask() {
        startTask.cancel();
        startTask = null;
    }

    /**
     * Sends the announcement
     *
     * @param player the player
     */
    public void send(Player player) {
        for (String message : description) {
            MessageUtil.sendCenteredMessage(player, message);
        }

        ClickEvent onClick = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dungeonsxl join " + name);

        BaseComponent[] message = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', DMessage.ANNOUNCER_CLICK.getMessage()));
        for (BaseComponent slice : message) {
            slice.setClickEvent(onClick);
        }

        TextComponent center = new TextComponent(DefaultFontInfo.center(BaseComponent.toPlainText(message)).replaceAll(BaseComponent.toPlainText(message), ""));

        ArrayList<BaseComponent> toSend = new ArrayList<>(Arrays.asList(message));
        toSend.add(0, center);
        MessageUtil.sendMessage(player, toSend.toArray(new BaseComponent[]{}));
    }

    /**
     * Shows the group selection GUI
     *
     * @param player the player
     */
    public void showGUI(Player player) {
        updateButtons();
        Inventory gui = GUIUtil.createGUI(plugin, ChatColor.DARK_RED + name, buttons);
        plugin.getGUIs().add(gui);
        player.openInventory(gui);
    }

    /**
     * @param player the player
     * @param button the clicked button
     */
    public void clickGroupButton(Player player, ItemStack button) {
        DGroup dGroup = getDGroupByButton(button);
        DGroup pGroup = DGroup.getByPlayer(player);
        Color color = Color.getByWoolType(plugin.getCaliburn().getExItem(button));

        for (DGroup group : dGroups) {
            if (dGroups.contains(pGroup) && pGroup != null && pGroup.isCustom() && pGroup.getCaptain() == player) {
                dGroups.set(dGroups.indexOf(pGroup), null);
            }

            if (group != null && group.getPlayers().contains(player)) {
                group.removePlayer(player);
            }
        }

        if (dGroup != null && pGroup == null) {
            if (dGroup.getPlayers().size() < maxPlayersPerGroup) {
                dGroup.addPlayer(player);
            }

        } else if (dGroup == null && pGroup == null) {
            DGroupCreateEvent event = new DGroupCreateEvent(dGroup, player, DGroupCreateEvent.Cause.ANNOUNCER);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                dGroups.set(buttons.indexOf(button), new DGroup(plugin, player, color));
            }

        } else if (dGroup == null && pGroup != null) {
            pGroup.setName(color);
            dGroups.set(buttons.indexOf(button), pGroup);

        } else if (pGroup != null && dGroups.contains(pGroup) && pGroup != dGroup) {
            dGroups.set(dGroups.indexOf(pGroup), null);
            pGroup.setName(color);
            dGroups.set(buttons.indexOf(button), pGroup);
        }

        showGUI(player);

        if (areRequirementsFulfilled()) {
            if (startTask == null) {
                startTask = new AnnouncerStartGameTask(plugin, this);
                startTask.runTaskLater(plugin, 20 * 30L);
            } else {
                startTask.getProgressBar().addPlayer(player);
            }
        }
    }

    /**
     * Updates the buttons to group changes.
     */
    public void updateButtons() {
        int groupCount = 0;

        buttons = new ArrayList<>(dGroups.size());
        do {
            String name = ChatColor.DARK_GRAY + "EMPTY GROUP";
            int playerCount = 0;
            List<String> lore = new ArrayList<>();

            DGroup dGroup = dGroups.get(groupCount);
            if (!plugin.getDGroupCache().contains(dGroup)) {
                dGroups.set(groupCount, null);

            } else if (dGroup != null) {
                name = ChatColor.AQUA + dGroup.getName();
                playerCount = dGroup.getPlayers().size();
                for (Player player : dGroup.getPlayers().getOnlinePlayers()) {
                    lore.add((dGroup.getCaptain().equals(player) ? ChatColor.GOLD : ChatColor.GRAY) + player.getName());
                }
            }

            boolean full = playerCount >= maxPlayersPerGroup;

            Color color = plugin.getMainConfig().getGroupColorPriority(groupCount);
            ItemStack button = color.getWoolMaterial().toItemStack();
            ItemMeta meta = button.getItemMeta();
            meta.setDisplayName(name + (full ? ChatColor.DARK_RED : ChatColor.GREEN) + " [" + playerCount + "/" + maxPlayersPerGroup + "]");
            meta.setLore(lore);
            button.setItemMeta(meta);
            buttons.add(button);

            groupCount++;
        } while (groupCount != maxGroupsPerGame);
    }

    /**
     * @param button the button
     * @return the matching DGroup
     */
    public DGroup getDGroupByButton(ItemStack button) {
        int index = buttons.indexOf(button);
        if (dGroups.size() <= index || index < 0) {
            return null;
        } else {
            return dGroups.get(index);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{name=" + name + /*"; dungeon=" + dungeon + */ "}";
    }

}
