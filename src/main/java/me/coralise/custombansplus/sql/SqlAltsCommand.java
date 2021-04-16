/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.coralise.custombansplus.sql;

import me.coralise.custombansplus.*;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 *
 * @author wayne
 */
public class SqlAltsCommand extends SqlAbstractCommand{

    SqlAltsCommand() {
        super("alternateaccounts", "custombansplus.ban", true);
        m = (CustomBansPlus) GetJavaPlugin.getPlugin();
    }
    
    public final CustomBansPlus m;
    CommandSender sender;
    String target;
    String out = "";
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!isValid(sender)){
            return false;
        }
        
        if(args.length != 1){
            sender.sendMessage("§e/alts <username> - Shows the player's list of alternate accounts.");
            return true;
        }

        target = SqlCache.getPlayerIgn(args[0]);

        if (target == null){
            sender.sendMessage("§ePlayer " + args[0] + " has never been on the server.");
            return true;
        }

        this.sender = sender;

        Bukkit.getScheduler().runTask(m, () -> {
            
            String targetIp = m.getSqlIp(target);
            List<String> ignList = SqlCache.getSameIps(targetIp);
            
            sender.sendMessage("§a-----");
            sender.sendMessage("§aList of §f" + target + " §aAlt Accounts at IP §f" + targetIp + "§a:");

            ignList.forEach((username) -> {
                if(SqlCache.isPlayerBanned(username)) out = out.concat("§c" + username + "§f, ");
                else if(m.getOfflinePlayer(username).isOnline()) out = out.concat("§a" + username + "§f, ");
                else out = out.concat("§7" + username + "§f, ");
            });

            sender.sendMessage(out.substring(0, out.length()-2));
            sender.sendMessage("§a-----");
            
        });
        
        return true;
        
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        return null;
        
    }
    
}
