package io.github.dre2n.dungeonsxl.reward;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.bukkit.entity.Player;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.reward.Reward;
import io.github.dre2n.dungeonsxl.reward.RewardType;
import io.github.dre2n.dungeonsxl.reward.RewardTypeDefault;

public abstract class Reward {
	
	static DungeonsXL plugin = DungeonsXL.getPlugin();
	
	public static Reward create(RewardType type) {
		try {
			Constructor<? extends Reward> constructor = type.getHandler().getConstructor();
			return constructor.newInstance();
			
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
			plugin.getLogger().info("DungeonsXL could not find the handler class of the reward " + type.getIdentifier() + ".");
			if ( !(type instanceof RewardTypeDefault)) {
				plugin.getLogger().info("Please note that this reward is an unsupported feature added by an addon!");
			}
		}
		
		return null;
	}
	
	// Abstract methods
	
	public abstract void giveTo(Player player);
	
	public abstract RewardType getType();
	
}
