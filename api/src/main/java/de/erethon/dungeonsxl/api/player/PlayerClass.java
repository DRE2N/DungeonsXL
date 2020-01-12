/*
 * Copyright (C) 2014-2020 Daniel Saukel
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
package de.erethon.dungeonsxl.api.player;

import de.erethon.caliburn.CaliburnAPI;
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
public class PlayerClass {

    private String name;

    private List<ItemStack> items = new ArrayList<>();
    private boolean dog;

    /**
     * Creates a PlayerClass from a class YAML file. The name is taken from the file name.
     *
     * @param caliburn the CaliburnAPI instance
     * @param file     the class config file
     */
    public PlayerClass(CaliburnAPI caliburn, File file) {
        this(caliburn, file.getName().substring(0, file.getName().length() - 4), YamlConfiguration.loadConfiguration(file));
    }

    /**
     * Creates a PlayerClass from the given class config.
     *
     * @param caliburn the CaliburnAPI instance
     * @param name     the class name
     * @param config   the config
     */
    public PlayerClass(CaliburnAPI caliburn, String name, FileConfiguration config) {
        this.name = name;

        if (config.contains("items")) {
            items = caliburn.deserializeStackList(config, "items");
        }

        if (config.contains("dog")) {
            dog = config.getBoolean("dog");
        }
    }

    public PlayerClass(String name, List<ItemStack> items, boolean dog) {
        this.items = items;
        this.name = name;
        this.dog = dog;
    }

    /**
     * Returns the name of the class.
     *
     * @return the name of the class
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the list of the items this class gives to a player.
     *
     * @return the list of the the items this class gives to a player
     */
    public List<ItemStack> getItems() {
        return items;
    }

    /**
     * Adds the given item to this class.
     *
     * @param itemStack the ItemStack to add
     */
    public void addItem(ItemStack itemStack) {
        items.add(itemStack);
    }

    /**
     * Removes the given item from this class.
     *
     * @param itemStack the ItemStack to remove
     */
    public void removeItem(ItemStack itemStack) {
        items.remove(itemStack);
    }

    /**
     * Returns if the class gives the player a dog.
     *
     * @return if the class has a dog
     * @deprecated More dynamic pet features might make this obsolete in the future.
     */
    @Deprecated
    public boolean hasDog() {
        return dog;
    }

    /**
     * Sets if the class gives the player a dog.
     *
     * @param dog if the class shall give the player a dog
     * @deprecated More dynamic pet features might make this obsolete in the future.
     */
    @Deprecated
    public void setDog(boolean dog) {
        this.dog = dog;
    }

}
