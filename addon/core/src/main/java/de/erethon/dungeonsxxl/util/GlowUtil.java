/*
 * Copyright (C) 2020-2022 Daniel Saukel
 *
 * All rights reserved.
 */
package de.erethon.dungeonsxxl.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import net.minecraft.server.v1_16_R3.EntityShulker;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.Packet;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_16_R3.PacketPlayOutSpawnEntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
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
    private Map<Player, GlowData<net.minecraft.server.v1_16_R3.Entity>> playerGlows = new HashMap<>();
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
    public org.bukkit.entity.Entity addBlockGlow(Block block, ChatColor color) {
        Shulker entity = block.getWorld().spawn(new Location(block.getWorld(), block.getX() + .5, block.getY(), block.getZ() + .5), Shulker.class);
        entity.setAI(false);
        entity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
        entity.setInvulnerable(true);
        addGlow(entity, color);
        glowingBlocks.put(block, entity);
        return entity;
    }

    /**
     * Adds a packet-level colored glow effect to the block that is only visible to certain players.
     *
     * @param block   the block
     * @param color   the glow color
     * @param players the players who can see the effect
     */
    public void addBlockGlow(Block block, ChatColor color, Player... players) {
        EntityShulker entity = new EntityShulker(EntityTypes.SHULKER, ((CraftWorld) block.getWorld()).getHandle());
        entity.setLocation(block.getX() + .5, block.getY(), block.getZ() + .5, 0, 0);
        entity.setFlag(6, true);
        entity.setInvisible(true);
        for (Player player : players) {
            sendPacket(player, new PacketPlayOutSpawnEntityLiving(entity));
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
    public org.bukkit.entity.Entity addRainbowBlockGlow(Block block) {
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
    public org.bukkit.entity.Entity addRainbowBlockGlow(Block block, Long cancelTime) {
        Shulker entity = block.getWorld().spawn(new Location(block.getWorld(), block.getX() + .5, block.getY(), block.getZ() + .5), Shulker.class);
        entity.setAI(false);
        entity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
        entity.setInvulnerable(true);
        glowingBlocks.put(block, entity);
        addRainbowGlow(entity, cancelTime);
        return entity;
    }

    /**
     * Adds a packet-level rainbow colored glow effect to the block that is only visible to certain players.
     * <p>
     * Returns the repeating task that handles color changes.
     *
     * @param block   the block
     * @param players
     */
    public void addRainbowBlockGlow(Block block, Player... players) {
        addRainbowBlockGlow(block, null, players);
    }

    /**
     * Adds a packet-level rainbow colored glow effect to the block that is only visible to certain players.
     * <p>
     * Returns the repeating task that handles color changes.
     *
     * @param block      the block
     * @param cancelTime the time in milliseconds until the glow effect shall end; null = forever
     * @param players
     */
    public void addRainbowBlockGlow(Block block, Long cancelTime, Player... players) {
        EntityShulker entity = new EntityShulker(EntityTypes.SHULKER, ((CraftWorld) block.getWorld()).getHandle());
        entity.setLocation(block.getX() + .5, block.getY(), block.getZ() + .5, 0, 0);
        entity.setFlag(6, true);
        entity.setInvisible(true);
        for (Player player : players) {
            sendPacket(player, new PacketPlayOutSpawnEntityLiving(entity));
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
        org.bukkit.entity.Entity bukkitEntity = glowingBlocks.get(block);
        if (bukkitEntity != null) {
            bukkitEntity.remove();
            glowingBlocks.remove(block);
            runnable.removeEntity(bukkitEntity);
        }

        for (Entry<Player, GlowData<net.minecraft.server.v1_16_R3.Entity>> entry : playerGlows.entrySet()) {
            net.minecraft.server.v1_16_R3.Entity nmsEntity = entry.getValue().get(block);
            if (nmsEntity != null) {
                sendPacket(entry.getKey(), new PacketPlayOutEntityDestroy(nmsEntity.getId()));
                runnable.removeEntity(nmsEntity);
            }
        }
    }

    /**
     * Adds a colored glow effect to an entity and handles its scoreboard team membership.
     *
     * @param entity a Bukkit Entity
     * @param color  the glow color
     */
    public void addGlow(org.bukkit.entity.Entity entity, ChatColor color) {
        getTeam(color).addEntry(asEntry(entity));
        entity.setGlowing(true);
    }

    /**
     * Adds a colored glow effect to an entity and handles its scoreboard team membership.
     *
     * @param entity an NMS Entity
     * @param color  the glow color
     */
    public void addGlow(net.minecraft.server.v1_16_R3.Entity entity, ChatColor color) {
        getTeam(color).addEntry(asEntry(entity));
        entity.setFlag(6, true);
    }

    /**
     * Adds a changing glow effect to an entity.
     *
     * @param entity an NMS Entity
     */
    public void addRainbowGlow(org.bukkit.entity.Entity entity) {
        addRainbowGlow(entity, null);
    }

    /**
     * Adds a changing glow effect to an entity.
     *
     * @param entity     an NMS Entity
     * @param cancelTime the time in milliseconds until the glow effect shall end; null = forever
     */
    public void addRainbowGlow(org.bukkit.entity.Entity entity, Long cancelTime) {
        entity.setGlowing(true);
        runnable.addEntity(entity, cancelTime != null ? System.currentTimeMillis() + cancelTime : null);
    }

    /**
     * Adds a changing glow effect to an entity.
     *
     * @param entity an NMS Entity
     */
    public void addRainbowGlow(net.minecraft.server.v1_16_R3.Entity entity) {
        addRainbowGlow(entity, null);
    }

    /**
     * Adds a changing glow effect to an entity.
     *
     * @param entity     an NMS Entity
     * @param cancelTime the time in milliseconds until the glow effect shall end; null = forever
     */
    public void addRainbowGlow(net.minecraft.server.v1_16_R3.Entity entity, Long cancelTime) {
        entity.setFlag(6, true);
        runnable.addEntity(entity, cancelTime != null ? System.currentTimeMillis() + cancelTime : null);
    }

    /**
     * Removes the glow effect from an entity and handles its scoreboard team membership.
     *
     * @param entity a Bukkit Entity
     */
    public void removeGlow(org.bukkit.entity.Entity entity) {
        entity.setGlowing(false);
        teams.values().forEach(t -> t.removeEntry(asEntry(entity)));
        runnable.removeEntity(entity);
    }

    /**
     * Removes the glow effect from an entity and handles its scoreboard team membership.
     *
     * @param entity an NMS Entity
     */
    public void removeGlow(net.minecraft.server.v1_16_R3.Entity entity) {
        entity.setFlag(6, false);
        teams.values().forEach(t -> t.removeEntry(asEntry(entity)));
        runnable.removeEntity(entity);
    }

    private static String asEntry(org.bukkit.entity.Entity entity) {
        return entity instanceof Player ? entity.getName() : entity.getUniqueId().toString();
    }

    private static String asEntry(net.minecraft.server.v1_16_R3.Entity entity) {
        return entity instanceof Player ? entity.getName() : entity.getUniqueID().toString();
    }

    private static void sendPacket(Player player, Packet<?> packet) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
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
                if (entry.getKey() instanceof org.bukkit.entity.Entity) {
                    run((org.bukkit.entity.Entity) entry.getKey(), entry.getValue());
                } else if (entry.getKey() instanceof net.minecraft.server.v1_16_R3.Entity) {
                    run((net.minecraft.server.v1_16_R3.Entity) entry.getKey(), entry.getValue());
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

        private void run(net.minecraft.server.v1_16_R3.Entity entity, Long cancelTime) {
            getTeam(color).removeEntry(asEntry(entity));
            if (cancelTime != null && System.currentTimeMillis() >= cancelTime) {
                entities.remove(entity);
                for (Entry<Player, GlowData<net.minecraft.server.v1_16_R3.Entity>> entry : playerGlows.entrySet()) {
                    if (!entry.getValue().glowingBlocks.containsValue(entity)) {
                        continue;
                    }
                    Player player = entry.getKey();
                    sendPacket(player, new PacketPlayOutEntityDestroy(entity.getId()));
                    entry.getValue().remove(entity);
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
