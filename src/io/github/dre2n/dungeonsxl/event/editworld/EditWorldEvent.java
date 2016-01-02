package io.github.dre2n.dungeonsxl.event.editworld;

import io.github.dre2n.dungeonsxl.dungeon.EditWorld;

import org.bukkit.event.Event;

public abstract class EditWorldEvent extends Event {
	
	private EditWorld editWorld;
	
	public EditWorldEvent(EditWorld editWorld) {
		this.editWorld = editWorld;
	}
	
	/**
	 * @return the editWorld
	 */
	public EditWorld getEditWorld() {
		return editWorld;
	}
	
	/**
	 * @param editWorld
	 * the editWorld to set
	 */
	public void setEditWorld(EditWorld editWorld) {
		this.editWorld = editWorld;
	}
	
}
