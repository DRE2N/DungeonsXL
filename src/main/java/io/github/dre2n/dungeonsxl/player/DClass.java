/*
 * Copyright (C) 2012-2016 Frank Baumann
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
package io.github.dre2n.dungeonsxl.player;

import java.util.concurrent.CopyOnWriteArrayList;
import org.bukkit.inventory.ItemStack;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class DClass {

    private CopyOnWriteArrayList<ItemStack> items = new CopyOnWriteArrayList<>();
    private String name;
    private boolean dog;

    public DClass(String name, CopyOnWriteArrayList<ItemStack> items, boolean dog) {
        this.items = items;
        this.name = name;
        this.dog = dog;
    }

    /**
     * @return the items
     */
    public CopyOnWriteArrayList<ItemStack> getItems() {
        return items;
    }

    /**
     * @param itemStack
     * the ItemStack to add
     */
    public void setItems(ItemStack itemStack) {
        items.add(itemStack);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     * the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return if the class has a dog
     */
    public boolean hasDog() {
        return dog;
    }

    /**
     * @param dog
     * set if the class has a dog
     */
    public void setDog(boolean dog) {
        this.dog = dog;
    }

}
