package me.coralise.custombansplus.yaml;
import me.coralise.custombansplus.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class YamlIPBanCommand extends YamlAbstractCommand {

    YamlIPBanCommand() {
        super("cbpipban", "custombansplus.ban", true);
    }

    public static final CustomBansPlus m = (CustomBansPlus) GetJavaPlugin.getPlugin();
    String target;
    String value;
    String reason;
    String targetIP;
    static String type;
    CommandSender sdr;
    ConsoleCommandSender cnsl = Bukkit.getServer().getConsoleSender();
    String annType;

    int s;

    public boolean isBanned(String target){

        return m.getBansConfig().getKeys(false).contains(target);

    }

    public boolean banPage(){
        
        Bukkit.getOnlinePlayers().stream()
            .filter(p -> m.getYamlIp(p.getName()).equalsIgnoreCase(targetIP))
            .forEach(p -> p.kickPlayer(YamlAbstractBanCommand.getBanMsg(p.getName(), null)));
        
        return true;
        
    }

    public String setIpBanned(){
        
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String duration = "";
        
        switch(type){
            case "sev":
                int sevNum = Integer.parseInt(value.substring(1));
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

        String bannedOn = formatter.format(new Date());
        
        m.getBansConfig().set(targetIP + ".duration", duration);
        m.getBansConfig().set(targetIP + ".reason", reason);
        if(sdr instanceof Player) m.getBansConfig().set(targetIP + ".banned-by", m.getUuid(sdr));
        else m.getBansConfig().set(targetIP + ".banned-by", "CONSOLE");
        m.getBansConfig().set(targetIP + ".banned-on", bannedOn);
        
        //DURA

        if(type.equalsIgnoreCase("dura")){
            
            String unbanDate = m.calculateUnpunishDate(value);
            m.getBansConfig().set(targetIP + ".unban-date", unbanDate);
            
            try {
                m.getBansConfig().save(m.getBansFile());
            } catch (IOException ex) {
                //none
            }

            YamlCache.setBan(targetIP);
            
            return type;
        }
        
        //PERM

        m.getBansConfig().set(targetIP + ".unban-date", "None");
        
        try {
            m.getBansConfig().save(m.getBansFile());
        } catch (IOException ex) {
            //none
        }

        YamlCache.setBan(targetIP);
        
        return type;
        
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!isValid(sender)){
            return false;
        }

        sdr = sender;
        
        if(args.length == 0){
            sender.sendMessage("§e/ipban [-s] <player> <snum/duration/permanent> <reason> - Bans specified player.");
            return true;
        }
        
        s = 0;
            
            if(args[0].equalsIgnoreCase("-s"))
                s = 1;
            
            if(args.length < 2+s){
                sender.sendMessage("§cPlease enter a valid ban option.");
                return true;
            }
            
            target = YamlCache.getPlayerIgn(args[0+s]);
            
            if(target == null){
                sender.sendMessage("§ePlayer " + args[0+s] + " has never been on the server.");
                return false;
            }
            
            String tgtUuid = m.getUuid(target);
            
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
                annType = "ipbanNoRsn";
            else
                annType = "ipban";

            targetIP = m.getYamlIp(target);

            type = YamlAbstractBanCommand.getBanType(value);

            if(type == null){
                sender.sendMessage("§cEnter a valid ban option.");
                return true;
            }

            if(YamlCache.isPlayerBanned(tgtUuid) && !sender.hasPermission("custombansplus.overwrite")){
                sender.sendMessage("§cPlayer " + target + " is already banned and you don't have overwrites permission.");
                return true;
            }
            
            Bukkit.getScheduler().runTask(m, () -> {
                type = setIpBanned();
            
                YamlCache.getSameIps(targetIP).forEach(p -> YamlAbstractBanCommand.banPlayer(p, targetIP, args[1+s]));
            });
        
            if(annValue.equalsIgnoreCase("perm")) annValue = "Permanent";

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
            
        int stab = 0;
        if(args[0].equalsIgnoreCase("-s"))
            s++;

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
