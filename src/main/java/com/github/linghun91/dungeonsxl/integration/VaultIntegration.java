package com.github.linghun91.dungeonsxl.integration;

import com.github.linghun91.dungeonsxl.DungeonsXL;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultIntegration {
    private final DungeonsXL plugin;
    private Economy economy;
    private boolean enabled = false;
    
    public VaultIntegration(DungeonsXL plugin) {
        this.plugin = plugin;
    }
    
    public boolean init() {
        try {
            RegisteredServiceProvider<Economy> rsp = 
                plugin.getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp != null) {
                economy = rsp.getProvider();
                enabled = true;
                return true;
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to initialize Vault: " + e.getMessage());
        }
        return false;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public Economy getEconomy() {
        return economy;
    }
}
