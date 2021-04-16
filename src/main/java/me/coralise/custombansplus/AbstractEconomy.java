package me.coralise.custombansplus;

import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;

public abstract class AbstractEconomy {

    private static Economy econ = null;
    
    public static Economy getEconomy() {
        return econ;
    }

    static CustomBansPlus m = (CustomBansPlus) GetJavaPlugin.getPlugin();

    public static boolean setupEconomy() {
        if (m.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = m.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

}
