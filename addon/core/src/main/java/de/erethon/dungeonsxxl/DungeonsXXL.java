/*
 * Copyright (C) 2020-2026 Daniel Saukel
 *
 * All rights reserved.
 */
package de.erethon.dungeonsxxl;

import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.api.DungeonModule;
import de.erethon.dungeonsxl.api.Requirement;
import de.erethon.dungeonsxl.api.Reward;
import de.erethon.dungeonsxl.api.dungeon.GameRule;
import de.erethon.dungeonsxl.api.sign.DungeonSign;
import de.erethon.dungeonsxl.api.trigger.Trigger;
import de.erethon.dungeonsxxl.requirement.*;
import de.erethon.dungeonsxxl.sign.*;
import de.erethon.dungeonsxxl.util.GlowUtil;
import de.erethon.xlib.compatibility.Internals;
import de.erethon.xlib.plugin.DREPlugin;
import de.erethon.xlib.plugin.DREPluginSettings;
import de.erethon.xlib.util.Registry;

/**
 * @author Daniel Saukel
 */
public class DungeonsXXL extends DREPlugin implements DungeonModule {

    private static DungeonsXXL instance;
    private DungeonsXL dxl;
    private GlowUtil glowUtil;

    public DungeonsXXL() {
        settings = DREPluginSettings.builder()
                .internals(Internals.v1_16_R3)
                .metrics(false)
                .spigotMCResourceId(-1)
                .build();
    }

    @Override
    public void onEnable() {
        instance = this;
        dxl = DungeonsXL.getInstance();
        glowUtil = new GlowUtil(this);
    }

    /**
     * Returns the instance of this plugin.
     *
     * @return the instance of this plugin
     */
    public static DungeonsXXL getInstance() {
        return instance;
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

    @Override
    public void initRequirements(Registry<String, Class<? extends Requirement>> registry) {
        registry.add("feeItems", FeeItemsRequirement.class);
    }

    @Override
    public void initRewards(Registry<String, Class<? extends Reward>> registry) {
    }

    @Override
    public void initSigns(Registry<String, Class<? extends DungeonSign>> registry) {
        registry.add("FIREWORK", FireworkSign.class);
        registry.add("GLOWINGBLOCK", GlowingBlockSign.class);
        registry.add("INTERACTWALL", InteractWallSign.class);
        registry.add("PARTICLE", ParticleSign.class);
    }

    @Override
    public void initGameRules(Registry<String, GameRule> registry) {
    }

    @Override
    public void initTriggers(Registry<Character, Class<? extends Trigger>> triggerRegistry) {
    }

}
