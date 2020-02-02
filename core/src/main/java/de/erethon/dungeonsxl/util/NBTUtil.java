/*
 * Copyright (C) 2012-2020 Frank Baumann
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

import static de.erethon.dungeonsxl.util.ReflectionUtil.*;
import org.bukkit.inventory.ItemStack;

/**
 * A minimalistic NMS ItemStack NBT util.
 *
 * @author Daniel Saukel
 */
public class NBTUtil {

    public static final String DUNGEON_ITEM_KEY = "DungeonItem";

    /**
     * Returns the NBT data of an ItemStack
     *
     * @param item the Bukkit representation of the ItemStack
     * @return the NBT data of the ItemStack
     */
    public static Object getTag(ItemStack item) {
        return invoke(ITEM_STACK_GET_TAG, invoke(CRAFT_ITEM_STACK_AS_NMS_COPY, null, item));
    }

    /**
     * Returns a new NBTTagCompound
     *
     * @return a new NBTTagCompound
     */
    public static Object createTag() {
        return newInstance(NBT_TAG_COMPOUND);
    }

    /**
     * Returns a copy of the ItemStack with the applied NBT data
     *
     * @param item the Bukkit representation of the ItemStack
     * @param tag  the NBT data to set
     * @return a new copy of the Bukkit ItemStack with the applied changes
     */
    public static ItemStack setTag(ItemStack item, Object tag) {
        Object nmsStack = invoke(CRAFT_ITEM_STACK_AS_NMS_COPY, null, item);
        invoke(ITEM_STACK_SET_TAG, nmsStack, tag);
        return (ItemStack) invoke(CRAFT_ITEM_STACK_AS_BUKKIT_COPY, null, nmsStack);
    }

    /**
     * Returns if the NBTTagCompound contains the key
     *
     * @param tag the NBTTagCompound
     * @param key a key
     * @return if the NBTTagCompound contains the key
     */
    public static boolean hasKey(Object tag, String key) {
        return (boolean) invoke(NBT_TAG_COMPOUND_HAS_KEY, tag, key);
    }

    /**
     * Adds the key and its value to the NBTTagCompound
     *
     * @param tag   the NBTTagCompound
     * @param key   the key to add
     * @param value the value to add
     */
    public static void addBoolean(Object tag, String key, boolean value) {
        invoke(NBT_TAG_COMPOUND_SET_BOOLEAN, tag, key, value);
    }

    /**
     * Removes the key from the NBTTagCompound
     *
     * @param tag the NBTTagCompound
     * @param key the key to remove
     */
    public static void removeKey(Object tag, String key) {
        invoke(NBT_TAG_COMPOUND_REMOVE, tag, key);
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
