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
package io.github.dre2n.dungeonsxl.mob;

import io.github.dre2n.commons.util.NumberUtil;
import io.github.dre2n.dungeonsxl.world.GameWorld;
import java.util.HashSet;
import java.util.Set;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

/**
 * @author Daniel Saukel
 */
public class CitizensMobProvider implements ExternalMobProvider {

    private String identifier = "CI";
    private Set<NPC> spawnedNPCs = new HashSet<>();

    /**
     * @return the spawned Citizens NPCs
     */
    public Set<NPC> getSpawnedNPCs() {
        return spawnedNPCs;
    }

    /**
     * @param npc
     * the NPC to add
     */
    public void addSpawnedNPC(NPC npc) {
        spawnedNPCs.add(npc);
    }

    /**
     * @param npc
     * the NPC to remove
     */
    public void removeSpawnedNPC(NPC npc) {
        spawnedNPCs.remove(npc);
    }

    @Override
    public String getIdentifier() {
        return identifier;
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
        NPC npc = CitizensAPI.getNPCRegistry().getById(NumberUtil.parseInt(mob));

        if (npc != null) {
            npc = npc.clone();
            if (npc.isSpawned()) {
                npc.despawn();
            }

            npc.spawn(location);
            spawnedNPCs.add(npc);

            GameWorld gameWorld = GameWorld.getByWorld(location.getWorld());
            new DMob((LivingEntity) npc.getEntity(), gameWorld, null, mob);
        }
    }

}
