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
package de.erethon.dungeonsxl.requirement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * RequirementType instance manager.
 *
 * @author Daniel Saukel
 */
public class RequirementTypeCache {

    private List<RequirementType> types = new ArrayList<>();

    public RequirementTypeCache() {
        types.addAll(Arrays.asList(RequirementTypeDefault.values()));
    }

    /**
     * @param identifier the identifier to check
     * @return the requirement type which has the identifier
     */
    public RequirementType getByIdentifier(String identifier) {
        for (RequirementType type : types) {
            if (type.getIdentifier().equals(identifier)) {
                return type;
            }
        }

        return null;
    }

    /**
     * @return the requirement types
     */
    public List<RequirementType> getRequirements() {
        return types;
    }

    /**
     * @param type the requirement type to add
     */
    public void addRequirement(RequirementType type) {
        types.add(type);
    }

    /**
     * @param type the requirement type to remove
     */
    public void removeRequirement(RequirementType type) {
        types.remove(type);
    }

}
