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

import de.erethon.commons.misc.ReflectionUtil;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.bukkit.block.Block;

/**
 * @author Daniel Saukel
 */
@Deprecated
public class MagicValueUtil {

    private static Method CRAFT_BLOCK_SET_DATA;

    static {
        try {
            CRAFT_BLOCK_SET_DATA = Class.forName(ReflectionUtil.ORG_BUKKIT_CRAFTBUKKIT + ".block.CraftBlock").getDeclaredMethod("setData", byte.class);
        } catch (NoSuchMethodException | SecurityException | ClassNotFoundException exception) {
        }
    }

    public static void setBlockData(Block block, byte data) {
        try {
            CRAFT_BLOCK_SET_DATA.invoke(block, data);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
        }
    }

}
