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
package io.github.dre2n.dungeonsxl.sign;

import io.github.dre2n.commons.util.FileUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel Saukel
 */
public class SignScripts {

    private List<SignScript> scripts = new ArrayList<>();

    public SignScripts(File file) {
        if (file.isDirectory()) {
            for (File script : FileUtil.getFilesForFolder(file)) {
                scripts.add(new SignScript(script));
            }
        }
    }

    /**
     * @return the script that has the name
     */
    public SignScript getByName(String name) {
        for (SignScript script : scripts) {
            if (script.getName().equalsIgnoreCase(name)) {
                return script;
            }
        }

        return null;
    }

    /**
     * @return the scripts
     */
    public List<SignScript> getScripts() {
        return scripts;
    }

    /**
     * @param script
     * the SignScript to add
     */
    public void addScript(SignScript script) {
        scripts.add(script);
    }

    /**
     * @param script
     * the SignScript to remove
     */
    public void removeScript(SignScript script) {
        scripts.remove(script);
    }

}
