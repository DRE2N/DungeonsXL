/*
 * Copyright (C) 2012-2020 Frank Baumann
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

import de.erethon.commons.misc.NumberUtil;
import de.erethon.dungeonsxl.world.DGameWorld;
import java.util.HashSet;
import java.util.Set;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.npc.AbstractNPC;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * ExternalMobProvider implementation for Citizens.
 *
 * @author Daniel Saukel
 */
public class CitizensMobProvider implements ExternalMobProvider, Listener {

    private static final String IDENTIFIER = "CI";

    private DNPCRegistry registry = new DNPCRegistry();
    private Set<NPC> spawnedNPCs = new HashSet<>();

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

        NPC npc = registry.createTransientClone((AbstractNPC) source);
        if (npc.isSpawned()) {
            npc.despawn();
        }

        npc.spawn(location);
        spawnedNPCs.add(npc);

        DGameWorld gameWorld = DGameWorld.getByWorld(location.getWorld());
        if (gameWorld == null) {
            return;
        }

        new DMob((LivingEntity) npc.getEntity(), gameWorld, mob);
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
