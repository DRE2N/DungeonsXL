package com.github.linghun91.dungeonsxl.api.player;

import com.github.linghun91.dungeonsxl.api.dungeon.Dungeon;
import org.bukkit.entity.Player;
import java.util.Collection;
import java.util.Optional;

/**
 * Player group
 * @author linghun91
 */
public interface PlayerGroup {
    GroupColor getColor();
    Optional<Player> getCaptain();
    Collection<Player> getMembers();
    void addMember(Player player);
    void removeMember(Player player);
    Optional<Dungeon> getDungeon();
    int getLives();
    void setLives(int lives);
    int getScore();
    void addScore(int amount);
}
