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
package de.erethon.dungeonsxl.player.groupadapter;

import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.events.bukkit.party.BukkitPartiesPartyPostCreateEvent;
import com.alessiodp.parties.api.events.bukkit.party.BukkitPartiesPartyPreDeleteEvent;
import com.alessiodp.parties.api.events.bukkit.party.BukkitPartiesPartyRenameEvent;
import com.alessiodp.parties.api.events.bukkit.player.BukkitPartiesPlayerPostJoinEvent;
import com.alessiodp.parties.api.events.bukkit.player.BukkitPartiesPlayerPostLeaveEvent;
import com.alessiodp.parties.api.interfaces.PartiesAPI;
import com.alessiodp.parties.api.interfaces.Party;
import com.alessiodp.parties.api.interfaces.PartyPlayer;
import de.erethon.commons.chat.MessageUtil;
import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.player.GroupAdapter;
import de.erethon.dungeonsxl.api.player.PlayerGroup;
import de.erethon.dungeonsxl.config.DMessage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * This class may be used as a reference for implementations of the GroupAdapter API.
 *
 * @author Daniel Saukel
 */
public class PartiesAdapter extends GroupAdapter<Party> implements Listener {

    private PartiesAPI partiesAPI;

    public PartiesAdapter(DungeonsAPI api) {
        super(api, Philosophy.PERSISTENT);
        Bukkit.getPluginManager().registerEvents(this, api);
        partiesAPI = Parties.getApi();
    }

    @Override
    public Party createExternalGroup(PlayerGroup dGroup) {
        if (!partiesAPI.createParty(dGroup.getRawName(), partiesAPI.getPartyPlayer(dGroup.getLeader().getUniqueId()))) {
            return null;
        }
        Party eGroup = partiesAPI.getParty(dGroup.getRawName());
        groups.put(dGroup, new ExternalGroupData<>(eGroup, true));
        return eGroup;
    }

    @Override
    public PlayerGroup createDungeonGroup(Party eGroup) {
        PlayerGroup dGroup = dxl.createGroup(Bukkit.getPlayer(eGroup.getLeader()), eGroup.getName());
        eGroup.getMembers().forEach(uuid -> dGroup.addPlayer(Bukkit.getPlayer(uuid), false));
        groups.put(dGroup, new ExternalGroupData<>(eGroup, false));
        return dGroup;
    }

    @Override
    public Party getExternalGroup(Player member) {
        PartyPlayer pPlayer = getPartyPlayer(member);
        if (pPlayer == null) {
            return null;
        }
        return partiesAPI.getParty(pPlayer.getPartyName());
    }

    @Override
    public boolean isExternalGroupMember(Party eGroup, Player player) {
        if (eGroup == null) {
            return false;
        }
        return eGroup.getMembers().contains(player.getUniqueId());
    }

    @Override
    public boolean addExternalGroupMember(Party eGroup, Player member) {
        return eGroup.addMember(getPartyPlayer(member));
    }

    @Override
    public boolean removeExternalGroupMember(Party eGroup, Player member) {
        PartyPlayer pPlayer = getPartyPlayer(member);
        if (pPlayer == null) {
            return false;
        }
        if (eGroup == null) {
            return false;
        }
        eGroup.removeMember(pPlayer);
        if (eGroup.getMembers().isEmpty()) {
            eGroup.delete();
        }
        return true;
    }

    @Override
    public boolean deleteCorrespondingGroup(PlayerGroup dGroup) {
        ExternalGroupData<Party> data = groups.get(dGroup);
        if (data == null || !data.isCreatedByDXL()) {
            return false;
        }
        data.get().delete();
        groups.remove(dGroup);
        return true;
    }

    @Override
    public boolean areSimilar(PlayerGroup dGroup, Party eGroup) {
        if (dGroup == null || eGroup == null) {
            return false;
        }

        Collection<UUID> members = new ArrayList<>(dGroup.getMembers().getUniqueIds());
        for (UUID member : eGroup.getMembers()) {
            if (!members.contains(member)) {
                return false;
            }
            members.remove(member);
        }
        return members.isEmpty();
    }

    @EventHandler
    public void onCreation(BukkitPartiesPartyPostCreateEvent event) {
        // Event is called asynchronously
        new BukkitRunnable() {
            @Override
            public void run() {
                createDungeonGroup(event.getParty());
            }
        }.runTask(dxl);
    }

    @EventHandler
    public void onDeletion(BukkitPartiesPartyPreDeleteEvent event) {
        PlayerGroup dGroup = getDungeonGroup(event.getParty());
        if (dGroup != null) {
            groups.remove(dGroup); // This avoids circular deleting of groups
            dGroup.delete();
        }
    }

    @EventHandler
    public void onRename(BukkitPartiesPartyRenameEvent event) {
        PlayerGroup dGroup = getDungeonGroup(event.getParty());
        if (dGroup != null) {
            dGroup.delete();
        }
        if (dxl.getPlayerGroupCache().get(event.getNewPartyName()) != null) {
            MessageUtil.sendMessage(getPlayer(event.getPartyPlayer()), DMessage.ERROR_NAME_IN_USE.getMessage(event.getNewPartyName()));
            event.setCancelled(true);
            return;
        }
        dGroup.setName(event.getNewPartyName());
    }

    @EventHandler
    public void onJoin(BukkitPartiesPlayerPostJoinEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                syncJoin(getPlayer(event.getPartyPlayer()));
            }
        }.runTask(dxl);
    }

    @EventHandler
    public void onLeave(BukkitPartiesPlayerPostLeaveEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Player player = getPlayer(event.getPartyPlayer());
                PlayerGroup group = dxl.getPlayerGroup(player);
                if (group != null) {
                    group.removePlayer(player);
                }
            }
        }.runTask(dxl);
    }

    private Player getPlayer(PartyPlayer player) {
        return Bukkit.getPlayer(player.getPlayerUUID());
    }

    private PartyPlayer getPartyPlayer(Player player) {
        return partiesAPI.getPartyPlayer(player.getUniqueId());
    }

}
