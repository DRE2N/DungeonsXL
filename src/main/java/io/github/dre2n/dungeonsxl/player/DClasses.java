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

import io.github.dre2n.commons.util.FileUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel Saukel
 */
public class DClasses {

    private List<DClass> dClasses = new ArrayList<>();

    public DClasses(File file) {
        if (file.isDirectory()) {
            for (File script : FileUtil.getFilesForFolder(file)) {
                dClasses.add(new DClass(script));
            }
        }
    }

    /**
     * @return the dClass that has the name
     */
    public DClass getByName(String name) {
        for (DClass dClass : dClasses) {
            if (dClass.getName().equalsIgnoreCase(name)) {
                return dClass;
            }
        }

        return null;
    }

    /**
     * @return the dClasses
     */
    public List<DClass> getDClasses() {
        return dClasses;
    }

    /**
     * @param dClass
     * the DClass to add
     */
    public void addDClass(DClass dClass) {
        dClasses.add(dClass);
    }

    /**
     * @param dClass
     * the DClass to remove
     */
    public void removeDClass(DClass dClass) {
        dClasses.remove(dClass);
    }

}
