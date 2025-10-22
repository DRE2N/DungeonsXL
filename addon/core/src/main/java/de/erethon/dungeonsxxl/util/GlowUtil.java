/*
 * Copyright (C) 2020-2023 Daniel Saukel
 *
 * All rights reserved.
 */
package de.erethon.dungeonsxxl.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Shulker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

/**
 * Modern Paper API implementation of glow effects without NMS.
 * Updated for Paper 1.21.8
 *
 * @author Daniel Saukel
 */
public class GlowUtil implements Listener {

    private static final Random RANDOM = new Random();

    private Map<ChatColor, Team> teams = new HashMap<>();
    private GlowData<Entity> glowingBlocks = new GlowData<>();
    private Map<Player, GlowData<Entity>> playerGlows = new HashMap<>();
    private GlowRunnable runnable = new GlowRunnable();

    public GlowUtil(Plugin plugin) {
        runnable.runTaskTimer(plugin, 0L, 2L);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private Team getTeam(ChatColor color) {
        if (!teams.containsKey(color)) {
            Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam("DXL_" + color.getChar());
            if (team == null) {
                team = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam("DXL_" + color.getChar());
                team.setColor(color);
            }
            teams.put(color, team);
        }
        return teams.get(color);
    }

    /**
     * Adds a colored glow effect to the block that is visible to all players.
     *
     * @param block the block
     * @param color the glow color
     * @return the spawned entity that provides the glow effect
     */
    public Entity addBlockGlow(Block block, ChatColor color) {
        Shulker entity = block.getWorld().spawn(new Location(block.getWorld(), block.getX() + .5, block.getY(), block.getZ() + .5), Shulker.class);
        entity.setAI(false);
        entity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
        entity.setInvulnerable(true);
        entity.setSilent(true);
        entity.setGravity(false);
        addGlow(entity, color);
        glowingBlocks.put(block, entity);
        return entity;
    }

    /**
     * Adds a colored glow effect to the block that is only visible to certain players.
     * Note: In Paper API 1.21.8, per-player entity visibility can be achieved through
     * the entity tracking API or by spawning entities only visible to specific players.
     * For simplicity, this implementation creates a shared entity that all specified players can see.
     *
     * @param block   the block
     * @param color   the glow color
     * @param players the players who can see the effect
     */
    public void addBlockGlow(Block block, ChatColor color, Player... players) {
        // In modern Paper API, we create a regular entity but can control visibility
        // through Paper's entity visibility API if needed
        Shulker entity = block.getWorld().spawn(new Location(block.getWorld(), block.getX() + .5, block.getY(), block.getZ() + .5), Shulker.class);
        entity.setAI(false);
        entity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
        entity.setInvulnerable(true);
        entity.setSilent(true);
        entity.setGravity(false);
        addGlow(entity, color);

        // Store per-player glow data
        for (Player player : players) {
            if (playerGlows.get(player) == null) {
                playerGlows.put(player, new GlowData<>());
            }
            playerGlows.get(player).put(block, entity);
        }
    }

    /**
     * Adds a rainbow colored glow effect to the block that is visible to all players.
     *
     * @param block the block
     * @return the spawned entity that provides the glow effect
     */
    public Entity addRainbowBlockGlow(Block block) {
        return addRainbowBlockGlow(block, (Long) null);
    }

    /**
     * Adds a rainbow colored glow effect to the block that is visible to all players.
     * <p>
     * The task is cancelled automatically when the entity dies.
     *
     * @param block      the block
     * @param cancelTime the time in milliseconds until the glow effect shall end; null = forever
     * @return the spawned entity that provides the glow effect
     */
    public Entity addRainbowBlockGlow(Block block, Long cancelTime) {
        Shulker entity = block.getWorld().spawn(new Location(block.getWorld(), block.getX() + .5, block.getY(), block.getZ() + .5), Shulker.class);
        entity.setAI(false);
        entity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
        entity.setInvulnerable(true);
        entity.setSilent(true);
        entity.setGravity(false);
        glowingBlocks.put(block, entity);
        addRainbowGlow(entity, cancelTime);
        return entity;
    }

    /**
     * Adds a rainbow colored glow effect to the block that is only visible to certain players.
     *
     * @param block   the block
     * @param players the players who can see the effect
     */
    public void addRainbowBlockGlow(Block block, Player... players) {
        addRainbowBlockGlow(block, null, players);
    }

    /**
     * Adds a rainbow colored glow effect to the block that is only visible to certain players.
     *
     * @param block      the block
     * @param cancelTime the time in milliseconds until the glow effect shall end; null = forever
     * @param players    the players who can see the effect
     */
    public void addRainbowBlockGlow(Block block, Long cancelTime, Player... players) {
        Shulker entity = block.getWorld().spawn(new Location(block.getWorld(), block.getX() + .5, block.getY(), block.getZ() + .5), Shulker.class);
        entity.setAI(false);
        entity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
        entity.setInvulnerable(true);
        entity.setSilent(true);
        entity.setGravity(false);

        for (Player player : players) {
            if (playerGlows.get(player) == null) {
                playerGlows.put(player, new GlowData<>());
            }
            playerGlows.get(player).put(block, entity);
        }
        addRainbowGlow(entity, cancelTime);
    }

    /**
     * Removes the glow effect from a glowing block.
     *
     * @param block the block
     */
    public void removeBlockGlow(Block block) {
        Entity bukkitEntity = glowingBlocks.get(block);
        if (bukkitEntity != null) {
            bukkitEntity.remove();
            glowingBlocks.remove(block);
            runnable.removeEntity(bukkitEntity);
        }

        for (Entry<Player, GlowData<Entity>> entry : playerGlows.entrySet()) {
            Entity entity = entry.getValue().get(block);
            if (entity != null) {
                entity.remove();
                runnable.removeEntity(entity);
            }
        }
    }

    /**
     * Adds a colored glow effect to an entity and handles its scoreboard team membership.
     *
     * @param entity a Bukkit Entity
     * @param color  the glow color
     */
    public void addGlow(Entity entity, ChatColor color) {
        getTeam(color).addEntry(asEntry(entity));
        entity.setGlowing(true);
    }

    /**
     * Adds a changing glow effect to an entity.
     *
     * @param entity an Entity
     */
    public void addRainbowGlow(Entity entity) {
        addRainbowGlow(entity, null);
    }

    /**
     * Adds a changing glow effect to an entity.
     *
     * @param entity     an Entity
     * @param cancelTime the time in milliseconds until the glow effect shall end; null = forever
     */
    public void addRainbowGlow(Entity entity, Long cancelTime) {
        entity.setGlowing(true);
        runnable.addEntity(entity, cancelTime != null ? System.currentTimeMillis() + cancelTime : null);
    }

    /**
     * Removes the glow effect from an entity and handles its scoreboard team membership.
     *
     * @param entity a Bukkit Entity
     */
    public void removeGlow(Entity entity) {
        entity.setGlowing(false);
        teams.values().forEach(t -> t.removeEntry(asEntry(entity)));
        runnable.removeEntity(entity);
    }

    private static String asEntry(Entity entity) {
        return entity instanceof Player ? entity.getName() : entity.getUniqueId().toString();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        removeBlockGlow(event.getBlock());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerGlows.remove(event.getPlayer());
    }

    private class GlowRunnable extends BukkitRunnable {

        private Map<Object, Long> entities = new HashMap<>();
        private ChatColor color;

        private void addEntity(Object entity, Long cancelTime) {
            entities.put(entity, cancelTime);
        }

        private void removeEntity(Object entity) {
            entities.remove(entity);
        }

        @Override
        public void run() {
            color = ChatColor.values()[RANDOM.nextInt(ChatColor.values().length - 1)];
            for (Entry<Object, Long> entry : entities.entrySet().toArray(new Entry[entities.size()])) {
                if (entry.getKey() instanceof Entity) {
                    run((Entity) entry.getKey(), entry.getValue());
                }
            }
        }

        private void run(Entity entity, Long cancelTime) {
            getTeam(color).removeEntry(asEntry(entity));
            if ((cancelTime != null && System.currentTimeMillis() >= cancelTime) || entity.isDead()) {
                entities.remove(entity);
                glowingBlocks.remove(entity);
                if (!entity.isDead()) {
                    entity.setGlowing(false);
                } else {
                    entity.remove();
                }
                return;
            }
            getTeam(color).addEntry(asEntry(entity));
        }
    }

    static class GlowData<T> {

        Map<Block, T> glowingBlocks = new HashMap<>();

        T get(Block block) {
            return glowingBlocks.get(block);
        }

        void remove(Block block) {
            glowingBlocks.remove(block);
        }

        void remove(T entity) {
            for (Entry<Block, T> entry : glowingBlocks.entrySet().toArray(new Entry[glowingBlocks.size()])) {
                if (entry.getValue().equals(entity)) {
                    glowingBlocks.remove(entry.getKey());
                }
            }
        }

        void put(Block block, T entity) {
            glowingBlocks.put(block, entity);
        }
    }
}
