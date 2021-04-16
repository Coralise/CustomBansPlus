/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.coralise.custombansplus.sql;

import me.coralise.custombansplus.*;

import java.util.ArrayList;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

/**
 *
 * @author wayne
 */
public class SqlBanCommand extends SqlAbstractCommand{

    public SqlBanCommand() {
        super("cbpban", "custombansplus.ban", true);
    }
    
    public static final CustomBansPlus m = (CustomBansPlus) GetJavaPlugin.getPlugin();
    String target;
    String value;
    String reason;
    static String type;
    CommandSender sender;
    String[] args;
    ConsoleCommandSender cnsl = Bukkit.getServer().getConsoleSender();
    String annType;
    
    public void setBanned(){
        
        String duration = "";
        boolean clearInv = false;
        List<String> cmds = new ArrayList<String>();
        Double balDeduct = 0.0;
        
        switch(type){
            case "sev":
                int sevNum = Integer.parseInt(value.substring(1));
                clearInv = m.getSevConfig().getBoolean(sevNum+".clear-inv");
                cmds = m.getSevConfig().getStringList(sevNum+".console-commands");
                balDeduct = m.getSevConfig().getDouble(sevNum+".baldeduct");
                if(m.getSevConfig().getString(sevNum + ".duration").equalsIgnoreCase("Permanent")){
                    type = "perm";
                    duration = "Permanent";
                    break;
                }
                type = "dura";
                value = m.getSevConfig().getString(sevNum + ".duration");
                duration = value;
                break;
            case "perm":
                duration = "Permanent";
                break;
            case "dura":
                duration = value;
                break;
        }

        m.checkSevValues(target, clearInv, balDeduct, cmds);

        String targetUuid = m.getUuid(target);
        
        SqlMethods.setBan(targetUuid, sender, reason, duration);
        
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        this.sender = sender;
        this.args = args;

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
        
        target = SqlCache.getPlayerIgn(args[0+s]);

        if (target == null){
            sender.sendMessage(ChatColor.YELLOW + "Player " + args[0+s] + " has never been on the server.");
            return false;
        }
        
        value = args[1+s];

        reason = "";
        if(args.length > 2+s){
            reason = args[2+s];
            for(int x = 3+s; x < args.length;x++){
                reason = reason.concat(" " + args[x]);
            }
        }
        if(reason.equalsIgnoreCase("") && !m.getConfig().getBoolean("toggle-no-reason"))
            reason = m.getConfig().getString("default-reason");

        if(reason.equalsIgnoreCase(""))
            annType = "banNoRsn";
        else
            annType = "ban";
        
        type = SqlAbstractBanCommand.getBanType(value);

        if(type == null){
            sender.sendMessage("§cEnter a valid ban option.");
            return true;
        }

        if(SqlCache.isIpBanned(m.getSqlIp(target))){
            sender.sendMessage("§cPlayer " + target + " is already IP Banned. Use /ipban instead to overwrite.");
            return false;
        }
        if(SqlCache.isPlayerBanned(target) && !sender.hasPermission("custombansplus.overwrite")){
            sender.sendMessage("§cPlayer " + target + " is already banned and you don't have overwrites permission.");
            return false;
        }

        Bukkit.getScheduler().runTask(m, () -> {
            
            SqlMethods.updateHistoryStatus(target, "Ban", "Overwritten", sender);
      
            setBanned();
            SqlAbstractBanCommand.banPage(target);

            SqlMethods.addHistory(target, "Ban", null, null);

            if(value.equalsIgnoreCase("perm")) value = "Permanent";
            
        });

        if (s == 0) SqlAbstractAnnouncer.getAnnouncer(target, sender.getName(), args[1+s], reason, annType);
        else SqlAbstractAnnouncer.getSilentAnnouncer(target, sender.getName(), args[1+s], reason, annType);
        
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
