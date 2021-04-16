/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.coralise.custombansplus;

import org.bukkit.plugin.Plugin;

/**
 *
 * @author wayne
 */
public class GetJavaPlugin {
    
    public static CustomBansPlus plugin;
    
    public static void setPlugin(CustomBansPlus p){
        
        plugin = p;
        
    }
    
    public static Plugin getPlugin(){
        return plugin;
    }
    
}
