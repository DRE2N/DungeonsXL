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
package de.erethon.dungeonsxl.player.groupadapter;

import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.events.bukkit.party.BukkitPartiesPartyPreDeleteEvent;
import com.alessiodp.parties.api.events.bukkit.party.BukkitPartiesPartyPreRenameEvent;
import com.alessiodp.parties.api.events.bukkit.player.BukkitPartiesPlayerPostJoinEvent;
import com.alessiodp.parties.api.events.bukkit.player.BukkitPartiesPlayerPostLeaveEvent;
import com.alessiodp.parties.api.interfaces.PartiesAPI;
import com.alessiodp.parties.api.interfaces.Party;
import com.alessiodp.parties.api.interfaces.PartyPlayer;
import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.player.GroupAdapter;
import de.erethon.dungeonsxl.api.player.PlayerGroup;
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
        super(api);
        Bukkit.getPluginManager().registerEvents(this, api);
        partiesAPI = Parties.getApi();
    }

    @Override
    public PlayerGroup createDungeonGroup(Party eGroup) {
        PlayerGroup dGroup = dxl.createGroup(Bukkit.getPlayer(eGroup.getLeader()), eGroup.getName());
        eGroup.getOnlineMembers().forEach(p -> dGroup.addMember(Bukkit.getPlayer(p.getPlayerUUID()), false));
        groups.put(dGroup, eGroup);
        return dGroup;
    }

    @Override
    public Party getExternalGroup(Player member) {
        PartyPlayer pPlayer = partiesAPI.getPartyPlayer(member.getUniqueId());
        if (pPlayer == null) {
            return null;
        }
        return partiesAPI.getParty(pPlayer.getPartyName());
    }

    @Override
    public int getGroupOnlineSize(Party eGroup) {
        return eGroup.getOnlineMembers().size();
    }

    @Override
    public boolean isExternalGroupMember(Party eGroup, Player player) {
        if (eGroup == null) {
            return false;
        }
        return eGroup.getMembers().contains(player.getUniqueId());
    }

    @EventHandler
    public void onDeletion(BukkitPartiesPartyPreDeleteEvent event) {
        PlayerGroup dGroup = getDungeonGroup(event.getParty());
        if (dGroup != null) {
            groups.remove(dGroup);
            dGroup.delete();
        }
    }

    @EventHandler
    public void onRename(BukkitPartiesPartyPreRenameEvent event) {
        PlayerGroup group = getDungeonGroup(event.getParty());
        if (group == null) {
            return;
        }
        group.setName(event.getNewPartyName());
    }

    @EventHandler
    public void onJoin(BukkitPartiesPlayerPostJoinEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                PlayerGroup group = getDungeonGroup(event.getParty());
                if (group == null || group.isPlaying()) {
                    return;
                }
                group.addMember(getPlayer(event.getPartyPlayer()), false);
            }
        }.runTask(dxl);
    }

    @EventHandler
    public void onLeave(BukkitPartiesPlayerPostLeaveEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                PlayerGroup group = getDungeonGroup(event.getParty());
                if (group == null || group.isPlaying()) {
                    return;
                }
                group.removeMember(getPlayer(event.getPartyPlayer()), false);
            }
        }.runTask(dxl);
    }

    private Player getPlayer(PartyPlayer player) {
        return Bukkit.getPlayer(player.getPlayerUUID());
    }

}
