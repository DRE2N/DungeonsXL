/*
 * Copyright (C) 2020-2022 Daniel Saukel
 *
 * All rights reserved.
 */
package de.erethon.dungeonsxxl.sign;

import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.sign.Rocker;
import de.erethon.dungeonsxl.api.world.InstanceWorld;
import de.erethon.dungeonsxl.player.DPermission;
import de.erethon.bedrock.misc.BlockUtil;
import de.erethon.bedrock.misc.EnumUtil;
import de.erethon.dungeonsxl.world.DGameWorld;
import de.erethon.dungeonsxxl.DungeonsXXL;
import de.erethon.dungeonsxxl.world.block.GlowingBlock;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;

/**
 * Turns the attached block into a glowing block.
 *
 * @author Daniel Saukel
 */
public class GlowingBlockSign extends Rocker {

    private ChatColor color = ChatColor.DARK_RED;
    private Double time;

    private GlowingBlock glowingBlock;

    public GlowingBlockSign(DungeonsAPI api, Sign sign, String[] lines, InstanceWorld instance) {
        super(api, sign, lines, instance);
    }

    /**
     * Returns the glowing block.
     *
     * @return the glowing block
     */
    public GlowingBlock getGlowingBlock() {
        return glowingBlock;
    }

    /**
     * Returns the color of the glowing block or null if it is a rainbow block.
     *
     * @return the color of the glowing block or null if it is a rainbow block
     */
    public ChatColor getColor() {
        return color;
    }

    @Override
    public String getName() {
        return "GlowingBlock";
    }

    @Override
    public String getBuildPermission() {
        return DPermission.SIGN.getNode() + ".glowingblock";
    }

    @Override
    public boolean isOnDungeonInit() {
        return false;
    }

    @Override
    public boolean isProtected() {
        return false;
    }

    @Override
    public boolean isSetToAir() {
        return true;
    }

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public void initialize() {
        if (getLine(1).equalsIgnoreCase("RAINBOW")) {
            color = null;
        } else {
            ChatColor color = EnumUtil.getEnumIgnoreCase(ChatColor.class, getLine(1));
            if (color != null) {
                this.color = color;
            }
        }
        try {
            time = Double.parseDouble(getLine(2));
        } catch (NumberFormatException exception) {
        }
    }

    @Override
    public void activate() {
        if (active) {
            return;
        }

        ((DGameWorld) getGameWorld()).addGameBlock(
                glowingBlock = new GlowingBlock(DungeonsXXL.getInstance(), BlockUtil.getAttachedBlock(getSign().getBlock()), color, time));
        active = true;
    }

    @Override
    public void deactivate() {
        if (!active) {
            return;
        }

        glowingBlock.removeGlow();
        active = false;
    }

}
