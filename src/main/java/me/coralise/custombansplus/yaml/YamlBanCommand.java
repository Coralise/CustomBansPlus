/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.coralise.custombansplus.yaml;
import me.coralise.custombansplus.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author wayne
 */
public class YamlBanCommand extends YamlAbstractCommand{

    public YamlBanCommand() {
        super("cbpban", "custombansplus.ban", true);
    }
    
    public static final CustomBansPlus m = (CustomBansPlus) GetJavaPlugin.getPlugin();
    String target;
    String tgtUuid;
    String value;
    String reason;
    OfflinePlayer proTarget;
    static String type;
    CommandSender sdr;
    ConsoleCommandSender cnsl = Bukkit.getServer().getConsoleSender();
    int s;
    int stab;
    String annType;

    public String setBanned(){
        
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
        
        m.getBansConfig().set(tgtUuid + ".duration", duration);
        m.getBansConfig().set(tgtUuid + ".reason", reason);
        if(sdr instanceof Player) m.getBansConfig().set(tgtUuid + ".banned-by", m.getUuid(sdr));
        else m.getBansConfig().set(tgtUuid + ".banned-by", "CONSOLE");
        
        Date cDate = new Date();
        String bannedOn = formatter.format(cDate);
        
        m.getBansConfig().set(tgtUuid + ".banned-on", bannedOn);
        
        if(type.equalsIgnoreCase("dura")){

            String unbanDate = m.calculateUnpunishDate(value);
            m.getBansConfig().set(tgtUuid + ".unban-date", unbanDate);
            m.getBansConfig().set(tgtUuid + ".type", "Temp Ban");

        }else{
        
            m.getBansConfig().set(tgtUuid + ".unban-date", "None");
            m.getBansConfig().set(tgtUuid + ".type", "Perm Ban");
        
        }

        try {
            m.getBansConfig().save(m.getBansFile());
        } catch (IOException ex) {
            Logger.getLogger(YamlBanCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return type;
        
    }
    
    public boolean banPage(){
        
        if(proTarget.isOnline()){
            Player p = Bukkit.getPlayer(target);
            
            p.kickPlayer(YamlAbstractBanCommand.getBanMsg(target, null));
        }
        
        return true;
        
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if(!isValid(sender)){
            return false;
        }

        sdr = sender;
        
        if(args.length == 0){
            sender.sendMessage("§e/ban [-s] <player> <severity#/duration/permanent> <reason> - Bans specified player.");
            return true;
        }
        
        s = 0;
        
        if(args[0].equalsIgnoreCase("-s"))
            s = 1;
        
        if(args.length < 2+s){
            sender.sendMessage(ChatColor.RED + "Please enter a valid ban option.");
            return true;
        }
        
        target = YamlCache.getPlayerIgn(args[0+s]);
        
        if(target == null){
            sender.sendMessage("§ePlayer " + args[0+s] + " has never been on the server.");
            return false;
        }

        tgtUuid = m.getUuid(target);
        
        value = args[1+s];
        String annValue = args[1+s];
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

        proTarget = m.getOfflinePlayer(target);

        type = YamlAbstractBanCommand.getBanType(value);

        if(type == null){
            sender.sendMessage("§cEnter a valid ban option.");
            return true;
        }
        
        if(YamlCache.isPlayerBanned(tgtUuid) && !sender.hasPermission("custombansplus.overwrite")){
            sender.sendMessage(ChatColor.RED + "Player " + target + " is already banned and you don't have overwrites permission.");
            return true;
        }
        if(YamlCache.isIpBanned(m.getYamlIp(target))){
            sender.sendMessage("§cPlayer " + target + " is already IP banned. Overwrite the ban by doing /ipban.");
            return true;
        }
        
        Bukkit.getScheduler().runTask(m, () -> {
            type = setBanned();
        
            banPage();
            YamlAbstractBanCommand.addHistory(target, sender.getName(), "ban", null, null);

            YamlCache.setBan(tgtUuid);
        });

        if(annValue.equalsIgnoreCase("perm")) annValue = "Permanent";

        if(annValue.equalsIgnoreCase("Permanent")) annType = "perm" + annType;
        else annType = "temp" + annType;

        if (s == 0) YamlAbstractAnnouncer.getAnnouncer(target, sdr.getName(), annValue, reason, annType);
        else YamlAbstractAnnouncer.getSilentAnnouncer(target, sdr.getName(), annValue, reason, annType);
        
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
            
        stab = 0;
        if(args[0].equalsIgnoreCase("-s"))
            stab++;

        if(args.length == 2+stab){

            m.getSevConfig().getKeys(false).forEach(p -> {tabComplete.add("s"+p);});
            
            tabComplete.add("perm");
            tabComplete.add("Xs");
            tabComplete.add("Xm");
            tabComplete.add("Xh");
            tabComplete.add("Xd");
            return tabComplete;

        }

        if(args.length == 3+stab){

            tabComplete.add("<reason>");
            return tabComplete;

        }

        return null;
        
    }
    
}
