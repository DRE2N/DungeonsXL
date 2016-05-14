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
package io.github.dre2n.dungeonsxl.announcer;

import io.github.dre2n.commons.compatibility.CompatibilityHandler;
import io.github.dre2n.commons.util.guiutil.GUIUtil;
import io.github.dre2n.commons.util.messageutil.MessageUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.DMessages;
import io.github.dre2n.dungeonsxl.dungeon.Dungeon;
import io.github.dre2n.dungeonsxl.event.dgroup.DGroupCreateEvent;
import io.github.dre2n.dungeonsxl.player.DGroup;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author Daniel Saukel
 */
public class Announcer {

    DungeonsXL plugin = DungeonsXL.getInstance();

    private String name;

    private List<String> description;
    private List<String> worlds;

    private String dungeonName;
    private String mapName;

    private short maxGroupsPerGame;
    private int maxPlayersPerGroup;

    private List<DGroup> dGroups;
    private List<ItemStack> buttons;

    /**
     * @param file
     * the script file
     */
    public Announcer(File file) {
        this(file.getName().substring(0, file.getName().length() - 4), YamlConfiguration.loadConfiguration(file));
    }

    /**
     * @param name
     * the name of the Announcer
     * @param config
     * the config that stores the information
     */
    public Announcer(String name, FileConfiguration config) {
        this.name = name;

        description = config.getStringList("description");
        worlds = config.getStringList("worlds");

        String identifier = config.getString("identifier");
        boolean multiFloor = config.getBoolean("multiFloor");
        if (multiFloor) {
            dungeonName = identifier;

            Dungeon dungeon = plugin.getDungeons().getDungeon(identifier);
            if (dungeon != null) {
                mapName = dungeon.getConfig().getStartFloor();
            }

        } else {
            mapName = identifier;
        }

        maxGroupsPerGame = (short) config.getInt("maxGroupsPerGame");
        dGroups = new ArrayList<>(Collections.nCopies(maxGroupsPerGame + 1, (DGroup) null));
        maxPlayersPerGroup = config.getInt("maxPlayersPerGroup");
    }

    /**
     * @param name
     * the name of the Announcer
     * @param description
     * the description messages
     * @param worlds
     * the names of the worlds where the announcement will be seen or null to broadcast it to all worlds
     * @param identifier
     * the dungeon identifier
     * @param multiFloor
     * if the identifier refers to an MFD (true) or an SFD (false)
     * @param maxGroupsPerGame
     * the amount of groups in one game
     * @param maxPlayersPerGame
     * the amount of players in one group
     */
    public Announcer(String name, List<String> description, List<String> worlds, String identifier, boolean multiFloor, short maxGroupsPerGame, int maxPlayersPerGroup) {
        this.name = name;
        this.description = description;
        this.worlds = worlds;

        if (multiFloor) {
            dungeonName = identifier;

            Dungeon dungeon = plugin.getDungeons().getDungeon(identifier);
            if (dungeon != null) {
                mapName = dungeon.getConfig().getStartFloor();
            }

        } else {
            mapName = identifier;
        }

        this.maxGroupsPerGame = maxGroupsPerGame;
        this.dGroups = new ArrayList<>(Collections.nCopies(maxGroupsPerGame + 1, (DGroup) null));
        this.maxPlayersPerGroup = maxPlayersPerGroup;
    }

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
     * @param description
     * the description to set
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
     * @param worlds
     * the worlds to set
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
     * @param dungeonName
     * the name of the dungeon to set
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
     * @param mapName
     * the name of the map to set
     */
    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    /**
     * @return the maximum amount of groups per game
     */
    public short getMaxGroupsPerGame() {
        return maxGroupsPerGame;
    }

    /**
     * @param amount
     * the amount to set
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
     * @param amount
     * the amount to set
     */
    public void setMaxPlayersPerGroup(int amount) {
        maxPlayersPerGroup = amount;
    }

    /**
     * Sends the announcement
     */
    public void send(Player player) {
        for (String message : description) {
            MessageUtil.broadcastCenteredMessage(message);
        }

        if (CompatibilityHandler.getInstance().isSpigot()) {
            ClickEvent onClick = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dungeonsxl join " + name);

            BaseComponent[] message = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', DMessages.ANNOUNCER_CLICK.getMessage()));
            for (BaseComponent slice : message) {
                slice.setClickEvent(onClick);
            }

            player.spigot().sendMessage(message);

        } else {
            MessageUtil.sendCenteredMessage(player, DMessages.ANNOUNCER_CMD.getMessage(getName().toUpperCase()));
        }
    }

    /**
     * Shows the group selection GUI
     */
    public void showGUI(Player player) {
        updateButtons();
        Inventory gui = GUIUtil.createGUI(plugin, ChatColor.DARK_RED + name, buttons);
        plugin.addGUI(gui);
        player.closeInventory();
        player.openInventory(gui);
    }

    /**
     * @param button
     * the clicked button
     */
    public void clickGroupButton(Player player, ItemStack button) {
        DGroup dGroup = getDGroupByButton(button);
        DGroup pGroup = DGroup.getByPlayer(player);

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
            plugin.getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                dGroups.set(buttons.indexOf(button), new DGroup(player));
            }

        } else if (dGroup == null && pGroup != null) {
            dGroups.set(buttons.indexOf(button), pGroup);

        } else if (pGroup != null && dGroups.contains(pGroup) && pGroup != dGroup) {
            dGroups.set(dGroups.indexOf(pGroup), null);
            dGroups.set(buttons.indexOf(button), pGroup);
        }

        showGUI(player);
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

            DGroup dGroup = dGroups.get(groupCount);
            if (!plugin.getDGroups().contains(dGroup)) {
                dGroups.set(groupCount, null);

            } else if (dGroup != null) {
                name = ChatColor.AQUA + dGroup.getName();
                playerCount = dGroup.getPlayers().size();
            }

            boolean full = playerCount >= maxPlayersPerGroup;

            ItemStack button = new ItemStack(Material.WOOL, playerCount, plugin.getMainConfig().getGroupColorPriority().get(groupCount));
            ItemMeta meta = button.getItemMeta();
            meta.setDisplayName(name + (full ? ChatColor.DARK_RED : ChatColor.GREEN) + " [" + playerCount + "/" + maxPlayersPerGroup + "]");
            button.setItemMeta(meta);
            buttons.add(button);

            groupCount++;
        } while (groupCount != maxGroupsPerGame);
    }

    /**
     * @param button
     * the button
     * @return the matching DGroup
     */
    public DGroup getDGroupByButton(ItemStack button) {
        int index = buttons.indexOf(button);
        return dGroups.get(index);
    }

}
