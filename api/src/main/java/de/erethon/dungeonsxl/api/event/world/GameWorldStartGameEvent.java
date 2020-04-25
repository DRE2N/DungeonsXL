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
package de.erethon.dungeonsxl.api.event.world;

import de.erethon.dungeonsxl.api.dungeon.Game;
import de.erethon.dungeonsxl.api.world.GameWorld;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * @author Daniel Saukel
 */
public class GameWorldStartGameEvent extends GameWorldEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private Game game;
    private Location startLocation;

    public GameWorldStartGameEvent(GameWorld gameWorld, Game game, Location location) {
        super(gameWorld, gameWorld.getDungeon());
        this.game = game;
    }

    /**
     * Returns the game.
     *
     * @return the game
     */
    public Game getGame() {
        return game;
    }

    /**
     * Returns the location where the players are teleported.
     *
     * @return the location where the players are teleported
     */
    public Location getStartLocation() {
        return startLocation;
    }

    /**
     * Sets the location where the players are teleported.
     *
     * @param startLocation a location in the game world
     * @throws IllegalArgumentException if the given location is not in the game world.
     */
    public void setStartLocation(Location startLocation) {
        if (startLocation.getWorld() != getGameWorld().getWorld()) {
            throw new IllegalArgumentException("Location " + startLocation + " is not in world " + getGameWorld().getWorld());
        }
        this.startLocation = startLocation;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
