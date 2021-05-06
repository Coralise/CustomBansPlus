/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.coralise.custombansplus.sql;
import me.coralise.custombansplus.*;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

/**
 *
 * @author Coralise
 */
public class SqlUnbanCommand extends SqlAbstractCommand {

    public SqlUnbanCommand() {
        super("cbpunban", "custombansplus.unban", true);
    }
    
    public final CustomBansPlus m = (CustomBansPlus) ClassGetter.getPlugin();
    ConsoleCommandSender cnsl = Bukkit.getServer().getConsoleSender();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!isValid(sender)){
            return false;
        }
        
        if(args.length == 0){
            sender.sendMessage("§e/unban <player> - Unbans specified player.");
            return true;
        }

        int s = 0;
        
        if(args[0].equalsIgnoreCase("-s"))
            s = 1;
        
        target = SqlCache.getPlayerIgn(args[0+s]);
        this.sender = sender;

        if (target == null){
            sender.sendMessage("§ePlayer " + args[0+s] + " has never been on the server.");
            return true;
        }
        UUID tgtUuid = m.getUuid(target);
        
        if(!SqlCache.isPlayerBanned(tgtUuid)){
            sender.sendMessage("§cPlayer " + target + " is not banned.");
            return true;
        }
        
        Bukkit.getScheduler().runTask(m, () -> {

            try {
                SqlMethods.updateHistoryStatus(tgtUuid, "Ban", "Unbanned", sender);
            } catch (SQLException e) {
                e.printStackTrace();
            }
    
            SqlCache.removeBan(tgtUuid, "Unbanned", sender);
            SqlCache.getOciCache().remove(m.getUuid(target));
            m.updateSqlOci();

        });

        if (s == 0) SqlAbstractAnnouncer.getAnnouncer(target, sender.getName(), null, null, "unban");
        else SqlAbstractAnnouncer.getSilentAnnouncer(target, sender.getName(), null, null, "unban");
        
        return true;
        
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        return null;
        
    }

    String target;
    CommandSender sender;
    
}
