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
package de.erethon.dungeonsxl.world;

import de.erethon.caliburn.item.ExItem;
import de.erethon.commons.misc.EnumUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.game.GameType;
import de.erethon.dungeonsxl.game.rule.GameRuleDefault;
import de.erethon.dungeonsxl.game.rule.YamlGameRuleProvider;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import org.bukkit.World.Environment;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * The world configuration is a simple game rule source. Besides game rules, WorldConfig also stores some map specific data such as the invited players. It is
 * used directly in dungeon map config.yml files, but also part of dungeon and main config files.
 *
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class WorldConfig extends YamlGameRuleProvider {

    private File file;

    private List<String> invitedPlayers = new ArrayList<>();
    private GameType forcedGameType;
    private Environment worldEnvironment;

    public WorldConfig(DungeonsXL plugin) {
        super(plugin);
    }

    public WorldConfig(DungeonsXL plugin, File file) {
        super(plugin);

        this.file = file;
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        load(config);
    }

    public WorldConfig(DungeonsXL plugin, ConfigurationSection configFile) {
        super(plugin, configFile);
    }

    @Override
    public void load(ConfigurationSection configFile) {
        if (configFile.contains("invitedPlayers")) {
            invitedPlayers = configFile.getStringList("invitedPlayers");
        }

        if (configFile.contains("worldEnvironment")) {
            Environment env = EnumUtil.getEnum(Environment.class, configFile.getString("worldEnvironment"));
            worldEnvironment = env != null ? env : Environment.NORMAL;
        }

        if (configFile.contains("forcedGameType")) {
            forcedGameType = plugin.getGameTypeCache().getByName(configFile.getString("forcedGameType"));
        }
    }

    public void save() {
        if (file == null) {
            return;
        }
        FileConfiguration configFile = YamlConfiguration.loadConfiguration(file);

        // Messages
        if (msgs != null) {
            for (int msgs : this.msgs.keySet()) {
                configFile.set("message." + msgs, this.msgs.get(msgs));
            }
        }

        List<String> secureObjectIds = new ArrayList<>();
        for (ExItem item : (Set<ExItem>) getState(GameRuleDefault.SECURE_OBJECTS)) {
            secureObjectIds.add(item.getId());
        }

        configFile.set("secureObjects", (Set<ExItem>) getState(GameRuleDefault.SECURE_OBJECTS));
        configFile.set("invitedPlayers", invitedPlayers);
        if (worldEnvironment != null) {
            configFile.set("worldEnvironment", worldEnvironment.name());
        }

        try {
            configFile.save(file);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * @return the UUIDs or names of the players invited to edit the map
     */
    public CopyOnWriteArrayList<String> getInvitedPlayers() {
        CopyOnWriteArrayList<String> tmpInvitedPlayers = new CopyOnWriteArrayList<>();
        tmpInvitedPlayers.addAll(invitedPlayers);
        return tmpInvitedPlayers;
    }

    /**
     * @param uuid the player's unique ID
     */
    public void addInvitedPlayer(String uuid) {
        if (!invitedPlayers.contains(uuid)) {
            invitedPlayers.add(uuid);
        }
    }

    /**
     * @param uuid the player's unique ID
     * @param name the player's name
     */
    public void removeInvitedPlayers(String uuid, String name) {
        invitedPlayers.remove(uuid);
        // remove player from a 0.9.1 and lower file
        invitedPlayers.remove(name);
    }

    /**
     * @return the forcedGameType
     */
    public GameType getForcedGameType() {
        return forcedGameType;
    }

    /**
     * @param forcedGameType the forcedGameType to set
     */
    public void setForcedGameType(GameType forcedGameType) {
        this.forcedGameType = forcedGameType;
    }

    /**
     * @return the world environment
     */
    public Environment getWorldEnvironment() {
        return worldEnvironment;
    }

    /**
     * @param worldEnvironment the world environment to set
     */
    public void setWorldEnvironment(Environment worldEnvironment) {
        this.worldEnvironment = worldEnvironment;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{file=" + file.getPath() + "}";
    }

}
