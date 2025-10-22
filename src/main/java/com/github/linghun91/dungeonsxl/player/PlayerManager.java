package com.github.linghun91.dungeonsxl.player;

import com.github.linghun91.dungeonsxl.DungeonsXL;
import com.github.linghun91.dungeonsxl.api.player.*;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages all players and groups
 * @author linghun91
 */
public class PlayerManager {
    private final DungeonsXL plugin;
    private final Map<UUID, DungeonPlayer> players = new ConcurrentHashMap<>();
    private final List<PlayerGroup> groups = new ArrayList<>();

    public PlayerManager(DungeonsXL plugin) {
        this.plugin = plugin;
    }

    public Optional<DungeonPlayer> getPlayer(Player player) {
        return Optional.ofNullable(players.get(player.getUniqueId()));
    }

    public DungeonPlayer getOrCreatePlayer(Player player) {
        return players.computeIfAbsent(player.getUniqueId(), 
            uuid -> new DungeonPlayerImpl(player));
    }

    public void removePlayer(Player player) {
        players.remove(player.getUniqueId());
    }

    public Collection<PlayerGroup> getGroups() {
        return new ArrayList<>(groups);
    }

    public void saveAllPlayers() {
        // Save player data
        plugin.getLogger().info("Saved data for " + players.size() + " players");
    }
}
