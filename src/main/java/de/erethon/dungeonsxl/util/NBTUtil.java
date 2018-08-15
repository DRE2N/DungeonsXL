/*
 * Copyright (C) 2012-2018 Frank Baumann
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.erethon.dungeonsxl.util;

import static de.erethon.commons.misc.ReflectionUtil.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.bukkit.inventory.ItemStack;

/**
 * A minimalistic NMS ItemStack NBT util.
 *
 * @author Daniel Saukel
 */
public class NBTUtil {

    public static final String DUNGEON_ITEM_KEY = "DungeonItem";

    private static Method HAS_KEY;
    private static Method REMOVE;
    private static Method SET_BOOLEAN;

    static {
        try {
            HAS_KEY = NBT_TAG_COMPOUND.getDeclaredMethod("hasKey", String.class);
            REMOVE = NBT_TAG_COMPOUND.getDeclaredMethod("remove", String.class);
            SET_BOOLEAN = NBT_TAG_COMPOUND.getDeclaredMethod("setBoolean", String.class, boolean.class);
        } catch (NoSuchMethodException | SecurityException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Returns the NBT data of an ItemStack
     *
     * @param item the Bukkit representation of the ItemStack
     * @return the NBT data of the ItemStack
     */
    public static Object getTag(ItemStack item) {
        try {
            return ITEM_STACK_GET_TAG.invoke(CRAFT_ITEM_STACK_AS_NMS_COPY.invoke(null, item));
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    /**
     * Returns a new NBTTagCompound
     *
     * @return a new NBTTagCompound
     */
    public static Object createTag() {
        try {
            return NBT_TAG_COMPOUND.newInstance();
        } catch (IllegalAccessException | InstantiationException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    /**
     * Returns a copy of the ItemStack with the applied NBT data
     *
     * @param item the Bukkit representation of the ItemStack
     * @param tag  the NBT data to set
     * @return a new copy of the Bukkit ItemStack with the applied changes
     */
    public static ItemStack setTag(ItemStack item, Object tag) {
        try {
            Object nmsStack = CRAFT_ITEM_STACK_AS_NMS_COPY.invoke(null, item);
            ITEM_STACK_SET_TAG.invoke(nmsStack, tag);
            return (ItemStack) CRAFT_ITEM_STACK_AS_BUKKIT_COPY.invoke(null, nmsStack);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    /**
     * Returns if the NBTTagCompound contains the key
     *
     * @param tag the NBTTagCompound
     * @param key a key
     * @return if the NBTTagCompound contains the key
     */
    public static boolean hasKey(Object tag, String key) {
        try {
            return (boolean) HAS_KEY.invoke(tag, key);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    /**
     * Adds the key and its value to the NBTTagCompound
     *
     * @param tag   the NBTTagCompound
     * @param key   the key to add
     * @param value the value to add
     */
    public static void addBoolean(Object tag, String key, boolean value) {
        try {
            SET_BOOLEAN.invoke(tag, key, value);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Removes the key from the NBTTagCompound
     *
     * @param tag the NBTTagCompound
     * @param key the key to remove
     */
    public static void removeKey(Object tag, String key) {
        try {
            REMOVE.invoke(tag, key);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Returns if the item is a dungeon item
     *
     * @param item a Bukkit ItemStack
     * @return if the item is a dungeon item
     */
    public static boolean isDungeonItem(ItemStack item) {
        return getTag(item) != null && hasKey(getTag(item), DUNGEON_ITEM_KEY);
    }

}
