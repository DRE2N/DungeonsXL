/*
 * Copyright (C) 2016 Daniel Saukel
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

import io.github.dre2n.dungeonsxl.DungeonsXL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author Daniel Saukel
 */
public class ExternalMobProviders {

    private Set<ExternalMobProvider> providers = new HashSet<>();

    public ExternalMobProviders() {
        // Supported providers
        providers.addAll(Arrays.asList(ExternalMobPlugin.values()));

        // Custom providers
        for (Entry<String, Object> customExternalMobProvider : DungeonsXL.getInstance().getMainConfig().getExternalMobProviders().entrySet()) {
            providers.add(new CustomExternalMobProvider(customExternalMobProvider));
        }
    }

    /**
     * @param identifier
     * the identifier for ExternalMob signs
     */
    public ExternalMobProvider getByIdentifier(String identifier) {
        for (ExternalMobProvider provider : providers) {
            if (provider.getIdentifier().equals(identifier)) {
                return provider;
            }
        }

        return null;
    }

    /**
     * @return the loaded ExternalMobProviders
     */
    public Set<ExternalMobProvider> getProviders() {
        return providers;
    }

    /**
     * @param provider
     * the provider to register
     */
    public void addExternalMobProvider(ExternalMobProvider provider) {
        providers.add(provider);
    }

    /**
     * @param provider
     * the provider to unregister
     */
    public void removeExternalMobProvider(ExternalMobProvider provider) {
        providers.remove(provider);
    }

}
