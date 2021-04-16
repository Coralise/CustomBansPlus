/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.coralise.custombansplus.yaml;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author coralise
 */
public abstract class YamlAbstractCommand implements CommandExecutor, TabCompleter{
    
    public static JavaPlugin plugin;
    public final String permission;
    public final boolean console;
    public final String com;
    
    YamlAbstractCommand(String cmd, String p, boolean c){
        
        permission = p;
        console = c;
        com = cmd;
        
        plugin.getCommand(com).setExecutor(this);
        plugin.getCommand(com).setTabCompleter(this);
        
    }
    
    public boolean isValid(CommandSender sender){
        
        if(!(sender instanceof Player) && !console){
            sender.sendMessage("§cConsole cannot use this command.");
            return false;
        }
        
        if(sender instanceof Player && permission != null && !sender.hasPermission(permission)){
            sender.sendMessage("§cYou do not have permission to use this command.");
            return false;
        }
        
        return true;
        
    }
    
    public static void registerCommands(JavaPlugin p){
        
        plugin = p;
        
        new YamlSevCommand();
        new YamlCBCommand();
        new YamlAltsCommand();
        new YamlBanCommand();
        new YamlUnbanCommand();
        new YamlHistoryCommand();
        new YamlKickCommand();
        new YamlIPBanCommand();
        new YamlMuteCommand();
        new YamlUnmuteCommand();
        new YamlWarnCommand();
        
    }
    
}
