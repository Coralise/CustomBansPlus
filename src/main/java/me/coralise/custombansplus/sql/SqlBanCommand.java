/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.coralise.custombansplus.sql;

import me.coralise.custombansplus.*;
import me.coralise.custombansplus.enums.BanType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author wayne
 */
public class SqlBanCommand extends SqlAbstractCommand{

    public SqlBanCommand() {
        super("cbpban", "custombansplus.ban", true);
    }
    
    public static final CustomBansPlus m = (CustomBansPlus) ClassGetter.getPlugin();
    
    public void setBanned(CommandSender sender, UUID tgtUuid, BanType banType, String reason, String duration){
        
        if (sender instanceof Player)
            SqlCache.setBan(tgtUuid, SqlCache.playerCache.get(tgtUuid).getIp(), banType, reason, duration, m.getUuid(sender));
        else
            SqlCache.setBan(tgtUuid, SqlCache.playerCache.get(tgtUuid).getIp(), banType, reason, duration);
        
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!isValid(sender)){
            return false;
        }
        
        if(args.length == 0){
            sender.sendMessage("§e/ban [-s] <player> <snum/duration/permanent> <reason> - Bans specified player.");
            return false;
        }
        
        int s = 0;
        
        if(args[0].equalsIgnoreCase("-s"))
            s = 1;
        
        if(args.length < 2+s){
            sender.sendMessage(ChatColor.RED + "Please enter a valid ban option.");
            return false;
        }
        
        String target = SqlCache.getPlayerIgn(args[0+s]);

        if (target == null){
            sender.sendMessage(ChatColor.YELLOW + "Player " + args[0+s] + " has never been on the server.");
            return false;
        }

        UUID tgtUuid = m.getUuid(target);

        String reason = "";
        if(args.length > 2+s){
            reason = args[2+s];
            for(int x = 3+s; x < args.length;x++){
                reason = reason.concat(" " + args[x]);
            }
        }
        if(reason.equalsIgnoreCase("") && !m.getConfig().getBoolean("toggle-no-reason"))
            reason = m.parseMessage(m.getConfig().getString("defaults.reason"));

        String annType;
        if(reason.equalsIgnoreCase(""))
            annType = "banNoRsn";
        else
            annType = "ban";

        if(SqlCache.isIpBanned(m.getSqlIp(tgtUuid))){
            sender.sendMessage("§cPlayer " + target + " is already IP Banned. Use /ipban instead to overwrite.");
            return false;
        }
        if(SqlCache.isPlayerBanned(tgtUuid) && !sender.hasPermission("custombansplus.overwrite")){
            sender.sendMessage("§cPlayer " + target + " is already banned and you don't have overwrites permission.");
            return false;
        }

        String value = args[1+s];
        if(m.getType(value) == null){
            sender.sendMessage("§cEnter a valid ban option.");
            return true;
        }
        String duration = m.getSevDuration(value);
        BanType banType = m.getBanType(value);

        int fs = s;
        if (duration.equalsIgnoreCase("perm")) annType = "perm" + annType;
        else annType = "temp" + annType;
        String fAnnType = annType;
        String fReason = reason;

        new Thread(() -> {
            try {
                SqlMethods.updateHistoryStatus(tgtUuid, "Ban", "Overwritten", sender);
                m.checkSevValues(tgtUuid, value);
                setBanned(sender, tgtUuid, banType, fReason, duration);;

                SqlAbstractBanCommand.banPage(tgtUuid);
                SqlMethods.addHistory(tgtUuid, "Ban", null, null);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (fs == 0) SqlAbstractAnnouncer.getAnnouncer(target, sender.getName(), args[1+fs], fReason, fAnnType);
            else SqlAbstractAnnouncer.getSilentAnnouncer(target, sender.getName(), args[1+fs], fReason, fAnnType);
        }).start();
        
        return true;
        
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // /ban Coralise 30d Xray
        List<String> tabComplete = new ArrayList<String>();

        if(args.length == 1){
            if(!args[0].contains("-") && args[0].length() != 0) return null;
            tabComplete.add("-s");
            Bukkit.getOnlinePlayers().forEach(p -> {tabComplete.add(p.getName());});
            return tabComplete;
        }
            
        int s = 0;
        if(args[0].equalsIgnoreCase("-s"))
            s++;

        if(args.length == 2+s){

            m.getSevConfig().getKeys(false).forEach(p -> {tabComplete.add("s"+p);});
            
            tabComplete.add("perm");
            tabComplete.add("Xs");
            tabComplete.add("Xm");
            tabComplete.add("Xh");
            tabComplete.add("Xd");
            return tabComplete;

        }

        if(args.length == 3+s){

            tabComplete.add("<reason>");
            return tabComplete;

        }

        return null;
        
    }
    
}
