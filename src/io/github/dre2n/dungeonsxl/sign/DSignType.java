package io.github.dre2n.dungeonsxl.sign;

public interface DSignType {
	
	/**
	 * @return the name
	 */
	public String getName();
	
	/**
	 * @return the buildPermission
	 */
	public String getBuildPermission();
	
	/**
	 * @return the onDungeonInit
	 */
	public boolean isOnDungeonInit();
	
	/**
	 * @return the handler
	 */
	public Class<? extends DSign> getHandler();
	
}
