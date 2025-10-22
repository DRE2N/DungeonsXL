package com.github.linghun91.dungeonsxl.api.world;

import org.bukkit.entity.Player;

/**
 * Represents an edit world instance
 *
 * @author linghun91
 */
public interface EditWorld extends InstanceWorld {

    Player getEditor();
    boolean save();
    boolean hasUnsavedChanges();
    void markDirty();
    void clearDirty();
}
