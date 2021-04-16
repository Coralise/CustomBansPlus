/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.coralise.custombansplus.yaml;
import me.coralise.custombansplus.*;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 *
 * @author wayne
 */
public class YamlAltsCommand extends YamlAbstractCommand{

    YamlAltsCommand() {
        super("alternateaccounts", "custombansplus.ban", true);
        m = (CustomBansPlus) GetJavaPlugin.getPlugin();
    }
    
    public final CustomBansPlus m;
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!isValid(sender)){
            return false;
        }
        
        if(args.length != 1){
            sender.sendMessage("§e/alts <username> - Shows the player's list of alternate accounts.");
            return true;
        }
        
        String target = YamlCache.getPlayerIgn(args[0]);
        
        if(target == null){
            sender.sendMessage("§cPlayer " + args[0] + " has never entered the server.");
            return true;
        }

        String targetIp = m.getYamlIp(target);
        
        if(targetIp == null){
            sender.sendMessage("§cPlayer " + target + " has never entered the server prior to the recent purge.");
            return true;
        }
        
        String ignList = "";
        
        sender.sendMessage("§a-----");
        sender.sendMessage("§aList of §f" + target + "'s §aAlt Accounts:");
        ignList = YamlCache.listAlts(targetIp);
        sender.sendMessage(ignList);
        sender.sendMessage("§a-----");
        
        return true;
        
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        
        List<String> tabComplete = new ArrayList<String>();

        tabComplete.add("placeholder");

        return null;
        
    }
    
}
