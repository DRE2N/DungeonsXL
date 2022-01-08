/*
 * Copyright (C) 2020-2022 Daniel Saukel
 *
 * All rights reserved.
 */
package de.erethon.dungeonsxxl.util;

import java.util.Random;
import org.bukkit.Color;
import static org.bukkit.Color.*;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

/**
 * Util class for randomized fireworks.
 *
 * @author Daniel Saukel
 */
public class FireworkUtil {

    private static final Random RANDOM = new Random();
    private static final Color[] COLORS = {YELLOW, AQUA, BLACK, BLUE, FUCHSIA, GRAY, GREEN, LIME, MAROON, NAVY, OLIVE, ORANGE, PURPLE, RED, SILVER, TEAL, WHITE};

    /**
     * Spawns a randomized firework.
     *
     * @param location the location where the firework is fired
     * @return the Firework
     */
    public static Firework spawnRandom(Location location) {
        Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta meta = firework.getFireworkMeta();
        Random r = new Random();
        int rt = r.nextInt(4) + 1;
        FireworkEffect.Type type = FireworkEffect.Type.BALL;
        if (rt == 1) {
            type = FireworkEffect.Type.BALL;
        }
        if (rt == 2) {
            type = FireworkEffect.Type.BALL_LARGE;
        }
        if (rt == 3) {
            type = FireworkEffect.Type.BURST;
        }
        if (rt == 4) {
            type = FireworkEffect.Type.CREEPER;
        }
        if (rt == 5) {
            type = FireworkEffect.Type.STAR;
        }
        FireworkEffect effect = FireworkEffect.builder().flicker(r.nextBoolean()).withColor(randomColor()).withFade(randomColor()).with(type).trail(r.nextBoolean()).build();
        meta.addEffect(effect);
        int rp = r.nextInt(2) + 1;
        meta.setPower(rp);
        firework.setFireworkMeta(meta);
        return firework;
    }

    private static Color randomColor() {
        return COLORS[RANDOM.nextInt(COLORS.length - 1)];
    }

}
