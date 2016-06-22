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
package io.github.dre2n.dungeonsxl.world;

import io.github.dre2n.commons.util.NumberUtil;
import java.io.File;
import java.util.Set;

/**
 * @author Daniel Saukel
 */
public class Worlds {

    private Set<ResourceWorld> resources;
    private Set<InstanceWorld> instances;

    public Worlds(File folder) {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                resources.add(new ResourceWorld(file));
            }
        }
    }

    /* Getters and setters */
    /**
     * @return the ResourceWorld that has this name
     */
    public ResourceWorld getResourceByName(String name) {
        for (ResourceWorld world : resources) {
            if (world.getName().equals(name)) {
                return world;
            }
        }

        return null;
    }

    /**
     * @return the InstanceWorld that has this name
     */
    public InstanceWorld getInstanceByName(String name) {
        String[] splitted = name.split("_");
        if (splitted.length != 3) {
            return null;
        }

        return getInstanceById(NumberUtil.parseInt(splitted[2], -1));
    }

    /**
     * @return the InstanceWorld that has this ID
     */
    public InstanceWorld getInstanceById(int id) {
        for (InstanceWorld world : instances) {
            if (world.getId() == id) {
                return world;
            }
        }

        return null;
    }

    /**
     * @return the ResourceWorlds in the maps folder
     */
    public Set<ResourceWorld> getResources() {
        return resources;
    }

    /**
     * @return the loaded InstanceWorlds in the world container
     */
    public Set<InstanceWorld> getInstances() {
        return instances;
    }

}
