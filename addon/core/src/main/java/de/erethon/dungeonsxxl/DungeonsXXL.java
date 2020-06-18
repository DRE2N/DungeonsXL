/*
 * Copyright (C) 2020 Daniel Saukel
 *
 * All rights reserved.
 */
package de.erethon.dungeonsxxl;

import de.erethon.commons.compatibility.Internals;
import de.erethon.commons.javaplugin.DREPlugin;
import de.erethon.commons.javaplugin.DREPluginSettings;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxxl.requirement.*;
import de.erethon.dungeonsxxl.sign.*;
import de.erethon.dungeonsxxl.util.GlowUtil;

/**
 * @author Daniel Saukel
 */
public class DungeonsXXL extends DREPlugin {

    private DungeonsXL dxl;
    private GlowUtil glowUtil;

    public DungeonsXXL() {
        settings = DREPluginSettings.builder()
                .internals(Internals.v1_15_R1)
                .metrics(false)
                .spigotMCResourceId(-1)
                .build();
    }

    @Override
    public void onEnable() {
        dxl = DungeonsXL.getInstance();
        glowUtil = new GlowUtil(this);

        dxl.getRequirementRegistry().add("feeItems", FeeItemsRequirement.class);

        dxl.getSignRegistry().add("Firework", FireworkSign.class);
        dxl.getSignRegistry().add("GlowingBlock", GlowingBlockSign.class);
        dxl.getSignRegistry().add("InteractWall", InteractWallSign.class);
        dxl.getSignRegistry().add("Particle", ParticleSign.class);
    }

    /**
     * Returns the instance of this plugin.
     *
     * @return the instance of this plugin
     */
    public static DungeonsXXL getInstance() {
        return (DungeonsXXL) DREPlugin.getInstance();
    }

    /**
     * Returns the current {@link de.erethon.dungeonsxl.DungeonsXL} singleton.
     *
     * @return the current {@link de.erethon.dungeonsxl.DungeonsXL} singleton
     */
    public DungeonsXL getDXL() {
        return dxl;
    }

    /**
     * The loaded instance of GlowUtil.
     *
     * @return the loaded instance of GlowUtil
     */
    public GlowUtil getGlowUtil() {
        return glowUtil;
    }

}
