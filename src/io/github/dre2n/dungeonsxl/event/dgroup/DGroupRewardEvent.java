package io.github.dre2n.dungeonsxl.event.dgroup;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.reward.Reward;

public class DGroupRewardEvent extends DGroupEvent implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;
	
	private List<Reward> rewards = new ArrayList<Reward>();
	private List<Player> excludedPlayers = new ArrayList<Player>();
	
	public DGroupRewardEvent(DGroup dGroup) {
		super(dGroup);
		this.rewards = dGroup.getRewards();
	}
	
	/**
	 * @return the rewards
	 */
	public List<Reward> getRewards() {
		return rewards;
	}
	
	/**
	 * @param reward
	 * the reward to add
	 */
	public void addRewards(Reward reward) {
		rewards.add(reward);
	}
	
	/**
	 * @param reward
	 * the reward to remove
	 */
	public void removeRewards(Reward reward) {
		rewards.remove(reward);
	}
	
	/**
	 * @return the excludedPlayers
	 */
	public List<Player> getExcludedPlayers() {
		return excludedPlayers;
	}
	
	/**
	 * @param player
	 * the player to add
	 */
	public void addExcludedPlayer(Player player) {
		excludedPlayers.add(player);
	}
	
	/**
	 * @param player
	 * the player to remove
	 */
	public void removeExcludedPlayer(Player player) {
		excludedPlayers.remove(player);
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
