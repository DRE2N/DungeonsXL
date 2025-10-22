package com.github.linghun91.dungeonsxl.api.dungeon;

import com.github.linghun91.dungeonsxl.api.world.ResourceWorld;
import java.util.List;

/**
 * Represents a dungeon
 * @author linghun91
 */
public interface Dungeon {
    String getName();
    boolean isMultiFloor();
    ResourceWorld getStartFloor();
    ResourceWorld getEndFloor();
    List<ResourceWorld> getFloors();
    GameRuleContainer getRules();
}
