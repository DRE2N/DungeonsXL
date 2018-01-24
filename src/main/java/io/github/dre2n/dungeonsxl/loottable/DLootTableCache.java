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
package io.github.dre2n.dungeonsxl.loottable;

import io.github.dre2n.commons.misc.FileUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * DLootTable instance manager.
 *
 * @author Daniel Saukel
 */
public class DLootTableCache {

    private List<DLootTable> lootTables = new ArrayList<>();

    public DLootTableCache(File file) {
        if (file.isDirectory()) {
            for (File script : FileUtil.getFilesForFolder(file)) {
                lootTables.add(new DLootTable(script));
            }
        }
    }

    /**
     * @return the loot table that has the name
     */
    public DLootTable getByName(String name) {
        for (DLootTable lootTable : lootTables) {
            if (lootTable.getName().equalsIgnoreCase(name)) {
                return lootTable;
            }
        }

        return null;
    }

    /**
     * @return the loot tables
     */
    public List<DLootTable> getDLootTables() {
        return lootTables;
    }

    /**
     * @param lootTable
     * the DLootTable to add
     */
    public void addDLootTable(DLootTable lootTable) {
        lootTables.add(lootTable);
    }

    /**
     * @param lootTable
     * the DLootTable to remove
     */
    public void removeDLootTable(DLootTable lootTable) {
        lootTables.remove(lootTable);
    }

}
