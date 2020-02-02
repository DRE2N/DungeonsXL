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

import de.erethon.commons.compatibility.CompatibilityHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.bukkit.inventory.ItemStack;

/**
 * @author Daniel Saukel
 */
public class ReflectionUtil {

    static String INTERNALS_VERSION = CompatibilityHandler.getInstance().getInternals().toString();
    static String ORG_BUKKIT_CRAFTBUKKIT = "org.bukkit.craftbukkit." + INTERNALS_VERSION;
    static String NET_MINECRAFT_SERVER = "net.minecraft.server." + INTERNALS_VERSION;
    static Class NBT_TAG_COMPOUND;
    static Method NBT_TAG_COMPOUND_HAS_KEY;
    static Method NBT_TAG_COMPOUND_REMOVE;
    static Method NBT_TAG_COMPOUND_SET_BOOLEAN;
    static Class ITEM_STACK;
    static Method ITEM_STACK_GET_TAG;
    static Method ITEM_STACK_SET_TAG;
    static Class CRAFT_ITEM_STACK;
    static Method CRAFT_ITEM_STACK_AS_BUKKIT_COPY;
    static Method CRAFT_ITEM_STACK_AS_NMS_COPY;
    static Method CRAFT_BLOCK_SET_DATA;

    static {
        try {
            NBT_TAG_COMPOUND = Class.forName(NET_MINECRAFT_SERVER + ".NBTTagCompound");
            NBT_TAG_COMPOUND_HAS_KEY = NBT_TAG_COMPOUND.getDeclaredMethod("hasKey", String.class);
            NBT_TAG_COMPOUND_REMOVE = NBT_TAG_COMPOUND.getDeclaredMethod("remove", String.class);
            NBT_TAG_COMPOUND_SET_BOOLEAN = NBT_TAG_COMPOUND.getDeclaredMethod("setBoolean", String.class, boolean.class);
            ITEM_STACK = Class.forName(NET_MINECRAFT_SERVER + ".ItemStack");
            ITEM_STACK_GET_TAG = ITEM_STACK.getDeclaredMethod("getTag");
            ITEM_STACK_SET_TAG = ITEM_STACK.getDeclaredMethod("setTag", NBT_TAG_COMPOUND);
            CRAFT_ITEM_STACK = Class.forName(ORG_BUKKIT_CRAFTBUKKIT + ".inventory.CraftItemStack");
            CRAFT_ITEM_STACK_AS_BUKKIT_COPY = CRAFT_ITEM_STACK.getDeclaredMethod("asBukkitCopy", ITEM_STACK);
            CRAFT_ITEM_STACK_AS_NMS_COPY = CRAFT_ITEM_STACK.getDeclaredMethod("asNMSCopy", ItemStack.class);
            CRAFT_BLOCK_SET_DATA = Class.forName(ReflectionUtil.ORG_BUKKIT_CRAFTBUKKIT + ".block.CraftBlock").getDeclaredMethod("setData", byte.class);
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalArgumentException exception) {
            exception.printStackTrace();
        }
    }

    static Object newInstance(Class clazz) {
        try {
            return clazz.newInstance();
        } catch (IllegalAccessException | InstantiationException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    static Object invoke(Method method, Object instance, Object... args) {
        try {
            return method.invoke(instance, args);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
            exception.printStackTrace();
            return null;
        }
    }

}
