/*
 * Copyright (C) 2014-2022 Daniel Saukel
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
package de.erethon.dungeonsxl.api;

import de.erethon.commons.misc.Registry;
import de.erethon.dungeonsxl.api.dungeon.GameRule;
import de.erethon.dungeonsxl.api.sign.DungeonSign;

/**
 * Class that manages initialization of several registries.
 * <p>
 * Addons should implement this interface and add their feature implementations to the registry in the respective method.
 *
 * @author Daniel Saukel
 */
public interface DungeonModule {

    /**
     * Initializes the {@link de.erethon.dungeonsxl.api.Requirement requirement} registry.
     *
     * @param requirementRegistry the registry
     */
    void initRequirements(Registry<String, Class<? extends Requirement>> requirementRegistry);

    /**
     * Initializes the {@link de.erethon.dungeonsxl.api.Reward reward} registry.
     *
     * @param rewardRegistry the registry
     */
    void initRewards(Registry<String, Class<? extends Reward>> rewardRegistry);

    /**
     * Initializes the {@link de.erethon.dungeonsxl.api.sign.DungeonSign dungeon sign} registry.
     *
     * @param signRegistry the registry
     */
    void initSigns(Registry<String, Class<? extends DungeonSign>> signRegistry);

    /**
     * Initializes the {@link de.erethon.dungeonsxl.api.dungeon.GameRule game rule} registry.
     *
     * @param gameRuleRegistry the registry
     */
    void initGameRules(Registry<String, GameRule> gameRuleRegistry);

}
