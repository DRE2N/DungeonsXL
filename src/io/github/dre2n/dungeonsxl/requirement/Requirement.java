package io.github.dre2n.dungeonsxl.requirement;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.bukkit.entity.Player;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.event.requirement.RequirementRegistrationEvent;

public abstract class Requirement {
	
	protected static DungeonsXL plugin = DungeonsXL.getPlugin();
	
	public static Requirement create(RequirementType type) {
		Requirement requirement = null;
		
		try {
			Constructor<? extends Requirement> constructor = type.getHandler().getConstructor();
			requirement = constructor.newInstance();
			
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
			plugin.getLogger().info("An error occurred while accessing the handler class of the requirement " + type.getIdentifier() + ": " + exception.getClass().getSimpleName());
			if ( !(type instanceof RequirementTypeDefault)) {
				plugin.getLogger().info("Please note that this requirement is an unsupported feature added by an addon!");
			}
		}
		
		RequirementRegistrationEvent event = new RequirementRegistrationEvent(requirement);
		
		if (event.isCancelled()) {
			return null;
		}
		
		return requirement;
	}
	
	// Abstract methods
	
	public abstract boolean check(Player player);
	
	public abstract void demand(Player player);
	
	public abstract RequirementType getType();
	
}
