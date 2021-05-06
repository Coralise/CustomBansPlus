/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.coralise.custombansplus.sql;

import me.coralise.custombansplus.*;

import java.util.List;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 *
 * @author wayne
 */
public class SqlAltsCommand extends SqlAbstractCommand{

    SqlAltsCommand() {
        super("alternateaccounts", "custombansplus.ban", true);
    }
    
    public final CustomBansPlus m = (CustomBansPlus) ClassGetter.getPlugin();
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!isValid(sender)){
            return false;
        }
        
        if(args.length != 1){
            sender.sendMessage("§e/alts <username> - Shows the player's list of alternate accounts.");
            return true;
        }

        String target = SqlCache.getPlayerIgn(args[0]);

        if (target == null){
            sender.sendMessage("§ePlayer " + args[0] + " has never been on the server.");
            return true;
        }

        UUID tgtUuid = m.getUuid(target);

        new Thread(() -> {
            String targetIp = m.getSqlIp(tgtUuid);
            List<UUID> ignList = SqlCache.getSameIps(targetIp);
            
            sender.sendMessage("§a-----");
            sender.sendMessage("§aList of §f" + target + " §aAlt Accounts at IP §f" + targetIp + "§a:");

            String out = "";
            for (UUID uuid : ignList) {
                if (SqlCache.isPlayerBanned(uuid)) out = out.concat("§c" + SqlCache.getPlayerObject(uuid).getUsername() + "§f, ");
                else if(m.getOfflinePlayer(uuid).isOnline()) out = out.concat("§a" + SqlCache.getPlayerObject(uuid).getUsername() + "§f, ");
                else out = out.concat("§7" + SqlCache.getPlayerObject(uuid).getUsername() + "§f, ");
            }
            
            sender.sendMessage(out.substring(0, out.length()-2));
            sender.sendMessage("§a-----");

        }).start();
        
        return true;
        
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        return null;
        
    }
    
}
