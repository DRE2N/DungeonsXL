/*
 * Copyright (C) 2012-2022 Frank Baumann
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
package de.erethon.dungeonsxl.mob;

import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.dungeon.GameRule;
import de.erethon.dungeonsxl.api.mob.ExternalMobProvider;
import de.erethon.dungeonsxl.api.world.GameWorld;
import de.erethon.dungeonsxl.util.commons.misc.NumberUtil;
import java.util.HashSet;
import java.util.Set;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.npc.AbstractNPC;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * ExternalMobProvider implementation for Citizens.
 *
 * @author Daniel Saukel
 */
public class CitizensMobProvider implements ExternalMobProvider, Listener {

    private DungeonsAPI api;

    private static final String IDENTIFIER = "CI";

    private DNPCRegistry registry = new DNPCRegistry();
    private Set<NPC> spawnedNPCs = new HashSet<>();

    public CitizensMobProvider(DungeonsAPI api) {
        this.api = api;
    }

    /**
     * @return the DungeonsXL NPC registry
     */
    public DNPCRegistry getNPCRegistry() {
        return registry;
    }

    /**
     * @return the spawned Citizens NPCs
     */
    public Set<NPC> getSpawnedNPCs() {
        return spawnedNPCs;
    }

    /**
     * @param npc the NPC to add
     */
    public void addSpawnedNPC(NPC npc) {
        spawnedNPCs.add(npc);
    }

    /**
     * @param npc the NPC to remove
     */
    public void removeSpawnedNPC(NPC npc) {
        spawnedNPCs.remove(npc);
        npc.destroy();
    }

    public void removeSpawnedNPCs(World world) {
        Set<NPC> worldNPCs = new HashSet<>();
        for (NPC npc : spawnedNPCs) {
            if (npc.getStoredLocation().getWorld().equals(world)) {
                worldNPCs.add(npc);
            }
        }
        worldNPCs.forEach(this::removeSpawnedNPC);
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public String getRawCommand() {
        return null;
    }

    @Override
    public String getCommand(String mob, String world, double x, double y, double z) {
        return null;
    }

    @Override
    public void summon(String mob, Location location) {
        NPC source = CitizensAPI.getNPCRegistry().getById(NumberUtil.parseInt(mob));
        if (!(source instanceof AbstractNPC)) {
            return;
        }

        GameWorld gameWorld = api.getGameWorld(location.getWorld());
        if (gameWorld == null) {
            return;
        }

        boolean nativeRegistry = gameWorld.getDungeon().getRules().getState(GameRule.USE_NATIVE_CITIZENS_REGISTRY);
        NPC npc = nativeRegistry ? source.clone() : registry.createTransientClone((AbstractNPC) source);
        if (npc.isSpawned()) {
            npc.despawn();
        }

        npc.spawn(location);
        spawnedNPCs.add(npc);
        api.wrapEntity((LivingEntity) npc.getEntity(), gameWorld, mob);
    }

    /* Listeners */
    @EventHandler
    public void onNPCDeath(NPCDeathEvent event) {
        NPC npc = event.getNPC();
        if (spawnedNPCs.contains(npc)) {
            CitizensAPI.getNPCRegistry().deregister(npc);
            removeSpawnedNPC(npc);
        }
    }

}
