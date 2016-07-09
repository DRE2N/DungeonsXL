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
package io.github.dre2n.dungeonsxl.mob;

import io.github.dre2n.commons.util.FileUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel Saukel
 */
public class DMobTypes {

    private List<DMobType> dMobTypes = new ArrayList<>();

    public DMobTypes(File file) {
        if (file.isDirectory()) {
            for (File script : FileUtil.getFilesForFolder(file)) {
                dMobTypes.add(new DMobType(script));
            }
        }
    }

    /**
     * @return the dMobType that has the name
     */
    public DMobType getByName(String name) {
        for (DMobType dMobType : dMobTypes) {
            if (dMobType.getName().equalsIgnoreCase(name)) {
                return dMobType;
            }
        }

        return null;
    }

    /**
     * @return the dMobTypes
     */
    public List<DMobType> getDMobTypes() {
        return dMobTypes;
    }

    /**
     * @param dMobType
     * the DMobType to add
     */
    public void addDMobType(DMobType dMobType) {
        dMobTypes.add(dMobType);
    }

    /**
     * @param dMobType
     * the DMobType to remove
     */
    public void removeDMobType(DMobType dMobType) {
        dMobTypes.remove(dMobType);
    }

}
