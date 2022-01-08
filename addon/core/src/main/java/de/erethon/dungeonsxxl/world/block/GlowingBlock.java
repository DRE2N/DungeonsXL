/*
 * Copyright (C) 2020-2022 Daniel Saukel
 *
 * All rights reserved.
 */
package de.erethon.dungeonsxxl.world.block;

import de.erethon.dungeonsxl.world.block.GameBlock;
import de.erethon.dungeonsxxl.DungeonsXXL;
import de.erethon.dungeonsxxl.util.GlowUtil;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Daniel Saukel
 */
public class GlowingBlock extends GameBlock {

    private GlowUtil glowUtil;
    
    public GlowingBlock(DungeonsXXL plugin, Block block, ChatColor color, Double time) {
        super(plugin.getDXL(), block);
        glowUtil = plugin.getGlowUtil();

        Long millis;
        if (time != null) {
            millis = (long) (time * 1000);
        } else {
            millis = null;
        }

        if (color != null) {
            glowUtil.addBlockGlow(block, color);
            if (millis != null) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        removeGlow();
                    }
                }.runTaskLater(plugin, millis / 50);
            }
        } else {
            glowUtil.addRainbowBlockGlow(block, millis);
        }
    }

    public void removeGlow() {
        glowUtil.removeBlockGlow(block);
    }

    @Override
    public boolean onBreak(BlockBreakEvent event) {
        return false;
    }

}
