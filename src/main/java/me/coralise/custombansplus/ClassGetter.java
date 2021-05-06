/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.coralise.custombansplus;

import org.bukkit.plugin.Plugin;

import me.coralise.custombansplus.yaml.YamlReportCommand;
import me.coralise.custombansplus.yaml.YamlReportsCommand;

/**
 *
 * @author wayne
 */
public abstract class ClassGetter {
    
    private static CustomBansPlus plugin;
    private static YamlReportsCommand yrc;
    private static YamlReportCommand yrc2;
    
    public static void setPlugin (CustomBansPlus p) {
        plugin = p;
    }

    public static void setYamlReportsCommand (YamlReportsCommand yrc) {
        ClassGetter.yrc = yrc;
    }

    public static void setYamlReportCommand (YamlReportCommand yrc2) {
        ClassGetter.yrc2 = yrc2;
    }
    
    public static Plugin getPlugin() {
        return plugin;
    }

    public static YamlReportsCommand getYamlReportsCommand() {
        return yrc;
    }

    public static YamlReportCommand getYamlReportCommand() {
        return yrc2;
    }
    
}
