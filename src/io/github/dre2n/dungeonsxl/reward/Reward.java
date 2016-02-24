package io.github.dre2n.dungeonsxl.reward;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.bukkit.entity.Player;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.event.reward.RewardRegistrationEvent;
import io.github.dre2n.dungeonsxl.reward.Reward;
import io.github.dre2n.dungeonsxl.reward.RewardType;
import io.github.dre2n.dungeonsxl.reward.RewardTypeDefault;

public abstract class Reward {
	
	protected static DungeonsXL plugin = DungeonsXL.getPlugin();
	
	public static Reward create(RewardType type) {
		Reward reward = null;
		
		try {
			Constructor<? extends Reward> constructor = type.getHandler().getConstructor();
			reward = constructor.newInstance();
			
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
			plugin.getLogger().info("An error occurred while accessing the handler class of the reward " + type.getIdentifier() + ": " + exception.getClass().getSimpleName());
			if ( !(type instanceof RewardTypeDefault)) {
				plugin.getLogger().info("Please note that this reward is an unsupported feature added by an addon!");
			}
		}
		
		RewardRegistrationEvent event = new RewardRegistrationEvent(reward);
		
		if (event.isCancelled()) {
			return null;
		}
		
		return reward;
	}
	
	// Abstract methods
	
	public abstract void giveTo(Player player);
	
	public abstract RewardType getType();
	
}
