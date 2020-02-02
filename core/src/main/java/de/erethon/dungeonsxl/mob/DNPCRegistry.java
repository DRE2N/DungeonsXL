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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.UUID;
import net.citizensnpcs.Settings.Setting;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCCreateEvent;
import net.citizensnpcs.api.npc.AbstractNPC;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.persistence.PersistenceLoader;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.trait.MobType;
import net.citizensnpcs.api.util.DataKey;
import net.citizensnpcs.api.util.MemoryDataKey;
import net.citizensnpcs.trait.ArmorStandTrait;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.trait.MountTrait;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

/**
 * @author Daniel Saukel
 */
public class DNPCRegistry implements NPCRegistry {

    @Override
    public NPC createNPC(EntityType type, String name) {
        return createNPC(type, UUID.randomUUID(), 0, name);
    }

    @Override
    public NPC createNPC(EntityType type, UUID uuid, int id, String name) {
        NPC npc = null;
        try {
            Method method = CitizensAPI.getNPCRegistry().getClass().getDeclaredMethod("getByType", EntityType.class, UUID.class, int.class, String.class);
            method.setAccessible(true);
            npc = (NPC) method.invoke(CitizensAPI.getNPCRegistry(), type, uuid, id, name);

        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
            throw new IllegalStateException("Could not create NPC: " + exception.getClass().getSimpleName());
        }

        if (npc == null) {
            throw new IllegalStateException("Could not create NPC: npc is null");
        }

        Bukkit.getPluginManager().callEvent(new NPCCreateEvent(npc));

        if (type == EntityType.ARMOR_STAND && !npc.hasTrait(ArmorStandTrait.class)) {
            npc.addTrait(ArmorStandTrait.class);
        }
        if (Setting.DEFAULT_LOOK_CLOSE.asBoolean()) {
            npc.addTrait(LookClose.class);
        }
        npc.addTrait(MountTrait.class);

        return npc;
    }

    @Override
    public void deregister(NPC npc) {
        CitizensAPI.getNPCRegistry().deregister(npc);
    }

    @Override
    public void deregisterAll() {
        CitizensAPI.getNPCRegistry().deregisterAll();
    }

    @Override
    public NPC getById(int id) {
        return CitizensAPI.getNPCRegistry().getById(id);
    }

    @Override
    public NPC getByUniqueId(UUID uuid) {
        return CitizensAPI.getNPCRegistry().getByUniqueId(uuid);
    }

    @Override
    public NPC getByUniqueIdGlobal(UUID uuid) {
        return CitizensAPI.getNPCRegistry().getByUniqueIdGlobal(uuid);
    }

    @Override
    public NPC getNPC(Entity entity) {
        return CitizensAPI.getNPCRegistry().getNPC(entity);
    }

    @Override
    public boolean isNPC(Entity entity) {
        return CitizensAPI.getNPCRegistry().isNPC(entity);
    }

    @Override
    public Iterable<NPC> sorted() {
        return CitizensAPI.getNPCRegistry().sorted();
    }

    @Override
    public Iterator<NPC> iterator() {
        return CitizensAPI.getNPCRegistry().iterator();
    }

    /**
     * Clones an NPC without spamming the config.
     *
     * @param npc the NPC to clone
     * @return a clone of the NPC
     */
    public NPC createTransientClone(AbstractNPC npc) {
        NPC copy = createNPC(npc.getTrait(MobType.class).getType(), npc.getFullName());
        DataKey key = new MemoryDataKey();
        save(npc, key);
        copy.load(key);
        for (Trait trait : copy.getTraits()) {
            trait.onCopy();
        }
        return copy;
    }

    // Like in AbstractNPC#save(DataKey), but without persistence stuff
    public void save(AbstractNPC npc, DataKey root) {
        if (!npc.data().get(NPC.SHOULD_SAVE_METADATA, true)) {
            return;
        }
        npc.data().saveTo(root.getRelative("metadata"));
        root.setString("name", npc.getFullName());
        root.setString("uuid", npc.getUniqueId().toString());

        StringBuilder traitNames = new StringBuilder();
        for (Trait trait : npc.getTraits()) {
            DataKey traitKey = root.getRelative("traits." + trait.getName());
            trait.save(traitKey);
            PersistenceLoader.save(trait, traitKey);
            //npc.removedTraits.remove(trait.getName());
            traitNames.append(trait.getName() + ",");
        }
        if (traitNames.length() > 0) {
            root.setString("traitnames", traitNames.substring(0, traitNames.length() - 1));
        } else {
            root.setString("traitnames", "");
        }
        //npc.removedTraits.clear();
    }

}
