/*
 * Copyright (C) 2014-2023 Daniel Saukel
 *
 * This library is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNULesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.erethon.dungeonsxl.api.trigger;

import com.google.common.collect.Sets;
import de.erethon.bedrock.chat.MessageUtil;
import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.event.trigger.TriggerActionEvent;
import static de.erethon.dungeonsxl.api.trigger.TriggerTypeKey.*;
import de.erethon.dungeonsxl.api.world.GameWorld;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * A condition to fulfill in order to trigger a {@link TriggerListener} such as a {@link de.erethon.dungeonsxl.api.sign.DungeonSign}.
 * <p>
 * Implementations of this interface must always include a constructor of the types (DungeonsAPI, TriggerListener, LogicalExpression, String).
 *
 * @author Daniel Saukel
 */
public interface Trigger {

    /**
     * A Set of trigger types that are looked up in their game world before they are created, and, if an equal trigger already exists, returned instead of a new
     * one. For example, an instance trigger is not created when its entered into the trigger line of a listening sign, but when an interact sign explicitly
     * creates it.
     *
     * @see GameWorld#createTrigger(TriggerListener, LogicalExpression)
     */
    static final Set<Character> IDENTIFIABLE = Sets.newHashSet(GENERIC, INTERACT, MOB, PROGRESS, USE_ITEM, WAVE);

    /**
     * Constructs a subtype of Trigger.
     * <p>
     * Implementations of this interface must always include a constructor of the types (DungeonsAPI, TriggerListener, LogicalExpression, String).
     *
     * @param <T>        the type of the trigger
     * @param typeKey    the key that represents the type on dungeon signs and in the registry, see {@link TriggerTypeKey}
     * @param api        the API instance; not null
     * @param owner      an object that listens to the trigger
     * @param expression the atomic expression that the trigger uses; not null
     * @param value      the value of the trigger. This is the text of the expression without the type key; not null
     * @see GameWorld#createTrigger(TriggerListener, LogicalExpression)
     * @see AbstractTrigger#AbstractTrigger(DungeonsAPI, TriggerListener, LogicalExpression, String)
     * @return the constructed Trigger object
     */
    static <T extends Trigger> T construct(char typeKey, DungeonsAPI api, TriggerListener owner, LogicalExpression expression, String value) {
        Class<T> clss = null;
        try {
            clss = (Class<T>) api.getTriggerRegistry().get(typeKey);
            return construct(clss, api, owner, expression, value);
        } catch (Exception exception) {
            MessageUtil.log(api, "&4It looks like the trigger \"" + typeKey + "\"/" + clss + " was not registered correctly:");
            exception.printStackTrace();
            return null;
        }
    }

    /**
     * Constructs a subtype of Trigger.
     * <p>
     * Implementations of this interface must always include a constructor of the types (DungeonsAPI, TriggerListener, LogicalExpression, String).
     *
     * @param <T>        the type of the trigger
     * @param clss       the implementation Class object
     * @param api        the API instance
     * @param owner      an object that listens to the trigger
     * @param expression the atomic expression that the trigger uses; not null
     * @param value      the value of the trigger. This is the text of the expression without the type key; not null
     * @see GameWorld#createTrigger(TriggerListener, LogicalExpression)
     * @see AbstractTrigger#AbstractTrigger(DungeonsAPI, TriggerListener, LogicalExpression, String)
     * @return the constructed Trigger object
     */
    static <T extends Trigger> T construct(Class<T> clss, DungeonsAPI api, TriggerListener owner, LogicalExpression expression, String value) {
        try {
            Constructor constructor = clss.getConstructor(DungeonsAPI.class, TriggerListener.class, LogicalExpression.class, String.class);
            return (T) constructor.newInstance(api, owner, expression, value);

        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException exception) {
            MessageUtil.log(api, "&4Could not create a trigger of the type \"" + clss
                    + "\". A trigger implementation needs a constructor with the types (DungeonsAPI, TriggerListener, LogicalExpression, String).");
            exception.printStackTrace();
            return null;
        }
    }

    /**
     * Returns a char that that identifies the trigger as of a specific type.
     * <p>
     * Triggers from the default implementations are stored in {@link TriggerTypeKey}.
     *
     * @return a char that that identifies the trigger as of a specific type
     */
    char getKey();

    /**
     * Returns the raw value the trigger was initialized with. May contain an identifier or arguments in any shape or form.
     *
     * @return the raw value the trigger was initialized with. May contain an identifier or arguments in any shape or form
     */
    String getValue();

    /**
     * The {@link GameWorld} the trigger works in.
     *
     * @return the {@link GameWorld} the trigger works in
     */
    GameWorld getGameWorld();

    /**
     * Returns if the trigger is triggered.
     *
     * @return if the trigger is triggered
     */
    boolean isTriggered();

    /**
     * Sets if the trigger is triggered.
     *
     * @param triggered the state of the trigger
     */
    void setTriggered(boolean triggered);

    /**
     * Returns the last player who triggered the trigger.
     *
     * @return the last player who triggered the trigger
     */
    Player getTriggeringPlayer();

    /**
     * Updates {@link #getTriggeringPlayer()} to the given player.
     *
     * @param player the player to set
     */
    void setTriggeringPlayer(Player player);

    /**
     * A set of the objects that listen to this trigger.
     *
     * @return a set of the objects that listen to this trigger
     */
    Set<TriggerListener> getListeners();

    /**
     * Adds the given object to listen to this trigger.
     *
     * @param listener the listener to add
     * @return if adding the listener was successful
     */
    boolean addListener(TriggerListener listener);

    /**
     * Removes the given listener.
     *
     * @param listener the listener to remove
     * @return if removing the listener was successful
     */
    boolean removeListener(TriggerListener listener);

    /**
     * Unregisters the trigger from the {@link GameWorld}.
     * <p>
     * This is used to disable triggers, such as distance triggers removing themselves after the first player got into range.
     *
     * @return if unregistering the listener was successful
     */
    boolean unregisterTrigger();

    /**
     * Updates the listeners; to be used when the state of the trigger is updated; by default in {@link #trigger()}.
     */
    void updateListeners();

    /**
     * Called when the trigger is triggered.
     * <p>
     * This method provides default procedures when the conditions of any trigger are fulfilled, such as calling events and updating listeners. Use
     * {@link #onTrigger()} to implement specific behavior (such as self-removal for default distance triggers).
     * <p>
     * This method does NOT change {@link #isTriggered()}. Due to the difference in behavior of triggers (working button-like, switch-like etc.), this is to be
     * handled in {@link #onTrigger()}.
     *
     * @param switching        if the action changes the the state of {@link #isTriggered()}
     * @param triggeringPlayer the player who fulfilled the (last) conditions of this trigger
     */
    default void trigger(boolean switching, Player triggeringPlayer) {
        List<TriggerListener> fired = getListeners().stream()
                .filter(l -> l.getTriggerExpression().isSatisfied())
                .toList();
        TriggerActionEvent event = new TriggerActionEvent(this, fired);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        setTriggeringPlayer(triggeringPlayer);
        onTrigger(switching);
        updateListeners();
        postTrigger();
    }

    /**
     * Called when the trigger is triggered.
     * <p>
     * This method can be used to implement specific behavior (such as self-removal for default distance triggers). Call {@link #trigger()} instead when the
     * condition to trigger the trigger is fulfilled.
     *
     * @param switching if the action changes the the state of {@link #isTriggered()}
     */
    void onTrigger(boolean switching);

    /**
     * Called after listeners are updated.
     * <p>
     * This method can be used to implement specific behavior such as reactivating itself after the actions of the listeners are done, such as presence triggers
     * performing their action every time a player gets close.
     */
    default void postTrigger() {
    }

}
