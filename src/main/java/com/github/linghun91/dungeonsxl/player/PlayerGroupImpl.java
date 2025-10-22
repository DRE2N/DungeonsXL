package com.github.linghun91.dungeonsxl.player;

import com.github.linghun91.dungeonsxl.api.dungeon.Game;
import com.github.linghun91.dungeonsxl.api.player.DungeonPlayer;
import com.github.linghun91.dungeonsxl.api.player.GroupColor;
import com.github.linghun91.dungeonsxl.api.player.PlayerGroup;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Implementation of PlayerGroup
 */
public class PlayerGroupImpl implements PlayerGroup {
    
    private final UUID groupId;
    private final GroupColor color;
    private final Set<DungeonPlayer> members;
    private final Map<UUID, String> invitations;
    private DungeonPlayer leader;
    private Game game;
    private int score;
    private int lives;
    
    public PlayerGroupImpl(GroupColor color) {
        this.groupId = UUID.randomUUID();
        this.color = color;
        this.members = new HashSet<>();
        this.invitations = new HashMap<>();
        this.score = 0;
        this.lives = 3; // Default lives
    }
    
    @Override
    public UUID getId() {
        return groupId;
    }
    
    @Override
    public GroupColor getColor() {
        return color;
    }
    
    @Override
    public Set<DungeonPlayer> getMembers() {
        return new HashSet<>(members);
    }
    
    @Override
    public void addMember(DungeonPlayer player) {
        members.add(player);
        if (leader == null) {
            leader = player;
        }
    }
    
    @Override
    public void removeMember(DungeonPlayer player) {
        members.remove(player);
        if (leader != null && leader.equals(player)) {
            leader = members.isEmpty() ? null : members.iterator().next();
        }
    }
    
    @Override
    public boolean isMember(Player player) {
        return members.stream()
            .anyMatch(dp -> dp.getPlayer().getUniqueId().equals(player.getUniqueId()));
    }
    
    @Override
    public DungeonPlayer getLeader() {
        return leader;
    }
    
    @Override
    public void setLeader(DungeonPlayer player) {
        if (members.contains(player)) {
            this.leader = player;
        }
    }
    
    @Override
    public Game getGame() {
        return game;
    }
    
    @Override
    public void setGame(Game game) {
        this.game = game;
    }
    
    @Override
    public int getScore() {
        return score;
    }
    
    @Override
    public void addScore(int points) {
        this.score += points;
    }
    
    @Override
    public int getLives() {
        return lives;
    }
    
    @Override
    public void setLives(int lives) {
        this.lives = lives;
    }
    
    @Override
    public void removeLife() {
        if (lives > 0) {
            lives--;
        }
    }
    
    @Override
    public void sendMessage(String message) {
        members.forEach(member -> {
            if (member.getPlayer() != null && member.getPlayer().isOnline()) {
                member.getPlayer().sendMessage(message);
            }
        });
    }
    
    @Override
    public void invite(Player player, DungeonPlayer inviter) {
        invitations.put(player.getUniqueId(), inviter.getPlayer().getName());
    }
    
    @Override
    public boolean hasInvitation(Player player) {
        return invitations.containsKey(player.getUniqueId());
    }
    
    @Override
    public void removeInvitation(Player player) {
        invitations.remove(player.getUniqueId());
    }
}
