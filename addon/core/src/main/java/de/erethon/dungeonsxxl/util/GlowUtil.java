/*
 * Copyright (C) 2020-2026 Daniel Saukel
 *
 * All rights reserved.
 *
 * --- 1.21.11 PORT NOTE ---
 * The original class contained packet-level, per-player variants of the glow
 * effect that depended on net.minecraft.server.v1_16_R3.* (deleted in MC 1.17)
 * and CraftBukkit v1_16_R3 relocations (deleted in MC 1.20.5).
 *
 * Those methods (addBlockGlow(Block, ChatColor, Player...), per-player rainbow
 * variant, addGlow(NMS.Entity, ...), addRainbowGlow(NMS.Entity, ...),
 * removeGlow(NMS.Entity)) are REMOVED. On 1.21.11 there is no stable Bukkit
 * API for per-player fake entities; implementing them requires Mojang-mapped
 * NMS (unstable between minor versions) or ProtocolLib/PacketEvents.
 *
 * The world-visible variants are preserved and use only the public Bukkit
 * API. Callers that need per-player glow must re-implement via ProtocolLib.
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
 * @author Daniel Saukel
 */
public class GlowUtil implements Listener {

    private static final Random RANDOM = new Random();

    private Map<ChatColor, Team> teams = new HashMap<>();
    private GlowData<org.bukkit.entity.Entity> glowingBlocks = new GlowData<>();
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

    public org.bukkit.entity.Entity addBlockGlow(Block block, ChatColor color) {
        Shulker entity = block.getWorld().spawn(new Location(block.getWorld(), block.getX() + .5, block.getY(), block.getZ() + .5), Shulker.class);
        entity.setAI(false);
        entity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
        entity.setInvulnerable(true);
        addGlow(entity, color);
        glowingBlocks.put(block, entity);
        return entity;
    }

    public org.bukkit.entity.Entity addRainbowBlockGlow(Block block) {
        return addRainbowBlockGlow(block, (Long) null);
    }

    public org.bukkit.entity.Entity addRainbowBlockGlow(Block block, Long cancelTime) {
        Shulker entity = block.getWorld().spawn(new Location(block.getWorld(), block.getX() + .5, block.getY(), block.getZ() + .5), Shulker.class);
        entity.setAI(false);
        entity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
        entity.setInvulnerable(true);
        glowingBlocks.put(block, entity);
        addRainbowGlow(entity, cancelTime);
        return entity;
    }

    public void removeBlockGlow(Block block) {
        org.bukkit.entity.Entity bukkitEntity = glowingBlocks.get(block);
        if (bukkitEntity != null) {
            bukkitEntity.remove();
            glowingBlocks.remove(block);
            runnable.removeEntity(bukkitEntity);
        }
    }

    public void addGlow(org.bukkit.entity.Entity entity, ChatColor color) {
        getTeam(color).addEntry(asEntry(entity));
        entity.setGlowing(true);
    }

    public void addRainbowGlow(org.bukkit.entity.Entity entity) {
        addRainbowGlow(entity, null);
    }

    public void addRainbowGlow(org.bukkit.entity.Entity entity, Long cancelTime) {
        entity.setGlowing(true);
        runnable.addEntity(entity, cancelTime != null ? System.currentTimeMillis() + cancelTime : null);
    }

    public void removeGlow(org.bukkit.entity.Entity entity) {
        entity.setGlowing(false);
        teams.values().forEach(t -> t.removeEntry(asEntry(entity)));
        runnable.removeEntity(entity);
    }

    private static String asEntry(org.bukkit.entity.Entity entity) {
        return entity instanceof Player ? entity.getName() : entity.getUniqueId().toString();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        removeBlockGlow(event.getBlock());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // retained for API compatibility; per-player packet maps have been removed
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
                if (entry.getKey() instanceof org.bukkit.entity.Entity) {
                    run((org.bukkit.entity.Entity) entry.getKey(), entry.getValue());
                }
            }
        }

        private void run(org.bukkit.entity.Entity entity, Long cancelTime) {
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
