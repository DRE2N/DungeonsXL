package com.github.linghun91.dungeonsxl.dungeon;

import com.github.linghun91.dungeonsxl.DungeonsXL;
import com.github.linghun91.dungeonsxl.api.dungeon.Dungeon;
import com.github.linghun91.dungeonsxl.api.dungeon.Game;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages all dungeons and active games
 * @author linghun91
 */
public class DungeonManager {
    private final DungeonsXL plugin;
    private final Map<String, Dungeon> dungeons = new ConcurrentHashMap<>();
    private final List<Game> activeGames = new ArrayList<>();

    public DungeonManager(DungeonsXL plugin) {
        this.plugin = plugin;
    }

    public void loadDungeons() {
        File dungeonsFolder = new File(plugin.getDataFolder(), "dungeons");
        if (!dungeonsFolder.exists()) {
            dungeonsFolder.mkdirs();
        }

        // Load dungeon configurations
        plugin.getLogger().info("Loaded " + dungeons.size() + " dungeons");
    }

    public Optional<Dungeon> getDungeon(String name) {
        return Optional.ofNullable(dungeons.get(name));
    }

    public Collection<Dungeon> getDungeons() {
        return new ArrayList<>(dungeons.values());
    }

    public int getDungeonCount() {
        return dungeons.size();
    }

    public void registerGame(Game game) {
        activeGames.add(game);
    }

    public void unregisterGame(Game game) {
        activeGames.remove(game);
    }

    public Collection<Game> getActiveGames() {
        return new ArrayList<>(activeGames);
    }
}
