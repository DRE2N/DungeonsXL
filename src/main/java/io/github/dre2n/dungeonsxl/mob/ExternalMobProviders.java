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

import io.github.dre2n.commons.util.messageutil.MessageUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import org.bukkit.Bukkit;

/**
 * @author Daniel Saukel
 */
public class ExternalMobProviders {

    private Set<ExternalMobProvider> providers = new HashSet<>();

    public ExternalMobProviders() {
        // Supported providers
        providers.addAll(Arrays.asList(ExternalMobPlugin.values()));
        if (Bukkit.getPluginManager().getPlugin("Citizens") != null) {
            providers.add(new CitizensMobProvider());
        } else {
            MessageUtil.log("Could not find compatible Citizens plugin. The mob provider Citizens (\"CI\") will not get enabled...");
        }

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
     * @return the Citizens provider
     */
    public CitizensMobProvider getCitizensMobProvider() {
        for (ExternalMobProvider provider : providers) {
            if (provider instanceof CitizensMobProvider) {
                return (CitizensMobProvider) provider;
            }
        }

        return null;
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
