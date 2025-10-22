package com.github.linghun91.dungeonsxl.api.trigger;

/**
 * Trigger interface
 * @author linghun91
 */
public interface Trigger {
    String getId();
    TriggerType getType();
    boolean isTriggered();
    void trigger();
    void reset();
}
