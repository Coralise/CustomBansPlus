package me.coralise.custombansplus.sql;
import me.coralise.custombansplus.*;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class SqlIPBanCommand extends SqlAbstractCommand {

    SqlIPBanCommand() {
        super("cbpipban", "custombansplus.ban", true);
    }

    public final CustomBansPlus m = (CustomBansPlus) GetJavaPlugin.getPlugin();
    String target;
    String value;
    String reason;
    String targetIp;
    static String type;
    CommandSender sender;
    ConsoleCommandSender cnsl = Bukkit.getServer().getConsoleSender();
    String valueOption;
    String annType;

    public String setIpBan(){
        
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

        String bannerUuid = "CONSOLE";
        if(sender instanceof Player) bannerUuid = m.getUuid(sender);

        SqlMethods.setIpBan(targetIp, bannerUuid, reason, duration);
        
        return type;
        
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!isValid(sender)){
            return false;
        }

        this.sender = sender;
        type = null;
        
        if(args.length == 0){
            sender.sendMessage("§e/ipban [-s] <player> <snum/duration/permanent> <reason> - Bans specified player.");
            return true;
        }
        
        int s = 0;
            
            if(args[0].equalsIgnoreCase("-s"))
                s = 1;
            
            if(args.length < 2+s){
                sender.sendMessage("§cPlease enter a valid ban option.");
                return true;
            }
            
            target = SqlCache.getPlayerIgn(args[0+s]);

            if (target == null){
                sender.sendMessage("§ePlayer " + args[0+s] + " has never been on the server.");
                return true;
            }
            
            value = args[1+s];
            valueOption = args[1+s];

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

            targetIp = m.getSqlIp(target);
            
            type = SqlAbstractBanCommand.getBanType(value);

            if(type == null){
                sender.sendMessage("§cEnter a valid ban option.");
                return true;
            }

            if(SqlCache.isIpBanned(targetIp) && !sender.hasPermission("custombansplus.overwrite")){
                sender.sendMessage("§cIP is already banned and you don't have overwrites permission.");
                return true;
            }

            Bukkit.getScheduler().runTask(m, () -> {
         
                setIpBan();
                
                SqlCache.getSameIps(targetIp).forEach(p -> {
                    SqlMethods.updateHistoryStatus(p, "Ban", "Overwritten", sender);
                    m.checkSevValues(p, valueOption);
                    SqlMethods.banPlayer(p);
                    SqlMethods.addHistory(p, "Ban", null, null);
                    SqlAbstractBanCommand.banPage(p);
                });
            
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
