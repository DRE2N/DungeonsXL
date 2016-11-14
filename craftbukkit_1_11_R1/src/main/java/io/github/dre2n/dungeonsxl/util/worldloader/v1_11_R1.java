/*
 * Copyright (C) 2012-2016 Frank Baumann
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.dre2n.dungeonsxl.util.worldloader;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.logging.Logger;
import net.minecraft.server.v1_11_R1.BlockPosition;
import net.minecraft.server.v1_11_R1.Convertable;
import net.minecraft.server.v1_11_R1.EntityTracker;
import net.minecraft.server.v1_11_R1.EnumDifficulty;
import net.minecraft.server.v1_11_R1.EnumGamemode;
import net.minecraft.server.v1_11_R1.IDataManager;
import net.minecraft.server.v1_11_R1.IProgressUpdate;
import net.minecraft.server.v1_11_R1.MinecraftServer;
import net.minecraft.server.v1_11_R1.ServerNBTManager;
import net.minecraft.server.v1_11_R1.WorldData;
import net.minecraft.server.v1_11_R1.WorldLoaderServer;
import net.minecraft.server.v1_11_R1.WorldManager;
import net.minecraft.server.v1_11_R1.WorldServer;
import net.minecraft.server.v1_11_R1.WorldSettings;
import net.minecraft.server.v1_11_R1.WorldType;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.craftbukkit.v1_11_R1.CraftServer;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginManager;

/**
 * @author Daniel Saukel
 */
public class v1_11_R1 extends InternalsProvider {

    MinecraftServer console;
    CraftServer server = ((CraftServer) Bukkit.getServer());
    Map<String, World> worlds;
    PluginManager pluginManager = Bukkit.getPluginManager();
    File worldContainer = Bukkit.getWorldContainer();
    Logger logger = Bukkit.getLogger();

    v1_11_R1() {
        try {
            Field fConsole = CraftServer.class.getDeclaredField("console");
            fConsole.setAccessible(true);
            console = (MinecraftServer) fConsole.get(server);

            Field fWorlds = CraftServer.class.getDeclaredField("worlds");
            fWorlds.setAccessible(true);
            worlds = (Map<String, World>) fWorlds.get(server);

        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException exception) {
            exception.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    World createWorld(WorldCreator creator) {
        String name = creator.name();
        ChunkGenerator generator = creator.generator();
        File folder = new File(worldContainer, name);
        World world = Bukkit.getWorld(name);
        WorldType type = WorldType.getType(creator.type().getName());
        boolean generateStructures = creator.generateStructures();

        if (world != null) {
            return world;
        }

        if ((folder.exists()) && (!folder.isDirectory())) {
            throw new IllegalArgumentException("File exists with the name '" + name + "' and isn't a folder");
        }

        if (generator == null) {
            generator = server.getGenerator(name);
        }

        Convertable converter = new WorldLoaderServer(worldContainer, server.getHandle().getServer().getDataConverterManager());
        if (converter.isConvertable(name)) {
            logger.info("Converting world '" + name + "'");
            converter.convert(name, new IProgressUpdate() {
                private long b = System.currentTimeMillis();

                @Override
                public void a(String s) {
                }

                @Override
                public void a(int i) {
                    if (System.currentTimeMillis() - this.b >= 1000L) {
                        this.b = System.currentTimeMillis();
                        MinecraftServer.LOGGER.info("Converting... " + i + "%");
                    }

                }

                @Override
                public void c(String s) {
                }
            });
        }

        int dimension = CraftWorld.CUSTOM_DIMENSION_OFFSET + console.worlds.size();
        boolean used = false;
        do {
            for (WorldServer server : console.worlds) {
                used = server.dimension == dimension;
                if (used) {
                    dimension++;
                    break;
                }
            }
        } while (used);
        boolean hardcore = false;

        IDataManager sdm = new ServerNBTManager(worldContainer, name, true, server.getHandle().getServer().getDataConverterManager());
        WorldData worlddata = sdm.getWorldData();
        WorldSettings worldSettings = null;
        if (worlddata == null) {
            worldSettings = new WorldSettings(creator.seed(), EnumGamemode.getById(server.getDefaultGameMode().getValue()), generateStructures, hardcore, type);
            worldSettings.setGeneratorSettings(creator.generatorSettings());
            worlddata = new WorldData(worldSettings, name);
        }
        worlddata.checkName(name); // CraftBukkit - Migration did not rewrite the level.dat; This forces 1.8 to take the last loaded world as respawn (in this case the end)
        WorldServer internal = (WorldServer) new WorldServer(console, sdm, worlddata, dimension, console.methodProfiler, creator.environment(), generator).b();

        if (!(worlds.containsKey(name.toLowerCase(java.util.Locale.ENGLISH)))) {
            return null;
        }

        if (worldSettings != null) {
            internal.a(worldSettings);
        }
        internal.scoreboard = server.getScoreboardManager().getMainScoreboard().getHandle();

        internal.tracker = new EntityTracker(internal);
        internal.addIWorldAccess(new WorldManager(console, internal));
        internal.worldData.setDifficulty(EnumDifficulty.EASY);
        internal.setSpawnFlags(true, true);
        console.worlds.add(internal);

        if (generator != null) {
            internal.getWorld().getPopulators().addAll(generator.getDefaultPopulators(internal.getWorld()));
        }

        pluginManager.callEvent(new WorldInitEvent(internal.getWorld()));
        logger.info("Preparing start region for level " + (console.worlds.size() - 1) + " (Seed: " + internal.getSeed() + ")");

        if (internal.getWorld().getKeepSpawnInMemory()) {
            short short1 = 196;
            long i = System.currentTimeMillis();
            for (int j = -short1; j <= short1; j += 16) {
                for (int k = -short1; k <= short1; k += 16) {
                    long l = System.currentTimeMillis();

                    if (l < i) {
                        i = l;
                    }

                    if (l > i + 1000L) {
                        int i1 = (short1 * 2 + 1) * (short1 * 2 + 1);
                        int j1 = (j + short1) * (short1 * 2 + 1) + k + 1;

                        logger.info("Preparing spawn area for " + name + ", " + (j1 * 100 / i1) + "%");
                        i = l;
                    }

                    BlockPosition chunkcoordinates = internal.getSpawn();
                    try {
                        internal.getChunkProviderServer().getChunkAt(chunkcoordinates.getX() + j >> 4, chunkcoordinates.getZ() + k >> 4);
                    } catch (Exception exception) {
                    }
                }
            }
        }
        pluginManager.callEvent(new WorldLoadEvent(internal.getWorld()));
        return internal.getWorld();
    }

}
