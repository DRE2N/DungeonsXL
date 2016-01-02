package io.github.dre2n.dungeonsxl.requirement;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.bukkit.entity.Player;

import io.github.dre2n.dungeonsxl.DungeonsXL;

public abstract class Requirement {
	
	static DungeonsXL plugin = DungeonsXL.getPlugin();
	
	public static Requirement create(RequirementType type) {
		try {
			Constructor<? extends Requirement> constructor = type.getHandler().getConstructor(String.class);
			return constructor.newInstance();
			
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
			plugin.getLogger().info("DungeonsXL could not find the handler class of the requirement " + type.getIdentifier() + ".");
			if ( !(type instanceof RequirementTypeDefault)) {
				plugin.getLogger().info("Please note that this requirement is an unsupported feature added by an addon!");
			}
		}
		
		return null;
	}
	
	// Abstract methods
	
	public abstract boolean check(Player player);
	
	public abstract void demand(Player player);
	
	public abstract RequirementType getType();
	
}
