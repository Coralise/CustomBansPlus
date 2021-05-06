package me.coralise.custombansplus.sql;
import me.coralise.custombansplus.*;
import me.coralise.custombansplus.enums.BanType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SqlIPBanCommand extends SqlAbstractCommand {

    SqlIPBanCommand() {
        super("cbpipban", "custombansplus.ban", true);
    }

    public final CustomBansPlus m = (CustomBansPlus) ClassGetter.getPlugin();
    String target;
    UUID tgtUuid;
    String value;
    String duration;
    String reason;
    String targetIp;
    BanType banType;
    CommandSender sender;
    String annType;

    public void setIpBan(){

        banType = m.getBanTypeIP(value);
        SqlCache.getSameIps(targetIp).forEach(uuid -> m.checkSevValues(uuid, value));

        if (sender instanceof Player)
            SqlCache.setBan(targetIp, banType, reason, duration, m.getUuid(sender));
        else 
            SqlCache.setBan(targetIp, banType, reason, duration);
        
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!isValid(sender)){
            return false;
        }

        this.sender = sender;
        
        if(args.length == 0){
            sender.sendMessage("§e/ipban [-s] <player> <snum/duration/permanent> <reason> - Bans specified player.");
            return true;
        }
        
        int s = 0;
            
        if(args[0].equalsIgnoreCase("-s"))
            s = 1;
        
        target = SqlCache.getPlayerIgn(args[0+s]);

        if (target == null){
            sender.sendMessage("§ePlayer " + args[0+s] + " has never been on the server.");
            return true;
        }

        tgtUuid = m.getUuid(target);
        targetIp = m.getSqlIp(tgtUuid);
        
        value = args[1+s];

        if (m.getType(value) == null) {
            sender.sendMessage("§cPlease enter a valid ban option.");
            return true;
        }
        duration = m.getSevDuration(value);
        banType = m.getBanTypeIP(value);

        reason = "";
        if(args.length > 2+s){
            reason = args[2+s];
            for(int x = 3+s; x < args.length;x++){
                reason = reason.concat(" " + args[x]);
            }
        }

        if(reason.equalsIgnoreCase("") && !m.getConfig().getBoolean("toggle-no-reason"))
            reason = m.parseMessage(m.getConfig().getString("defaults.reason"));
        if(reason.equalsIgnoreCase(""))
            annType = "ipbanNoRsn";
        else
            annType = "ipban";

        if(SqlCache.isIpBanned(targetIp) && !sender.hasPermission("custombansplus.overwrite")){
            sender.sendMessage("§cIP is already banned and you don't have overwrites permission.");
            return true;
        }

        if (duration.equalsIgnoreCase("perm")) annType = "perm" + annType;
        else annType = "temp" + annType;

        int fs = s;

        new Thread(() -> {
            try {
                SqlMethods.updateHistoryStatus(tgtUuid, "Ban", "Overwritten", sender);
                setIpBan();
                Bukkit.getOnlinePlayers().stream().filter(p -> m.getSqlIp(p.getUniqueId()).equalsIgnoreCase(targetIp)).forEach(p -> {
                    SqlAbstractBanCommand.banPage(p.getUniqueId());
                    try {
                        SqlMethods.addHistory(p.getUniqueId(), "Ban", sender, reason);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (fs == 0) SqlAbstractAnnouncer.getAnnouncer(target, sender.getName(), args[1+fs], reason, annType);
            else SqlAbstractAnnouncer.getSilentAnnouncer(target, sender.getName(), args[1+fs], reason, annType);
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
