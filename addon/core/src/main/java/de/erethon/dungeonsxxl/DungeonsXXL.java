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
import de.erethon.xlib.XLib;
import de.erethon.xlib.compatibility.Version;
import de.erethon.xlib.plugin.PluginInit;
import de.erethon.xlib.plugin.PluginMeta;
import de.erethon.xlib.spiget.comparator.VersionComparator;
import de.erethon.xlib.util.Registry;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Daniel Saukel
 */
public class DungeonsXXL extends JavaPlugin implements DungeonModule {

    // 1.21.11 port: XLib 7.0 replaced DREPluginSettings+Internals with PluginMeta.
    // PluginInit is now a helper, not a base class — we extend JavaPlugin directly
    // and hold a PluginInit instance for MessageHandler/resource-save helpers.
    public static final PluginMeta META = new PluginMeta.Builder("DungeonsXXL")
            .minVersion(Version.MC1_21_11)
            .paperState(PluginMeta.State.SUPPORTED)
            .spigotState(PluginMeta.State.SUPPORTED)
            .spigotMCResourceId(-1)
            .versionComparator(VersionComparator.SEM_VER_SNAPSHOT)
            .build();

    private static DungeonsXXL instance;
    private DungeonsXL dxl;
    private GlowUtil glowUtil;
    private PluginInit init;

    @Override
    public void onEnable() {
        instance = this;
        dxl = DungeonsXL.getInstance();
        init = new PluginInit(this, XLib.getInstance(), META);
        glowUtil = new GlowUtil(this);
        dxl.registerModule(this);
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

    public PluginInit getInitializer() {
        return init;
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
