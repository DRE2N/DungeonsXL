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
package de.erethon.dungeonsxl.player;

import de.erethon.caliburn.CaliburnAPI;
import de.erethon.dungeonsxl.DungeonsXL;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a class and a class script.
 *
 * @author Frank Baumann, Daniel Saukel
 */
public class DClass {

    private CaliburnAPI caliburn;

    private String name;

    private List<ItemStack> items = new ArrayList<>();
    private boolean dog;

    public DClass(DungeonsXL plugin, File file) {
        this(plugin, file.getName().substring(0, file.getName().length() - 4), YamlConfiguration.loadConfiguration(file));
    }

    public DClass(DungeonsXL plugin, String name, FileConfiguration config) {
        caliburn = plugin.getCaliburn();

        this.name = name;

        if (config.contains("items")) {
            items = caliburn.deserializeStackList(config, "items");
        }

        if (config.contains("dog")) {
            dog = config.getBoolean("dog");
        }
    }

    public DClass(String name, List<ItemStack> items, boolean dog) {
        this.items = items;
        this.name = name;
        this.dog = dog;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the items
     */
    public List<ItemStack> getItems() {
        return items;
    }

    /**
     * @param itemStack the ItemStack to add
     */
    public void addItem(ItemStack itemStack) {
        items.add(itemStack);
    }

    /**
     * @param itemStack the ItemStack to remove
     */
    public void removeItem(ItemStack itemStack) {
        items.remove(itemStack);
    }

    /**
     * @return if the class has a dog
     */
    public boolean hasDog() {
        return dog;
    }

    /**
     * @param dog set if the class has a dog
     */
    public void setDog(boolean dog) {
        this.dog = dog;
    }

}
