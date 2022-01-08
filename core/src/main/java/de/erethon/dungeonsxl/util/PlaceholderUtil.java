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
package de.erethon.dungeonsxl.util;

import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.api.dungeon.Game;
import de.erethon.dungeonsxl.api.player.PlayerGroup;
import de.erethon.dungeonsxl.api.world.GameWorld;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

/**
 * @author Daniel Saukel
 */
public class PlaceholderUtil extends PlaceholderExpansion {

    private DungeonsXL plugin;

    private String identifier;

    public PlaceholderUtil(DungeonsXL plugin, String identifier) {
        this.plugin = plugin;
        this.identifier = identifier;
    }

    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getRequiredPlugin() {
        return plugin.getName();
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }
    
    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        if (player == null) {
            return "";
        }
        PlayerGroup group = plugin.getPlayerGroup(player.getPlayer());

        switch (identifier) {
            case "group_members":
                return group != null ? group.getMembers().getNames().toString().substring(1, group.getMembers().getNames().toString().length() - 1) : "";
            case "group_name":
                return group != null ? group.getName() : "";
            case "group_name_raw":
                return group != null ? group.getRawName() : "";
            case "group_player_count":
                return group != null ? String.valueOf(group.getMembers().size()) : "";
            case "game_player_count":
                Game game = group.getGame();
                return game != null ? String.valueOf(game.getPlayers().size()) : "";
            case "floor_player_count":
                GameWorld gameWorld = group.getGameWorld();
                return gameWorld != null ? String.valueOf(gameWorld.getPlayers().size()) : "";
            case "dungeon_name":
                return group != null ? group.getDungeon().getName() : "";
            case "global_dungeon_count":
                return String.valueOf(plugin.getDungeonRegistry().size());
            case "global_floor_count":
                return String.valueOf(plugin.getMapRegistry().size());
            case "global_instance_count":
                return String.valueOf(plugin.getInstanceCache().size());
            default:
                return null;
        }
    }

}
